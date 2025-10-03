package de.fivesec.ca_stream.cert.entity

import com.fasterxml.jackson.databind.JsonNode
import de.fivesec.ca_stream.shared.entity.BaseEntity
import de.fivesec.ca_stream.shared.entity.BaseEntityProvider
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.Instant
import java.util.*

@Entity
@Table(name = "cert_events")
class CertDataEntity(

    @Id
    @Column(name = "id", columnDefinition = "uuid", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "domain", nullable = false)
    var domain: String,

    @Column(name = "subject_dn", nullable = false)
    var subjectDn: String,

    @Column(name = "issuer_dn", nullable = false)
    var issuerDn: String,

    @Column(name = "not_before", nullable = false)
    var notBefore: Instant,

    @Column(name = "not_after", nullable = false)
    var notAfter: Instant,

    @Column(name = "serial_hex", nullable = false)
    var serialHex: String,

    @Type(JsonType::class)
    @Column(name = "raw_entry", columnDefinition = "jsonb")
    var rawEntry: JsonNode? = null

) : BaseEntity(
    BaseEntityProvider.createdAt(),
    BaseEntityProvider.lastUpdatedAt(),
    BaseEntityProvider.createdBy(),
    BaseEntityProvider.lastUpdatedBy(),
)