package lacosmetics.planta.lacmanufacture.model.producto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.ManufacturingVersion;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.packaging.CasePack;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.receta.Insumo;
import lacosmetics.planta.lacmanufacture.model.producto.manufacturing.procesos.ProcesoProduccionCompleto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@DiscriminatorValue("T")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Terminado extends Producto{

    // 0: standard, active ,   1: obsoleto, deprecated
    private int status;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    /**
     * Una imagen o ilustracion del producto
     */
    private String fotoUrl;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @OrderBy("version DESC")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<ManufacturingVersion> manufacturingVersions;

    @OneToOne
    @JoinColumn(name = "current_version_id")
    private ManufacturingVersion currentVersion;

    // Métodos de conveniencia para mantener la compatibilidad con el código existente

    public List<Insumo> getInsumos() {
        return currentVersion != null ? currentVersion.getInsumos() : null;
    }

    public ProcesoProduccionCompleto getProcesoProduccionCompleto() {
        return currentVersion != null ? currentVersion.getProcesoProduccionCompleto() : null;
    }

    public CasePack getCasePack() {
        return currentVersion != null ? currentVersion.getCasePack() : null;
    }
}
