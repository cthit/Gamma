-- A short reservation keeps competing secret resets exclusive while bcrypt runs
-- outside SQL. Expiry lets another request recover after the reserving process dies.
CREATE TABLE g_client_secret_rotation
(
    client_uid UUID PRIMARY KEY REFERENCES g_client ON DELETE CASCADE,
    reservation_id UUID NOT NULL,
    expires_at TIMESTAMP NOT NULL
);
