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

CREATE TABLE g_user_totp
(
    user_id             UUID PRIMARY KEY REFERENCES g_user ON DELETE CASCADE,
    secret              VARCHAR(64) NOT NULL,
    last_used_time_step BIGINT      NOT NULL,
    created_at          TIMESTAMP   NOT NULL
);

CREATE TABLE g_user_recovery_key
(
    user_id    UUID PRIMARY KEY REFERENCES g_user ON DELETE CASCADE,
    key_hash   VARCHAR(64) NOT NULL,
    created_at TIMESTAMP   NOT NULL
);

ALTER TABLE g_user
    ADD COLUMN email_recovery BOOLEAN NOT NULL DEFAULT TRUE;
