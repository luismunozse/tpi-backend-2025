package ar.edu.utn.frc.backend.tpi.fleet.controller;

import ar.edu.utn.frc.backend.tpi.fleet.dto.TransportistaDto;
import ar.edu.utn.frc.backend.tpi.fleet.mapper.TransportistaMapper;
import ar.edu.utn.frc.backend.tpi.fleet.model.Transportista;
import ar.edu.utn.frc.backend.tpi.fleet.service.TransportistaService;
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
@RequestMapping("/api/v1/transportistas")
@RequiredArgsConstructor
public class TransportistaController {

    private final TransportistaService transportistaService;
    private final TransportistaMapper transportistaMapper;

    @GetMapping
    public List<TransportistaDto> obtenerTransportistas() {
        return transportistaService.obtenerTransportistas()
                .stream()
                .map(transportistaMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public TransportistaDto obtenerTransportista(@PathVariable Long id) {
        Transportista transportista = transportistaService.obtenerTransportistaPorId(id);
        return transportistaMapper.toDto(transportista);
    }

    @PostMapping
    public ResponseEntity<TransportistaDto> crearTransportista(@RequestBody TransportistaDto transportistaDto) {
        Transportista transportista = transportistaMapper.toEntity(transportistaDto);
        Transportista creado = transportistaService.crearTransportista(transportista);
        return ResponseEntity.status(HttpStatus.CREATED).body(transportistaMapper.toDto(creado));
    }

    @PutMapping("/{id}")
    public TransportistaDto actualizarTransportista(@PathVariable Long id, @RequestBody TransportistaDto transportistaDto) {
        Transportista transportista = transportistaMapper.toEntity(transportistaDto);
        Transportista actualizado = transportistaService.actualizarTransportista(id, transportista);
        return transportistaMapper.toDto(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTransportista(@PathVariable Long id) {
        transportistaService.eliminarTransportista(id);
        return ResponseEntity.noContent().build();
    }
}
