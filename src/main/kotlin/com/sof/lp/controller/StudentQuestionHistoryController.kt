package com.sof.lp.controller

import com.sof.lp.config.logger
import com.sof.lp.entity.StudentQuestionHistory
import com.sof.lp.entity.dto.CompleteQuestionDTO
import com.sof.lp.repository.StudentQuestionHistoryRepository
import com.sof.lp.service.StudentQuestionHistoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.student-question-history.base-url}")
class StudentQuestionHistoryController(
    private val studentQuestionHistoryService: StudentQuestionHistoryService
) {

    private val log = logger<StudentQuestionHistoryController>()

    @GetMapping("/{studentId}")
    fun getCompletedQuestionIds(@PathVariable studentId: String): ResponseEntity<List<Long>> {
        val ids = studentQuestionHistoryService.getCompletedQuestionIds(studentId)
        return if (ids.isNotEmpty()) {
            ResponseEntity(ids, HttpStatus.OK)
        } else {
            ResponseEntity.status(HttpStatus.NO_CONTENT).body(listOf())  // If all failed
        }
    }

}