package lacosmetics.planta.lacmanufacture.model.activos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lacosmetics.planta.lacmanufacture.model.personal.IntegrantePersonal;

import java.time.LocalDateTime;

/**
 * Registro de traslados o cambios de ubicación/responsable de un activo.
 */
@Entity
@Table(name = "traslado_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrasladoActivo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;
    
    /**
     * Fecha y hora del traslado
     */
    @Column(nullable = false)
    private LocalDateTime fechaTraslado;
    
    /**
     * Ubicación de origen
     */
    private String ubicacionOrigen;
    
    /**
     * Ubicación de destino
     */
    @Column(nullable = false)
    private String ubicacionDestino;
    
    /**
     * Responsable anterior del activo
     */
    @ManyToOne
    @JoinColumn(name = "responsable_anterior_id")
    private IntegrantePersonal responsableAnterior;
    
    /**
     * Nuevo responsable del activo
     */
    @ManyToOne
    @JoinColumn(name = "responsable_nuevo_id", nullable = false)
    private IntegrantePersonal responsableNuevo;
    
    /**
     * Motivo del traslado
     */
    private String motivo;
    
    /**
     * Observaciones adicionales
     */
    @Column(length = 500)
    private String observaciones;
}