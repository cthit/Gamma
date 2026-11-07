UPDATE g_user_activation
SET token = encode(sha256(convert_to(token, 'UTF8')), 'hex');

UPDATE g_password_reset
SET token = encode(sha256(convert_to(token, 'UTF8')), 'hex')
WHERE token IS NOT NULL;
