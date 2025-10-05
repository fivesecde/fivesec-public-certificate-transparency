package de.fivesec.ca_stream.cert.service

import de.fivesec.ca_stream.cert.dto.CertDataDto
import de.fivesec.ca_stream.cert.mapping.asCertDataDto
import de.fivesec.ca_stream.cert.repository.CertDataRepository
import de.fivesec.ca_stream.shared.dto.GenericSliceResponseDto
import de.fivesec.ca_stream.shared.dto.asGenericSliceResponseDto
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
@Transactional
class CaService(
    private val certDataRepository: CertDataRepository
) {
    fun getCertEventData(
        domain: String?,
        pageable: Pageable
    ): GenericSliceResponseDto<CertDataDto> {
        return if (domain == null) {
            certDataRepository.findAllByOrderByCreatedAtDesc(pageable)
                .asGenericSliceResponseDto { it.asCertDataDto() }
        } else {
            LOG.atInfo().setMessage("process search request").addKeyValue("domain", domain).log()
            certDataRepository.findByCriteria(domain.lowercase(), pageable)
                .asGenericSliceResponseDto { it.asCertDataDto() }
        }

    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CaService::class.java)
    }
}