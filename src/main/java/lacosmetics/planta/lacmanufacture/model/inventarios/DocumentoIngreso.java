package lacosmetics.planta.lacmanufacture.model.inventarios;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentoIngreso {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_ingreso_id", unique = true, updatable = false, nullable = false)
    private int docIngresoId;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Movimiento> itemsDocIngreso;


    @CreationTimestamp
    private LocalDateTime fechaMovimiento;


    /**
     * url de la foto, scan o documento fisico de soporte si lo hay
     */
    private String urlDocSoporte;


    /**
     * nombre de la persona o usuario que crea el documento de ingreso
     */
    private String nombreResponsable;

    /**
     * se usa este constructor cuando se hace un ingreso a almacen por motivo de una compra
     * @param ordenCompra
     */
    DocumentoIngreso(OrdenCompra ordenCompra){

    }

}
