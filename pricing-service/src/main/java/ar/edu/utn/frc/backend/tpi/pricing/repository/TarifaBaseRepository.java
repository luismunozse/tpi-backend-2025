package ar.edu.utn.frc.backend.tpi.pricing.repository;

import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaBase;
import ar.edu.utn.frc.backend.tpi.pricing.model.TipoCamion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarifaBaseRepository extends JpaRepository<TarifaBase, Long> {

    List<TarifaBase> findByTipoCamionOrderByRangoPesoMinKgAsc(TipoCamion tipoCamion);
}
