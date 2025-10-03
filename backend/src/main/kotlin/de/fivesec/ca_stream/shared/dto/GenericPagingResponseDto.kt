package de.fivesec.ca_stream.shared.dto

import org.springframework.data.domain.Page

data class GenericPagingResponseDto<T>(val data: List<T>, val paging: PagingMetaResponseDto)

data class PagingMetaResponseDto(
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long
) {
    companion object {
        @JvmStatic
        fun <T> map(page: Page<T>): PagingMetaResponseDto {
            return PagingMetaResponseDto(
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                page = page.number,
                size = page.size
            )
        }
    }
}
