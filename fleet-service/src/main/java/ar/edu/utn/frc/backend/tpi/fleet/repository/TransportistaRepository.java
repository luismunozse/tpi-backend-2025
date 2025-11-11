package ar.edu.utn.frc.backend.tpi.fleet.repository;

import ar.edu.utn.frc.backend.tpi.fleet.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {

    boolean existsByEmailIgnoreCase(String email);

    java.util.Optional<Transportista> findByEmailIgnoreCase(String email);
}
