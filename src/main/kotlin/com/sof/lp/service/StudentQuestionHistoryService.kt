package com.sof.lp.service

import com.sof.lp.config.logger
import com.sof.lp.controller.StudentQuestionHistoryController
import com.sof.lp.entity.StudentQuestionHistory
import com.sof.lp.entity.dto.StudentQuestionHistoryDTO
import com.sof.lp.repository.StudentQuestionHistoryRepository
import org.springframework.stereotype.Service

@Service
class StudentQuestionHistoryService(private val sqhRepo: StudentQuestionHistoryRepository) {

    private val log = logger<StudentQuestionHistoryController>()

    fun getCompletedQuestionIds(studentId: String): List<Long> {
        val history = sqhRepo.findByStudentId(studentId)
        val questionIds = history.map { it.questionId }
        log.info("questionIds: $questionIds")
        return questionIds
    }

    fun completeQuestion(histories: List<StudentQuestionHistoryDTO>): List<StudentQuestionHistory> {

        log.info("Save completed questions into StudentQuestionHistory. ")
        val history = histories.map {
            StudentQuestionHistory(
                studentId = it.studentId,
                questionId = it.questionId,
                subjectId = it.subjectId,
                topicId = it.topicId,
                correct = it.correct,
                difficulty = it.difficulty  // From exercise-generator
            )
        }
        log.info("Successfully saved. ")

        return sqhRepo.saveAll(history)

    }

    fun getAllActiveQuestionHistories (studentId: String, topicId: Long): List<StudentQuestionHistory> {
        return sqhRepo.findByStudentIdAndTopicIdAndActive(studentId, topicId, true)
    }

}