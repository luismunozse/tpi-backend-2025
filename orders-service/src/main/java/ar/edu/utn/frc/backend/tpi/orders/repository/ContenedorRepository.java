package ar.edu.utn.frc.backend.tpi.orders.repository;

import ar.edu.utn.frc.backend.tpi.orders.model.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {

    Optional<Contenedor> findByIdentificadorIgnoreCase(String identificador);
}
