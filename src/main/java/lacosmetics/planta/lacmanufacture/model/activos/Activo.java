package lacosmetics.planta.lacmanufacture.model.activos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lacosmetics.planta.lacmanufacture.model.contabilidad.CuentaContable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "activo")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Activo {

    // llave primaria
    @Id
    private String id;

    @Column(nullable = false)
    private String nombre;

    /**
     * diferente de proveedor. ej:
     * un proveedor puede vender laptop Dell pero no es el fabricante
     */
    private String brand;

    /**
     * URL a la ficha técnica del activo
     */
    private String url;

    /**
     * 0: activo
     * 1: obsoleto
     * 2: dado de baja
     */
    private int estado;

    private LocalDateTime fechaCodificacion;

    private LocalDateTime fechaBaja;

    // Campos para integración contable
    private BigDecimal valorAdquisicion;
    private BigDecimal valorResidual;
    private Integer vidaUtilMeses;
    private String metodoDespreciacion; // "LINEAL", "SUMA_DIGITOS", etc.

    // Relaciones con contabilidad
    @ManyToOne
    @JoinColumn(name = "cuenta_activo_codigo")
    private CuentaContable cuentaActivo;

    @ManyToOne
    @JoinColumn(name = "cuenta_depreciacion_codigo")
    private CuentaContable cuentaDepreciacionAcumulada;

    @ManyToOne
    @JoinColumn(name = "cuenta_gasto_depreciacion_codigo")
    private CuentaContable cuentaGastoDepreciacion;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaContableActivo categoria;

    @OneToMany(mappedBy = "activo", cascade = CascadeType.ALL)
    private List<DepreciacionActivo> depreciaciones;

    @OneToMany(mappedBy = "activo", cascade = CascadeType.ALL)
    private List<TrasladoActivo> traslados;

    @OneToMany(mappedBy = "activo", cascade = CascadeType.ALL)
    private List<MantenimientoActivo> mantenimientos;

    @OneToOne(mappedBy = "activo", cascade = CascadeType.ALL)
    private IncorporacionActivoLine lineaIncorporacion;

    @OneToMany(mappedBy = "activo", cascade = CascadeType.ALL)
    private List<DocumentoBajaActivo> documentosBaja;
}
