-- Script para obtener la estructura completa de la base de datos PostgreSQL
-- Este script generará información detallada sobre todas las tablas, columnas, 
-- restricciones, índices y relaciones en la base de datos.

-- 1. Información de tablas
SELECT 
    t.table_name,
    t.table_type,
    obj_description(pgc.oid, 'pg_class') as table_comment
FROM 
    information_schema.tables t
JOIN 
    pg_catalog.pg_class pgc ON t.table_name = pgc.relname
WHERE 
    t.table_schema = 'public'
ORDER BY 
    t.table_name;

-- 2. Información detallada de columnas
SELECT 
    t.table_name,
    c.column_name,
    c.data_type,
    c.character_maximum_length,
    c.numeric_precision,
    c.numeric_scale,
    c.is_nullable,
    c.column_default,
    col_description(pgc.oid, c.ordinal_position) as column_comment
FROM 
    information_schema.tables t
JOIN 
    information_schema.columns c ON t.table_name = c.table_name AND t.table_schema = c.table_schema
JOIN 
    pg_catalog.pg_class pgc ON t.table_name = pgc.relname
WHERE 
    t.table_schema = 'public'
ORDER BY 
    t.table_name, c.ordinal_position;

-- 3. Información de claves primarias
SELECT
    tc.table_name,
    kc.column_name
FROM
    information_schema.table_constraints tc
JOIN
    information_schema.key_column_usage kc ON tc.constraint_name = kc.constraint_name
WHERE
    tc.constraint_type = 'PRIMARY KEY'
    AND tc.table_schema = 'public'
ORDER BY
    tc.table_name,
    kc.ordinal_position;

-- 4. Información de claves foráneas
SELECT
    tc.table_name,
    kc.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM
    information_schema.table_constraints tc
JOIN
    information_schema.key_column_usage kc ON tc.constraint_name = kc.constraint_name
JOIN
    information_schema.constraint_column_usage ccu ON ccu.constraint_name = tc.constraint_name
WHERE
    tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_schema = 'public'
ORDER BY
    tc.table_name,
    kc.column_name;

-- 5. Información de índices
SELECT
    t.relname AS table_name,
    i.relname AS index_name,
    a.attname AS column_name,
    ix.indisunique AS is_unique,
    ix.indisprimary AS is_primary
FROM
    pg_class t,
    pg_class i,
    pg_index ix,
    pg_attribute a
WHERE
    t.oid = ix.indrelid
    AND i.oid = ix.indexrelid
    AND a.attrelid = t.oid
    AND a.attnum = ANY(ix.indkey)
    AND t.relkind = 'r'
    AND t.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')
ORDER BY
    t.relname,
    i.relname;

-- 6. Información de secuencias
SELECT
    sequence_name,
    data_type,
    start_value,
    minimum_value,
    maximum_value,
    increment
FROM
    information_schema.sequences
WHERE
    sequence_schema = 'public'
ORDER BY
    sequence_name;

-- 7. Información de restricciones (constraints)
SELECT
    tc.constraint_name,
    tc.table_name,
    tc.constraint_type,
    cc.check_clause
FROM
    information_schema.table_constraints tc
LEFT JOIN
    information_schema.check_constraints cc ON tc.constraint_name = cc.constraint_name
WHERE
    tc.table_schema = 'public'
ORDER BY
    tc.table_name,
    tc.constraint_name;

-- 8. Información de vistas
SELECT
    table_name AS view_name,
    view_definition
FROM
    information_schema.views
WHERE
    table_schema = 'public'
ORDER BY
    table_name;

-- 9. Información de funciones y procedimientos
SELECT
    p.proname AS function_name,
    pg_get_function_arguments(p.oid) AS function_arguments,
    t.typname AS return_type,
    p.prosrc AS function_definition
FROM
    pg_proc p
JOIN
    pg_type t ON p.prorettype = t.oid
JOIN
    pg_namespace n ON p.pronamespace = n.oid
WHERE
    n.nspname = 'public'
ORDER BY
    p.proname;

-- 10. Información de triggers
SELECT
    trigger_name,
    event_manipulation,
    event_object_table,
    action_statement,
    action_timing
FROM
    information_schema.triggers
WHERE
    trigger_schema = 'public'
ORDER BY
    event_object_table,
    trigger_name;