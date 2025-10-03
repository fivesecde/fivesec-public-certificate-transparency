package de.fivesec.ca_stream.cert.mapping

import de.fivesec.ca_stream.cert.dto.CertDataDto
import de.fivesec.ca_stream.cert.entity.CertDataEntity
import de.fivesec.ca_stream.shared.dto.GenericPagingResponseDto
import de.fivesec.ca_stream.shared.dto.PagingMetaResponseDto
import org.springframework.data.domain.Page

fun <T, R> Page<T>.asGenericPagingResponseDto(
    mappingFunction: (T) -> (R)
): GenericPagingResponseDto<R> {
    return GenericPagingResponseDto(
        data = this.content.map(mappingFunction),
        paging =
            PagingMetaResponseDto(
                page = this.number,
                size = this.size,
                totalPages = this.totalPages,
                totalElements = this.totalElements
            )
    )
}

fun CertDataEntity.asCertDataDto() = CertDataDto(
    id = id,
    domain = domain,
    subjectDn = subjectDn,
    issuerDn = issuerDn,
    notBefore = notBefore,
    notAfter = notAfter,
    serialHex = serialHex,
    rawEntry = rawEntry
)