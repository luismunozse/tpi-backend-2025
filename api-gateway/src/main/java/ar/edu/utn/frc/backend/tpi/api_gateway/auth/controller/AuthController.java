package ar.edu.utn.frc.backend.tpi.api_gateway.auth.controller;

import ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto.KeycloakTokenResponse;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto.LoginRequest;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto.RegisterRequest;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto.RegisterResponse;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.service.KeycloakAuthService;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakAuthService keycloakAuthService;
    private final UserRegistrationService userRegistrationService;

    public AuthController(KeycloakAuthService keycloakAuthService,
                          UserRegistrationService userRegistrationService) {
        this.keycloakAuthService = keycloakAuthService;
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/login")
    public ResponseEntity<KeycloakTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(keycloakAuthService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRegistrationService.register(request));
    }
}
