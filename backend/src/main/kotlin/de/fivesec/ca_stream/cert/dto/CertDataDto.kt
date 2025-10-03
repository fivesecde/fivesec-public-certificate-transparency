package de.fivesec.ca_stream.cert.dto

import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant
import java.util.*

data class CertDataDto(
    val id: UUID,
    val domain: String,
    val subjectDn: String,
    val issuerDn: String,
    val notBefore: Instant,
    val notAfter: Instant,
    val serialHex: String,
    val rawEntry: JsonNode? = null
)
