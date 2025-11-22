
-- para listar las restricciones de llave foranea del modelo antes de la mejora de uuid
SELECT
    tc.table_schema,
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_schema AS foreign_table_schema,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM
    information_schema.table_constraints AS tc
        JOIN information_schema.key_column_usage AS kcu
             ON tc.constraint_name = kcu.constraint_name
                 AND tc.table_schema = kcu.table_schema
        JOIN information_schema.constraint_column_usage AS ccu
             ON ccu.constraint_name = tc.constraint_name
                 AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
  AND ccu.table_name = 'proveedores'
  AND ccu.column_name = 'id';


-- SQL to deactivate (drop) the foreign key constraints that reference proveedores.id

-- Disable constraint on factura_compra_activo table
ALTER TABLE public.factura_compra_activo
    DROP CONSTRAINT "FKtbkcxnuuicces9h500drm0vd";

-- Disable constraint on facturas_compras table
ALTER TABLE public.facturas_compras
    DROP CONSTRAINT "FKsso561ilnful43u00cn494r05";

-- Disable constraint on orden_compra table
ALTER TABLE public.orden_compra
    DROP CONSTRAINT "FK83x6g3uo8dsqx02h6h5sfdfko";

-- Disable constraint on orden_compra_activo table
ALTER TABLE public.orden_compra_activo
    DROP CONSTRAINT "FKef1x43u8kv6i8w5abrx276msh";



