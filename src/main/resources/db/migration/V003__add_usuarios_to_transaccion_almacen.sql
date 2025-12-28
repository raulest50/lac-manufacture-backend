-- Migración para agregar soporte de usuarios realizadores y aprobador en transacciones de almacén
-- Fecha: 2025-01-XX
-- Descripción: Agrega campos para rastrear usuarios responsables de realizar dispensaciones 
--              y usuario que aprueba la transacción

-- Crear tabla de relación many-to-many para usuarios responsables
CREATE TABLE IF NOT EXISTS transaccion_almacen_usuarios_realizadores (
    transaccion_id INTEGER NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (transaccion_id, usuario_id),
    FOREIGN KEY (transaccion_id) REFERENCES transaccion_almacen(transaccion_id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Agregar columna para usuario aprobador
ALTER TABLE transaccion_almacen 
    ADD COLUMN IF NOT EXISTS usuario_aprobador_id BIGINT NULL;

-- Agregar foreign key para usuario aprobador
ALTER TABLE transaccion_almacen 
    ADD CONSTRAINT fk_transaccion_usuario_aprobador 
    FOREIGN KEY (usuario_aprobador_id) REFERENCES users(id) ON DELETE SET NULL;

-- Crear índices para mejorar performance
CREATE INDEX IF NOT EXISTS idx_transaccion_usuarios_realizadores_transaccion 
    ON transaccion_almacen_usuarios_realizadores(transaccion_id);
    
CREATE INDEX IF NOT EXISTS idx_transaccion_usuarios_realizadores_usuario 
    ON transaccion_almacen_usuarios_realizadores(usuario_id);
    
CREATE INDEX IF NOT EXISTS idx_transaccion_usuario_aprobador 
    ON transaccion_almacen(usuario_aprobador_id);

