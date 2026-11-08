-- A requested email change that waits until the user confirms the new address
-- through the link mailed to it. One pending change per user. Only a SHA-256
-- hash of the link's token is stored.
CREATE TABLE g_email_change
(
    user_id    UUID PRIMARY KEY REFERENCES g_user ON DELETE CASCADE,
    new_email  VARCHAR(100) NOT NULL,
    token_hash VARCHAR(64)  NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL
);
