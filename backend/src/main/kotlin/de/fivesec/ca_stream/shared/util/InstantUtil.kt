package de.fivesec.ca_stream.shared.util

import java.time.Instant
import java.time.temporal.ChronoUnit

object InstantUtil {
    @JvmStatic
    fun now(): Instant {
        return Instant.now().truncatedTo(ChronoUnit.MICROS)
    }
}
