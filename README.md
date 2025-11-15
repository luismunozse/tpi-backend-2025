TPI Backend 2025
================

Este proyecto implementa el backend de una empresa transportista usando una arquitectura de microservicios con Spring Boot, API Gateway y Keycloak para autenticación/autorización.

Arquitectura
------------

- **api-gateway**: puerta de entrada al backend. Valida JWT de Keycloak y enruta hacia los microservicios de dominio.
- **orders-service**: gestiona solicitudes de transporte, contenedores, clientes y el ciclo de vida del envío. Orquesta llamadas a los demás servicios.
- **fleet-service**: administra camiones, transportistas y mantenimientos. Expone endpoints para buscar camiones disponibles y actualizar su estado.
- **locations-service**: maneja ciudades, depósitos, rutas y cálculo de distancias entre coordenadas.
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
cd api-gateway && mvn clean package -DskipTests
cd ../orders-service && mvn clean package -DskipTests
cd ../fleet-service && mvn clean package -DskipTests
cd ../locations-service && mvn clean package -DskipTests
cd ../pricing-service && mvn clean package -DskipTests
```

Ejecución con Docker Compose
----------------------------

1. Ubicarse en la carpeta que contiene `docker-compose.yml`:

   ```bash
   cd tpi-backend-2025/tpi-backend-2025
   ```

2. Levantar todos los contenedores (bases, servicios, gateway, Keycloak, pgadmin):

   ```bash
   docker compose up --build
   ```

3. Servicios principales:

   - API Gateway: `http://localhost:8080`
   - Keycloak: `http://localhost:8081` (admin: `admin` / `admin`, salvo que lo hayas cambiado)
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

1. Crear un **realm** (por ejemplo `tpi-backend`), o usar el que ya tengas configurado según el instructivo de la cátedra.
2. Crear un **client**:
   - `Client ID`: `tpi-backend-client`
   - Tipo: OpenID Connect.
   - Redirect URIs: `http://localhost:8080/*`
   - Habilitar `Standard flow` (authorization_code) y/o `Direct access grants` (password) según el flujo que quieras usar desde Postman.
3. Crear roles de realm para los tres perfiles:
   - `cliente`
   - `operador`
   - `transportista`
4. Crear usuarios y asignarles el/los roles correspondientes.

El API Gateway está configurado como **resource server** y valida tokens JWT usando el endpoint JWKS de Keycloak. La autorización por ruta se realiza en `SecurityConfig`, por ejemplo:

- `/api/ordenes/**` → requiere rol `CLIENTE` u `OPERADOR`.
- `/api/fleet/**` y `/api/pricing/**` → rol `OPERADOR`.
- `/api/locations/**` → rol `OPERADOR` o `TRANSPORTISTA`.

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
   - `POST {{gateway}}/api/ordenes` para crear una solicitud de transporte (cliente).
   - `GET {{gateway}}/api/fleet/camiones/disponibles` para buscar camiones (operador).
   - `POST {{gateway}}/api/pricing/calculos/tarifa` para probar el cálculo de tarifa (operador).

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
