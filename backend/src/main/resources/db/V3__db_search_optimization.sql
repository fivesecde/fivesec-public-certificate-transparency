-- GIN Index für LIKE '%...%' queries
CREATE INDEX CONCURRENTLY idx_cert_events_domain_trgm
    ON cert_events USING gin (domain gin_trgm_ops);