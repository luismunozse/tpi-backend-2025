package ar.edu.utn.frc.backend.tpi.api_gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // Endpoints públicos de autenticación
                        .requestMatchers("/auth/login").permitAll()
                        // Solo admin puede registrar nuevos usuarios
                        .requestMatchers("/auth/register").hasRole("ADMIN")
                        // Autorización por endpoint según roles del enunciado
                        .requestMatchers("/api/ordenes/**").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers("/api/fleet/**").hasRole("ADMIN")
                        .requestMatchers("/api/pricing/**").hasRole("ADMIN")
                        .requestMatchers("/api/locations/**").hasAnyRole("ADMIN", "TRANSPORTISTA")
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return converter;
    }

    /**
     * Decoder JWT personalizado que no valida el issuer.
     * Esto es necesario porque Keycloak usa URLs internas de Docker (http://keycloak:8080)
     * pero se accede desde el host (http://localhost:8081).
     *
     * El Gateway corre dentro de Docker, por lo que debe usar el nombre del servicio 'keycloak:8080'
     * para conectarse al servidor de Keycloak en la red interna de Docker.
     */
    @Bean
    public org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder() {
        // Usar el nombre del servicio de Docker, no localhost
        String jwkSetUri = "http://keycloak:8080/realms/tpi-backend/protocol/openid-connect/certs";

        org.springframework.security.oauth2.jwt.NimbusJwtDecoder jwtDecoder =
            org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // Desactivar validación de issuer para entorno de desarrollo
        // El token dice issuer="http://keycloak:8080/realms/tpi-backend" pero esto no es alcanzable desde fuera de Docker
        org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> withTimestamp =
            new org.springframework.security.oauth2.jwt.JwtTimestampValidator();

        jwtDecoder.setJwtValidator(withTimestamp);

        return jwtDecoder;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        log.debug("=== EXTRACTING AUTHORITIES ===");
        log.debug("JWT Subject: {}", jwt.getSubject());
        log.debug("JWT Claims: {}", jwt.getClaims());

        List<String> roles = extractRealmRoles(jwt);

        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.debug("Extracted authorities: {}", authorities);
        log.debug("=== END EXTRACTING AUTHORITIES ===");

        return authorities;
    }

    private List<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        log.debug("realm_access claim: {}", realmAccess);

        if (realmAccess == null) {
            log.warn("No realm_access claim found in JWT");
            return List.of();
        }

        Object roles = realmAccess.get("roles");
        log.debug("Roles from realm_access: {}", roles);

        if (roles instanceof List<?> list) {
            List<String> extractedRoles = list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
            log.debug("Extracted realm roles: {}", extractedRoles);
            return extractedRoles;
        }

        log.warn("roles claim is not a List, found type: {}", roles != null ? roles.getClass() : "null");
        return List.of();
    }
}
