package com.sof.lp.service

import com.sof.lp.config.logger
import com.sof.lp.entity.StudentProfile
import com.sof.lp.entity.StudentQuestionHistory
import com.sof.lp.entity.TopicMastery
import com.sof.lp.entity.dto.MasteryResponseDTO
import com.sof.lp.entity.dto.StudentQuestionHistoryDTO
import com.sof.lp.entity.dto.SubmissionRequestDTO
import com.sof.lp.repository.StudentProfileRepository
import com.sof.lp.repository.StudentQuestionHistoryRepository
import com.sof.lp.repository.TopicMasteryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import kotlin.math.tanh

@Service
class StudentProfileService(
    private val profileRepo: StudentProfileRepository,
    private val masteryRepo: TopicMasteryRepository,
    private val masteryCalculator: MasteryCalculator,
    private val studentQuestionHistoryService: StudentQuestionHistoryService) {

    private val log = logger<StudentProfileService>()

    @Transactional
    fun createOrUpdateProfile(studentId: String): StudentProfile {
        val profile = profileRepo.findByStudentId(studentId)
        return profile ?: profileRepo.save(StudentProfile(studentId = studentId))
    }

    @Transactional
    fun getOrCreateMastery(profile: StudentProfile, topicId: Long): TopicMastery {
        return masteryRepo.findByProfileAndTopicId(profile, topicId)
            ?: masteryRepo.save(TopicMastery(profile = profile, topicId = topicId))
    }

    /**
     * Processes batch submission of response histories, validates, groups by topic, and updates masteries.
     * Throws ResponseStatusException for bad requests (empty histories or multiple student IDs).
     * For each topic group, attempts to process and collect updated masteries in memory; logs and skips on per-topic errors.
     * Saves all successful masteries in one batch via saveAll.
     * Returns the list of successful MasteryResponseDTOs (may be empty if all topics failed).
     */
    @Transactional
    fun submit(request: SubmissionRequestDTO): List<MasteryResponseDTO> {
        if (request.histories.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Histories cannot be empty")
        }

        val allStudentIds = request.histories.map { it.studentId }.toSet()
        if (allStudentIds.size > 1) {
            log.warn("Submission contains multiple student IDs: $allStudentIds")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "All histories must belong to the same student")
        }
        val studentId = allStudentIds.first()

        // Create/update profile once, outside loop
        val profile = createOrUpdateProfile(studentId)

        val groups = request.histories.groupBy { it.topicId }

        val updatedMasteries = mutableListOf<TopicMastery>()
        val responses = mutableListOf<MasteryResponseDTO>()

        for ((topicId, groupHistories) in groups) {
            try {
                val mastery = getOrCreateMastery(profile, topicId)

                if (studentQuestionHistoryService.completeQuestion(groupHistories).isNotEmpty()) {
                    // Hunt on accumulated active histories
                    val theta = masteryCalculator.huntTheta(studentQuestionHistoryService.getAllActiveQuestionHistories(studentId, topicId))
                    val newMastery = TopicMastery(
                        id = mastery.id,  // Set for update if exists
                        profile = profile,
                        topicId = topicId,
                        theta = theta,
                        masteryScore = (50 * (1 + tanh(theta))).toInt(),
                        active = true,
                        lastModifiedDate = Instant.now()
                    )

                    updatedMasteries.add(newMastery)

                    val dto = MasteryResponseDTO(
                        theta = newMastery.theta,
                        masteryScore = newMastery.masteryScore,
                        lastModifiedBy = newMastery.lastModifiedDate,
                        topicId = newMastery.topicId
                    )
                    responses.add(dto)
                } else {
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Histories cannot be saved for topic $topicId")
                }
            } catch (e: Exception) {
                log.error("Error processing topic $topicId for student $studentId: ${e.message}", e)
                // Skip to process others
            }
        }

        val result = masteryRepo.saveAll(updatedMasteries)
        // Batch save all updated masteries in one DB action
        if (result.isNotEmpty()) {
            return responses
        } else {
            log.error("Error updating topic mastery. ")
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot update Topic Mastery. ")
        }

    }

}
