package ar.edu.utn.frc.backend.tpi.locations.controller;

import ar.edu.utn.frc.backend.tpi.locations.dto.DepositoDto;
import ar.edu.utn.frc.backend.tpi.locations.mapper.DepositoMapper;
import ar.edu.utn.frc.backend.tpi.locations.model.Deposito;
import ar.edu.utn.frc.backend.tpi.locations.service.DepositoService;
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
@RequestMapping("/api/v1/depositos")
@RequiredArgsConstructor
public class DepositoController {

    private final DepositoService depositoService;
    private final DepositoMapper depositoMapper;

    @GetMapping
    public List<DepositoDto> obtenerDepositos(@RequestParam(value = "ciudadId", required = false) Integer ciudadId) {
        List<Deposito> depositos = ciudadId == null
                ? depositoService.obtenerDepositos()
                : depositoService.obtenerDepositosPorCiudad(ciudadId);
        return depositos.stream()
                .map(depositoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public DepositoDto obtenerDeposito(@PathVariable Integer id) {
        Deposito deposito = depositoService.obtenerDepositoPorId(id);
        return depositoMapper.toDto(deposito);
    }

    @PostMapping
    public ResponseEntity<DepositoDto> crearDeposito(@RequestBody DepositoDto depositoDto) {
        Deposito deposito = depositoMapper.toEntity(depositoDto);
        Deposito creado = depositoService.crearDeposito(deposito);
        return ResponseEntity.status(HttpStatus.CREATED).body(depositoMapper.toDto(creado));
    }

    @PutMapping("/{id}")
    public DepositoDto actualizarDeposito(@PathVariable Integer id, @RequestBody DepositoDto depositoDto) {
        Deposito deposito = depositoMapper.toEntity(depositoDto);
        Deposito actualizado = depositoService.actualizarDeposito(id, deposito);
        return depositoMapper.toDto(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDeposito(@PathVariable Integer id) {
        depositoService.eliminarDeposito(id);
        return ResponseEntity.noContent().build();
    }
}
