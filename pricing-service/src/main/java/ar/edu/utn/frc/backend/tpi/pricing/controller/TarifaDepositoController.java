package ar.edu.utn.frc.backend.tpi.pricing.controller;

import ar.edu.utn.frc.backend.tpi.pricing.dto.TarifaDepositoDto;
import ar.edu.utn.frc.backend.tpi.pricing.mapper.TarifaDepositoMapper;
import ar.edu.utn.frc.backend.tpi.pricing.model.TarifaDeposito;
import ar.edu.utn.frc.backend.tpi.pricing.service.TarifaDepositoService;
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
@RequestMapping("/api/v1/tarifas/depositos")
@RequiredArgsConstructor
public class TarifaDepositoController {

    private final TarifaDepositoService tarifaDepositoService;
    private final TarifaDepositoMapper tarifaDepositoMapper;

    @GetMapping
    public List<TarifaDepositoDto> obtenerTarifas() {
        return tarifaDepositoService.obtenerTarifas()
                .stream()
                .map(tarifaDepositoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public TarifaDepositoDto obtenerTarifa(@PathVariable Long id) {
        return tarifaDepositoMapper.toDto(tarifaDepositoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TarifaDepositoDto> crearTarifa(@RequestBody TarifaDepositoDto dto) {
        TarifaDeposito tarifa = tarifaDepositoMapper.toEntity(dto);
        TarifaDeposito creada = tarifaDepositoService.crear(tarifa);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarifaDepositoMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public TarifaDepositoDto actualizarTarifa(@PathVariable Long id, @RequestBody TarifaDepositoDto dto) {
        TarifaDeposito tarifa = tarifaDepositoMapper.toEntity(dto);
        TarifaDeposito actualizada = tarifaDepositoService.actualizar(id, tarifa);
        return tarifaDepositoMapper.toDto(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarifa(@PathVariable Long id) {
        tarifaDepositoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
