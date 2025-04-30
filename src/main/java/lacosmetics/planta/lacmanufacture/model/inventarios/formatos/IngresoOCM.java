package lacosmetics.planta.lacmanufacture.model.inventarios.formatos;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lacosmetics.planta.lacmanufacture.model.compras.OrdenCompra;
import lacosmetics.planta.lacmanufacture.model.dto.DocIngresoDTA;
import lacosmetics.planta.lacmanufacture.model.inventarios.DocMovs;
import lacosmetics.planta.lacmanufacture.model.inventarios.Movimientos;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("OC_IN")
@NoArgsConstructor
public class IngresoOCM extends DocMovs {


    

    /**
     * Constructor used when registering a warehouse entry due to a purchase.
     *
     * @param ordenCompra the purchase order whose items will be used to create movements
     * @param urlDocSoporte URL of the supporting document (photo, scan, etc.)
     * @param nombreResponsable Name of the person creating the entry document
     * @param observaciones Additional remarks or observations
     */
    public IngresoOCM(OrdenCompra ordenCompra, String urlDocSoporte, String nombreResponsable, String observaciones) {
        this.setNombreResponsable(nombreResponsable);
        this.setUrlDocSoporte(urlDocSoporte);
        this.setItemsDocIngreso(
                ordenCompra.getItemsOrdenCompra().stream()
                        .map(Movimientos::new)
                        .collect(Collectors.toList())
        );
        this.setObservaciones(observaciones);
    }

    /**
     * Constructor used when creating an entry based on the provided DTO.
     * The urlDocSoporte will be set later after the file is saved.
     * @param docIngresoDTO the incoming DTO containing order, user and observations.
     */
    public IngresoOCM(DocIngresoDTA docIngresoDTO) {
        // If an order exists, create Movimiento items from its itemsOrdenCompra.
        if (docIngresoDTO.getOrdenCompra() != null) {
            this.setItemsDocIngreso(
                    docIngresoDTO.getOrdenCompra().getItemsOrdenCompra().stream().map(Movimientos::new).collect(Collectors.toList())
            );
        }
        this.setNombreResponsable(docIngresoDTO.getUser());
        this.setObservaciones(docIngresoDTO.getObservaciones());
    }


}
