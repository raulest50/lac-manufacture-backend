    package lacosmetics.planta.lacmanufacture.model.inventarios;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lacosmetics.planta.lacmanufacture.model.contabilidad.AsientoContable;
    import lacosmetics.planta.lacmanufacture.model.inventarios.dto.IngresoOCM_DTA;
    import lacosmetics.planta.lacmanufacture.model.users.User;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import org.hibernate.annotations.CreationTimestamp;

    import java.time.LocalDateTime;
    import java.util.List;


    @Entity
    @Table(name = "transaccion_almacen")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class TransaccionAlmacen {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(unique = true, updatable = false, nullable = false)
        private int transaccionId;

        @OneToMany(mappedBy = "transaccionAlmacen", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<Movimiento> movimientosTransaccion;

        @CreationTimestamp
        private LocalDateTime fechaTransaccion;

        /**
         * url de la foto, scan o documento fisico de soporte si lo hay
         */
        private String urlDocSoporte;

        // Bidirectional relationship with Users
        @ManyToOne
        @JoinColumn(name = "usuario_id")  // This column will hold the foreign key
        @JsonBackReference
        private User user;

        /**
         * Estado contable de la transacción
         */
        @Enumerated(EnumType.STRING)
        private EstadoContable estadoContable = EstadoContable.PENDIENTE;

        /**
         * Referencia al asiento contable asociado a esta transacción
         */
        @OneToOne
        @JoinColumn(name = "asiento_contable_id")
        private AsientoContable asientoContable;

        private TipoEntidadCausante tipoEntidadCausante;

        /**
         * Una transaccion de almancen se compone de 1 o mas movimientos de inventario.
         * no pouede haber ningun movimiento de almancen que no este asociado a una
         * transaccion de inventario. De la misma forma ninguna transaccion de
         * inventario o almancen puede existir si no esta asociada a una entidad
         * causante, por Ej: Orden de Compra de materiales (OCM), Orden de Produccion,
         * Orden de ajuste de almance (OAA).
         */
        private int idEntidadCausante;

        private String observaciones;

        public TransaccionAlmacen(IngresoOCM_DTA ingresoOCM_dta) {
            this.movimientosTransaccion = ingresoOCM_dta.getTransaccionAlmacen().getMovimientosTransaccion();
            this.tipoEntidadCausante = TipoEntidadCausante.OCM;
            this.idEntidadCausante = ingresoOCM_dta.getOrdenCompraMateriales().getOrdenCompraId();
            this.observaciones = ingresoOCM_dta.getObservaciones();
            this.estadoContable = EstadoContable.PENDIENTE; // Por defecto, pendiente de contabilización
            // El usuario se asignará en el servicio
        }

        public enum TipoEntidadCausante{
            OCM, // orden de compra de materiales
            OP, // orden de produccion
            OTA, // orden de tranferencia de almacen
            OAA, // orden de ajuste de almacen
        }

        /**
         * Estados posibles para la contabilización de una transacción
         */
        public enum EstadoContable {
            PENDIENTE,      // No ha sido contabilizada
            CONTABILIZADA,  // Ya tiene asiento contable
            NO_APLICA       // No requiere contabilización
        }

    }
