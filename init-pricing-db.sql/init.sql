CREATE TABLE IF NOT EXISTS tarifas_base (
    id                          BIGSERIAL PRIMARY KEY,
    tipo_camion                 VARCHAR(50) NOT NULL,
    rango_peso_min_kg           DOUBLE PRECISION,
    rango_peso_max_kg           DOUBLE PRECISION,
    rango_volumen_min_m3        DOUBLE PRECISION,
    rango_volumen_max_m3        DOUBLE PRECISION,
    costo_por_kilometro         DOUBLE PRECISION,
    costo_por_kilometro_combustible DOUBLE PRECISION,
    costo_fijo_gestion          DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS recargos_tarifa (
    id          BIGSERIAL PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    porcentaje  DOUBLE PRECISION,
    monto_fijo  DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS tarifas_deposito (
    id               BIGSERIAL PRIMARY KEY,
    costo_por_dia    DOUBLE PRECISION,
    cargo_por_ingreso DOUBLE PRECISION,
    cargo_por_salida  DOUBLE PRECISION
);
