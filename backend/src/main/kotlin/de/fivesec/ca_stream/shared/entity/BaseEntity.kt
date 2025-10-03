package de.fivesec.ca_stream.shared.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import java.io.Serializable
import java.time.Instant
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant,
    @LastModifiedDate @Column(name = "last_updated_at") var lastUpdatedAt: Instant,
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    open var createdBy: String,
    @LastModifiedBy @Column(name = "last_updated_by") var lastUpdatedBy: String
) : Serializable
