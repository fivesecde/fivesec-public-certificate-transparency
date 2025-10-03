CREATE TABLE cert_events
(
    id              UUID                                      NOT NULL DEFAULT gen_random_uuid(),
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    created_by      TEXT                                      NOT NULL,
    last_updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    last_updated_by TEXT                                      NOT NULL,
    domain          TEXT                                      NOT NULL,
    subject_dn      TEXT                                      NOT NULL,
    issuer_dn       TEXT                                      NOT NULL,
    not_before      TIMESTAMP WITH TIME ZONE                  NOT NULL,
    not_after       TIMESTAMP WITH TIME ZONE                  NOT NULL,
    serial_hex      TEXT                                      NOT NULL,
    raw_entry       JSONB,
    PRIMARY KEY (id)
);

CREATE INDEX idx_cert_events_domain
    ON cert_events (domain);


CREATE TABLE ct_state
(
    log_base        TEXT,
    next_index      BIGINT                                    NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    created_by      TEXT                                      NOT NULL,
    last_updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    last_updated_by TEXT                                      NOT NULL,
    PRIMARY KEY (log_base)
);