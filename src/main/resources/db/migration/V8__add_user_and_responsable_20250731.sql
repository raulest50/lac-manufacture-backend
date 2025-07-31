-- Add usuario_id column to integrante_personal table
ALTER TABLE integrante_personal ADD COLUMN IF NOT EXISTS usuario_id BIGINT;

-- Add foreign key constraint to users table
ALTER TABLE integrante_personal 
ADD CONSTRAINT fk_integrante_personal_usuario 
FOREIGN KEY (usuario_id) REFERENCES users(id);

-- Add ubicacion and responsable_id columns to activo table
ALTER TABLE activo ADD COLUMN IF NOT EXISTS ubicacion VARCHAR(255);
ALTER TABLE activo ADD COLUMN IF NOT EXISTS responsable_id BIGINT;

-- Add foreign key constraint to integrante_personal table
ALTER TABLE activo 
ADD CONSTRAINT fk_activo_responsable 
FOREIGN KEY (responsable_id) REFERENCES integrante_personal(id);