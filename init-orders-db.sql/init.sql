CREATE TABLE IF NOT EXISTS clientes (
    id       BIGSERIAL PRIMARY KEY,
    nombre   VARCHAR(255),
    email    VARCHAR(255) UNIQUE,
    telefono VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS contenedores (
    id               BIGSERIAL PRIMARY KEY,
    identificador    VARCHAR(255) UNIQUE,
    peso_kg          DOUBLE PRECISION,
    volumen_m3       DOUBLE PRECISION,
    estado           VARCHAR(50),
    cliente_id       BIGINT,
    CONSTRAINT fk_contenedor_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE TABLE IF NOT EXISTS rutas (
    id                   BIGSERIAL PRIMARY KEY,
    distancia_total_km   DOUBLE PRECISION,
    duracion_total_horas DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS tramos (
    id                      BIGSERIAL PRIMARY KEY,
    orden                   INTEGER,
    tipo                    VARCHAR(50),
    origen_nombre           VARCHAR(255),
    destino_nombre          VARCHAR(255),
    origen_latitud          DOUBLE PRECISION,
    origen_longitud         DOUBLE PRECISION,
    destino_latitud         DOUBLE PRECISION,
    destino_longitud        DOUBLE PRECISION,
    distancia_estimada_km   DOUBLE PRECISION,
    duracion_estimada_horas DOUBLE PRECISION,
    estado                  VARCHAR(50),
    fecha_estimada_inicio   TIMESTAMP,
    fecha_estimada_fin      TIMESTAMP,
    fecha_real_inicio       TIMESTAMP,
    fecha_real_fin          TIMESTAMP,
    costo_estimado          DOUBLE PRECISION,
    costo_real              DOUBLE PRECISION,
    camion_id               BIGINT,
    ruta_id                 BIGINT,
    CONSTRAINT fk_tramo_ruta
        FOREIGN KEY (ruta_id) REFERENCES rutas (id)
);

CREATE TABLE IF NOT EXISTS solicitudes_transporte (
    id                    BIGSERIAL PRIMARY KEY,
    numero_solicitud      VARCHAR(255) UNIQUE,
    estado                VARCHAR(50),
    cliente_id            BIGINT,
    contenedor_id         BIGINT,
    ruta_id               BIGINT,
    costo_estimado        DOUBLE PRECISION,
    costo_final           DOUBLE PRECISION,
    tiempo_estimado_horas DOUBLE PRECISION,
    tiempo_real_horas     DOUBLE PRECISION,
    fecha_creacion        TIMESTAMP,
    fecha_actualizacion   TIMESTAMP,
    CONSTRAINT fk_solicitud_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT fk_solicitud_contenedor
        FOREIGN KEY (contenedor_id) REFERENCES contenedores (id),
    CONSTRAINT fk_solicitud_ruta
        FOREIGN KEY (ruta_id) REFERENCES rutas (id)
);
