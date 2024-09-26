package lacosmetics.planta.lacmanufacture.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.notPersisted.ReporteCompraDTA;
import lacosmetics.planta.lacmanufacture.model.producto.MateriaPrima;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase seria el equivalente a una factura de HyL pero solo con la informacion relevante para
 * esta app de produccion
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoMovimeintoMP {

    @Id
    private int idGrupoMovimiento;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "grupo_movimiento_id")
    @JsonManagedReference
    private List<MovimientoMP> listaMovimientosMP;

    private LocalDate fechaMovimiento;

    // 0 compra hyl, 1 consumo interno, 2 perdidas
    private int tipoMovimiento;

    /**
     * se usa materia prima por brevedad para no crear un DTA, pero en realidad al usar Materia prima,
     * costo representa el nuevo costo expresado en la factura de compra, cantidad representa la cantidad
     * a aumentar en stock, y al hacer set de materia prima, solo se toma el id referencia para que quede
     * como llave foranea en la tabla de MovimientoMP pero nada mas.
     * @param reporteCompra
     */
    public GrupoMovimeintoMP(ReporteCompraDTA reporteCompra){
        this.idGrupoMovimiento = reporteCompra.getIdcompra();
        this.fechaMovimiento = reporteCompra.getFechacompra();
        this.tipoMovimiento = 0; // factura de compra HyL

        List<MovimientoMP> temp_list = new ArrayList<>();

        for(MateriaPrima m : reporteCompra.getProductos()){
            MovimientoMP mov = new MovimientoMP();
            mov.setCantidad(m.getCantidad()); // cantidad comprada en la factura
            mov.setCosto(m.getCosto()); // ultimo costo sacado de la factura desde HyL
            mov.setMateriaPrima(m); // solo para que ponga la llave foranea
            temp_list.add(mov);
        }
        this.listaMovimientosMP = temp_list;
    }
}
