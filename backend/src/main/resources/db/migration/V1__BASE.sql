CREATE TABLE internal_text
(
    text_id UUID PRIMARY KEY,
    sv      VARCHAR(2048) NOT NULL,
    en      VARCHAR(2048) NOT NULL
);

CREATE TABLE ituser
(
    user_id                 UUID PRIMARY KEY,
    cid                     VARCHAR(12)  NOT NULL UNIQUE,
    password                VARCHAR(255) NOT NULL,
    nick                    VARCHAR(50)  NOT NULL,
    first_name              VARCHAR(50)  NOT NULL,
    last_name               VARCHAR(50)  NOT NULL,
    email                   VARCHAR(100) NOT NULL UNIQUE,
    language                VARCHAR(15)  NULL,
    user_agreement_accepted TIMESTAMP    NOT NULL DEFAULT NOW(),
    acceptance_year         INTEGER,
    version                 INT,
    gdpr_training           BOOLEAN               DEFAULT FALSE,
    locked                  BOOLEAN               DEFAULT FALSE
);

CREATE TABLE user_avatar_uri
(
    user_id    UUID REFERENCES ituser ON DELETE CASCADE,
    avatar_uri VARCHAR(255) NOT NULL,
    version    INT
);

CREATE TABLE password_reset
(
    token      VARCHAR(100) UNIQUE,
    user_id    UUID PRIMARY KEY REFERENCES ituser ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TABLE super_group_type
(
    super_group_type_name VARCHAR(30) PRIMARY KEY
);

CREATE TABLE fkit_super_group
(
    super_group_id        UUID PRIMARY KEY,
    e_name                VARCHAR(50) NOT NULL UNIQUE,
    pretty_name           VARCHAR(50) NOT NULL,
    super_group_type_name VARCHAR(30) NOT NULL REFERENCES super_group_type,
    description           UUID REFERENCES internal_text ON DELETE CASCADE,
    version               INT
);

CREATE TABLE fkit_group
(
    group_id       UUID PRIMARY KEY,
    e_name         VARCHAR(50) NOT NULL UNIQUE,
    pretty_name    VARCHAR(50) NOT NULL,
    super_group_id UUID        NOT NULL REFERENCES fkit_super_group,
    version        INT
);

CREATE TABLE post
(
    post_id      UUID PRIMARY KEY,
    post_name    UUID NOT NULL REFERENCES internal_text ON DELETE CASCADE,
    email_prefix VARCHAR(20),
    version      INT
);

CREATE TABLE authority_level
(
    authority_level VARCHAR(30) PRIMARY KEY
);

CREATE TABLE authority_post
(
    super_group_id  UUID REFERENCES fkit_super_group,
    post_id         UUID REFERENCES post,
    authority_level VARCHAR(30) REFERENCES authority_level,
    PRIMARY KEY (post_id, super_group_id, authority_level)
);

CREATE TABLE authority_super_group
(
    super_group_id  UUID REFERENCES fkit_super_group,
    authority_level VARCHAR(30) REFERENCES authority_level,
    PRIMARY KEY (super_group_id, authority_level)
);

CREATE TABLE authority_user
(
    user_id         UUID REFERENCES ituser ON DELETE CASCADE,
    authority_level VARCHAR(30) REFERENCES authority_level,
    PRIMARY KEY (user_id, authority_level)
);

CREATE TABLE membership
(
    user_id              UUID REFERENCES ituser ON DELETE CASCADE,
    group_id             UUID REFERENCES fkit_group ON DELETE CASCADE,
    post_id              UUID REFERENCES post ON DELETE CASCADE,
    unofficial_post_name VARCHAR(100),
    PRIMARY KEY (user_id, group_id, post_id)
);

CREATE TABLE whitelist_cid
(
    cid VARCHAR(10) PRIMARY KEY CHECK (LOWER(cid) = cid)
);

CREATE TABLE user_activation
(
    cid        VARCHAR(10) PRIMARY KEY REFERENCES whitelist_cid,
    token      VARCHAR(10) UNIQUE NOT NULL,
    created_at TIMESTAMP          NOT NULL DEFAULT current_timestamp
);

CREATE TABLE itclient
(
    client_uid    UUID PRIMARY KEY,
    client_id     VARCHAR(100) UNIQUE,
    client_secret VARCHAR(100) NOT NULL,
    redirect_uri  VARCHAR(256) NOT NULL,
    pretty_name   VARCHAR(30)  NOT NULL,
    description   UUID REFERENCES internal_text ON DELETE CASCADE
);

CREATE TABLE itclient_scope
(
    client_uid UUID REFERENCES itclient,
    scope      VARCHAR(30) NOT NULL,
    PRIMARY KEY (client_uid, scope)
);

CREATE TABLE itclient_authority_level_restriction
(
    client_uid      UUID REFERENCES itclient,
    authority_level VARCHAR(30) REFERENCES authority_level,
    PRIMARY KEY (client_uid, authority_level)
);

CREATE TABLE apikey
(
    api_key_id  UUID PRIMARY KEY,
    pretty_name VARCHAR(30) NOT NULL,
    description UUID REFERENCES internal_text ON DELETE CASCADE,
    token       VARCHAR(150) UNIQUE,
    key_type    VARCHAR(30) NOT NULL,
    version     INT
);

CREATE TABLE itclient_apikey
(
    client_uid UUID PRIMARY KEY REFERENCES itclient ON DELETE CASCADE,
    api_key_id UUID REFERENCES apikey ON DELETE CASCADE
);

CREATE TABLE it_user_approval
(
    user_id    UUID REFERENCES ituser ON DELETE CASCADE,
    client_uid UUID REFERENCES itclient ON DELETE CASCADE,
    PRIMARY KEY (user_id, client_uid)
);

CREATE TABLE group_images_uri
(
    group_id   UUID REFERENCES fkit_group ON DELETE CASCADE,
    avatar_uri VARCHAR(255),
    banner_uri VARCHAR(255),
    version    INT
);

CREATE TABLE settings
(
    id                          UUID PRIMARY KEY,
    updated_at                  TIMESTAMP NOT NULL,
    last_updated_user_agreement TIMESTAMP,
    version                     INT
);


CREATE TABLE settings_info_api_super_group_types
(
    settings_id           UUID REFERENCES settings,
    super_group_type_name VARCHAR(30) REFERENCES super_group_type,
    PRIMARY KEY (settings_id, super_group_type_name)
);


