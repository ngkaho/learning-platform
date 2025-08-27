package com.sof.lp.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
data class Student (

    @Id
    val id: String? = null,
    val lastName: String? = null,
    val firstName: String? = null,
    val givenName: String? = null,
    val chineseLastName: String? = null,
    val chineseFirstName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: Instant? = null,
    val sex: String? = null,
    val organiserId: String? = null,
    val classId: String? = null,
    val guardianId: String? = null,
    val joinDate: Instant? = null,
    val active: Boolean = true,
    val lastModifiedDate: Instant = Instant.now(),
    val createdDate: Instant = Instant.now()

)