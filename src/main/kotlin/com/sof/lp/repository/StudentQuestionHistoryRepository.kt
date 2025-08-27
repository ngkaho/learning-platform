package com.sof.lp.repository

import com.sof.lp.entity.StudentQuestionHistory
import org.springframework.data.jpa.repository.JpaRepository

interface StudentQuestionHistoryRepository : JpaRepository<StudentQuestionHistory, Long> {

    fun findByStudentId(studentId: String): List<StudentQuestionHistory>

    fun findByStudentIdAndTopicIdAndActive(studentId: String, topicId: Long, active: Boolean): List<StudentQuestionHistory>

}