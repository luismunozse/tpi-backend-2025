package ar.edu.utn.frc.backend.tpi.locations.repository;

import ar.edu.utn.frc.backend.tpi.locations.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
}
