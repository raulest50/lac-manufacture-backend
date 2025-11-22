# Proveedor Model Migration Documentation

## Previous Proveedor Version (Current Commit)

```java
@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private String id; // Nit

    /**
     * 0: cedula de ciudadania
     * 1: nit
     */
    private int tipoIdentificacion;

    private String nombre;
    private String direccion;

    /**
     * 0: Regimen comun
     * 1: Regimen simplificado
     * 2: Regimen especial
     */
    private int regimenTributario;

    private String ciudad;
    private String departamento;

    /**
     * Instead of a single contacto, we store a list of JSON objects.
     * Each object can represent a contact with its own attributes.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> contactos;

    private String url;
    private String observacion;

    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    /**
     * 0: credito
     * 1: contado
     * condicion de pago is persisted both in orden compra and proveedor. this
     * way it is possible to change condicion de pago in time and also keeo a history
     * of condiciones de pago over time in case it is changed.
     */
    private String condicionPago;

    /**
     * categorias:
     * 0: Servicios Operativos
     * 1: Materias Primas
     * 2: Materiales de Empaque
     * 3: Servicios Administrativos
     * 4: Equipos y Otros Servicios
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "categorias")
    private int[] categorias;

    private String rutUrl;
    private String camaraUrl;
}
```

## Changes Made

The following changes have been implemented:

1. **Proveedor Model**: 
   - The `uuid` field is now the primary key with `@Id` annotation
   - The `id` field (business identifier) is kept but without the `@Id` annotation
   - The `id` field is still unique but can now be updated if needed

2. **Related Entity Models**:
   - Updated `FacturaCompraActivo`, `OrdenCompraActivo`, `FacturaCompra`, and `OrdenCompraMateriales` to reference the `uuid` field instead of the `id` field
   - Changed the `@JoinColumn` annotations to reference the new column names

3. **Repository**:
   - Changed `ProveedorRepo` to extend `JpaRepository<Proveedor, UUID>` instead of `JpaRepository<Proveedor, String>`
   - Updated query methods to work with the business identifier (`id`) instead of using it as the primary key

4. **Service**:
   - Updated `ProveedorService` methods to work with UUID as the primary key
   - Maintained compatibility with frontend by continuing to use business identifiers in API endpoints

## Reason for Changes

The primary reasons for these changes are:

1. **Flexibility**: Using a surrogate key (UUID) as the primary key allows the business identifier (NIT, CC, etc.) to be modified without affecting relationships.

2. **Data Integrity**: Foreign key relationships now use UUID, ensuring referential integrity even when business identifiers change.

3. **Error Correction**: Makes it easier to correct data entry errors in business identifiers without complex migration scripts.

4. **Frontend Compatibility**: API endpoints continue to use business identifiers, requiring no changes to frontend code.

5. **Best Practices**: Follows database design best practices by separating technical concerns (unique identification) from business concerns (business identifiers).

## Required SQL Migrations

```sql
-- Migration Script for Proveedor Primary Key Change
-- This script migrates from using business identifier (id) as primary key
-- to using a surrogate key (UUID) while maintaining data integrity

-- Step 1: Add new UUID columns to referencing tables
-- These columns will store the UUID references to proveedores
ALTER TABLE factura_compra_activo ADD COLUMN proveedor_uuid UUID;
ALTER TABLE facturas_compras ADD COLUMN proveedor_uuid UUID;
ALTER TABLE orden_compra ADD COLUMN proveedor_uuid UUID;
ALTER TABLE orden_compra_activo ADD COLUMN proveedor_uuid UUID;

-- Step 2: Update the new columns with values from the existing relationships
-- We join with proveedores table to get the corresponding UUID for each business ID
UPDATE factura_compra_activo fca
SET proveedor_uuid = p.uuid
FROM proveedores p
WHERE p.id = fca.proveedor_id;

UPDATE facturas_compras fc
SET proveedor_uuid = p.uuid
FROM proveedores p
WHERE p.id = fc.proveedor_id;

UPDATE orden_compra oc
SET proveedor_uuid = p.uuid
FROM proveedores p
WHERE p.id = oc.proveedor_id;

UPDATE orden_compra_activo oca
SET proveedor_uuid = p.uuid
FROM proveedores p
WHERE p.id = oca.proveedor_id;

-- Step 3: Make the UUID columns NOT NULL after populating them
ALTER TABLE factura_compra_activo ALTER COLUMN proveedor_uuid SET NOT NULL;
ALTER TABLE facturas_compras ALTER COLUMN proveedor_uuid SET NOT NULL;
ALTER TABLE orden_compra ALTER COLUMN proveedor_uuid SET NOT NULL;
ALTER TABLE orden_compra_activo ALTER COLUMN proveedor_uuid SET NOT NULL;

-- Step 4: Add new foreign key constraints using the UUID columns
ALTER TABLE factura_compra_activo
ADD CONSTRAINT fk_factura_compra_activo_proveedor_uuid
FOREIGN KEY (proveedor_uuid) REFERENCES proveedores(uuid);

ALTER TABLE facturas_compras
ADD CONSTRAINT fk_facturas_compras_proveedor_uuid
FOREIGN KEY (proveedor_uuid) REFERENCES proveedores(uuid);

ALTER TABLE orden_compra
ADD CONSTRAINT fk_orden_compra_proveedor_uuid
FOREIGN KEY (proveedor_uuid) REFERENCES proveedores(uuid);

ALTER TABLE orden_compra_activo
ADD CONSTRAINT fk_orden_compra_activo_proveedor_uuid
FOREIGN KEY (proveedor_uuid) REFERENCES proveedores(uuid);

-- Step 5: Drop old foreign key constraints
-- This uses a dynamic approach to find and drop the constraints
DO $$
DECLARE
    fk_name text;
BEGIN
    -- For factura_compra_activo
    SELECT constraint_name INTO fk_name
    FROM information_schema.table_constraints
    WHERE table_name = 'factura_compra_activo'
    AND constraint_type = 'FOREIGN KEY'
    AND constraint_name LIKE '%proveedor%';

    IF fk_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE factura_compra_activo DROP CONSTRAINT ' || quote_ident(fk_name);
    END IF;

    -- For facturas_compras
    SELECT constraint_name INTO fk_name
    FROM information_schema.table_constraints
    WHERE table_name = 'facturas_compras'
    AND constraint_type = 'FOREIGN KEY'
    AND constraint_name LIKE '%proveedor%';

    IF fk_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE facturas_compras DROP CONSTRAINT ' || quote_ident(fk_name);
    END IF;

    -- For orden_compra
    SELECT constraint_name INTO fk_name
    FROM information_schema.table_constraints
    WHERE table_name = 'orden_compra'
    AND constraint_type = 'FOREIGN KEY'
    AND constraint_name LIKE '%proveedor%';

    IF fk_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE orden_compra DROP CONSTRAINT ' || quote_ident(fk_name);
    END IF;

    -- For orden_compra_activo
    SELECT constraint_name INTO fk_name
    FROM information_schema.table_constraints
    WHERE table_name = 'orden_compra_activo'
    AND constraint_type = 'FOREIGN KEY'
    AND constraint_name LIKE '%proveedor%';

    IF fk_name IS NOT NULL THEN
        EXECUTE 'ALTER TABLE orden_compra_activo DROP CONSTRAINT ' || quote_ident(fk_name);
    END IF;
END $$;

-- Step 6: Make the business identifier (id) column updatable
-- This allows correcting business identifiers when needed
ALTER TABLE proveedores ALTER COLUMN id DROP NOT NULL;
ALTER TABLE proveedores ALTER COLUMN id SET DEFAULT NULL;

-- Step 7: Optional - Drop the old proveedor_id columns if no longer needed
-- Uncomment these lines after verifying the migration was successful
-- ALTER TABLE factura_compra_activo DROP COLUMN proveedor_id;
-- ALTER TABLE facturas_compras DROP COLUMN proveedor_id;
-- ALTER TABLE orden_compra DROP COLUMN proveedor_id;
-- ALTER TABLE orden_compra_activo DROP COLUMN proveedor_id;
```

This migration script ensures a smooth transition from using the business identifier as the primary key to using UUID, while maintaining data integrity and backward compatibility.
