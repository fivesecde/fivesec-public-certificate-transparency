package de.fivesec.ca_stream.cert.controller

import de.fivesec.ca_stream.cert.dto.CertDataDto
import de.fivesec.ca_stream.cert.service.CaService
import de.fivesec.ca_stream.shared.dto.GenericSliceResponseDto
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ca")
class CaController(
    private val caService: CaService
) {
    @GetMapping("/events")
    fun getEvents(
        @RequestParam(required = false) domain: String?,
        pageable: Pageable,
    ): GenericSliceResponseDto<CertDataDto> {
        return caService.getCertEventData(domain, pageable)
    }
}