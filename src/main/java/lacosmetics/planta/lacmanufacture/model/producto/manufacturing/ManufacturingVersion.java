package lacosmetics.planta.lacmanufacture.model.producto.manufacturing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.packaging.CasePack;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.ProcesoProduccionCompleto;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.producto.Producto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "manufacturing_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturingVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Versión del proceso de manufactura.
     * Se utiliza Double para permitir versiones como 1.0, 1.1, 2.01, etc.
     */
    private Double version;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    private String descripcionCambio;

    private boolean activo;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "manufacturing_version_id")
    private List<Insumo> insumos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "proceso_prod_id")
    private ProcesoProduccionCompleto procesoProduccionCompleto;

    /**
     * Packaging del producto.
     * Este campo es opcional y solo aplicable para productos Terminados.
     * Para productos SemiTerminados, este campo será nulo.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "case_pack_id")
    private CasePack casePack;

    /**
     * Verifica si esta versión corresponde a un producto Terminado.
     * @return true si el producto es de tipo Terminado, false en caso contrario
     */
    public boolean isForTerminado() {
        return producto != null && producto.getTipo_producto().equals("T");
    }

    /**
     * Calcula la cantidad de insumos necesarios para producir una cantidad específica del producto.
     * @param cantidadAProducir Cantidad del producto que se desea producir
     * @return Lista de insumos con cantidades ajustadas para la producción solicitada
     */
    public List<Insumo> calcularInsumosParaProduccion(double cantidadAProducir) {
        List<Insumo> resultado = new ArrayList<>();

        // Usar directamente el rendimiento teórico sin validación redundante
        double factorRendimiento = procesoProduccionCompleto.getRendimientoTeorico();

        // Ajustar por rendimiento teórico
        double cantidadAjustada = cantidadAProducir / factorRendimiento;

        // Crear nuevas instancias de Insumo con cantidades ajustadas
        for (Insumo insumo : insumos) {
            Insumo insumoCalculado = new Insumo();
            insumoCalculado.setProducto(insumo.getProducto());
            insumoCalculado.setCantidadRequerida(insumo.getCantidadRequerida() * cantidadAjustada);
            resultado.add(insumoCalculado);
        }

        return resultado;
    }

    /**
     * Calcula la cantidad de insumos necesarios para producir una cantidad específica del producto
     * y los agrupa por producto para facilitar su uso en la planificación de producción.
     * @param cantidadAProducir Cantidad del producto que se desea producir
     * @return Mapa de productos a cantidades totales requeridas
     */
    public Map<Producto, Double> calcularInsumosAgrupados(double cantidadAProducir) {
        List<Insumo> insumosCalculados = calcularInsumosParaProduccion(cantidadAProducir);

        // Agrupar por producto y sumar cantidades
        return insumosCalculados.stream()
                .collect(Collectors.groupingBy(
                        Insumo::getProducto,
                        Collectors.summingDouble(Insumo::getCantidadRequerida)
                ));
    }
}
