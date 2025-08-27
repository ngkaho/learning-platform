package com.sof.lp.repository

import com.sof.lp.entity.StudentProfile
import org.springframework.data.jpa.repository.JpaRepository

interface StudentProfileRepository : JpaRepository<StudentProfile, Long> {

    fun findByStudentId(studentId: String): StudentProfile?

}