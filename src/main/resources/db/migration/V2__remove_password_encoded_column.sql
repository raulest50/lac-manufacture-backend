-- Drop the password_encoded column from the users table
ALTER TABLE users DROP COLUMN IF EXISTS password_encoded;