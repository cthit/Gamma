-- Scope assignment table
CREATE TABLE g_api_key_scope (
    api_key_id UUID REFERENCES g_api_key (api_key_id) ON DELETE CASCADE,
    scope      VARCHAR(50) NOT NULL,
    PRIMARY KEY (api_key_id, scope)
);

-- Unified super group type restrictions (replaces g_api_key_to_super_group_type + g_api_key_account_scaffold_requires_managed)
CREATE TABLE g_api_key_super_group_type (
    api_key_id            UUID REFERENCES g_api_key (api_key_id) ON DELETE CASCADE,
    super_group_type_name VARCHAR(30) REFERENCES g_super_group_type (super_group_type_name),
    gdpr_filter           BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (api_key_id, super_group_type_name)
);

-- Migrate existing INFO keys
INSERT INTO g_api_key_scope (api_key_id, scope)
SELECT ak.api_key_id, s.scope
FROM g_api_key ak
CROSS JOIN (VALUES ('PROFILES_READ'), ('DIRECTORY_READ'), ('SUPER_GROUPS_READ'), ('GROUPS_READ'), ('MEMBERSHIPS_READ')) AS s(scope)
WHERE ak.key_type = 'INFO';

-- Migrate existing INFO settings
INSERT INTO g_api_key_super_group_type (api_key_id, super_group_type_name, gdpr_filter)
SELECT s.api_key_id, jt.super_group_type_name, false
FROM g_api_key_settings s
JOIN g_api_key_to_super_group_type jt ON s.settings_id = jt.settings_id
WHERE s.api_key_id IN (SELECT api_key_id FROM g_api_key WHERE key_type = 'INFO');

-- Migrate existing CLIENT keys
INSERT INTO g_api_key_scope (api_key_id, scope)
SELECT api_key_id, 'CLIENTS_SELF'
FROM g_api_key
WHERE key_type = 'CLIENT';

-- Migrate existing ALLOW_LIST keys
INSERT INTO g_api_key_scope (api_key_id, scope)
SELECT api_key_id, 'ALLOWLIST_WRITE'
FROM g_api_key
WHERE key_type = 'ALLOW_LIST';

-- Migrate existing ACCOUNT_SCAFFOLD keys
INSERT INTO g_api_key_scope (api_key_id, scope)
SELECT api_key_id, 'ACCOUNTS_PROVISION'
FROM g_api_key
WHERE key_type = 'ACCOUNT_SCAFFOLD';

-- Migrate existing ACCOUNT_SCAFFOLD settings (preserving requiresManaged as gdpr_filter)
INSERT INTO g_api_key_super_group_type (api_key_id, super_group_type_name, gdpr_filter)
SELECT s.api_key_id, jt.super_group_type_name,
    CASE WHEN rm.settings_id IS NOT NULL THEN true ELSE false END
FROM g_api_key_settings s
JOIN g_api_key_to_super_group_type jt ON s.settings_id = jt.settings_id
LEFT JOIN g_api_key_account_scaffold_requires_managed rm
    ON rm.settings_id = s.settings_id AND rm.super_group_type_name = jt.super_group_type_name
WHERE s.api_key_id IN (SELECT api_key_id FROM g_api_key WHERE key_type = 'ACCOUNT_SCAFFOLD');

-- Legacy settings tables, superseded by g_api_key_super_group_type
DROP TABLE g_api_key_account_scaffold_requires_managed;
DROP TABLE g_api_key_to_super_group_type;
DROP TABLE g_api_key_settings;

-- The api key type is superseded by scopes
ALTER TABLE g_api_key DROP COLUMN key_type;
