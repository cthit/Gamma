ALTER TABLE g_post
    ADD COLUMN post_order INT DEFAULT 0 CHECK ( post_order >= 0 );