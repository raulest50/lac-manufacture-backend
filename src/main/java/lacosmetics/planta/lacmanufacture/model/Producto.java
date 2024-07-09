package lacosmetics.planta.lacmanufacture.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    public enum Tipo {
        MATERIA_PRIMA("Materia Prima"),
        SEMI_TERMINADO("Semi Terminado"),
        TERMINADO("Terminado");

        private final String displayName;

        Tipo(String displayName) {
            this.displayName = displayName;
        }

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }

        @JsonCreator
        public static Tipo forValue(String value) {
            for (Tipo tipo : Tipo.values()) {
                if (tipo.getDisplayName().equalsIgnoreCase(value)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Unknown value: " + value);
        }
    }

    public enum Unit_Type {
        KG("KG"),
        L("L"),
        U("U");

        private final String displayName;

        Unit_Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// id assigned by the database ascendig order
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(length = 200)
    private String nombre;
    private String notas;

    @Min(value=0, message = "El costo no puede ser negativo") // validacion en db engine
    private int costo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Tipo tipo;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    // KG or L
    @Enumerated(EnumType.STRING)
    @Column(length = 4)
    private Unit_Type unit_type;

    @Min(value=0, message = "La Cantidad por unidad no puede ser negativa") // Cantidad por unidad
    private double cant_x_unidad;



}


