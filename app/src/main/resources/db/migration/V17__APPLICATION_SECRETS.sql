-- Secrets Gamma generates for itself on first start, so deploying needs no key setting. Features
-- derive their own keys from these with HKDF rather than using them directly.
CREATE TABLE g_application_secret (
    name VARCHAR(64) PRIMARY KEY,
    value BYTEA NOT NULL CHECK (octet_length(value) = 32)
);
