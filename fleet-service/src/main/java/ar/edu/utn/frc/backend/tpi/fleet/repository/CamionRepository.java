package ar.edu.utn.frc.backend.tpi.fleet.repository;

import ar.edu.utn.frc.backend.tpi.fleet.model.Camion;
import ar.edu.utn.frc.backend.tpi.fleet.model.EstadoCamion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CamionRepository extends JpaRepository<Camion, Long> {

    boolean existsByDominioIgnoreCase(String dominio);

    Optional<Camion> findByDominioIgnoreCase(String dominio);

    List<Camion> findByEstado(EstadoCamion estado);

    List<Camion> findByEstadoAndCapacidadPesoKgGreaterThanEqualAndCapacidadVolumenM3GreaterThanEqual(
            EstadoCamion estado,
            Double capacidadPesoKg,
            Double capacidadVolumenM3
    );
}
