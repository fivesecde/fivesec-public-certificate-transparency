package de.fivesec.ca_stream.cert.repository

import de.fivesec.ca_stream.cert.entity.CertDataEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface CertDataRepository : JpaRepository<CertDataEntity, UUID> {
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Slice<CertDataEntity>

    @Query(
        """
        SELECT c FROM CertDataEntity c
        WHERE (:domain IS NULL OR LOWER(c.domain) LIKE LOWER(CONCAT('%', :domain, '%')))
        ORDER BY c.createdAt DESC
    """
    )
    fun findByCriteria(
        @Param("domain") domain: String?,
        pageable: Pageable
    ): Slice<CertDataEntity>
}