CREATE INDEX CONCURRENTLY idx_cert_events_created_at_id
    ON cert_events (created_at DESC, id);

CREATE INDEX CONCURRENTLY idx_cert_events_covering
    ON cert_events (created_at DESC, id)
    INCLUDE (domain, subject_dn, issuer_dn, not_before, not_after, serial_hex);

CREATE INDEX CONCURRENTLY idx_cert_events_recent
    ON cert_events (created_at DESC, id)
    WHERE created_at > NOW() - INTERVAL '90 days';

CREATE INDEX CONCURRENTLY idx_cert_events_domain_created_at
    ON cert_events (domain, created_at DESC);
