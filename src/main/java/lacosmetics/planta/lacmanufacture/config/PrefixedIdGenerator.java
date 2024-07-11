package lacosmetics.planta.lacmanufacture.config;

import lacosmetics.planta.lacmanufacture.model.MateriaPrima;
import lacosmetics.planta.lacmanufacture.model.SemiTerminado;
import lacosmetics.planta.lacmanufacture.model.Terminado;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
public class PrefixedIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return switch (object) {
            case MateriaPrima materiaPrima -> "P" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            case SemiTerminado semiTerminado -> "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            case Terminado terminado -> "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            case null, default -> UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        };
    }
}
