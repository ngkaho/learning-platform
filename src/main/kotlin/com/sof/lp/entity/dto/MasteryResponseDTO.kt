package com.sof.lp.entity.dto

import java.time.Instant

data class MasteryResponseDTO (
    val theta: Double,
    val masteryScore: Int,
    val lastModifiedBy: Instant,
    val topicId: Long
)