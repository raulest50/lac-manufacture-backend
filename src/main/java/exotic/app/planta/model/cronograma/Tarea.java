package exotic.app.planta.model.cronograma;

import jakarta.persistence.*;
import exotic.app.planta.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaAsignacion;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaReporteCumplimiento;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaCierre;

    private String descripcionTarea;


    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private List<AvanceTarea> avances;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable_id")
    private User usuarioResponsable;

    @ManyToOne
    @JoinColumn(name = "usuarios_seguimiento")
    private User usuariosSeguimiento;


}
