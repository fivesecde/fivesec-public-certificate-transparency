package de.fivesec.ca_stream.cert.parser

import com.fasterxml.jackson.databind.JsonNode
import de.fivesec.ca_stream.cert.dto.CertEventDto
import de.fivesec.ca_stream.cert.model.ParsedChain
import org.springframework.stereotype.Service
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.security.auth.x500.X500Principal

@Service
class CtEntryParser(
    private val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
) {

    fun parseEntry(entryNode: JsonNode): ParsedChain? {
        val leafInputB64 = entryNode.path("leaf_input").asText(null) ?: return null
        val extraDataB64 = entryNode.path("extra_data").asText(null) // KANN null sein!

        val leaf = B64.decodeStd(leafInputB64)
        val li = beReader(leaf)

        li.u8()        // 0
        li.u8()       // 0 = TIMESTAMPED_ENTRY
        li.u64()                     // timestamp
        val entryType = li.u16()     // 0 = X509_ENTRY, 1 = PRECERT_ENTRY

        val certs = mutableListOf<X509Certificate>()

        // helper: u24-Blob aus Reader lesen
        fun readBlobU24OrNull(r: BEReader): ByteArray? {
            if (r.remaining() < 3) return null
            val len = r.u24()
            if (len <= 0 || r.remaining() < len) return null
            return r.take(len)
        }

        // Versuch 1: Leaf-Zertifikat direkt aus leaf_input ziehen (X509_ENTRY)
        var leafCert: X509Certificate? = null
        if (entryType == 0) { // X509
            // Nach entry_type kommt typischerweise die Länge (u24) + DER-Zertifikat
            val li2 = beReader(leaf.copyOf())
            li2.u8(); li2.u8(); li2.u64(); li2.u16() // bis nach entryType vorspulen
            val maybeCert = readBlobU24OrNull(li2)
            if (maybeCert != null && maybeCert.isNotEmpty() && (maybeCert[0].toInt() and 0xFF) == 0x30) {
                runCatching {
                    leafCert = cf.generateCertificate(maybeCert.inputStream()) as X509Certificate
                    certs += leafCert!!
                }
            }
        } else {
            // PRECERT: in leaf_input steckt TBS (u24). Das ist KEIN volles X.509 → hier NICHT cf.generateCertificate() aufrufen.
            // Wir könnten TBS später mit BC parsen; fürs Erste ignorieren wir es und holen nur Chain-Zerts aus extra_data (falls vorhanden).
            val li2 = beReader(leaf.copyOf())
            li2.u8(); li2.u8(); li2.u64(); li2.u16() // bis entryType
            readBlobU24OrNull(li2) // TBS konsumieren/überspringen
        }

        // Versuch 2: Chain aus extra_data lesen (falls vorhanden)
        if (extraDataB64 != null) {
            val extra = B64.decodeStd(extraDataB64)
            val rd = beReader(extra)

            fun readCertChain() {
                while (rd.remaining() >= 3) {
                    val blob = readBlobU24OrNull(rd) ?: break
                    // Ein echtes DER-Zert startet (nahezu immer) mit 0x30 (SEQUENCE)
                    if (blob.isNotEmpty() && (blob[0].toInt() and 0xFF) == 0x30) {
                        runCatching {
                            val cert = cf.generateCertificate(blob.inputStream()) as X509Certificate
                            certs += cert
                            if (leafCert == null && entryType == 0) {
                                leafCert =
                                    cert // falls wir das Leaf noch nicht hatten, erstes aus Chain als Leaf interpretieren
                            }
                        }.onFailure {
                            // ignorieren – könnte chain_count o.ä. sein
                        }
                    } else {
                        // Nicht-Zertifikat (z. B. Count/TBS etc.) → ignorieren
                    }
                }
            }

            if (entryType == 0) {
                // Bei manchen Logs kommt vor der Chain ein Count-Blob (kein 0x30) – mit Heuristik einfach überspringen.
                val peekReader = beReader(extra.copyOf())
                val peek = readBlobU24OrNull(peekReader)
                if (peek != null && (peek.isEmpty() || (peek[0].toInt() and 0xFF) != 0x30)) {
                    // Count weglesen
                    readBlobU24OrNull(rd) // Hinweis: wenn du hier „neu“ liest, nimm den gleichen Reader; für Kürze ignoriert
                }
                // vereinfachend: direkt komplette Chain lesen
                readCertChain()
            } else {
                // PRECERT: TBS bereits im leaf_input übersprungen; jetzt nur Chain lesen
                readCertChain()
            }
        }

        // Wenn wir bis hier kein Leaf haben und nichts in certs – dann gab's nichts Verwertbares
        if (leafCert == null && certs.isEmpty()) {
            return ParsedChain(null, emptyList()) // oder null, wenn du wirklich nichts willst
        }

        return ParsedChain(leafCert, certs)
    }

    fun toEvent(parsed: ParsedChain, rawEntry: JsonNode): CertEventDto {
        val leaf = parsed.leafCert
        val domains = extractDomains(leaf)
        val subject = leaf?.subjectX500Principal?.name
        val issuer = leaf?.issuerX500Principal?.name
        val nb = leaf?.notBefore?.toInstant()
        val na = leaf?.notAfter?.toInstant()
        val serial = leaf?.serialNumber?.toString(16)
        return CertEventDto(domains, subject, issuer, nb, na, serial, rawEntry)
    }

    private fun extractDomains(cert: X509Certificate?): List<String> {
        if (cert == null) return emptyList()
        val out = mutableSetOf<String>()

        runCatching {
            parseCN(cert.subjectX500Principal)?.let { out += it.lowercase().trim() }
        }
        runCatching {
            val sans = cert.subjectAlternativeNames ?: return@runCatching
            for (san in sans) {
                if (san.size >= 2 && (san[0] as? Int) == 2) {
                    val name = san[1]?.toString()?.lowercase()?.trim()
                    if (!name.isNullOrBlank()) out += name
                }
            }
        }

        return out.toList()
    }

    private fun parseCN(p: X500Principal): String? {
        val dn = p.name // RFC2253
        val parts = dn.split(",").map { it.trim() }
        val cn = parts.firstOrNull { it.startsWith("CN=") }?.substringAfter("CN=")
        return cn?.takeIf { it.isNotBlank() }
    }
}
