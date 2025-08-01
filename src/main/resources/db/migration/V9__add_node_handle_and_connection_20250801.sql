-- 1. Create proceso_produccion_node table if it doesn't exist
CREATE TABLE IF NOT EXISTS proceso_produccion_node (
    p_node_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    proceso_id INT,
    posicion_x DOUBLE PRECISION NOT NULL DEFAULT 0,
    posicion_y DOUBLE PRECISION NOT NULL DEFAULT 0
);

-- Add foreign key constraint if proceso_produccion table exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'proceso_produccion') THEN
        -- Check if the constraint already exists
        IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                      WHERE constraint_name = 'fk_proceso_produccion_node_proceso' 
                      AND table_name = 'proceso_produccion_node') THEN
            ALTER TABLE proceso_produccion_node 
            ADD CONSTRAINT fk_proceso_produccion_node_proceso 
            FOREIGN KEY (proceso_id) REFERENCES proceso_produccion(proceso_id);
        END IF;
    END IF;
END $$;

-- 2. Remove inputs and outputs columns from proceso_produccion_node table if they exist
ALTER TABLE proceso_produccion_node DROP COLUMN IF EXISTS inputs;
ALTER TABLE proceso_produccion_node DROP COLUMN IF EXISTS outputs;

-- 3. Create node_handle table
CREATE TABLE IF NOT EXISTS node_handle (
    handle_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    node_id BIGINT NOT NULL,
    frontend_handle_id VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,  -- Enum: SOURCE, TARGET
    position VARCHAR(50) NOT NULL,  -- Enum: TOP, RIGHT, BOTTOM, LEFT
    label VARCHAR(255),
    CONSTRAINT fk_node_handle_node FOREIGN KEY (node_id) REFERENCES proceso_produccion_node(p_node_id) ON DELETE CASCADE
);

-- 4. Create node_connection table
CREATE TABLE IF NOT EXISTS node_connection (
    connection_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    source_handle_id BIGINT NOT NULL,
    target_handle_id BIGINT NOT NULL,
    CONSTRAINT fk_node_connection_source FOREIGN KEY (source_handle_id) REFERENCES node_handle(handle_id) ON DELETE CASCADE,
    CONSTRAINT fk_node_connection_target FOREIGN KEY (target_handle_id) REFERENCES node_handle(handle_id) ON DELETE CASCADE
);

-- 5. Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_node_handle_node_id ON node_handle(node_id);
CREATE INDEX IF NOT EXISTS idx_node_connection_source ON node_connection(source_handle_id);
CREATE INDEX IF NOT EXISTS idx_node_connection_target ON node_connection(target_handle_id);
