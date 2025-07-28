-- V5__update_clientes_table_20250728.sql

-- First, check if the clientes table exists, and create it if it doesn't
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'clientes') THEN
        CREATE TABLE clientes (
            cliente_id SERIAL PRIMARY KEY,
            nombre VARCHAR(255),
            email VARCHAR(255),
            telefono VARCHAR(255),
            direccion VARCHAR(255),
            condiciones_pago VARCHAR(255),
            limite_credito INTEGER,
            fecha_registro TIMESTAMP,
            url_rut VARCHAR(255),
            url_cam_comer VARCHAR(255)
        );
    END IF;
END
$$;

-- Then, check for each column and add it if it doesn't exist
DO $$
BEGIN
    -- Check for nombre column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'nombre') THEN
        ALTER TABLE clientes ADD COLUMN nombre VARCHAR(255);
    END IF;

    -- Check for email column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'email') THEN
        ALTER TABLE clientes ADD COLUMN email VARCHAR(255);
    END IF;

    -- Check for telefono column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'telefono') THEN
        ALTER TABLE clientes ADD COLUMN telefono VARCHAR(255);
    END IF;

    -- Check for direccion column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'direccion') THEN
        ALTER TABLE clientes ADD COLUMN direccion VARCHAR(255);
    END IF;

    -- Check for condiciones_pago column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'condiciones_pago') THEN
        ALTER TABLE clientes ADD COLUMN condiciones_pago VARCHAR(255);
    END IF;

    -- Check for limite_credito column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'limite_credito') THEN
        ALTER TABLE clientes ADD COLUMN limite_credito INTEGER;
    END IF;

    -- Check for fecha_registro column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'fecha_registro') THEN
        ALTER TABLE clientes ADD COLUMN fecha_registro TIMESTAMP;
    END IF;

    -- Check for url_rut column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'url_rut') THEN
        ALTER TABLE clientes ADD COLUMN url_rut VARCHAR(255);
    END IF;

    -- Check for url_cam_comer column
    IF NOT EXISTS (SELECT FROM information_schema.columns 
                   WHERE table_name = 'clientes' AND column_name = 'url_cam_comer') THEN
        ALTER TABLE clientes ADD COLUMN url_cam_comer VARCHAR(255);
    END IF;
END
$$;

-- Rename columns if they exist with different names (based on JPA naming conventions)
-- Note: IDE may show warnings for these column names, but they are checked dynamically at runtime
-- These checks handle potential naming differences between JPA entity fields (camelCase) 
-- and database columns (snake_case)
DO $$
BEGIN
    -- Check if condicionesPago exists but condiciones_pago doesn't
    IF EXISTS (SELECT FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'condicionespago') 
       AND NOT EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_name = 'clientes' AND column_name = 'condiciones_pago') THEN
        ALTER TABLE clientes RENAME COLUMN condicionespago TO condiciones_pago;
    END IF;

    -- Check if limiteCredito exists but limite_credito doesn't
    IF EXISTS (SELECT FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'limitecredito') 
       AND NOT EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_name = 'clientes' AND column_name = 'limite_credito') THEN
        ALTER TABLE clientes RENAME COLUMN limitecredito TO limite_credito;
    END IF;

    -- Check if fechaRegistro exists but fecha_registro doesn't
    IF EXISTS (SELECT FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'fecharegistro') 
       AND NOT EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_name = 'clientes' AND column_name = 'fecha_registro') THEN
        ALTER TABLE clientes RENAME COLUMN fecharegistro TO fecha_registro;
    END IF;

    -- Check if urlRut exists but url_rut doesn't
    IF EXISTS (SELECT FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'urlrut') 
       AND NOT EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_name = 'clientes' AND column_name = 'url_rut') THEN
        ALTER TABLE clientes RENAME COLUMN urlrut TO url_rut;
    END IF;

    -- Check if urlCamComer exists but url_cam_comer doesn't
    IF EXISTS (SELECT FROM information_schema.columns 
               WHERE table_name = 'clientes' AND column_name = 'urlcamcomer') 
       AND NOT EXISTS (SELECT FROM information_schema.columns 
                      WHERE table_name = 'clientes' AND column_name = 'url_cam_comer') THEN
        ALTER TABLE clientes RENAME COLUMN urlcamcomer TO url_cam_comer;
    END IF;
END
$$;

-- Add this section to drop columns that exist in the database but not in the model
DO $$
DECLARE
    column_to_keep text[] := array['cliente_id', 'nombre', 'email', 'telefono', 'direccion', 
                                  'condiciones_pago', 'limite_credito', 'fecha_registro', 
                                  'url_rut', 'url_cam_comer'];
    col record;
BEGIN
    FOR col IN 
        SELECT column_name 
        FROM information_schema.columns 
        WHERE table_name = 'clientes' 
          AND table_schema = 'public'
          AND column_name != ALL(column_to_keep)
    LOOP
        EXECUTE format('ALTER TABLE clientes DROP COLUMN IF EXISTS %I', col.column_name);
        RAISE NOTICE 'Dropped column: %', col.column_name;
    END LOOP;
END
$$;
