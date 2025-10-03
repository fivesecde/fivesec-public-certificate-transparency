package de.fivesec.ca_stream.cert.repository

import de.fivesec.ca_stream.cert.entity.CertStateEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CertStateRepository : JpaRepository<CertStateEntity, String>