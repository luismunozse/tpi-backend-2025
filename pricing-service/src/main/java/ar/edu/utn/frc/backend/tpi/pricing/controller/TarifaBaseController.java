package ar.edu.utn.frc.backend.tpi.pricing.controller;

import ar.edu.utn.frc.backend.tpi.pricing.dto.TarifaBaseDto;
import ar.edu.utn.frc.backend.tpi.pricing.mapper.TarifaBaseMapper;
import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaBase;
import ar.edu.utn.frc.backend.tpi.pricing.model.TipoCamion;
import ar.edu.utn.frc.backend.tpi.pricing.service.TarifaBaseService;
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
@RequestMapping("/api/v1/tarifas/base")
@RequiredArgsConstructor
public class TarifaBaseController {

    private final TarifaBaseService tarifaBaseService;
    private final TarifaBaseMapper tarifaBaseMapper;

    @GetMapping
    public List<TarifaBaseDto> obtenerTarifas(@RequestParam(value = "tipoCamion", required = false) String tipoCamion) {
        List<TarifaBase> tarifas = tipoCamion == null
                ? tarifaBaseService.obtenerTarifas()
                : tarifaBaseService.obtenerTarifasPorTipo(TipoCamion.valueOf(tipoCamion.toUpperCase()));
        return tarifas.stream().map(tarifaBaseMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public TarifaBaseDto obtenerTarifa(@PathVariable Long id) {
        return tarifaBaseMapper.toDto(tarifaBaseService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TarifaBaseDto> crearTarifa(@RequestBody TarifaBaseDto dto) {
        TarifaBase tarifa = tarifaBaseMapper.toEntity(dto);
        TarifaBase creada = tarifaBaseService.crear(tarifa);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaBaseMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public TarifaBaseDto actualizarTarifa(@PathVariable Long id, @RequestBody TarifaBaseDto dto) {
        TarifaBase tarifa = tarifaBaseMapper.toEntity(dto);
        TarifaBase actualizada = tarifaBaseService.actualizar(id, tarifa);
        return tarifaBaseMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id) {
        tarifaBaseService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
