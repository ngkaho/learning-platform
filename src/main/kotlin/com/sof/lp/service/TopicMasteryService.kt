package com.sof.lp.service

import com.sof.lp.config.logger
import com.sof.lp.controller.TopicMasteryController
import com.sof.lp.entity.dto.MasteryResponseDTO
import com.sof.lp.entity.dto.TopicMasteryRequestDTO
import com.sof.lp.repository.StudentProfileRepository
import com.sof.lp.repository.TopicMasteryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TopicMasteryService(
    private val profileRepo: StudentProfileRepository,
    private val masteryRepo: TopicMasteryRepository
) {

    private val log = logger<TopicMasteryController>()

    fun getTopicMasteries(request: TopicMasteryRequestDTO): List<MasteryResponseDTO> {
        val profile = profileRepo.findByStudentId(request.studentId)
            ?: run {
                log.warn("Student profile not found for ID: ${request.studentId}")
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found for ID: ${request.studentId}")
            }

        val masteries = masteryRepo.findByProfileAndTopicIdIn(profile, request.topicIds)
        return masteries.map { mastery ->
            MasteryResponseDTO(
                theta = mastery.theta,
                masteryScore = mastery.masteryScore,
                lastModifiedBy = mastery.lastModifiedDate,  // Assuming this is the intended field
                topicId = mastery.topicId
            )
        }
    }

}