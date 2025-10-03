package de.fivesec.ca_stream.cert.dto

import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant

data class CertEventDto(
    val domains: List<String>,
    val subject: String?,
    val issuer: String?,
    val notBefore: Instant?,
    val notAfter: Instant?,
    val serialHex: String?,
    val rawEntry: JsonNode
)
