package ar.edu.utn.frc.backend.tpi.locations.controller;

import ar.edu.utn.frc.backend.tpi.locations.dto.ProvinciaDto;
import ar.edu.utn.frc.backend.tpi.locations.mapper.ProvinciaMapper;
import ar.edu.utn.frc.backend.tpi.locations.model.Provincia;
import ar.edu.utn.frc.backend.tpi.locations.service.ProvinciaService;
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
@RequestMapping("/api/v1/provincias")
@RequiredArgsConstructor
public class ProvinciaController {

    private final ProvinciaService provinciaService;
    private final ProvinciaMapper provinciaMapper;

    @GetMapping
    public List<ProvinciaDto> obtenerProvincias() {
        return provinciaService.obtenerProvincias()
                .stream()
                .map(provinciaMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ProvinciaDto obtenerProvincia(@PathVariable Long id) {
        Provincia provincia = provinciaService.obtenerProvinciaPorId(id);
        return provinciaMapper.toDto(provincia);
    }

    @PostMapping
    public ResponseEntity<ProvinciaDto> crearProvincia(@RequestBody ProvinciaDto provinciaDto) {
        Provincia provincia = provinciaMapper.toEntity(provinciaDto);
        Provincia creada = provinciaService.crearProvincia(provincia);
        return ResponseEntity.status(HttpStatus.CREATED).body(provinciaMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public ProvinciaDto actualizarProvincia(@PathVariable Long id, @RequestBody ProvinciaDto provinciaDto) {
        Provincia provincia = provinciaMapper.toEntity(provinciaDto);
        Provincia actualizada = provinciaService.actualizarProvincia(id, provincia);
        return provinciaMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProvincia(@PathVariable Long id) {
        provinciaService.eliminarProvincia(id);
        return ResponseEntity.noContent().build();
    }
}
