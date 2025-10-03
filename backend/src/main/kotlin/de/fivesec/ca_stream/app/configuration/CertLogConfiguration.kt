package de.fivesec.ca_stream.app.configuration

import jakarta.validation.Valid
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "crt")
data class CertLogConfiguration(
    var base: String = "https://ct.googleapis.com/logs/us1/argon2025",
    var batchSize: Int = 256,
    var warmStartEntries: Long = 512,
    var scheduleMillis: Long = 3000,
    var connectTimeoutMillis: Long = 5000,
    var readTimeoutMillis: Long = 10000,
    @field:Valid val apiKey: String
)