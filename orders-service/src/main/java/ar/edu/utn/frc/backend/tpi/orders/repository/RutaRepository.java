package ar.edu.utn.frc.backend.tpi.orders.repository;

import ar.edu.utn.frc.backend.tpi.orders.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, Long> {
}
