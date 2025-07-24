-- Update the check constraint for accesos table to include PAGOS_PROVEEDORES
ALTER TABLE accesos 
DROP CONSTRAINT accesos_modulo_acceso_check,
ADD CONSTRAINT accesos_modulo_acceso_check 
CHECK (modulo_acceso IN ('USUARIOS', 'PRODUCTOS', 'PRODUCCION', 'STOCK', 'PROVEEDORES', 
                         'COMPRAS', 'SEGUIMIENTO_PRODUCCION', 'CLIENTES', 'VENTAS', 
                         'TRANSACCIONES_ALMACEN', 'ACTIVOS', 'CONTABILIDAD', 'PERSONAL_PLANTA', 
                         'BINTELLIGENCE', 'CARGA_MASIVA', 'ADMINISTRACION_ALERTAS', 
                         'MASTER_CONFIGS', 'CRONOGRAMA', 'ORGANIGRAMA', 'PAGOS_PROVEEDORES'));