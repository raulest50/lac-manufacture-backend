-- Rename table familia to categoria
ALTER TABLE familia RENAME TO categoria;

-- Rename columns in categoria table
ALTER TABLE categoria RENAME COLUMN familia_id TO categoria_id;
ALTER TABLE categoria RENAME COLUMN familia_nombre TO categoria_nombre;
ALTER TABLE categoria RENAME COLUMN familia_descripcion TO categoria_descripcion;

-- Update foreign key in productos table
ALTER TABLE productos RENAME COLUMN familia_id TO categoria_id;

-- Update sequence if it exists
-- Note: This assumes the sequence is named familia_seq, adjust if different
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = 'familia_seq') THEN
        ALTER SEQUENCE familia_seq RENAME TO categoria_seq;
    END IF;
END
$$;

-- Add comment to document the change
COMMENT ON TABLE categoria IS 'Renamed from familia as part of refactoring';
