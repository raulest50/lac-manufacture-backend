package exotic.app.planta.model.producto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "familia")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Familia {
    @Id
    private int familiaId;

    @Column(unique = true)
    private String familiaNombre;

    private String familiaDescripcion;
}