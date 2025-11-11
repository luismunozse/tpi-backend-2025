package ar.edu.utn.frc.backend.tpi.locations.controller;

import ar.edu.utn.frc.backend.tpi.locations.dto.CoordenadaDto;
import ar.edu.utn.frc.backend.tpi.locations.mapper.CoordenadaMapper;
import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import ar.edu.utn.frc.backend.tpi.locations.service.CoordenadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coordenadas")
@RequiredArgsConstructor
public class CoordenadaController {

    private final CoordenadaService coordenadaService;
    private final CoordenadaMapper coordenadaMapper;

    @GetMapping
    public List<CoordenadaDto> obtenerCoordenadas() {
        return coordenadaService.obtenerCoordenadas()
                .stream()
                .map(coordenadaMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CoordenadaDto obtenerCoordenada(@PathVariable Integer id) {
        Coordenada coordenada = coordenadaService.obtenerCoordenadaPorId(id);
        return coordenadaMapper.toDto(coordenada);
    }

    @PostMapping
    public ResponseEntity<CoordenadaDto> crearCoordenada(@RequestBody CoordenadaDto coordenadaDto) {
        Coordenada coordenada = coordenadaMapper.toEntity(coordenadaDto);
        Coordenada creada = coordenadaService.crearCoordenada(coordenada);
        return ResponseEntity.status(HttpStatus.CREATED).body(coordenadaMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public CoordenadaDto actualizarCoordenada(@PathVariable Integer id, @RequestBody CoordenadaDto coordenadaDto) {
        Coordenada coordenada = coordenadaMapper.toEntity(coordenadaDto);
        Coordenada actualizada = coordenadaService.actualizarCoordenada(id, coordenada);
        return coordenadaMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCoordenada(@PathVariable Integer id) {
        coordenadaService.eliminarCoordenada(id);
        return ResponseEntity.noContent().build();
    }
}
