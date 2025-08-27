package com.sof.lp.service

import com.sof.lp.config.logger
import com.sof.lp.entity.StudentProfile
import com.sof.lp.entity.StudentQuestionHistory
import com.sof.lp.entity.TopicMastery
import com.sof.lp.entity.dto.StudentQuestionHistoryDTO
import com.sof.lp.repository.StudentProfileRepository
import com.sof.lp.repository.StudentQuestionHistoryRepository
import com.sof.lp.repository.TopicMasteryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.math.tanh

@Service
class StudentProfileService(
    private val profileRepo: StudentProfileRepository,
    private val masteryRepo: TopicMasteryRepository,
    private val historyRepo: StudentQuestionHistoryRepository,
    private val masteryCalculator: MasteryCalculator) {

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

    @Transactional
    fun addTopicMastery(studentId: String, topicId: Long): TopicMastery {
        val profile = createOrUpdateProfile(studentId)
        return masteryRepo.findByProfileAndTopicId(profile, topicId) ?: masteryRepo.save(TopicMastery(profile = profile, topicId = topicId))
    }

    @Transactional
    fun submitResponses(studentId: String, topicId: Long, newHistories: List<StudentQuestionHistoryDTO>): TopicMastery {
        val profile = createOrUpdateProfile(studentId)
        val mastery = getOrCreateMastery(profile, topicId)  // Helper to find/create TopicMastery

        // Accumulate: Save new histories (with difficulty from DTO)
        val history = newHistories.map {
            StudentQuestionHistory(
                studentId = studentId,
                questionId = it.questionId,
                subjectId = it.subjectId,
                topicId = topicId,
                correct = it.correct,
                difficulty = it.difficulty  // From exercise-generator
            )
        }
        historyRepo.saveAll(history)  // New repo: StudentQuestionHistoryRepository

        // Hunt on accumulated active histories
        val allActiveHistories = historyRepo.findByStudentIdAndTopicIdAndActive(studentId, topicId, true)
        val theta = masteryCalculator.huntTheta(allActiveHistories)
        val newMastery = TopicMastery(
            id = mastery.id,
            profile = profile,
            topicId = topicId,
            theta = theta,
            masteryScore = (50 * (1 + tanh(theta))).toInt(),
            active = true,
            lastModifiedDate = Instant.now()
        )

        return masteryRepo.save(newMastery)

    }

}
