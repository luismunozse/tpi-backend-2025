package ar.edu.utn.frc.backend.tpi.fleet.controller;

import ar.edu.utn.frc.backend.tpi.fleet.dto.MantenimientoDto;
import ar.edu.utn.frc.backend.tpi.fleet.mapper.MantenimientoMapper;
import ar.edu.utn.frc.backend.tpi.fleet.model.Mantenimiento;
import ar.edu.utn.frc.backend.tpi.fleet.service.MantenimientoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mantenimientos")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;
    private final MantenimientoMapper mantenimientoMapper;

    @GetMapping
    public List<MantenimientoDto> obtenerMantenimientos(@RequestParam(value = "camionId", required = false) Long camionId) {
        List<Mantenimiento> mantenimientos = camionId == null
                ? mantenimientoService.obtenerMantenimientos()
                : mantenimientoService.obtenerMantenimientosPorCamion(camionId);
        return mantenimientos.stream().map(mantenimientoMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public MantenimientoDto obtenerMantenimiento(@PathVariable Long id) {
        Mantenimiento mantenimiento = mantenimientoService.obtenerMantenimientoPorId(id);
        return mantenimientoMapper.toDto(mantenimiento);
    }

    @PostMapping
    public ResponseEntity<MantenimientoDto> crearMantenimiento(@RequestBody MantenimientoDto mantenimientoDto) {
        Mantenimiento mantenimiento = mantenimientoMapper.toEntity(mantenimientoDto);
        Mantenimiento creado = mantenimientoService.crearMantenimiento(mantenimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimientoMapper.toDto(creado));
    }

    @PutMapping("/{id}")
    public MantenimientoDto actualizarMantenimiento(@PathVariable Long id, @RequestBody MantenimientoDto mantenimientoDto) {
        Mantenimiento mantenimiento = mantenimientoMapper.toEntity(mantenimientoDto);
        Mantenimiento actualizado = mantenimientoService.actualizarMantenimiento(id, mantenimiento);
        return mantenimientoMapper.toDto(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMantenimiento(@PathVariable Long id) {
        mantenimientoService.eliminarMantenimiento(id);
        return ResponseEntity.noContent().build();
    }
}
