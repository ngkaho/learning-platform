package com.sof.lp.controller

import com.sof.lp.config.logger
import com.sof.lp.entity.StudentQuestionHistory
import com.sof.lp.entity.dto.CompleteQuestionDTO
import com.sof.lp.repository.StudentQuestionHistoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("\${api.student-question-history.base-url}")
class StudentQuestionHistoryController(
    private val studentQuestionHistoryRepository: StudentQuestionHistoryRepository
) {

    private val log = logger<StudentQuestionHistoryController>()

    @GetMapping("/{studentId}")
    fun getCompletedQuestionIds(@PathVariable studentId: String): ResponseEntity<List<Long>> {
        val history = studentQuestionHistoryRepository.findByStudentId(studentId)
        val questionIds = history.map { it.questionId }
        log.info("questionIds: $questionIds")
        return ResponseEntity.ok(questionIds)
    }

    @PostMapping
    fun completeQuestion(@RequestBody request: CompleteQuestionDTO): ResponseEntity<String> {
        val history = StudentQuestionHistory(
            studentId = request.studentId,
            questionId = request.questionId,
            subjectId = request.subjectId,
            topicId = request.topicId,
            correct = request.correct,
            difficulty = request.difficulty
        )
        studentQuestionHistoryRepository.save(history)
        return ResponseEntity.ok("Question recorded")
    }
}