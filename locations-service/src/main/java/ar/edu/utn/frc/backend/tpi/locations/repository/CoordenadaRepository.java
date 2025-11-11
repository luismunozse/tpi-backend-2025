package ar.edu.utn.frc.backend.tpi.locations.repository;

import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordenadaRepository extends JpaRepository<Coordenada, Integer> {
}
