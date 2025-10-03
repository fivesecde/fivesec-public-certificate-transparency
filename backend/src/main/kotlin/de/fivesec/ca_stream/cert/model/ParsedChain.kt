package de.fivesec.ca_stream.cert.model

import java.security.cert.X509Certificate

data class ParsedChain(
    val leafCert: X509Certificate?,
    val chain: List<X509Certificate>
)
