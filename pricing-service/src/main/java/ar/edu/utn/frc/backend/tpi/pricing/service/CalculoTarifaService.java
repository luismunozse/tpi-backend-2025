package ar.edu.utn.frc.backend.tpi.pricing.service;

import ar.edu.utn.frc.backend.tpi.pricing.config.PricingProperties;
import ar.edu.utn.frc.backend.tpi.pricing.dto.CalculoTarifaRequestDto;
import ar.edu.utn.frc.backend.tpi.pricing.dto.CalculoTarifaResponseDto;
import ar.edu.utn.frc.backend.tpi.pricing.model.RecargoTarifa;
import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaBase;
import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaDeposito;
import ar.edu.utn.frc.backend.tpi.pricing.model.TipoCamion;
import ar.edu.utn.frc.backend.tpi.pricing.repository.RecargoTarifaRepository;
import ar.edu.utn.frc.backend.tpi.pricing.repository.TarifaBaseRepository;
import ar.edu.utn.frc.backend.tpi.pricing.repository.TarifaDepositoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculoTarifaService {

    private final TarifaBaseRepository tarifaBaseRepository;
    private final TarifaDepositoRepository tarifaDepositoRepository;
    private final RecargoTarifaRepository recargoTarifaRepository;
    private final PricingProperties pricingProperties;

    public CalculoTarifaResponseDto calcularTarifa(CalculoTarifaRequestDto request) {
        validarRequest(request);

        TarifaBase tarifaBase = encontrarTarifaBase(request);
        TarifaDeposito tarifaDeposito = obtenerTarifaDeposito();
        List<RecargoTarifa> recargos = obtenerRecargos(request.getRecargosAplicados());

        double costoBaseKm = calcularCostoBaseKm(tarifaBase, request.getDistanciaTotalKm());
        double costoCombustible = calcularCostoCombustible(request);
        double costosDeposito = calcularCostosDeposito(tarifaDeposito, request.getDiasTotalesEnDeposito());
        double costosGestion = calcularCostosGestion(tarifaBase, request.getCantidadTramos());
        List<CalculoTarifaResponseDto.DetalleRecargoDto> detalleRecargos = calcularDetalleRecargos(recargos, costoBaseKm + costoCombustible + costosDeposito + costosGestion);
        double totalRecargos = detalleRecargos.stream().mapToDouble(CalculoTarifaResponseDto.DetalleRecargoDto::getMontoAplicado).sum();

        double total = costoBaseKm + costoCombustible + costosDeposito + costosGestion + totalRecargos;

        return CalculoTarifaResponseDto.builder()
                .costoBaseKm(costoBaseKm)
                .costoCombustible(costoCombustible)
                .costosDeposito(costosDeposito)
                .costosGestion(costosGestion)
                .recargos(totalRecargos)
                .detalleRecargos(detalleRecargos)
                .costoTotal(total)
                .build();
    }

    private void validarRequest(CalculoTarifaRequestDto request) {
        if (request.getDistanciaTotalKm() == null || request.getDistanciaTotalKm() <= 0) {
            throw new IllegalArgumentException("La distancia total debe ser mayor a cero");
        }
        if (request.getPesoContenedorKg() == null || request.getPesoContenedorKg() <= 0) {
            throw new IllegalArgumentException("El peso del contenedor debe ser mayor a cero");
        }
        if (request.getVolumenContenedorM3() == null || request.getVolumenContenedorM3() <= 0) {
            throw new IllegalArgumentException("El volumen del contenedor debe ser mayor a cero");
        }
        if (request.getCantidadTramos() == null || request.getCantidadTramos() <= 0) {
            throw new IllegalArgumentException("La cantidad de tramos debe ser mayor a cero");
        }
    }

    private TarifaBase encontrarTarifaBase(CalculoTarifaRequestDto request) {
        TipoCamion tipoCamion = determinarTipoCamion(request.getPesoContenedorKg(), request.getVolumenContenedorM3());
        return tarifaBaseRepository.findByTipoCamionOrderByRangoPesoMinKgAsc(tipoCamion).stream()
                .filter(tarifa -> enRango(request.getPesoContenedorKg(), tarifa.getRangoPesoMinKg(), tarifa.getRangoPesoMaxKg())
                        && enRango(request.getVolumenContenedorM3(), tarifa.getRangoVolumenMinM3(), tarifa.getRangoVolumenMaxM3()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No se encontró una tarifa base para el contenedor"));
    }

    private TarifaDeposito obtenerTarifaDeposito() {
        return tarifaDepositoRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No se configuró tarifa de depósito"));
    }

    private List<RecargoTarifa> obtenerRecargos(List<Long> recargosIds) {
        if (recargosIds == null || recargosIds.isEmpty()) {
            return List.of();
        }
        return recargoTarifaRepository.findAllById(recargosIds);
    }

    private double calcularCostoBaseKm(TarifaBase tarifaBase, Double distanciaTotalKm) {
        return tarifaBase.getCostoPorKilometro() * distanciaTotalKm;
    }

    private double calcularCostoCombustible(CalculoTarifaRequestDto request) {
        double consumo = request.getConsumoCamionLtsPorKm() != null ? request.getConsumoCamionLtsPorKm() : 0.0;
        Double costoLitroConfig = pricingProperties.getCostoLitro();
        double costoLitro = request.getCostoCombustiblePorLitro() != null
                ? request.getCostoCombustiblePorLitro()
                : (costoLitroConfig != null ? costoLitroConfig : 0.0);
        double distancia = request.getDistanciaRecorridaPorCamionKm() != null
                ? request.getDistanciaRecorridaPorCamionKm()
                : request.getDistanciaTotalKm();
        return consumo * costoLitro * distancia;
    }

    private double calcularCostosDeposito(TarifaDeposito tarifaDeposito, Integer dias) {
        int diasTotales = dias != null ? dias : 0;
        return tarifaDeposito.getCargoPorIngreso()
                + tarifaDeposito.getCargoPorSalida()
                + tarifaDeposito.getCostoPorDia() * diasTotales;
    }

    private double calcularCostosGestion(TarifaBase tarifaBase, Integer cantidadTramos) {
        int tramos = cantidadTramos != null ? cantidadTramos : 0;
        return tarifaBase.getCostoFijoGestion() * tramos;
    }

    private List<CalculoTarifaResponseDto.DetalleRecargoDto> calcularDetalleRecargos(List<RecargoTarifa> recargos, double subtotal) {
        List<CalculoTarifaResponseDto.DetalleRecargoDto> detalles = new ArrayList<>();
        for (RecargoTarifa recargo : recargos) {
            double monto = 0.0;
            if (recargo.getPorcentaje() != null) {
                monto += subtotal * (recargo.getPorcentaje() / 100.0);
            }
            if (recargo.getMontoFijo() != null) {
                monto += recargo.getMontoFijo();
            }
            detalles.add(CalculoTarifaResponseDto.DetalleRecargoDto.builder()
                    .id(recargo.getId())
                    .descripcion(recargo.getDescripcion())
                    .porcentaje(recargo.getPorcentaje())
                    .montoFijo(recargo.getMontoFijo())
                    .montoAplicado(monto)
                    .build());
        }
        return detalles;
    }

    private boolean enRango(Double valor, Double minimo, Double maximo) {
        if (valor == null) {
            return false;
        }
        if (minimo != null && valor < minimo) {
            return false;
        }
        if (maximo != null && valor > maximo) {
            return false;
        }
        return true;
    }

    private TipoCamion determinarTipoCamion(Double peso, Double volumen) {
        if (peso == null || volumen == null) {
            throw new IllegalArgumentException("Peso y volumen son obligatorios");
        }
        if (peso <= 3000 && volumen <= 15) {
            return TipoCamion.LIVIANO;
        } else if (peso <= 8000 && volumen <= 40) {
            return TipoCamion.MEDIANO;
        }
        return TipoCamion.PESADO;
    }
}
