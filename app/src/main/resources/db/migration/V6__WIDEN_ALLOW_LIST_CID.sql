-- User cids are up to 12 characters (g_user.cid and the Cid validator), but the
-- allow list and activation tables only fit 10, so allowing an 11 or 12
-- character cid failed with an error instead of being stored.
ALTER TABLE g_user_activation ALTER COLUMN cid TYPE VARCHAR(12);
ALTER TABLE g_allow_list ALTER COLUMN cid TYPE VARCHAR(12);
