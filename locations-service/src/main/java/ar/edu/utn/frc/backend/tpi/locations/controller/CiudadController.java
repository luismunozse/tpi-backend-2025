package ar.edu.utn.frc.backend.tpi.locations.controller;

import ar.edu.utn.frc.backend.tpi.locations.dto.CiudadDto;
import ar.edu.utn.frc.backend.tpi.locations.mapper.CiudadMapper;
import ar.edu.utn.frc.backend.tpi.locations.model.Ciudad;
import ar.edu.utn.frc.backend.tpi.locations.service.CiudadService;
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
@RequestMapping("/api/v1/ciudades")
@RequiredArgsConstructor
public class CiudadController {

    private final CiudadService ciudadService;
    private final CiudadMapper ciudadMapper;

    @GetMapping
    public List<CiudadDto> obtenerCiudades() {
        return ciudadService.obtenerCiudades()
                .stream()
                .map(ciudadMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CiudadDto obtenerCiudad(@PathVariable Integer id) {
        Ciudad ciudad = ciudadService.obtenerCiudadPorId(id);
        return ciudadMapper.toDto(ciudad);
    }

    @PostMapping
    public ResponseEntity<CiudadDto> crearCiudad(@RequestBody CiudadDto ciudadDto) {
        Ciudad ciudad = ciudadMapper.toEntity(ciudadDto);
        Ciudad creada = ciudadService.crearCiudad(ciudad);
        return ResponseEntity.status(HttpStatus.CREATED).body(ciudadMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public CiudadDto actualizarCiudad(@PathVariable Integer id, @RequestBody CiudadDto ciudadDto) {
        Ciudad ciudad = ciudadMapper.toEntity(ciudadDto);
        Ciudad actualizada = ciudadService.actualizarCiudad(id, ciudad);
        return ciudadMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCiudad(@PathVariable Integer id) {
        ciudadService.eliminarCiudad(id);
        return ResponseEntity.noContent().build();
    }
}
