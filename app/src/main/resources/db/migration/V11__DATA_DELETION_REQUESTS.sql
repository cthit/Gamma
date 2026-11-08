CREATE TABLE g_data_deletion_request
(
    data_deletion_request_id UUID PRIMARY KEY,
    user_id                  UUID        NOT NULL,
    client_uid               UUID        NOT NULL,
    cid                      VARCHAR(12) NULL,
    client_name              VARCHAR(30) NULL,
    reason                   VARCHAR(30) NOT NULL CHECK (reason IN ('ACCOUNT_DELETED', 'APPROVAL_REVOKED')),
    re_approved_at           TIMESTAMP   NULL,
    cleared_by               UUID        NULL REFERENCES g_user (user_id) ON DELETE SET NULL,
    cleared_at               TIMESTAMP   NULL,
    created_at               TIMESTAMP   NOT NULL,
    CHECK (
        (cleared_at IS NULL AND cid IS NOT NULL AND client_name IS NOT NULL)
            OR
        (cleared_at IS NOT NULL AND cid IS NULL AND client_name IS NULL)
    )
);

CREATE INDEX g_data_deletion_request_open_idx
    ON g_data_deletion_request (user_id, client_uid)
    WHERE cleared_at IS NULL;

CREATE TABLE g_data_deletion_recipient
(
    email      VARCHAR(100) PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL
);
