package lacosmetics.planta.lacmanufacture.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movimiento_id", unique = true, updatable = false, nullable = false)
    private int movimientoId;

    // puede ser positivo o negativo
    private double cantidad;

    // aplica para los 3 tipos de productos
    @NotNull
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // causa del movimiento
    // VENTA, COMPRA, AVERIA, USO_INTERNO, PROD_INTERNO, OTROS
    private String tipo;

    private String observaciones;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;


    public Movimiento(Insumo insumo){
        cantidad = insumo.getCantidadRequerida();
        producto = insumo.getProducto();
        tipo = CausaMovimiento.USO_INTERNO;
    }


    public static class CausaMovimiento {
        public static final String COMPRA = "COMPRA";
        public static final String AVERIA = "AVERIA";
        public static final String USO_INTERNO = "USO_INTERNO";
        public static final String PROD_INTERNO = "PROD_INTERNO";
        public static final String SALIDA_APP_VENTAS = "SALIDA_APP_VENTAS";
    }






}
