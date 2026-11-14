-- Deleting an owner cascades through their clients and all client dependent rows.
ALTER TABLE g_client_scope DROP CONSTRAINT g_client_scope_client_uid_fkey;
ALTER TABLE g_client_scope ADD CONSTRAINT g_client_scope_client_uid_fkey
    FOREIGN KEY (client_uid) REFERENCES g_client (client_uid) ON DELETE CASCADE;

-- Keep only references for a user's outstanding contact reminders, never another email snapshot.
-- Reminders can be dismissed and are removed on reapproval, client deletion or account deletion.
CREATE TABLE g_withdrawn_client_contact (
    user_id UUID NOT NULL REFERENCES g_user (user_id) ON DELETE CASCADE,
    client_uid UUID NOT NULL REFERENCES g_client (client_uid) ON DELETE CASCADE,
    PRIMARY KEY (user_id, client_uid)
);

CREATE INDEX g_withdrawn_client_contact_client_idx ON g_withdrawn_client_contact (client_uid);

-- This outbox survives restarts and commits atomically with avatar updates/deletions,
-- including database cascades. It deliberately has no FK back to the deleted user.
CREATE TABLE g_image_removal (
    image_uri VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retry_after TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX g_image_removal_retry_idx ON g_image_removal (retry_after);

CREATE FUNCTION queue_removed_avatar() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO g_image_removal (image_uri) VALUES (OLD.avatar_uri) ON CONFLICT DO NOTHING;
    ELSIF OLD.avatar_uri IS DISTINCT FROM NEW.avatar_uri THEN
        INSERT INTO g_image_removal (image_uri) VALUES (OLD.avatar_uri) ON CONFLICT DO NOTHING;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER queue_removed_avatar
    AFTER DELETE OR UPDATE OF avatar_uri ON g_user_avatar_uri
    FOR EACH ROW EXECUTE FUNCTION queue_removed_avatar();
