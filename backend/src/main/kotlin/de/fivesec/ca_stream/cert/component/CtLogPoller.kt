package de.fivesec.ca_stream.cert.component

import com.fasterxml.jackson.databind.JsonNode
import de.fivesec.ca_stream.app.configuration.CertLogConfiguration
import de.fivesec.ca_stream.cert.entity.CertDataEntity
import de.fivesec.ca_stream.cert.entity.CertStateEntity
import de.fivesec.ca_stream.cert.parser.CtEntryParser
import de.fivesec.ca_stream.cert.repository.CertDataRepository
import de.fivesec.ca_stream.cert.repository.CertStateRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max
import kotlin.math.min

@Transactional
@Component
class CtLogPoller(
    private val ctLogClient: CtLogClient,
    private val configuration: CertLogConfiguration,
    private val parser: CtEntryParser,
    private val certDataRepository: CertDataRepository,
    private val ctStateRepository: CertStateRepository
) {
    private val nextIndexInMemory = AtomicLong(-1)

    @Scheduled(fixedDelay = 3000)
    fun poll() {
        try {
            val sth = ctLogClient.getSth() ?: return
            val treeSize = sth.path("tree_size").asLong(0)
            if (treeSize <= 0) return

            val logBase = configuration.base
            var start = ensureCursorLoaded(logBase)

            // small overlap to survive crashes/partial batches
            val overlap = 16L
            if (start > 0) start = max(0L, start - overlap)

            val batchSize = configuration.batchSize.coerceAtLeast(1)

            var processed = 0
            while (start < treeSize) {
                val end = min(start + (batchSize - 1), treeSize - 1)
                val page = ctLogClient.getEntries(start, end) ?: break
                val entries = page.path("entries")
                if (!entries.isArray || entries.isEmpty) break

                val pageCount = storePage(entries)
                processed += pageCount

                val next = end + 1
                saveCursor(logBase, next)
                nextIndexInMemory.set(next)

                if (processed >= 4096) break
                start = next
            }

            if (processed > 0) {
                LOG.info("CT: processed={} nextIndex={}", processed, nextIndexInMemory.get())
            }
        } catch (e: Exception) {
            LOG.error("ct poll failed", e)
        }
    }


    fun storePage(entries: JsonNode): Int {
        var cnt = 0
        entries.forEach { node ->
            runCatching {
                val parsed = parser.parseEntry(node) ?: return@runCatching
                val event = parser.toEvent(parsed, node)
                val domains = event.domains.ifEmpty { listOf(null) }.filterNotNull()

                for (d in domains) {
                    val entity = CertDataEntity(
                        domain = d,
                        subjectDn = event.subject ?: "",
                        issuerDn = event.issuer ?: "",
                        notBefore = event.notBefore ?: Instant.EPOCH,
                        notAfter = event.notAfter ?: Instant.EPOCH,
                        serialHex = event.serialHex ?: "",
                        rawEntry = event.rawEntry
                    )
                    certDataRepository.save(entity)
                    cnt++
                }
            }.onFailure { ex ->
                LOG.warn("skip entry due to parse/store error: {}", ex.message)
            }
        }
        return cnt
    }

    private fun ensureCursorLoaded(logBase: String): Long {
        val cached = nextIndexInMemory.get()
        if (cached >= 0) return cached

        val db = ctStateRepository.findById(logBase).orElse(null)
        val start = db?.nextIndex ?: warmStartFromSth()
        nextIndexInMemory.set(start)
        return start
    }

    private fun warmStartFromSth(): Long {
        val sth = ctLogClient.getSth()
        val treeSize = sth?.path("tree_size")?.asLong(0) ?: 0L
        if (treeSize <= 0) return 0L
        val warm = configuration.warmStartEntries
        return max(0L, treeSize - warm)
    }


    fun saveCursor(logBase: String, nextIndex: Long) {
        val existing = ctStateRepository.findById(logBase).orElse(null)
        if (existing == null) {
            ctStateRepository.save(CertStateEntity(logBase = logBase, nextIndex = nextIndex))
        } else {
            existing.nextIndex = nextIndex
            ctStateRepository.save(existing)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CtLogPoller::class.java)
    }
}
