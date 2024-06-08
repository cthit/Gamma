CREATE TABLE g_group
(
    group_id       UUID PRIMARY KEY,
    e_name         VARCHAR(50) NOT NULL UNIQUE,
    pretty_name    VARCHAR(50) NOT NULL,
    super_group_id UUID        NOT NULL REFERENCES g_super_group,
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,
    version        INT
);

CREATE TABLE g_post
(
    post_id      UUID PRIMARY KEY,
    post_name    UUID      NOT NULL REFERENCES g_text ON DELETE CASCADE,
    email_prefix VARCHAR(20),
    version      INT,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL
);

CREATE TABLE g_membership
(
    created_at           TIMESTAMP NOT NULL,
    user_id              UUID REFERENCES g_user ON DELETE CASCADE,
    group_id             UUID REFERENCES g_group ON DELETE CASCADE,
    post_id              UUID REFERENCES g_post ON DELETE CASCADE,
    unofficial_post_name VARCHAR(50),
    PRIMARY KEY (user_id, group_id, post_id)
);

CREATE TABLE g_allow_list
(
    created_at TIMESTAMP NOT NULL,
    cid        VARCHAR(10) PRIMARY KEY CHECK (LOWER(cid) = cid)
);

CREATE TABLE g_user_activation
(
    cid        VARCHAR(10) PRIMARY KEY REFERENCES g_allow_list,
    token      VARCHAR(10) UNIQUE NOT NULL,
    created_at TIMESTAMP          NOT NULL
);

CREATE TABLE g_client
(
    client_uid    UUID PRIMARY KEY,
    client_id     VARCHAR(100) UNIQUE,
    client_secret VARCHAR(255) NOT NULL CHECK (client_secret LIKE '{bcrypt}$%'),
    redirect_uri  VARCHAR(256) NOT NULL,
    pretty_name   VARCHAR(30)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    description   UUID REFERENCES g_text ON DELETE CASCADE,
    official      BOOLEAN      NOT NULL,
    created_by    UUID REFERENCES g_user (user_id) ON DELETE CASCADE,
    CHECK (
        (official = TRUE AND created_by IS NULL) OR (official = FALSE AND created_by IS NOT NULL)
        )
);

CREATE TABLE g_client_scope
(
    client_uid UUID REFERENCES g_client,
    SCOPE      VARCHAR(30) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (client_uid, SCOPE)
);

CREATE TABLE g_user_approval
(
    created_at TIMESTAMP NOT NULL,
    user_id    UUID REFERENCES g_user ON DELETE CASCADE,
    client_uid UUID REFERENCES g_client ON DELETE CASCADE,
    PRIMARY KEY (user_id, client_uid)
);

CREATE TABLE g_group_images_uri
(
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    group_id   UUID REFERENCES g_group ON DELETE CASCADE,
    avatar_uri VARCHAR(255),
    banner_uri VARCHAR(255),
    version    INT
);

CREATE TABLE g_api_key
(
    api_key_id  UUID PRIMARY KEY,
    pretty_name VARCHAR(30)  NOT NULL,
    token       VARCHAR(255) NOT NULL CHECK (token LIKE '{bcrypt}$%'),
    KEY_TYPE    VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    version     INT,
    description UUID REFERENCES g_text ON DELETE CASCADE
);

CREATE TABLE g_api_key_settings
(
    settings_id UUID PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    version     INT,
    api_key_id  UUID REFERENCES g_api_key (api_key_id) ON DELETE CASCADE
);

CREATE TABLE g_api_key_to_super_group_type
(
    settings_id           UUID REFERENCES g_api_key_settings (settings_id),
    created_at            TIMESTAMP NOT NULL,
    super_group_type_name VARCHAR(30) REFERENCES g_super_group_type,
    PRIMARY KEY (settings_id, super_group_type_name)
);

CREATE TABLE g_client_api_key
(
    created_at TIMESTAMP NOT NULL,
    client_uid UUID PRIMARY KEY REFERENCES g_client ON DELETE CASCADE,
    api_key_id UUID REFERENCES g_api_key ON DELETE CASCADE
);

CREATE TABLE g_client_authority
(
    created_at     TIMESTAMP NOT NULL,
    client_uid     UUID REFERENCES g_client (client_uid) ON DELETE CASCADE,
    authority_name VARCHAR(30),
    PRIMARY KEY (client_uid, authority_name)
);

CREATE TABLE g_client_authority_super_group
(
    created_at     TIMESTAMP NOT NULL,
    super_group_id UUID REFERENCES g_super_group,
    client_uid     UUID,
    authority_name VARCHAR(30),
    PRIMARY KEY (super_group_id, client_uid, authority_name),
    FOREIGN KEY (client_uid, authority_name) REFERENCES g_client_authority (client_uid, authority_name) ON DELETE CASCADE
);

CREATE TABLE g_client_authority_user
(
    created_at     TIMESTAMP NOT NULL,
    user_id        UUID REFERENCES g_user ON DELETE CASCADE,
    client_uid     UUID,
    authority_name VARCHAR(30),
    PRIMARY KEY (user_id, client_uid, authority_name),
    FOREIGN KEY (client_uid, authority_name) REFERENCES g_client_authority (client_uid, authority_name) ON DELETE CASCADE
);

CREATE TABLE g_client_restriction
(
    created_at     TIMESTAMP NOT NULL,
    restriction_id UUID,
    client_uid     UUID REFERENCES g_client ON DELETE CASCADE,
    PRIMARY KEY (client_uid)
);

CREATE TABLE g_client_restriction_super_group
(
    created_at     TIMESTAMP NOT NULL,
    super_group_id UUID REFERENCES g_super_group ON DELETE CASCADE,
    restriction_id UUID REFERENCES g_client_restriction ON DELETE CASCADE,
    PRIMARY KEY (super_group_id, restriction_id)
);