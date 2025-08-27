package com.sof.lp.entity.dto

data class CompleteQuestionDTO (
    val studentId: String,
    val questionId: Long,
    val subjectId: Long,
    val topicId: Long,
    val correct: Boolean,
    val difficulty: String
)