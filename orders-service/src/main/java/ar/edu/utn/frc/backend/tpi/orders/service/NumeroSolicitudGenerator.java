package ar.edu.utn.frc.backend.tpi.orders.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class NumeroSolicitudGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String generarNumero() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "SOL-" + timestamp + "-" + random;
    }
}
