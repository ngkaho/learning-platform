package com.sof.lp.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.Instant

@Entity
data class StudentProfile (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val studentId: String,  // Links to management-console's student entity via ID (e.g., fetched via API)

    @OneToMany(mappedBy = "profile", cascade = [CascadeType.ALL])
    @JsonIgnore
    val topicMasteries: MutableList<TopicMastery> = mutableListOf(),

    val active: Boolean = true,
    val lastModifiedDate: Instant = Instant.now(),
    val createdDate: Instant = Instant.now()

) {
    override fun toString(): String {
        return "StudentProfile(id=$id, studentId='$studentId', lastModifiedDate=$lastModifiedDate)"  // Exclude topicMasteries to break cycle
    }
}

@Entity
data class TopicMastery (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    val profile: StudentProfile,

    @Column(nullable = false)
    val topicId: Long,  // E.g., "math_p1_addition" (subject_grade_topic)

    @Column
    val theta: Double = 0.0,  // Hunted latent mastery

    @Column
    val masteryScore: Int = 50,  // Derived: 50 * (1 + tanh(theta))
    val active: Boolean = true,
    val lastModifiedDate: Instant = Instant.now(),
    val createdDate: Instant = Instant.now()

) {
    override fun toString(): String {
        return "TopicMastery(id=$id, topicId=$topicId, theta=$theta, masteryScore=$masteryScore, lastModifiedDate=$lastModifiedDate, profileId=${profile.id})"  // Safe: profile.id only, no full profile
    }
}