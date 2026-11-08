-- Passkeys. These two tables are read and written by Spring Security's
-- JdbcPublicKeyCredentialUserEntityRepository and JdbcUserCredentialRepository,
-- so their table and column names are Spring Security's
-- (org/springframework/security/user-entities-schema.sql and
-- user-credentials-schema-postgres.sql in spring-security-web).
-- user_entities.name is the id of the Gamma user that the passkeys belong to.
CREATE TABLE user_entities
(
    id           VARCHAR(1000) PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(200)
);

CREATE TABLE user_credentials
(
    credential_id                VARCHAR(1000) PRIMARY KEY,
    user_entity_user_id          VARCHAR(1000) NOT NULL REFERENCES user_entities ON DELETE CASCADE,
    public_key                   BYTEA         NOT NULL,
    signature_count              BIGINT,
    uv_initialized               BOOLEAN,
    backup_eligible              BOOLEAN       NOT NULL,
    authenticator_transports     VARCHAR(1000),
    public_key_credential_type   VARCHAR(100),
    backup_state                 BOOLEAN       NOT NULL,
    attestation_object           BYTEA,
    attestation_client_data_json BYTEA,
    created                      TIMESTAMP,
    last_used                    TIMESTAMP,
    label                        VARCHAR(1000) NOT NULL
);

CREATE INDEX user_credentials_user_entity_user_id_idx ON user_credentials (user_entity_user_id);

-- Removes the passkeys of a deleted user, since user_entities can't reference
-- g_user directly.
CREATE FUNCTION g_delete_user_passkeys() RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM user_entities WHERE name = OLD.user_id::text;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER g_user_delete_passkeys
    AFTER DELETE
    ON g_user
    FOR EACH ROW
EXECUTE FUNCTION g_delete_user_passkeys();

-- An authenticator app (TOTP) the user signs in with after their password.
-- last_used_time_step is the 30 second time step of the latest accepted code,
-- so that a code can't be used twice.
CREATE TABLE g_user_totp
(
    user_id             UUID PRIMARY KEY REFERENCES g_user ON DELETE CASCADE,
    secret              VARCHAR(64) NOT NULL,
    last_used_time_step BIGINT      NOT NULL,
    created_at          TIMESTAMP   NOT NULL
);

-- The recovery key a user can sign in with when they have lost their other
-- sign in methods. Only a SHA-256 hash of the key is stored.
CREATE TABLE g_user_recovery_key
(
    user_id    UUID PRIMARY KEY REFERENCES g_user ON DELETE CASCADE,
    key_hash   VARCHAR(64) NOT NULL,
    created_at TIMESTAMP   NOT NULL
);

-- Whether the user can reset how they sign in through a link mailed to them.
-- Admins never can, whatever this says.
ALTER TABLE g_user
    ADD COLUMN email_recovery BOOLEAN NOT NULL DEFAULT TRUE;
