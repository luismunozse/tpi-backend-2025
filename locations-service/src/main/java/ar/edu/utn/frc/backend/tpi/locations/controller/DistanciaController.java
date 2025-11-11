package ar.edu.utn.frc.backend.tpi.locations.controller;

import ar.edu.utn.frc.backend.tpi.locations.dto.DistanceRequestDto;
import ar.edu.utn.frc.backend.tpi.locations.dto.DistanceResponseDto;
import ar.edu.utn.frc.backend.tpi.locations.mapper.CoordenadaMapper;
import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import ar.edu.utn.frc.backend.tpi.locations.service.DistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/distancias")
@RequiredArgsConstructor
public class DistanciaController {

    private final DistanceService distanceService;
    private final CoordenadaMapper coordenadaMapper;

    @PostMapping("/calcular")
    public DistanceResponseDto calcularDistancia(@RequestBody DistanceRequestDto request) {
        Coordenada origen = coordenadaMapper.toEntity(request.getOrigen());
        Coordenada destino = coordenadaMapper.toEntity(request.getDestino());
        double distancia = distanceService.calcularDistanciaEnKilometros(origen, destino);
        return DistanceResponseDto.builder()
                .distanciaEnKilometros(distancia)
                .build();
    }
}
