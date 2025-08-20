-- Ensure inventareable column exists with default TRUE
ALTER TABLE productos ADD COLUMN IF NOT EXISTS inventareable BOOLEAN DEFAULT TRUE;
ALTER TABLE productos ALTER COLUMN inventareable SET DEFAULT TRUE;
