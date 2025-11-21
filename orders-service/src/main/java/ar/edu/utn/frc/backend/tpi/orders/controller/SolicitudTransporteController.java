package ar.edu.utn.frc.backend.tpi.orders.controller;

import ar.edu.utn.frc.backend.tpi.orders.dto.ActualizarEstadoSolicitudDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.AsignarCamionRequestDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.CalcularRutasAlternativasRequestDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.CalculoSolicitudResponseDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.CrearSolicitudRequestDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.RutasAlternativasResponseDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.SolicitudTransporteDto;
import ar.edu.utn.frc.backend.tpi.orders.dto.TramoDto;
import ar.edu.utn.frc.backend.tpi.orders.service.RutasAlternativasService;
import ar.edu.utn.frc.backend.tpi.orders.service.SolicitudTransporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
public class SolicitudTransporteController {

    private final SolicitudTransporteService solicitudService;
    private final RutasAlternativasService rutasAlternativasService;

    @PostMapping
    public ResponseEntity<SolicitudTransporteDto> crearSolicitud(@RequestBody @Valid CrearSolicitudRequestDto request) {
        SolicitudTransporteDto creada = solicitudService.crearSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public List<SolicitudTransporteDto> listar(@RequestParam(value = "estado", required = false) String estado) {
        return solicitudService.listar(Optional.ofNullable(estado));
    }

    @GetMapping("/{id}")
    public SolicitudTransporteDto obtener(@PathVariable Long id) {
        return solicitudService.obtenerPorId(id);
    }

    @PostMapping("/{id}/estado")
    public SolicitudTransporteDto actualizarEstado(@PathVariable Long id,
                                                   @RequestBody @Valid ActualizarEstadoSolicitudDto request) {
        return solicitudService.actualizarEstado(id, request.getEstado());
    }

    @PostMapping("/{id}/tramos/{tramoId}/asignacion")
    public TramoDto asignarCamion(@PathVariable Long id,
                                  @PathVariable Long tramoId,
                                  @RequestBody @Valid AsignarCamionRequestDto request) {
        return solicitudService.asignarCamion(id, tramoId, request);
    }

    @PostMapping("/{id}/tramos/{tramoId}/inicio")
    public TramoDto iniciarTramo(@PathVariable Long id, @PathVariable Long tramoId) {
        return solicitudService.registrarInicioTramo(id, tramoId);
    }

    @PostMapping("/{id}/tramos/{tramoId}/fin")
    public TramoDto finalizarTramo(@PathVariable Long id,
                                   @PathVariable Long tramoId,
                                   @RequestParam(value = "costoReal", required = false) Double costoReal) {
        return solicitudService.registrarFinTramo(id, tramoId, costoReal);
    }

    @PostMapping("/{id}/recalcular")
    public CalculoSolicitudResponseDto recalcular(@PathVariable Long id) {
        return solicitudService.recalcularCosto(id);
    }

    @GetMapping("/{id}/tracking")
    public List<TramoDto> obtenerTracking(@PathVariable Long id) {
        return solicitudService.obtenerTracking(id);
    }

    @PostMapping("/calcular-alternativas")
    public ResponseEntity<RutasAlternativasResponseDto> calcularRutasAlternativas(
            @RequestBody @Valid CalcularRutasAlternativasRequestDto request) {
        RutasAlternativasResponseDto alternativas = rutasAlternativasService.calcularRutasAlternativas(request);
        return ResponseEntity.ok(alternativas);
    }
}
