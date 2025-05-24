-- Set default value for password_encoded column for existing users
UPDATE users SET password_encoded = false WHERE password_encoded IS NULL;