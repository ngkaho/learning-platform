package com.sof.lp.entity.dto

data class TopicMasteryRequestDTO (
    val studentId: String,
    val topicIds: List<Long>
)