package lacosmetics.planta.lacmanufacture.model.produccion;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.procesos.ProcesoProduccionCompleto;
import lacosmetics.planta.lacmanufacture.model.producto.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;
import lacosmetics.planta.lacmanufacture.model.producto.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.producto.Terminado;
import lacosmetics.planta.lacmanufacture.model.produccion.PlanificacionProduccion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "ordenes_produccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orden_id", unique = true, updatable = false, nullable = false)
    private int ordenId;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Agregar a OrdenProduccion
    @OneToOne
    @JoinColumn(name = "proceso_completo_id")
    @com.fasterxml.jackson.annotation.JsonManagedReference(value = "orden-proceso")
    private ProcesoProduccionCompleto procesoProduccionCompleto;


    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //@JoinColumn(name = "orden_prod_id")
    @OneToMany(mappedBy = "ordenProduccion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrdenSeguimiento> ordenesSeguimiento = new ArrayList<>();

    // 0: en produccion, 1:terminada
    private int estadoOrden;

    private String observaciones;

    @Column(nullable = false)
    private int numeroLotes = 1; // Valor por defecto: 1 lote

    /**
     * instante en el que se crea la orden de produccion en el sistema.
     * debe ser asignada automaticamente por el backend
     */
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    /**
     * Instante planificado para autorizar el inicio de la orden. No puede ubicarse en el
     * pasado y facilita la coordinación con ventas para asegurar recursos y prioridades.
     */
    @Column(name = "fecha_lanzamiento")
    private LocalDateTime fechaLanzamiento;

    /**
     * Momento objetivo en el que la orden debería estar finalizada según el plan maestro.
     * Facilita el seguimiento de compromisos con clientes y la detección temprana de desvíos.
     */
    @Column(name = "fecha_final_planificada")
    private LocalDateTime fechaFinalPlanificada;

    /**
     * instante en el que realmente se inicia, o almenos que se reporta
     */
    private LocalDateTime fechaInicio;

    /**
     * instante en el que realmente se termina, o almenos que se reporta
     */
    private LocalDateTime fechaFinal;

    /**
     * Número de pedido comercial que origina la orden. Permite trazar el vínculo con ventas
     * y responder consultas comerciales sobre la fabricación de cada compromiso.
     */
    @Column(name = "numero_pedido_comercial")
    private String numeroPedidoComercial;

    /**
     * Área operativa encargada de ejecutar la orden. Favorece la coordinación entre equipos
     * cuando existen múltiples células de producción.
     */
    @Column(name = "area_operativa")
    private String areaOperativa;

    /**
     * Departamento operativo responsable de supervisar la orden. Permite asignar
     * responsabilidades y escalar desvíos a la gerencia adecuada.
     */
    @Column(name = "departamento_operativo")
    private String departamentoOperativo;

    @OneToOne
    @JoinColumn(name = "planificacion_id")
    @JsonManagedReference(value = "orden-planificacion")
    private PlanificacionProduccion planificacionProduccion;

    public OrdenProduccion(Producto producto, String observaciones, int numeroLotes) {
        this.producto = producto;
        this.observaciones = observaciones;
        this.estadoOrden = 0;
        this.numeroLotes = numeroLotes > 0 ? numeroLotes : 1;

        List<OrdenSeguimiento> ordenesSeguimiento = new ArrayList<>();
        List<Insumo> insumos = new ArrayList<>();

        if (producto instanceof Terminado) {
            Terminado terminado = (Terminado) producto;
            insumos = terminado.getInsumos();
        } else if (producto instanceof SemiTerminado) {
            SemiTerminado semiTerminado = (SemiTerminado) producto;
            insumos = semiTerminado.getInsumos();
        } else {
            throw new IllegalArgumentException("El producto debe ser Terminado o SemiTerminado");
        }

        for (Insumo insumo : insumos) {
            // Crear una copia del insumo con la cantidad ajustada
            Insumo insumoAjustado = new Insumo();
            insumoAjustado.setProducto(insumo.getProducto());
            insumoAjustado.setCantidadRequerida(insumo.getCantidadRequerida() * numeroLotes);

            ordenesSeguimiento.add(new OrdenSeguimiento(insumoAjustado, this));
        }
        this.ordenesSeguimiento = ordenesSeguimiento;
    }

    // Mantener constructor anterior para compatibilidad
    public OrdenProduccion(Producto producto, String observaciones) {
        this(producto, observaciones, 1);
    }

}
