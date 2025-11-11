package ar.edu.utn.frc.backend.tpi.fleet.controller;

import ar.edu.utn.frc.backend.tpi.fleet.dto.ActualizarEstadoCamionDto;
import ar.edu.utn.frc.backend.tpi.fleet.dto.CamionDto;
import ar.edu.utn.frc.backend.tpi.fleet.mapper.CamionMapper;
import ar.edu.utn.frc.backend.tpi.fleet.model.Camion;
import ar.edu.utn.frc.backend.tpi.fleet.model.EstadoCamion;
import ar.edu.utn.frc.backend.tpi.fleet.service.CamionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/camiones")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;
    private final CamionMapper camionMapper;

    @GetMapping
    public List<CamionDto> obtenerCamiones(@RequestParam(value = "estado", required = false) String estado) {
        List<Camion> camiones;
        if (estado == null) {
            camiones = camionService.obtenerCamiones();
        } else {
            camiones = camionService.obtenerCamionesPorEstado(EstadoCamion.valueOf(estado.toUpperCase()));
        }
        return camiones.stream().map(camionMapper::toDto).toList();
    }

    @GetMapping("/disponibles")
    public List<CamionDto> obtenerDisponibles(@RequestParam(value = "peso", required = false) Double peso,
                                              @RequestParam(value = "volumen", required = false) Double volumen) {
        List<Camion> camiones;
        if (peso != null && volumen != null) {
            camiones = camionService.buscarDisponiblesPorCapacidad(peso, volumen);
        } else {
            camiones = camionService.obtenerCamionesPorEstado(EstadoCamion.DISPONIBLE);
        }
        return camiones.stream().map(camionMapper::toDto).toList();
    }

    @GetMapping("/ocupados")
    public List<CamionDto> obtenerOcupados() {
        return camionService.obtenerCamionesPorEstado(EstadoCamion.OCUPADO)
                .stream()
                .map(camionMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CamionDto obtenerCamion(@PathVariable Long id) {
        return camionMapper.toDto(camionService.obtenerCamionPorId(id));
    }

    @PostMapping
    public ResponseEntity<CamionDto> crearCamion(@RequestBody CamionDto camionDto) {
        Camion camion = camionMapper.toEntity(camionDto);
        if (camion.getTransportista() == null && camionDto.getTransportista() != null && camionDto.getTransportista().getId() != null) {
            camion.setTransportista(camionMapper.referenciaTransportista(camionDto.getTransportista().getId()));
        }
        Camion creado = camionService.crearCamion(camion);
        return ResponseEntity.status(HttpStatus.CREATED).body(camionMapper.toDto(creado));
    }

    @PutMapping("/{id}")
    public CamionDto actualizarCamion(@PathVariable Long id, @RequestBody CamionDto camionDto) {
        Camion camion = camionMapper.toEntity(camionDto);
        if (camion.getTransportista() == null && camionDto.getTransportista() != null && camionDto.getTransportista().getId() != null) {
            camion.setTransportista(camionMapper.referenciaTransportista(camionDto.getTransportista().getId()));
        }
        Camion actualizado = camionService.actualizarCamion(id, camion);
        return camionMapper.toDto(actualizado);
    }

    @PatchMapping("/{id}/estado")
    public CamionDto actualizarEstado(@PathVariable Long id, @RequestBody ActualizarEstadoCamionDto request) {
        Camion actualizado = camionService.actualizarEstado(id, request.getEstado());
        return camionMapper.toDto(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCamion(@PathVariable Long id) {
        camionService.eliminarCamion(id);
        return ResponseEntity.noContent().build();
    }
}
