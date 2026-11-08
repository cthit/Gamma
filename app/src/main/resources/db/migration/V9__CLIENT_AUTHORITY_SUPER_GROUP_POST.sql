-- Gives a client authority to the members of a super group's groups who hold
-- a specific post, e.g. only the chairman of digIT.
CREATE TABLE g_client_authority_super_group_post
(
    created_at     TIMESTAMP NOT NULL,
    super_group_id UUID REFERENCES g_super_group ON DELETE CASCADE,
    post_id        UUID REFERENCES g_post ON DELETE CASCADE,
    client_uid     UUID,
    authority_name VARCHAR(30),
    PRIMARY KEY (super_group_id, post_id, client_uid, authority_name),
    FOREIGN KEY (client_uid, authority_name) REFERENCES g_client_authority (client_uid, authority_name) ON DELETE CASCADE
);
