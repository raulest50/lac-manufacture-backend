package exotic.app.planta.model.activos.fijos.compras;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un ítem en una orden de compra de activos fijos.
 * A diferencia de los ítems de materiales, estos no están vinculados a un activo fijo existente
 * ya que los activos fijos se codifican después de su llegada.
 */
@Entity
@Table(name = "item_orden_compra_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemOrdenCompraActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", unique = true, updatable = false, nullable = false)
    private int itemOrdenId;

    // Relación bidireccional con OrdenCompraActivo
    @ManyToOne
    @JoinColumn(name = "orden_compra_activo_id")
    @JsonBackReference
    private OrdenCompraActivo ordenCompraActivo;

    // Nombre descriptivo del activo fijo
    @Column(nullable = false, length = 255)
    private String nombre;
    
    // Cantidad de unidades
    private int cantidad;
    
    // Precio unitario sin IVA
    private double precioUnitario;

    private double ivaPercentage;

    // Valor del IVA por unidad
    private double ivaValue;
    
    // Subtotal (precio unitario * cantidad)
    private double subTotal;
    
    /**
     * Calcula el precio unitario final incluyendo IVA
     */
    public double getPrecioUnitarioFinal() {
        return precioUnitario + ivaValue;
    }


}