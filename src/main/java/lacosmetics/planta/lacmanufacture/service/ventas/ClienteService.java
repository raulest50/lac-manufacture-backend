package lacosmetics.planta.lacmanufacture.service.ventas;

import jakarta.transaction.Transactional;
import lacosmetics.planta.lacmanufacture.model.ventas.Cliente;
import lacosmetics.planta.lacmanufacture.repo.ventas.ClienteRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepo clienteRepo;

    public Cliente saveCliente(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    public Optional<Cliente> findClienteById(int id) {
        return clienteRepo.findById(id);
    }

    public List<Cliente> searchClientes(String q) {
        return clienteRepo.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }

    public Cliente updateCliente(int id, Cliente data) {
        Cliente existing = clienteRepo.findById(id).orElseThrow(() -> new RuntimeException("Cliente not found"));
        data.setClienteId(id);
        return clienteRepo.save(data);
    }

    public void deleteCliente(int id) {
        clienteRepo.deleteById(id);
    }
}
