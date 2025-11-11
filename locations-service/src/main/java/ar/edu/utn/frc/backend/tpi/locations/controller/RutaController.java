package ar.edu.utn.frc.backend.tpi.locations.controller;

import ar.edu.utn.frc.backend.tpi.locations.dto.RutaDto;
import ar.edu.utn.frc.backend.tpi.locations.mapper.RutaMapper;
import ar.edu.utn.frc.backend.tpi.locations.model.Ruta;
import ar.edu.utn.frc.backend.tpi.locations.service.RutaService;
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
@RequestMapping("/api/v1/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;
    private final RutaMapper rutaMapper;

    @GetMapping
    public List<RutaDto> obtenerRutas() {
        return rutaService.obtenerRutas().stream()
                .map(rutaMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public RutaDto obtenerRuta(@PathVariable Long id) {
        Ruta ruta = rutaService.obtenerRutaPorId(id);
        return rutaMapper.toDto(ruta);
    }

    @PostMapping
    public ResponseEntity<RutaDto> crearRuta(@RequestBody RutaDto rutaDto) {
        Ruta ruta = rutaMapper.toEntity(rutaDto);
        Ruta creada = rutaService.crearRuta(ruta);
        return ResponseEntity.status(HttpStatus.CREATED).body(rutaMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public RutaDto actualizarRuta(@PathVariable Long id, @RequestBody RutaDto rutaDto) {
        Ruta ruta = rutaMapper.toEntity(rutaDto);
        Ruta actualizada = rutaService.actualizarRuta(id, ruta);
        return rutaMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaService.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }
}
