-- Sessions that signed in at or before this time are no longer valid. Set when
-- the user is locked, so that unlocking the user doesn't bring back a session
-- from before the lock.
ALTER TABLE g_user
    ADD COLUMN sessions_revoked_at TIMESTAMP;
