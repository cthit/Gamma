-- g_ = gamma prefix to prevent using reserved names
CREATE
    TABLE
        g_text(
            text_id UUID PRIMARY KEY,
            sv VARCHAR(2048) NOT NULL,
            en VARCHAR(2048) NOT NULL
        );

CREATE
    TABLE
        g_user(
            user_id UUID PRIMARY KEY,
            cid VARCHAR(12) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL CHECK(
                password LIKE '{bcrypt}$%'
            ),
            nick VARCHAR(50) NOT NULL,
            first_name VARCHAR(50) NOT NULL,
            last_name VARCHAR(50) NOT NULL,
            email VARCHAR(100) NOT NULL UNIQUE,
            LANGUAGE VARCHAR(15) NULL,
            user_agreement_accepted TIMESTAMP NOT NULL DEFAULT NOW(),
            acceptance_year INTEGER,
            version INT,
            locked BOOLEAN DEFAULT FALSE
        );

CREATE
    TABLE
        g_user_avatar_uri(
            user_id UUID REFERENCES g_user ON
            DELETE
                CASCADE,
                avatar_uri VARCHAR(255) NOT NULL,
                version INT
        );

CREATE
    TABLE
        g_admin_user(
            user_id UUID PRIMARY KEY REFERENCES g_user(user_id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        g_gdpr_trained(
            user_id UUID PRIMARY KEY REFERENCES g_user(user_id) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        g_password_reset(
            token VARCHAR(100) UNIQUE,
            user_id UUID PRIMARY KEY REFERENCES g_user ON
            DELETE
                CASCADE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );

CREATE
    TABLE
        g_super_group_type(
            super_group_type_name VARCHAR(30) PRIMARY KEY
        );

CREATE
    TABLE
        g_super_group(
            super_group_id UUID PRIMARY KEY,
            e_name VARCHAR(50) NOT NULL UNIQUE,
            pretty_name VARCHAR(50) NOT NULL,
            super_group_type_name VARCHAR(30) NOT NULL REFERENCES g_super_group_type,
            description UUID REFERENCES g_text ON
            DELETE
                CASCADE,
                version INT
        );

CREATE
    TABLE
        g_group(
            group_id UUID PRIMARY KEY,
            e_name VARCHAR(50) NOT NULL UNIQUE,
            pretty_name VARCHAR(50) NOT NULL,
            super_group_id UUID NOT NULL REFERENCES g_super_group,
            version INT
        );

CREATE
    TABLE
        g_post(
            post_id UUID PRIMARY KEY,
            post_name UUID NOT NULL REFERENCES g_text ON
            DELETE
                CASCADE,
                email_prefix VARCHAR(20),
                version INT
        );

CREATE
    TABLE
        g_membership(
            user_id UUID REFERENCES g_user ON
            DELETE
                CASCADE,
                group_id UUID REFERENCES g_group ON
                DELETE
                    CASCADE,
                    post_id UUID REFERENCES g_post ON
                    DELETE
                        CASCADE,
                        unofficial_post_name VARCHAR(100),
                        PRIMARY KEY(
                            user_id,
                            group_id,
                            post_id
                        )
        );

CREATE
    TABLE
        g_allow_list(
            cid VARCHAR(10) PRIMARY KEY CHECK(
                LOWER( cid )= cid
            )
        );

CREATE
    TABLE
        g_user_activation(
            cid VARCHAR(10) PRIMARY KEY REFERENCES g_allow_list,
            token VARCHAR(10) UNIQUE NOT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );

CREATE
    TABLE
        g_client(
            client_uid UUID PRIMARY KEY,
            client_id VARCHAR(100) UNIQUE,
            client_secret VARCHAR(100) NOT NULL CHECK(
                client_secret LIKE '{bcrypt}$%'
            ),
            redirect_uri VARCHAR(256) NOT NULL,
            pretty_name VARCHAR(30) NOT NULL,
            description UUID REFERENCES g_text ON
            DELETE
                CASCADE,
                official BOOLEAN NOT NULL,
                created_by UUID REFERENCES g_user(user_id) ON
                DELETE
                    CASCADE,
                    CHECK(
                        (
                            official = TRUE
                            AND created_by IS NULL
                        )
                        OR(
                            official = FALSE
                            AND created_by IS NOT NULL
                        )
                    )
        );

CREATE
    TABLE
        g_client_scope(
            client_uid UUID REFERENCES g_client,
            SCOPE VARCHAR(30) NOT NULL,
            PRIMARY KEY(
                client_uid,
                SCOPE
            )
        );

CREATE
    TABLE
        g_api_key(
            api_key_id UUID PRIMARY KEY,
            pretty_name VARCHAR(30) NOT NULL,
            description UUID REFERENCES g_text ON
            DELETE
                CASCADE,
                token VARCHAR(150) UNIQUE,
                KEY_TYPE VARCHAR(30) NOT NULL,
                version INT
        );

CREATE
    TABLE
        g_client_api_key(
            client_uid UUID PRIMARY KEY REFERENCES g_client ON
            DELETE
                CASCADE,
                api_key_id UUID REFERENCES g_api_key ON
                DELETE
                    CASCADE
        );

CREATE
    TABLE
        g_user_approval(
            user_id UUID REFERENCES g_user ON
            DELETE
                CASCADE,
                client_uid UUID REFERENCES g_client ON
                DELETE
                    CASCADE,
                    PRIMARY KEY(
                        user_id,
                        client_uid
                    )
        );

CREATE
    TABLE
        g_group_images_uri(
            group_id UUID REFERENCES g_group ON
            DELETE
                CASCADE,
                avatar_uri VARCHAR(255),
                banner_uri VARCHAR(255),
                version INT
        );

CREATE
    TABLE
        g_settings(
            id UUID PRIMARY KEY,
            updated_at TIMESTAMP NOT NULL,
            version INT
        );

CREATE
    TABLE
        g_settings_info_api_super_group_types(
            settings_id UUID REFERENCES g_settings,
            super_group_type_name VARCHAR(30) REFERENCES g_super_group_type,
            PRIMARY KEY(
                settings_id,
                super_group_type_name
            )
        );

CREATE
    TABLE
        g_client_authority(
            client_uid UUID REFERENCES g_client(client_uid) ON
            DELETE
                CASCADE,
                authority_name VARCHAR(30),
                PRIMARY KEY(
                    client_uid,
                    authority_name
                )
        );

CREATE
    TABLE
        g_client_authority_super_group(
            super_group_id UUID REFERENCES g_super_group,
            client_uid UUID,
            authority_name VARCHAR(30),
            PRIMARY KEY(
                super_group_id,
                client_uid,
                authority_name
            ),
            FOREIGN KEY(
                client_uid,
                authority_name
            ) REFERENCES g_client_authority(
                client_uid,
                authority_name
            ) ON
            DELETE
                CASCADE
        );

CREATE
    TABLE
        g_client_authority_user(
            user_id UUID REFERENCES g_user ON
            DELETE
                CASCADE,
                client_uid UUID,
                authority_name VARCHAR(30),
                PRIMARY KEY(
                    user_id,
                    client_uid,
                    authority_name
                ),
                FOREIGN KEY(
                    client_uid,
                    authority_name
                ) REFERENCES g_client_authority(
                    client_uid,
                    authority_name
                ) ON
                DELETE
                    CASCADE
        );

CREATE
    TABLE
        g_client_restriction(
            restriction_id UUID,
            client_uid UUID REFERENCES g_client ON
            DELETE
                CASCADE,
                PRIMARY KEY(client_uid)
        );

CREATE
    TABLE
        g_client_restriction_super_group(
            super_group_id UUID REFERENCES g_super_group ON
            DELETE
                CASCADE,
                restriction_id UUID REFERENCES g_client_restriction ON
                DELETE
                    CASCADE,
                    PRIMARY KEY(
                        super_group_id,
                        restriction_id
                    )
        );
