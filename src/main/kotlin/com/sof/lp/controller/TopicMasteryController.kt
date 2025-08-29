package com.sof.lp.controller

import com.sof.lp.config.logger
import com.sof.lp.entity.dto.MasteryResponseDTO
import com.sof.lp.entity.dto.SubmissionRequestDTO
import com.sof.lp.entity.dto.TopicMasteryRequestDTO
import com.sof.lp.service.StudentProfileService
import com.sof.lp.service.TopicMasteryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
//import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("\${api.mastery.base-url}")
class TopicMasteryController(
    private val profileService: StudentProfileService,
    private val topicMasteryService: TopicMasteryService
) {

    private val log = logger<TopicMasteryController>()

    /**
     * Endpoint to submit responses for a topic, accumulate histories, hunt θ, and update mastery.
     * Test with POST body: { "histories": [{questionId:1, subjectId:1, correct:true, difficulty:"easy"}, ...] }
     */
    @PostMapping("\${api.mastery.submission}")
//    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER')")  // Secure; adjust as needed
    fun submit(@RequestBody request: SubmissionRequestDTO): ResponseEntity<List<MasteryResponseDTO>> {
        val responses = profileService.submit(request)
        return if (responses.isNotEmpty()) {
            ResponseEntity.ok(responses)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())  // If all failed
        }
    }

    /**
     * Endpoint to query mastery for a student/topic (e.g., for exercise-generator API call).
     * Test with POST /api/students (with body containing studentId and topicIds).
     * Note: Original comment suggested GET, but since it uses @RequestBody, keeping as POST.
     * If you want to switch to GET with path params or query params, let me know for further adjustments.
     */
    @PostMapping
//    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('SYSTEM')")
    fun get(@RequestBody request: TopicMasteryRequestDTO): ResponseEntity<List<MasteryResponseDTO>> {
        return try {
            val responses = topicMasteryService.getTopicMasteries(request)
            if (responses.isNotEmpty()) {
                ResponseEntity.ok(responses)
            } else {
                ResponseEntity.noContent().build()
            }
        } catch (e: ResponseStatusException) {
            ResponseEntity.notFound().build()
        }
    }
}