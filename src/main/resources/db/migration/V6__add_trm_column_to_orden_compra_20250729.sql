-- V6__add_trm_column_to_orden_compra_20250729.sql

-- Add the trm column to the orden_compra table if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.columns
        WHERE table_name = 'orden_compra' AND column_name = 'trm'
    ) THEN
        ALTER TABLE orden_compra ADD COLUMN trm DOUBLE PRECISION DEFAULT 0.0;
        RAISE NOTICE 'Added trm column to orden_compra table';
    ELSE
        RAISE NOTICE 'trm column already exists in orden_compra table';
    END IF;
END
$$;