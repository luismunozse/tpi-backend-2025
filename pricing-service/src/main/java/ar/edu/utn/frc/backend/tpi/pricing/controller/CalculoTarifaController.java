package ar.edu.utn.frc.backend.tpi.pricing.controller;

import ar.edu.utn.frc.backend.tpi.pricing.dto.CalculoTarifaRequestDto;
import ar.edu.utn.frc.backend.tpi.pricing.dto.CalculoTarifaResponseDto;
import ar.edu.utn.frc.backend.tpi.pricing.service.CalculoTarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/calculos")
@RequiredArgsConstructor
public class CalculoTarifaController {

    private final CalculoTarifaService calculoTarifaService;

    @PostMapping("/tarifa")
    public CalculoTarifaResponseDto calcularTarifa(@RequestBody CalculoTarifaRequestDto request) {
        return calculoTarifaService.calcularTarifa(request);
    }
}
