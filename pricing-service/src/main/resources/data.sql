INSERT INTO tarifas_base (tipo_camion, rango_peso_min_kg, rango_peso_max_kg, rango_volumen_min_m3, rango_volumen_max_m3, costo_por_kilometro, costo_por_kilometro_combustible, costo_fijo_gestion)
SELECT 'LIVIANO', 0, 3000, 0, 15, 1.50, 0.40, 25
WHERE NOT EXISTS (
    SELECT 1 FROM tarifas_base WHERE tipo_camion = 'LIVIANO' AND rango_peso_max_kg = 3000
);

INSERT INTO tarifas_base (tipo_camion, rango_peso_min_kg, rango_peso_max_kg, rango_volumen_min_m3, rango_volumen_max_m3, costo_por_kilometro, costo_por_kilometro_combustible, costo_fijo_gestion)
SELECT 'MEDIANO', 3000, 8000, 15, 40, 2.10, 0.55, 35
WHERE NOT EXISTS (
    SELECT 1 FROM tarifas_base WHERE tipo_camion = 'MEDIANO' AND rango_peso_max_kg = 8000
);

INSERT INTO tarifas_base (tipo_camion, rango_peso_min_kg, rango_peso_max_kg, rango_volumen_min_m3, rango_volumen_max_m3, costo_por_kilometro, costo_por_kilometro_combustible, costo_fijo_gestion)
SELECT 'PESADO', 8000, NULL, 40, NULL, 2.80, 0.70, 45
WHERE NOT EXISTS (
    SELECT 1 FROM tarifas_base WHERE tipo_camion = 'PESADO'
);

INSERT INTO recargos_tarifa (descripcion, porcentaje, monto_fijo)
SELECT 'Manipulación especial', 8.0, 0
WHERE NOT EXISTS (
    SELECT 1 FROM recargos_tarifa WHERE LOWER(descripcion) = LOWER('Manipulación especial')
);

INSERT INTO recargos_tarifa (descripcion, porcentaje, monto_fijo)
SELECT 'Entrega urgente', NULL, 150
WHERE NOT EXISTS (
    SELECT 1 FROM recargos_tarifa WHERE LOWER(descripcion) = LOWER('Entrega urgente')
);

INSERT INTO tarifas_deposito (costo_por_dia, cargo_por_ingreso, cargo_por_salida)
SELECT 45, 30, 30
WHERE NOT EXISTS (
    SELECT 1 FROM tarifas_deposito
);
