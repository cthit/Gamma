BEGIN;

-- Captured from Flyway after Gamma 2.5.1 applied its five released migrations.
CREATE TABLE flyway_schema_history (
    installed_rank INTEGER NOT NULL,
    version VARCHAR(50),
    description VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL,
    script VARCHAR(1000) NOT NULL,
    checksum INTEGER,
    installed_by VARCHAR(100) NOT NULL,
    installed_on TIMESTAMP NOT NULL DEFAULT NOW(),
    execution_time INTEGER NOT NULL,
    success BOOLEAN NOT NULL,
    CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank)
);
CREATE INDEX flyway_schema_history_s_idx ON flyway_schema_history (success);
INSERT INTO flyway_schema_history (
    installed_rank, version, description, type, script, checksum,
    installed_by, installed_on, execution_time, success
)
VALUES
    (1, '1', 'BASE', 'SQL', 'V1__BASE.sql', 279678195, 'gamma', '2026-01-01T00:00:00Z', 0, TRUE),
    (2, '2', 'TOKENS', 'SQL', 'V2__TOKENS.sql', 895381145, 'gamma', '2026-01-01T00:00:00Z', 0, TRUE),
    (3, '3', 'RESTRICT POST DELETION', 'SQL', 'V3__RESTRICT_POST_DELETION.sql', -1688145237, 'gamma', '2026-01-01T00:00:00Z', 0, TRUE),
    (4, '4', 'ADD POST ORDER', 'SQL', 'V4__ADD_POST_ORDER.sql', -857701886, 'gamma', '2026-01-01T00:00:00Z', 0, TRUE),
    (5, '5', 'ACCOUNT SCAFFOLD REQUIRES MANAGED', 'SQL', 'V5__ACCOUNT_SCAFFOLD_REQUIRES_MANAGED.sql', 1916179040, 'gamma', '2026-01-01T00:00:00Z', 0, TRUE);

-- These rows use the exact database and credential formats written by Gamma 2.5.1.
INSERT INTO g_text (text_id, sv, en, created_at)
VALUES (
    '50000000-0000-4000-8000-000000000001',
    'Beständig klient',
    'Persistent client',
    '2026-01-01T00:00:00Z'
);

INSERT INTO g_client (
    client_uid, client_id, client_secret, redirect_uri, pretty_name,
    created_at, description, official, created_by
)
VALUES (
    '55555555-5555-4555-8555-555555555555',
    'LEGACYCLIENT251000000000000000',
    '{bcrypt}$2y$10$43Xdh/xWiqXWBbJX8L3/H.3Kk0/Usl7uwZxQLKqz9UPYjaKmNNXcu',
    'https://legacy-client.example.org/callback',
    'Legacy 2.5.1 client',
    '2026-01-01T00:00:00Z',
    '50000000-0000-4000-8000-000000000001',
    TRUE,
    NULL
);

INSERT INTO g_client_scope (client_uid, scope, created_at)
VALUES
    ('55555555-5555-4555-8555-555555555555', 'PROFILE', '2026-01-01T00:00:00Z'),
    ('55555555-5555-4555-8555-555555555555', 'EMAIL', '2026-01-01T00:00:00Z');

INSERT INTO g_user_approval (created_at, user_id, client_uid)
VALUES (
    '2026-01-01T00:00:00Z',
    '88eec5c2-5ebb-4e13-9a76-fcc4dac9e74f',
    '55555555-5555-4555-8555-555555555555'
);

INSERT INTO g_client_restriction (created_at, restriction_id, client_uid)
VALUES (
    '2026-01-01T00:00:00Z',
    '55555555-5555-4555-8555-555555555555',
    '55555555-5555-4555-8555-555555555555'
);

INSERT INTO g_client_restriction_super_group (created_at, super_group_id, restriction_id)
VALUES (
    '2026-01-01T00:00:00Z',
    'aed27030-ad90-4526-855c-1e909b1dcecb',
    '55555555-5555-4555-8555-555555555555'
);

INSERT INTO g_group_images_uri (
    created_at, updated_at, group_id, avatar_uri, banner_uri, version
)
VALUES (
    '2026-01-01T00:00:00Z',
    '2026-01-01T00:00:00Z',
    '047ac437-a789-4cc5-bb6e-ba50efd7c509',
    '66666666-6666-4666-8666-666666666666/avatar.png',
    NULL,
    0
);

COMMIT;
