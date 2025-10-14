-- Ajuste de esquema para nuevas propiedades operativas y comerciales de la orden de producción
ALTER TABLE ordenes_produccion
    ADD COLUMN IF NOT EXISTS fecha_final_planificada TIMESTAMP;

ALTER TABLE ordenes_produccion
    ADD COLUMN IF NOT EXISTS numero_pedido_comercial VARCHAR(255);

ALTER TABLE ordenes_produccion
    ADD COLUMN IF NOT EXISTS area_operativa VARCHAR(255);

ALTER TABLE ordenes_produccion
    ADD COLUMN IF NOT EXISTS departamento_operativo VARCHAR(255);
