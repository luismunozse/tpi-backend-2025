package ar.edu.utn.frc.backend.tpi.locations.repository;

import ar.edu.utn.frc.backend.tpi.locations.model.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositoRepository extends JpaRepository<Deposito, Long> {
    List<Deposito> findByCiudad_Id(Long ciudadId);
}
