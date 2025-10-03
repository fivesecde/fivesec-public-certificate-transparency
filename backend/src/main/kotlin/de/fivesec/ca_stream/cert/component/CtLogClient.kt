package de.fivesec.ca_stream.cert.component

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.fivesec.ca_stream.app.configuration.CertLogConfiguration
import org.slf4j.LoggerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Component
class CtLogClient(
    private val mapper: ObjectMapper,
    config: CertLogConfiguration
) {

    private val client: WebClient


    init {
        val http = HttpClient.create()
            .responseTimeout(Duration.ofMillis(config.readTimeoutMillis))
        client = WebClient.builder()
            .baseUrl(config.base)
            .clientConnector(ReactorClientHttpConnector(http))
            .build()
        LOG.info("CT base={} batchSize={} intervalMs={}", config.base, config.batchSize, config.scheduleMillis)
    }

    fun getSth(): JsonNode? =
        client.get().uri("/ct/v1/get-sth")
            .retrieve()
            .bodyToMono(String::class.java)
            .map { mapper.readTree(it) }
            .onErrorResume { e ->
                LOG.warn("get-sth error: ${e.message}")
                Mono.empty()
            }
            .block()

    fun getEntries(start: Long, end: Long): JsonNode? =
        client.get().uri("/ct/v1/get-entries?start=$start&end=$end")
            .retrieve()
            .bodyToMono(String::class.java)
            .map { jacksonObjectMapper().readTree(it) }
            .onErrorResume { e ->
                LOG.warn("get-entries error: ${e.message}")
                Mono.empty()
            }
            .block()

    companion object {
        private val LOG = LoggerFactory.getLogger(this::class.java)
    }
}