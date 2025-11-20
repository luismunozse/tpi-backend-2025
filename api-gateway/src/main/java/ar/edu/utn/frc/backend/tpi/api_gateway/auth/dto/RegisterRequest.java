package ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank(message = "El usuario es obligatorio")
        String username,
        @NotBlank(message = "La contraseña es obligatoria")
        String password,
        @Email(message = "El email no es válido")
        @NotBlank(message = "El email es obligatorio")
        String email,
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        String lastName
) {
}
