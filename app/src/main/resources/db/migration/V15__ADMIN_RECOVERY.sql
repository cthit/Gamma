ALTER TABLE g_password_reset ADD COLUMN admin_recovery BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE g_user ADD COLUMN email_recovery_requires_verification BOOLEAN NOT NULL DEFAULT FALSE;
