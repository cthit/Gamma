ALTER TABLE g_membership
    DROP CONSTRAINT g_membership_post_id_fkey;

ALTER TABLE g_membership
    ADD CONSTRAINT g_membership_post_id_fkey FOREIGN KEY (post_id)
        REFERENCES g_post (post_id) ON DELETE RESTRICT;