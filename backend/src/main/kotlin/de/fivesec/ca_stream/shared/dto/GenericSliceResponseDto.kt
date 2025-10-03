package de.fivesec.ca_stream.shared.dto

import org.springframework.data.domain.Slice

data class GenericSliceResponseDto<T>(
    val data: List<T>,
    val slicing: SlicingMetaResponseDto
)

data class SlicingMetaResponseDto(
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        @JvmStatic
        fun <T> map(slice: Slice<T>): SlicingMetaResponseDto {
            return SlicingMetaResponseDto(
                page = slice.number,
                size = slice.size,
                hasNext = slice.hasNext(),
                hasPrevious = slice.hasPrevious()
            )
        }
    }
}

fun <T, R> Slice<T>.asGenericSliceResponseDto(transform: (T) -> R): GenericSliceResponseDto<R> {
    return GenericSliceResponseDto(
        data = this.content.map(transform),
        slicing = SlicingMetaResponseDto.map(this)
    )
}