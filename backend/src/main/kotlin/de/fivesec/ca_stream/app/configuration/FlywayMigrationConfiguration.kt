package de.fivesec.ca_stream.app.configuration

import jakarta.validation.Valid
import net.logstash.logback.argument.StructuredArguments.kv
import org.flywaydb.core.Flyway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import kotlin.jvm.java

@ConfigurationProperties(prefix = "flyway.strategy")
@Validated
data class FlywayStrategyConfiguration(
    @field:Valid val migrate: Boolean = true,
    @field:Valid val repair: Boolean = false
)

@Configuration
class FlywayMigrationConfiguration {

    @Bean
    fun flywayMigrationStrategy(
        flywayStrategyConfiguration: FlywayStrategyConfiguration
    ): FlywayMigrationStrategy? {
        return FlywayMigrationStrategy { flyway: Flyway? ->
            LOG.info(
                "{} - Flyway migration configuration set to: {}",
                kv("logEvent", "flywayMigrationSetup"),
                kv("configuration", flywayStrategyConfiguration)
            )
            try {
                if (flywayStrategyConfiguration.repair) {
                    LOG.warn(
                        "{} - Repairing Database by flyway",
                        kv("logEvent", "flywayMigrationRepair"),
                    )
                    flyway?.repair()
                }
                if (flywayStrategyConfiguration.migrate) {
                    LOG.info(
                        "{} - Migrating Database by flyway",
                        kv("logEvent", "flywayMigrationMigrate"),
                    )
                    flyway?.migrate()
                }
            } catch (e: Exception) {
                LOG.error(
                    "{} - Flyway migration threw exception: {}",
                    kv("logEvent", "flywayMigrationError"),
                    kv("errorMessage", e.message),
                    e
                )
            }
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
