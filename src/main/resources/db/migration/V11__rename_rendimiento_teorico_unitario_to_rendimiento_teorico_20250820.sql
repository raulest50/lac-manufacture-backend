-- Rename rendimiento_teorico_unitario to rendimiento_teorico
ALTER TABLE procesos_produccion 
RENAME COLUMN rendimiento_teorico_unitario TO rendimiento_teorico;

-- Update comment to document the change
COMMENT ON COLUMN procesos_produccion.rendimiento_teorico IS 'Rendimiento teórico por lote (cantidad total de unidades producidas por lote)';