-- Activation and password reset tokens are now stored as hex encoded SHA-256 hashes.
-- Hash the tokens that already exist so that they keep working until they expire.
UPDATE g_user_activation
SET token = encode(sha256(convert_to(token, 'UTF8')), 'hex');

UPDATE g_password_reset
SET token = encode(sha256(convert_to(token, 'UTF8')), 'hex')
WHERE token IS NOT NULL;
