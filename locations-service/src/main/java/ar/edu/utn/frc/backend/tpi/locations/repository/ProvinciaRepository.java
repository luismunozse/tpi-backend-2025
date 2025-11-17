package ar.edu.utn.frc.backend.tpi.locations.repository;

import ar.edu.utn.frc.backend.tpi.locations.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
}
