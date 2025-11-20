package ar.edu.utn.frc.backend.tpi.api_gateway.auth.service;

import ar.edu.utn.frc.backend.tpi.api_gateway.auth.KeycloakProperties;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto.RegisterRequest;
import ar.edu.utn.frc.backend.tpi.api_gateway.auth.dto.RegisterResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Service
public class UserRegistrationService {

    private final RestTemplate restTemplate;
    private final KeycloakProperties keycloakProperties;

    public UserRegistrationService(RestTemplate restTemplate, KeycloakProperties keycloakProperties) {
        this.restTemplate = restTemplate;
        this.keycloakProperties = keycloakProperties;
    }

    public RegisterResponse register(RegisterRequest request) {
        String adminToken = obtainAdminToken();
        HttpHeaders authHeaders = bearerHeaders(adminToken);

        String userId = createUser(request, authHeaders);
        setPassword(userId, request.password(), authHeaders);
        assignDefaultRole(userId, authHeaders);

        return new RegisterResponse(userId, request.username());
    }

    private String obtainAdminToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloakProperties.getClientId());
        if (!keycloakProperties.getClientSecret().isBlank()) {
            form.add("client_secret", keycloakProperties.getClientSecret());
        }
        form.add("username", keycloakProperties.getAdminUsername());
        form.add("password", keycloakProperties.getAdminPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    keycloakProperties.getTokenUrl(),
                    new HttpEntity<>(form, headers),
                    Map.class
            );
            Map<?, ?> body = response.getBody();
            if (body == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "Keycloak devolvio una respuesta vacia al solicitar el token de admin");
            }
            Object token = body.get("access_token");
            if (token == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "No se obtuvo access_token de Keycloak");
            }
            return token.toString();
        } catch (RestClientResponseException ex) {
            throw mapAdminError("token de admin", ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error al comunicarse con Keycloak (token admin)", ex);
        }
    }

    private String createUser(RegisterRequest request, HttpHeaders authHeaders) {
        Map<String, Object> userPayload = Map.of(
                "username", request.username(),
                "email", request.email(),
                "firstName", request.firstName(),
                "lastName", request.lastName(),
                "enabled", true,
                "emailVerified", false
        );

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    keycloakProperties.getAdminUsersUrl(),
                    new HttpEntity<>(userPayload, authHeaders),
                    Void.class
            );
            URI location = response.getHeaders().getLocation();
            if (location == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "Keycloak no devolvio ubicacion del usuario creado");
            }
            String path = location.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == CONFLICT.value()) {
                throw new ResponseStatusException(CONFLICT, "El usuario ya existe", ex);
            }
            throw new ResponseStatusException(BAD_GATEWAY, "Error al crear usuario en Keycloak", ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error al comunicarse con Keycloak (crear usuario)", ex);
        }
    }

    private void setPassword(String userId, String password, HttpHeaders authHeaders) {
        Map<String, Object> payload = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );
        String url = keycloakProperties.getAdminUsersUrl() + "/" + userId + "/reset-password";
        try {
            restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(payload, authHeaders), Void.class);
        } catch (RestClientResponseException ex) {
            throw mapAdminError("setear contrasena", ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error al comunicarse con Keycloak (password)", ex);
        }
    }

    private void assignDefaultRole(String userId, HttpHeaders authHeaders) {
        String roleName = keycloakProperties.getDefaultClientRole();
        String roleUrl = keycloakProperties.getAdminRolesUrl() + "/" + roleName;

        try {
            ResponseEntity<Map> roleResponse = restTemplate.exchange(
                    roleUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(emptyAuth(authHeaders)),
                    Map.class
            );
            Map<?, ?> roleBody = roleResponse.getBody();
            if (roleBody == null || roleBody.get("id") == null || roleBody.get("name") == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "No se pudo obtener el rol " + roleName + " en Keycloak");
            }

            Map<String, Object> roleAssignment = Map.of(
                    "id", roleBody.get("id"),
                    "name", roleBody.get("name")
            );

            String mappingUrl = keycloakProperties.getAdminUsersUrl() + "/" + userId + "/role-mappings/realm";
            restTemplate.postForEntity(mappingUrl, new HttpEntity<>(Collections.singletonList(roleAssignment), authHeaders), Void.class);
        } catch (RestClientResponseException ex) {
            if (HttpStatus.resolve(ex.getStatusCode().value()) == NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El rol " + roleName + " no existe en Keycloak", ex);
            }
            throw mapAdminError("asignar rol", ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(BAD_GATEWAY, "Error al comunicarse con Keycloak (roles)", ex);
        }
    }

    private HttpHeaders bearerHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private HttpHeaders emptyAuth(HttpHeaders authHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(authHeaders);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private ResponseStatusException mapAdminError(String contexto, RestClientResponseException ex) {
        int statusCode = ex.getStatusCode().value();
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == UNAUTHORIZED || status == FORBIDDEN) {
            return new ResponseStatusException(UNAUTHORIZED, "No autorizado en Keycloak al " + contexto, ex);
        }
        if (status == BAD_REQUEST || status == CONFLICT) {
            return new ResponseStatusException(status, ex.getResponseBodyAsString(), ex);
        }
        return new ResponseStatusException(BAD_GATEWAY, "Keycloak respondio " + statusCode + " al " + contexto, ex);
    }
}
