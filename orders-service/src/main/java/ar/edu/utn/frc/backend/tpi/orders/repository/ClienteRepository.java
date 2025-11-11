package ar.edu.utn.frc.backend.tpi.orders.repository;

import ar.edu.utn.frc.backend.tpi.orders.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);
}
