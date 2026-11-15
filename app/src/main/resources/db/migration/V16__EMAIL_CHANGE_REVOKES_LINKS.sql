-- Links mailed for a user's account stop working once the user's email changes, both when an admin
-- changes it and when the user confirms a new address:
--  * A password reset link was mailed to the old email, and whoever reads that address must no
--    longer be able to use it.
--  * A pending email change was asked for from the old email. Confirming it later must not undo a
--    change made since, such as an admin replacing the email.
-- Doing this in the database covers every way the email is changed, and commits atomically with it.
CREATE FUNCTION revoke_links_on_email_change() RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM g_password_reset WHERE user_id = NEW.user_id;
    DELETE FROM g_email_change WHERE user_id = NEW.user_id;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER revoke_links_on_email_change
    AFTER UPDATE OF email ON g_user
    FOR EACH ROW
    WHEN (OLD.email IS DISTINCT FROM NEW.email)
    EXECUTE FUNCTION revoke_links_on_email_change();
