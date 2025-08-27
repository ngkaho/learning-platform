package com.sof.lp.controller

import com.sof.lp.config.logger
import com.sof.lp.entity.dto.MasteryResponseDTO
import com.sof.lp.entity.dto.SubmissionRequestDTO
import com.sof.lp.repository.StudentProfileRepository
import com.sof.lp.repository.TopicMasteryRepository
import com.sof.lp.service.StudentProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
//import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.mastery.base-url}")
class StudentMasteryController(
    private val profileService: StudentProfileService,
    private val profileRepo: StudentProfileRepository,
    private val masteryRepo: TopicMasteryRepository
) {

    private val log = logger<StudentMasteryController>()

    /**
     * Endpoint to submit responses for a topic, accumulate histories, hunt θ, and update mastery.
     * Test with POST body: { "histories": [{questionId:1, subjectId:1, correct:true, difficulty:"easy"}, ...] }
     */
    @PostMapping("\${api.mastery.submission}")
//    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")  // Secure; adjust as needed
    fun submitResponses(
        @RequestBody request: SubmissionRequestDTO
    ): ResponseEntity<List<MasteryResponseDTO>> {

        // If request null or empty, return proper error
        if (request.histories.isEmpty()) {
            return ResponseEntity.badRequest().body(listOf())  // Or custom error DTO; empty list for simplicity
        }

        // Validate all histories have the same studentId
        val allStudentIds = request.histories.map { it.studentId }.toSet()
        if (allStudentIds.size > 1) {
            log.warn("Submission contains multiple student IDs: $allStudentIds")
            return ResponseEntity.badRequest()
                .body(listOf())  // Or error message: "All histories must belong to the same student"
        }
        val studentId = allStudentIds.first()

        // Group histories by topicId (allows multiple topics in one submission)
        val groups = request.histories.groupBy { it.topicId }

        // Process each group separately
        val responses = mutableListOf<MasteryResponseDTO>()
        for ((topicId, groupHistories) in groups) {
            try {
                // Submit for this topic group (accumulates and hunts θ)
                val mastery = profileService.submitResponses(studentId, topicId, groupHistories)

                // Map to DTO
                val dto = MasteryResponseDTO(
                    theta = mastery.theta,
                    masteryScore = mastery.masteryScore,
                    lastModifiedBy = mastery.lastModifiedDate,
                    topicId = mastery.topicId
                )
                responses.add(dto)
            } catch (e: Exception) {
                log.error("Error processing topic $topicId for student $studentId: ${e.message}", e)
                // Continue or return error; here, skip to process others
            }
        }

        return if (responses.isNotEmpty()) {
            ResponseEntity(responses, HttpStatus.OK)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(listOf())  // If all failed
        }
    }

    /**
     * Endpoint to query mastery for a student/topic (e.g., for exercise-generator API call).
     * Test with GET /api/students/123/topics/456/mastery
     */
    @GetMapping("/{studentId}/topics/{topicId}/mastery")
//    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('SYSTEM')")  // Allow exercise-generator as 'SYSTEM' via API key?
    fun getMastery(
        @PathVariable studentId: String,
        @PathVariable topicId: Long
    ): ResponseEntity<MasteryResponseDTO> {
        val profile = profileRepo.findByStudentId(studentId)
            ?: return ResponseEntity.notFound().build()

        val mastery = masteryRepo.findByProfileAndTopicId(profile, topicId)
            ?: return ResponseEntity.notFound().build()

        val response = MasteryResponseDTO(
            theta = mastery.theta,
            masteryScore = mastery.masteryScore,
            lastModifiedBy = mastery.lastModifiedDate,
            topicId = mastery.topicId
        )

        return ResponseEntity(response, HttpStatus.OK)
    }
}