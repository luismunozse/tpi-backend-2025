package ar.edu.utn.frc.backend.tpi.api_gateway.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Interceptor que extrae información del usuario autenticado del JWT
 * y la almacena en atributos de request para ser propagada por el Gateway.
 *
 * Según el enunciado: "Que el userId deje de ser un string arbitrario
 * y pase a ser el usuario autenticado"
 *
 * Los microservicios recibirán esta información mediante headers HTTP:
 * - X-User-Id: ID único del usuario (claim 'sub' del JWT)
 * - X-Username: Nombre de usuario (claim 'preferred_username')
 * - X-User-Email: Email del usuario (claim 'email')
 * - X-User-Roles: Roles del usuario separados por coma
 */
@Component
public class UserContextPropagationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserContextPropagationInterceptor.class);

    public static final String USER_ID_ATTRIBUTE = "X-User-Id";
    public static final String USERNAME_ATTRIBUTE = "X-Username";
    public static final String USER_EMAIL_ATTRIBUTE = "X-User-Email";
    public static final String USER_ROLES_ATTRIBUTE = "X-User-Roles";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            // Extraer información del usuario del JWT
            String userId = jwt.getSubject(); // El 'sub' claim contiene el ID único del usuario en Keycloak
            String username = jwt.getClaimAsString("preferred_username");
            String email = jwt.getClaimAsString("email");
            String roles = extractRoles(jwt);

            // Almacenar en atributos del request
            if (userId != null && !userId.isEmpty()) {
                request.setAttribute(USER_ID_ATTRIBUTE, userId);
                log.debug("Usuario autenticado: userId={}, username={}", userId, username);
            }
            if (username != null && !username.isEmpty()) {
                request.setAttribute(USERNAME_ATTRIBUTE, username);
            }
            if (email != null && !email.isEmpty()) {
                request.setAttribute(USER_EMAIL_ATTRIBUTE, email);
            }
            if (!roles.isEmpty()) {
                request.setAttribute(USER_ROLES_ATTRIBUTE, roles);
                log.debug("Roles del usuario: {}", roles);
            }
        }

        return true;
    }

    /**
     * Extrae los roles del token JWT desde el claim realm_access.roles
     */
    private String extractRoles(Jwt jwt) {
        try {
            Object realmAccessObj = jwt.getClaim("realm_access");
            if (realmAccessObj instanceof Map<?, ?> realmAccess) {
                Object rolesObj = realmAccess.get("roles");
                if (rolesObj instanceof List<?> rolesList) {
                    return String.join(",", rolesList.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .toList());
                }
            }
        } catch (Exception e) {
            log.warn("Error al extraer roles del JWT", e);
        }
        return "";
    }
}
