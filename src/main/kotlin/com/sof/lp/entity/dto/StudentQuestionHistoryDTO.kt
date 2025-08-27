package com.sof.lp.entity.dto

import java.time.Instant

data class StudentQuestionHistoryDTO (
    val studentId: String,
    val questionId: Long,
    val subjectId: Long,
    val topicId: Long,
    val completedDate: Instant,
    val correct: Boolean,
    val difficulty: String
)

data class SubmissionRequestDTO (
    val histories: List<StudentQuestionHistoryDTO>
)