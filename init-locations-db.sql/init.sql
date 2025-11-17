CREATE TABLE IF NOT EXISTS provincias (
    id     BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS coordenadas (
    id       BIGSERIAL PRIMARY KEY,
    latitud  DOUBLE PRECISION,
    longitud DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS ciudades (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(255),
    provincia_id  BIGINT,
    coordenada_id BIGINT,
    CONSTRAINT fk_ciudad_provincia
        FOREIGN KEY (provincia_id) REFERENCES provincias (id),
    CONSTRAINT fk_ciudad_coordenada
        FOREIGN KEY (coordenada_id) REFERENCES coordenadas (id)
);

CREATE TABLE IF NOT EXISTS depositos (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(255),
    direccion     VARCHAR(255),
    altura        INTEGER,
    ciudad_id     BIGINT,
    provincia_id  BIGINT,
    coordenada_id BIGINT,
    CONSTRAINT fk_deposito_ciudad
        FOREIGN KEY (ciudad_id) REFERENCES ciudades (id),
    CONSTRAINT fk_deposito_provincia
        FOREIGN KEY (provincia_id) REFERENCES provincias (id),
    CONSTRAINT fk_deposito_coordenada
        FOREIGN KEY (coordenada_id) REFERENCES coordenadas (id)
);

CREATE TABLE IF NOT EXISTS rutas (
    id          BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(255),
    descripcion VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS tramos (
    id                    BIGSERIAL PRIMARY KEY,
    orden                 INTEGER,
    tipo                  VARCHAR(50),
    distancia_estimada_km DOUBLE PRECISION,
    ruta_id               BIGINT,
    origen_deposito_id    BIGINT,
    destino_deposito_id   BIGINT,
    origen_coordenada_id  BIGINT,
    destino_coordenada_id BIGINT,
    CONSTRAINT fk_tramo_ruta
        FOREIGN KEY (ruta_id) REFERENCES rutas (id),
    CONSTRAINT fk_tramo_origen_deposito
        FOREIGN KEY (origen_deposito_id) REFERENCES depositos (id),
    CONSTRAINT fk_tramo_destino_deposito
        FOREIGN KEY (destino_deposito_id) REFERENCES depositos (id),
    CONSTRAINT fk_tramo_origen_coordenada
        FOREIGN KEY (origen_coordenada_id) REFERENCES coordenadas (id),
    CONSTRAINT fk_tramo_destino_coordenada
        FOREIGN KEY (destino_coordenada_id) REFERENCES coordenadas (id)
);
