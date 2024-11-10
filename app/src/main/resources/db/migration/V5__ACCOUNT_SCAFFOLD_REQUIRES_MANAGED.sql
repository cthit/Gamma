CREATE TABLE g_api_key_account_scaffold_requires_managed (
    created_at TIMESTAMP NOT NULL,
    settings_id UUID REFERENCES g_api_key_settings ON DELETE CASCADE,
    super_group_type_name VARCHAR(30) REFERENCES g_super_group_type,
    PRIMARY KEY (settings_id, super_group_type_name)
);