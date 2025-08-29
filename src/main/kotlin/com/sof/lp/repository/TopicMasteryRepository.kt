package com.sof.lp.repository

import com.sof.lp.entity.StudentProfile
import com.sof.lp.entity.TopicMastery
import org.springframework.data.jpa.repository.JpaRepository

interface TopicMasteryRepository : JpaRepository<TopicMastery, Long> {

    fun findByProfileAndTopicId(profile: StudentProfile, topicId: Long): TopicMastery?

    fun findByProfileAndTopicIdIn(profile: StudentProfile, topicIds: List<Long>): List<TopicMastery>

}