TPI Backend 2025
================

Este proyecto implementa el backend de una empresa transportista usando una arquitectura de microservicios con Spring Boot, API Gateway y Keycloak para autenticación/autorización.

Arquitectura
------------

- **api-gateway**: puerta de entrada al backend. Valida JWT de Keycloak y enruta hacia los microservicios de dominio.
- **orders-service**: gestiona solicitudes de transporte, contenedores, clientes y el ciclo de vida del envío. Orquesta llamadas a los demás servicios.
- **fleet-service**: administra camiones, transportistas y mantenimientos. Expone endpoints para buscar camiones disponibles y actualizar su estado.
- **locations-service**: maneja ciudades, depósitos, rutas y cálculo de distancias entre coordenadas consumiendo la API externa **Google Maps Distance Matrix**.
- **pricing-service**: administra tarifas y recargos, y calcula costos estimados y reales de los envíos.
- **PostgreSQL**: una base por servicio (orders, fleet, locations, pricing).
- **Keycloak**: servidor de identidad y gestión de roles (cliente, operador, transportista).

Prerrequisitos
--------------

- Java 17 (JDK 17).
- Maven 3.8+.
- Docker y Docker Compose.

Compilación local
-----------------

Desde la carpeta raíz del repo (`tpi-backend-2025`), compilar cada microservicio:

```bash
cd api-gateway ; mvn clean package -DskipTests
cd ../orders-service ; mvn clean package -DskipTests
cd ../fleet-service ; mvn clean package -DskipTests
cd ../locations-service ; mvn clean package -DskipTests
cd ../pricing-service ; mvn clean package -DskipTests
```

Ejecución con Docker Compose
----------------------------

1. Ubicarse en la carpeta que contiene `docker-compose.yml`:

   ```bash
   cd ..
   ```

2. Levantar todos los contenedores (bases, servicios, gateway, Keycloak, pgadmin):

   ```bash
   docker compose up --build
   ```

3. Servicios principales:

   - API Gateway: `http://localhost:8080`
   - Keycloak: `http://localhost:8081` (admin: `admin` / `admin`)
   - PgAdmin: `http://localhost:5050`
   - Microservicios (accesibles desde Docker por nombre de servicio):
     - `orders-service:8080`
     - `fleet-service:8080`
     - `locations-service:8080`
     - `pricing-service:8080`

4. Para detener todo:

   ```bash
   docker compose down
   ```

Configuración de Keycloak (resumen)
-----------------------------------

1. Crear un **realm**: `tpi-backend` (según el instructivo de la cátedra).
2. Crear un **client**:
   - `Client ID`: `tpi-backend-client`
   - Tipo: OpenID Connect (public client)
   - Redirect URIs: `http://localhost:8080/*`
   - Habilitar `Direct access grants` (password) para login desde Postman
3. Crear **roles de realm** para los tres perfiles del sistema:
   - `cliente` - Usuarios finales que solicitan transporte
   - `admin` - Administradores/operadores que gestionan el sistema
   - `transportista` - Choferes que ejecutan los traslados
4. Crear **usuarios** y asignarles roles:
   - Los usuarios `admin` pueden crear nuevos usuarios `cliente` mediante `/auth/register`
   - Los usuarios deben crearse manualmente en Keycloak o via endpoint protegido

### 📖 Guía Completa de Registro y Verificación

Para entender el flujo completo de registro de usuarios, verificación en Keycloak y creación de solicitudes, ver:

👉 **[GUIA_REGISTRO_Y_VERIFICACION.md](GUIA_REGISTRO_Y_VERIFICACION.md)**

Esta guía incluye:
- ✅ Flujo paso a paso de registro de usuario
- ✅ Cómo verificar en Keycloak Admin Console
- ✅ Cómo hacer login y crear primera solicitud
- ✅ Diferencia entre Usuario (Keycloak) y Cliente (Orders DB)
- ✅ Secuencia completa de pruebas con Postman
- ✅ Checklist de verificación

### Seguridad y Autenticación

El API Gateway está configurado como **OAuth2 Resource Server** y valida tokens JWT usando el endpoint JWKS de Keycloak.

**Autorización por rol**:
- `/auth/login` → público (sin autenticación)
- `/auth/register` → solo `ADMIN` (para crear usuarios cliente)
- `/api/ordenes/**` → `CLIENTE` o `ADMIN`
- `/api/fleet/**` → solo `ADMIN`
- `/api/pricing/**` → solo `ADMIN`
- `/api/locations/**` → `ADMIN` o `TRANSPORTISTA`

### Propagación de Información del Usuario

**Según el enunciado**: "Que el userId deje de ser un string arbitrario y pase a ser el usuario autenticado"

El Gateway **extrae automáticamente** la información del usuario autenticado del JWT y la propaga a los microservicios mediante headers HTTP:

- `X-User-Id`: ID único del usuario (claim `sub` del JWT)
- `X-Username`: Nombre de usuario (claim `preferred_username`)
- `X-User-Email`: Email del usuario (claim `email`)
- `X-User-Roles`: Roles separados por coma

**Los microservicios NO validan OAuth2**. Solo reciben estos headers y confían en la validación del Gateway. Esto cumple con el principio didáctico: "La clave es que no modificamos los microservicios para entender OAuth2: ellos siguen viendo solo HTTP + headers. El que entiende de seguridad es el gateway."

Verificación de Requisitos del Cliente
--------------------------------------

📋 **Ver análisis completo:** [ANALISIS_ENDPOINTS_CLIENTE.md](ANALISIS_ENDPOINTS_CLIENTE.md)

El sistema cumple con todos los requisitos del enunciado para el rol **Cliente**:

### ✅ 1. Registrar un pedido de traslado de contenedor
- **Endpoint:** `POST /api/ordenes/solicitudes`
- **Roles:** CLIENTE, ADMIN
- **Funcionalidad:** Crea solicitud con cálculo automático de distancias (Google Maps API) y costos estimados

### ✅ 2. Consultar el estado actual de su contenedor (seguimiento)
- **Tracking detallado:** `GET /api/ordenes/solicitudes/{id}/tracking`
- **Solicitud completa:** `GET /api/ordenes/solicitudes/{id}`
- **Listar solicitudes:** `GET /api/ordenes/solicitudes?estado={estado}`
- **Funcionalidad:** Estado de solicitud, estado de contenedor, fechas reales, camiones asignados

### ✅ 3. Ver el costo y tiempo estimado de entrega
- **Al crear:** Incluido en response de POST
- **Al consultar:** Incluido en GET de solicitud
- **Recalcular:** `POST /api/ordenes/solicitudes/{id}/recalcular`
- **Funcionalidad:** Costo total + desglose detallado (base, combustible, depósitos, gestión, recargos)

### 🔀 Feature Adicional: Rutas Alternativas
- **Endpoint:** `POST /api/ordenes/solicitudes/calcular-alternativas`
- **Funcionalidad:** Genera múltiples rutas usando Distance Matrix API, considera depósitos intermedios, calcula costos y tiempos, recomienda la más económica
- **Documentación:** [RUTAS_ALTERNATIVAS.md](RUTAS_ALTERNATIVAS.md)

Consumo de la API con Postman
-----------------------------

Se recomienda crear un Environment en Postman con estas variables:

- `gateway`: `http://localhost:8080`
- `keycloak`: `http://localhost:8081`
- `realm`: nombre de tu realm (por ejemplo `tpi-backend`)
- `kc_username`: usuario de pruebas en Keycloak
- `kc_password`: contraseña del usuario
- `token`: se completa con el `access_token` obtenido

Flujo típico:

1. Obtener un token desde Keycloak (`/realms/{realm}/protocol/openid-connect/token`) usando `grant_type=password` o `authorization_code`.
2. Copiar el `access_token` en la variable `token` del environment.
3. Invocar la API a través del gateway, por ejemplo:
   - `POST {{gateway}}/api/ordenes/solicitudes` para crear una solicitud de transporte (cliente).
   - `GET {{gateway}}/api/fleet/camiones/disponibles` para buscar camiones (operador).
   - `POST {{gateway}}/api/pricing/calculos/tarifa` para probar el cálculo de tarifa (operador).
   - `GET {{gateway}}/api/ordenes/solicitudes/{id}/tracking` para ver el tracking de tramos de una solicitud.

Se incluye una colección Postman lista para importar en `postman/TPI-Backend-2025.postman_collection.json`.

Rutas principales de cada microservicio
---------------------------------------

Todas las rutas expuestas abajo se consumen normalmente vía API Gateway, anteponiendo `{{gateway}}` y el prefijo configurado en el gateway (`/api/ordenes`, `/api/fleet`, `/api/locations`, `/api/pricing`).

- **Orders Service** (gestión de solicitudes de transporte)
  - Crear solicitud: `POST /api/v1/solicitudes`
  - Listar solicitudes: `GET /api/v1/solicitudes?estado={BORRADOR|PROGRAMADA|EN_TRANSITO|ENTREGADA|CANCELADA}`
  - Obtener solicitud: `GET /api/v1/solicitudes/{id}`
  - Actualizar estado de solicitud: `POST /api/v1/solicitudes/{id}/estado`
  - Asignar camión a tramo: `POST /api/v1/solicitudes/{id}/tramos/{tramoId}/asignacion`
  - Registrar inicio de tramo: `POST /api/v1/solicitudes/{id}/tramos/{tramoId}/inicio`
  - Registrar fin de tramo (costo real opcional): `POST /api/v1/solicitudes/{id}/tramos/{tramoId}/fin?costoReal={valor}`
  - Recalcular costo estimado: `POST /api/v1/solicitudes/{id}/recalcular`
  - Tracking de tramos (ordenados por orden): `GET /api/v1/solicitudes/{id}/tracking`

- **Fleet Service** (flota de camiones y transportistas)
  - Camiones:
    - Listar camiones: `GET /api/v1/camiones?estado={DISPONIBLE|OCUPADO}`
    - Listar camiones disponibles aptos para un contenedor: `GET /api/v1/camiones/disponibles?peso={kg}&volumen={m3}`
    - Obtener camión por id: `GET /api/v1/camiones/{id}`
    - Crear camión: `POST /api/v1/camiones`
    - Actualizar camión: `PUT /api/v1/camiones/{id}`
    - Actualizar estado de camión: `PATCH /api/v1/camiones/{id}/estado`
    - Eliminar camión: `DELETE /api/v1/camiones/{id}`
  - Transportistas y mantenimientos disponen de endpoints CRUD similares bajo `/api/v1/transportistas` y `/api/v1/mantenimientos`.

- **Locations Service** (ubicaciones, depósitos y rutas)
  - Ciudades: CRUD bajo `/api/v1/ciudades`
  - Provincias: CRUD bajo `/api/v1/provincias`
  - Depósitos: CRUD bajo `/api/v1/depositos`
  - Rutas y tramos: CRUD bajo `/api/v1/rutas`
  - Cálculo de distancias (Google Distance Matrix):
    - `POST /api/v1/distancias/calcular`
      - Body:
        ```json
        {
          "origen": { "latitud": -31.4, "longitud": -64.18 },
          "destino": { "latitud": -34.6, "longitud": -58.38 }
        }
        ```

- **Pricing Service** (tarifas y cálculo de costos)
  - Tarifa base: CRUD bajo `/api/v1/tarifas/base`
  - Tarifas de depósito: CRUD bajo `/api/v1/tarifas/depositos`
  - Recargos: CRUD bajo `/api/v1/tarifas/recargos`
  - Cálculo de tarifa:
    - `POST /api/v1/calculos/tarifa`
      - Request típico:
        ```json
        {
          "distanciaTotalKm": 700,
          "distanciaRecorridaPorCamionKm": 700,
          "pesoContenedorKg": 5000,
          "volumenContenedorM3": 25,
          "consumoCamionLtsPorKm": 0.35,
          "costoCombustiblePorLitro": null,
          "cantidadTramos": 3,
          "diasTotalesEnDeposito": 0,
          "recargosAplicados": []
        }
        ```

Swagger / documentación de APIs
-------------------------------

Cada microservicio expone su propia documentación OpenAPI/Swagger (vía `springdoc-openapi`):

- Orders Service: `http://localhost:8083/swagger-ui.html`
- Fleet Service: `http://localhost:8084/swagger-ui.html`
- Locations Service: `http://localhost:8085/swagger-ui.html`
- Pricing Service: `http://localhost:8086/swagger-ui.html`

Desde ahí podés explorar todos los endpoints, ver los modelos de request/response y probar llamadas directamente desde Swagger UI (teniendo en cuenta la autenticación mediante bearer token cuando sea necesario).

Detalles funcionales clave
--------------------------

- **Creación de solicitud** (`orders-service`):
  - Registra/actualiza cliente y contenedor.
  - Construye una ruta con tramos a partir del request.
  - Calcula distancia de tramos usando `locations-service` (si se informan coordenadas).
  - Solicita a `pricing-service` el cálculo de tarifa estimada y distribuye costo entre tramos.
  - Deja la solicitud en estado inicial (`BORRADOR`) con costo y tiempo estimados.

- **Flota** (`fleet-service`):
  - Mantiene camiones, estados y capacidades.
  - Permite listar camiones disponibles y ocupados, y actualizar su estado.

- **Ubicaciones** (`locations-service`):
  - Gestiona ciudades, provincias, depósitos y rutas.
  - Expone `/api/v1/distancias/calcular` para obtener la distancia en km entre dos coordenadas.

- **Precios** (`pricing-service`):
  - Gestiona tarifas base, tarifas de depósito y recargos.
  - Expone `/api/v1/calculos/tarifa` para calcular costos aproximados o reales de un envío.

Para más detalles, revisar los controladores de cada servicio en `*/controller` y los servicios de dominio en `*/service`.
