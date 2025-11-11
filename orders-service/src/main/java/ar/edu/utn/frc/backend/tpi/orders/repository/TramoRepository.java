package ar.edu.utn.frc.backend.tpi.orders.repository;

import ar.edu.utn.frc.backend.tpi.orders.model.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TramoRepository extends JpaRepository<Tramo, Long> {
}
