-- Add rendimiento_teorico_unitario to procesos_produccion table
ALTER TABLE procesos_produccion 
ADD COLUMN rendimiento_teorico_unitario DOUBLE PRECISION DEFAULT 0;

-- Add numero_lotes to ordenes_produccion table
ALTER TABLE ordenes_produccion 
ADD COLUMN numero_lotes INTEGER NOT NULL DEFAULT 1;

-- Add comments to document the changes
COMMENT ON COLUMN procesos_produccion.rendimiento_teorico_unitario IS 'Rendimiento teórico por lote';
COMMENT ON COLUMN ordenes_produccion.numero_lotes IS 'Número de lotes a producir en esta orden';