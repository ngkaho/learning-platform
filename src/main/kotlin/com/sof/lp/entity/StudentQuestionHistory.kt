package com.sof.lp.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
data class StudentQuestionHistory(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val studentId: String,
    val questionId: Long,
    val subjectId: Long,
    val topicId: Long,
    val completedDate: Instant = Instant.now(),
    val correct: Boolean,
    val difficulty: String,
    val active: Boolean = true,
    val lastModifiedDate: Instant = Instant.now(),
    val createdDate: Instant = Instant.now()
)