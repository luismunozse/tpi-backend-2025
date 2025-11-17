CREATE TABLE IF NOT EXISTS transportistas (
    id       BIGSERIAL PRIMARY KEY,
    nombre   VARCHAR(255),
    apellido VARCHAR(255),
    telefono VARCHAR(50),
    email    VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS camiones (
    id                         BIGSERIAL PRIMARY KEY,
    dominio                    VARCHAR(50) UNIQUE NOT NULL,
    capacidad_peso_kg          DOUBLE PRECISION,
    capacidad_volumen_m3       DOUBLE PRECISION,
    consumo_combustible_por_km DOUBLE PRECISION,
    costo_base_por_km          DOUBLE PRECISION,
    estado                     VARCHAR(50),
    transportista_id           BIGINT,
    CONSTRAINT fk_camion_transportista
        FOREIGN KEY (transportista_id) REFERENCES transportistas (id)
);

CREATE TABLE IF NOT EXISTS mantenimientos (
    id               BIGSERIAL PRIMARY KEY,
    camion_id        BIGINT,
    descripcion      VARCHAR(255),
    fecha_programada DATE,
    fecha_realizada  DATE,
    completado       BOOLEAN,
    CONSTRAINT fk_mantenimiento_camion
        FOREIGN KEY (camion_id) REFERENCES camiones (id)
);
