package ar.edu.utn.frc.backend.tpi.pricing.controller;

import ar.edu.utn.frc.backend.tpi.pricing.dto.RecargoTarifaDto;
import ar.edu.utn.frc.backend.tpi.pricing.mapper.RecargoTarifaMapper;
import ar.edu.utn.frc.backend.tpi.pricing.model.RecargoTarifa;
import ar.edu.utn.frc.backend.tpi.pricing.service.RecargoTarifaService;
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
@RequestMapping("/api/v1/recargos")
@RequiredArgsConstructor
public class RecargoTarifaController {

    private final RecargoTarifaService recargoTarifaService;
    private final RecargoTarifaMapper recargoTarifaMapper;

    @GetMapping
    public List<RecargoTarifaDto> obtenerRecargos() {
        return recargoTarifaService.obtenerRecargos()
                .stream()
                .map(recargoTarifaMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public RecargoTarifaDto obtenerRecargo(@PathVariable Long id) {
        return recargoTarifaMapper.toDto(recargoTarifaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RecargoTarifaDto> crearRecargo(@RequestBody RecargoTarifaDto dto) {
        RecargoTarifa recargo = recargoTarifaMapper.toEntity(dto);
        RecargoTarifa creado = recargoTarifaService.crear(recargo);
        return ResponseEntity.status(HttpStatus.CREATED).body(recargoTarifaMapper.toDto(creado));
    }

    @PutMapping("/{id}")
    public RecargoTarifaDto actualizarRecargo(@PathVariable Long id, @RequestBody RecargoTarifaDto dto) {
        RecargoTarifa recargo = recargoTarifaMapper.toEntity(dto);
        RecargoTarifa actualizado = recargoTarifaService.actualizar(id, recargo);
        return recargoTarifaMapper.toDto(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRecargo(@PathVariable Long id) {
        recargoTarifaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
