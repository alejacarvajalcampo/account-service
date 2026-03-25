DROP TABLE IF EXISTS movimientos;
DROP TABLE IF EXISTS cuentas;
DROP TABLE IF EXISTS clientes_referencia;

CREATE TABLE clientes_referencia (
    cliente_id BIGINT NOT NULL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    identificacion VARCHAR(20) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL
);

CREATE INDEX idx_clientes_referencia_identificacion ON clientes_referencia (identificacion);

CREATE TABLE cuentas (
    numero_cuenta BIGINT NOT NULL PRIMARY KEY,
    tipo_cuenta VARCHAR(30) NOT NULL,
    saldo_inicial DECIMAL(19, 2) NOT NULL,
    saldo_disponible DECIMAL(19, 2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id BIGINT NOT NULL,
    cliente_nombre VARCHAR(100) NOT NULL
);

CREATE INDEX idx_cuentas_cliente_id ON cuentas (cliente_id);

CREATE TABLE movimientos (
    movimiento_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL,
    numero_cuenta BIGINT NOT NULL,
    CONSTRAINT fk_movimientos_cuentas
        FOREIGN KEY (numero_cuenta)
        REFERENCES cuentas (numero_cuenta)
        ON DELETE CASCADE
);

CREATE INDEX idx_movimientos_numero_cuenta ON movimientos (numero_cuenta);
CREATE INDEX idx_movimientos_fecha ON movimientos (fecha);

INSERT INTO cuentas (
    numero_cuenta,
    tipo_cuenta,
    saldo_inicial,
    saldo_disponible,
    estado,
    cliente_id,
    cliente_nombre
) VALUES
    (478758, 'Ahorro', 2000.00, 1425.00, TRUE, 1, 'Jose Lema'),
    (225487, 'Corriente', 100.00, 700.00, TRUE, 2, 'Marianela Montalvo'),
    (495878, 'Ahorros', 0.00, 150.00, TRUE, 3, 'Juan Osorio'),
    (496825, 'Ahorros', 540.00, 0.00, TRUE, 2, 'Marianela Montalvo');

INSERT INTO movimientos (
    fecha,
    tipo_movimiento,
    valor,
    saldo,
    numero_cuenta
) VALUES
    ('2022-02-10', 'RETIRO', -575.00, 1425.00, 478758),
    ('2022-02-10', 'DEPOSITO', 600.00, 700.00, 225487),
    ('2022-02-10', 'DEPOSITO', 150.00, 150.00, 495878),
    ('2022-02-08', 'RETIRO', -540.00, 0.00, 496825);

INSERT INTO clientes_referencia (
    cliente_id,
    nombre,
    identificacion,
    estado
) VALUES
    (1, 'Jose Lema', '1234567890', TRUE),
    (2, 'Marianela Montalvo', '9876543210', TRUE),
    (3, 'Juan Osorio', '5678901234', TRUE);
