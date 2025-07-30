-- Migración para consolidar las tablas de activos fijos en una sola tabla

-- 1. Agregar las nuevas columnas a la tabla activo
ALTER TABLE activo ADD COLUMN IF NOT EXISTS tipo_activo VARCHAR(255) NOT NULL DEFAULT 'EQUIPO';
ALTER TABLE activo ADD COLUMN IF NOT EXISTS unidades_capacidad VARCHAR(255) NULL;
ALTER TABLE activo ADD COLUMN IF NOT EXISTS capacidad DOUBLE PRECISION NULL;

-- 2. Actualizar los registros existentes con el tipo correcto
-- Verificar si existe la tabla activo_produccion
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'activo_produccion') THEN
        -- Actualizar los activos de producción
        UPDATE activo a
        SET 
            tipo_activo = 'PRODUCCION',
            unidades_capacidad = (SELECT ap.unidades_capacidad FROM activo_produccion ap WHERE ap.id = a.id),
            capacidad = (SELECT ap.capacidad FROM activo_produccion ap WHERE ap.id = a.id)
        WHERE EXISTS (SELECT 1 FROM activo_produccion ap WHERE ap.id = a.id);
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'mobiliario') THEN
        -- Actualizar los mobiliarios
        UPDATE activo a
        SET tipo_activo = 'MOBILIARIO'
        WHERE EXISTS (SELECT 1 FROM mobiliario m WHERE m.id = a.id);
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'equipos') THEN
        -- Actualizar los equipos
        UPDATE activo a
        SET tipo_activo = 'EQUIPO'
        WHERE EXISTS (SELECT 1 FROM equipos e WHERE e.id = a.id);
    END IF;
END $$;

-- 3. Eliminar las tablas hijas si existen
DROP TABLE IF EXISTS activo_produccion;
DROP TABLE IF EXISTS mobiliario;
DROP TABLE IF EXISTS equipos;

-- 4. Eliminar el valor por defecto de tipo_activo para que sea obligatorio especificarlo
ALTER TABLE activo ALTER COLUMN tipo_activo DROP DEFAULT;
