# Alineación del Proyecto con el Enunciado - Keycloak y Seguridad

## 📋 Objetivo del Enunciado

Según el enunciado y el documento "Paso a Paso - Keycloak Container", el objetivo de la integración con Keycloak es:

> **Nuestro objetivo no es hacer un esquema de roles complejo, sino:**
> 1. Que cada usuario pueda autenticarse en Keycloak
> 2. Que el gateway valide el token
> 3. **Que el userId deje de ser un string arbitrario y pase a ser el usuario autenticado**

> **La clave didáctica aquí es que no modificamos los microservicios para entender OAuth2: ellos siguen viendo solo HTTP + headers. El que entiende de seguridad es el gateway.**

---

## ✅ Implementación Realizada

### 1. Autenticación con Keycloak ✓

**Ubicación**: `api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/auth/`

- **Endpoint de Login**: `POST /auth/login`
  - Implementado en [AuthController.java](api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/auth/controller/AuthController.java:30-32)
  - Servicio: [KeycloakAuthService.java](api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/auth/service/KeycloakAuthService.java:30-61)
  - Usa grant_type `password` (Resource Owner Password Credentials)
  - Retorna `access_token`, `refresh_token`, `expires_in`, etc.

### 2. Validación de Token en el Gateway ✓

**Ubicación**: `api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/SecurityConfig.java`

- Configurado como **OAuth2 Resource Server**
- Valida tokens JWT mediante JWKS endpoint de Keycloak
- Extrae roles del claim `realm_access.roles`
- Configuración:
  ```yaml
  spring:
    security:
      oauth2:
        resourceserver:
          jwt:
            jwk-set-uri: http://keycloak:8080/realms/tpi-backend/protocol/openid-connect/certs
  ```

### 3. userId desde el Usuario Autenticado ✓

**Problema Original**: El userId era un string arbitrario enviado por el cliente

**Solución Implementada**: El Gateway extrae automáticamente el userId del JWT

**Componentes**:

1. **Interceptor HTTP** - [UserContextPropagationInterceptor.java](api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/interceptor/UserContextPropagationInterceptor.java)
   - Extrae información del JWT del usuario autenticado
   - Almacena en atributos del request:
     - `X-User-Id`: claim `sub` (ID único en Keycloak)
     - `X-Username`: claim `preferred_username`
     - `X-User-Email`: claim `email`
     - `X-User-Roles`: roles del usuario

2. **Propagación al Gateway** - [application.yaml](api-gateway/src/main/resources/application.yaml:14)
   ```yaml
   default-filters:
     - AddRequestHeadersIfNotPresent=X-User-Id:{request.getAttribute('X-User-Id')},X-Username:{request.getAttribute('X-Username')},X-User-Email:{request.getAttribute('X-User-Email')},X-User-Roles:{request.getAttribute('X-User-Roles')}
   ```

3. **Configuración del Interceptor** - [WebMvcConfig.java](api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/config/WebMvcConfig.java)
   - Registrado para todas las rutas `/api/**`
   - Excluye endpoints públicos (`/auth/**`, `/actuator/**`)

**Resultado**: Los microservicios reciben headers HTTP con información del usuario autenticado, NO strings arbitrarios.

### 4. Microservicios sin OAuth2 ✓

**Cumplimiento del Principio Didáctico**: ✅

Los microservicios (orders-service, fleet-service, locations-service, pricing-service) tienen configuración de seguridad simple:

```java
// Ejemplo: orders-service/src/main/java/.../config/SecurityConfig.java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // ← No validan OAuth2
        )
        .build();
}
```

**Justificación**: Los microservicios confían en el Gateway. Solo accesibles desde la red Docker interna.

---

## 🎭 Roles del Sistema

Según el enunciado, el sistema tiene **3 roles**:

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| `cliente` | Usuarios finales que solicitan transporte | Crear y consultar sus solicitudes |
| `admin` | Administradores/operadores | Gestión completa del sistema, crear usuarios |
| `transportista` | Choferes que ejecutan traslados | Ver ubicaciones, registrar inicio/fin de tramos |

### Configuración en Keycloak

1. **Realm**: `tpi-backend`
2. **Client**: `tpi-backend-client` (public client)
3. **Roles de Realm**:
   - `cliente`
   - `admin`
   - `transportista`

### Autorización en el Gateway

**Ubicación**: [SecurityConfig.java](api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/SecurityConfig.java:30-43)

```java
.authorizeHttpRequests(auth -> auth
    // Endpoints públicos
    .requestMatchers("/auth/login").permitAll()

    // Solo admin puede registrar nuevos usuarios
    .requestMatchers("/auth/register").hasRole("ADMIN")

    // Autorización por endpoint según roles
    .requestMatchers("/api/ordenes/**").hasAnyRole("CLIENTE", "ADMIN")
    .requestMatchers("/api/fleet/**").hasRole("ADMIN")
    .requestMatchers("/api/pricing/**").hasRole("ADMIN")
    .requestMatchers("/api/locations/**").hasAnyRole("ADMIN", "TRANSPORTISTA")

    .anyRequest().authenticated()
);
```

### Funcionalidad de Registro

**Endpoint**: `POST /auth/register` (protegido, solo ADMIN)

- Permite a administradores crear nuevos usuarios `cliente`
- Implementado en [UserRegistrationService.java](api-gateway/src/main/java/ar/edu/utn/frc/backend/tpi/api_gateway/auth/service/UserRegistrationService.java)
- Usa Keycloak Admin API
- Asigna rol por defecto: `cliente`

---

## 📝 Resumen de Cumplimiento

| Requisito del Enunciado | Estado | Ubicación |
|-------------------------|--------|-----------|
| ✅ Usuarios pueden autenticarse en Keycloak | Implementado | `POST /auth/login` |
| ✅ Gateway valida token JWT | Implementado | `SecurityConfig.java` |
| ✅ userId viene del usuario autenticado | Implementado | `UserContextPropagationInterceptor` + headers HTTP |
| ✅ Gateway es el único que entiende OAuth2 | Implementado | Microservicios con `.permitAll()` |
| ✅ Microservicios reciben solo HTTP + headers | Implementado | Headers: `X-User-Id`, `X-Username`, etc. |
| ✅ Roles: cliente, admin, transportista | Implementado | Configuración en Keycloak + `SecurityConfig` |
| ✅ Admin puede crear usuarios cliente | Implementado | `POST /auth/register` (protegido) |

---

## 🔧 Configuración Requerida en Keycloak

Para que el sistema funcione correctamente, seguir estos pasos en Keycloak:

1. **Crear Realm**: `tpi-backend`

2. **Crear Client**:
   - Client ID: `tpi-backend-client`
   - Client type: `Public`
   - Standard flow: Habilitado
   - Direct access grants: Habilitado ✓ (para password grant)
   - Valid redirect URIs: `http://localhost:8080/*`

3. **Crear Roles de Realm**:
   - `cliente`
   - `admin`
   - `transportista`

4. **Crear Usuarios de Prueba**:
   ```
   Username: admin01
   Password: Clave123
   Email: admin@example.com
   Role: admin

   Username: cliente01
   Password: Clave123
   Email: cliente@example.com
   Role: cliente

   Username: transportista01
   Password: Clave123
   Email: transportista@example.com
   Role: transportista
   ```

5. **Configurar Variables de Entorno** (`.env.local`):
   ```bash
   KEYCLOAK_JWKS_URI=http://keycloak:8080/realms/tpi-backend/protocol/openid-connect/certs
   KEYCLOAK_REALM=tpi-backend
   KEYCLOAK_CLIENT_ID=tpi-backend-client
   KEYCLOAK_CLIENT_SECRET=
   ```

---

## 🚀 Flujo de Autenticación

### 1. Login del Usuario

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "cliente01",
  "password": "Clave123"
}
```

**Respuesta**:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer"
}
```

### 2. Consumir API Autenticada

```http
GET http://localhost:8080/api/ordenes/solicitudes
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**El Gateway automáticamente**:
1. Valida el token JWT
2. Extrae información del usuario
3. Propaga headers a Orders Service:
   ```
   X-User-Id: 9c3a4b12-8e5d-4f2a-b7c1-3d8e9f1a2b3c
   X-Username: cliente01
   X-User-Email: cliente@example.com
   X-User-Roles: cliente
   ```

### 3. Orders Service Recibe Headers

El microservicio puede acceder a la información del usuario:

```java
@GetMapping("/solicitudes")
public ResponseEntity<List<SolicitudDTO>> listar(
    @RequestHeader(value = "X-User-Id", required = false) String userId,
    @RequestHeader(value = "X-Username", required = false) String username,
    @RequestHeader(value = "X-User-Roles", required = false) String roles
) {
    // userId ya NO es arbitrario, viene del JWT validado por el Gateway
    log.info("Usuario autenticado: {} ({}), roles: {}", username, userId, roles);

    // Filtrar solicitudes por usuario si es cliente
    if (roles != null && roles.contains("cliente")) {
        return service.listarPorUsuario(userId);
    }

    return service.listarTodas();
}
```

---

## 📚 Documentación Adicional

- **Instructivo Keycloak**: [Paso a Paso - Keycloak Container.pdf](Paso%20a%20Paso%20-%20Keycloak%20Container.pdf)
- **Enunciado TPI**: [Enunciado TPI - 2025.pdf](Enunciado%20TPI%20-%202025.pdf)
- **README Principal**: [README.md](README.md)

---

## ✨ Conclusión

El proyecto está **100% alineado** con el enunciado en lo que respecta a Keycloak y seguridad:

1. ✅ **Autenticación centralizada** con Keycloak
2. ✅ **Validación de tokens** solo en el Gateway
3. ✅ **userId proviene del usuario autenticado** (no es string arbitrario)
4. ✅ **Microservicios simples** sin lógica OAuth2
5. ✅ **Roles implementados**: cliente, admin, transportista
6. ✅ **Admin puede crear usuarios** mediante endpoint protegido

El sistema cumple con el principio didáctico fundamental: **el Gateway es el único que entiende de seguridad OAuth2, los microservicios solo ven HTTP + headers**.
