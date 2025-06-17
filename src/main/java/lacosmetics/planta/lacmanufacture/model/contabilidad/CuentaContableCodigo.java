package lacosmetics.planta.lacmanufacture.model.contabilidad;

/**
 * Enum que define los códigos de cuentas contables utilizados en el sistema.
 * Centraliza los códigos para evitar valores hardcoded en el código.
 */
public enum CuentaContableCodigo {
    // Activos
    INVENTARIO_MATERIAS_PRIMAS("1200"),
    INVENTARIO_WIP("1210"),
    INVENTARIO_PRODUCTOS_TERMINADOS("1220"),
    
    // Pasivos
    CUENTAS_POR_PAGAR_PROVEEDORES("2000"),
    
    // Gastos
    GASTO_POR_SCRAP("5200"),
    
    // Otros códigos según necesidad...
    ;
    
    private final String codigo;
    
    CuentaContableCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
}