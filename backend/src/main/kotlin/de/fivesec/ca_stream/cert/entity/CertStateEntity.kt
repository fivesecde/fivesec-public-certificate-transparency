package de.fivesec.ca_stream.cert.entity

import de.fivesec.ca_stream.shared.entity.BaseEntity
import de.fivesec.ca_stream.shared.entity.BaseEntityProvider
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ct_state")
class CertStateEntity(
    @Id
    @Column(name = "log_base", nullable = false)  // PK is the log base URL
    var logBase: String,

    @Column(name = "next_index", nullable = false)
    var nextIndex: Long,
) : BaseEntity(
    BaseEntityProvider.createdAt(),
    BaseEntityProvider.lastUpdatedAt(),
    BaseEntityProvider.createdBy(),
    BaseEntityProvider.lastUpdatedBy(),
)