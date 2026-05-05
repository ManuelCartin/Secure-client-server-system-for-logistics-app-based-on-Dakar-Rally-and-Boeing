DROP DATABASE IF EXISTS tienda_app;
CREATE DATABASE tienda_app CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE tienda_app;


CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    correo VARCHAR(100),
    rol VARCHAR(50) NOT NULL, -- admin, chofer, despachador
    estado VARCHAR(1) DEFAULT 'A',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehiculos (
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(20) NOT NULL,
    modelo VARCHAR(100),
    estado VARCHAR(50) DEFAULT 'DISPONIBLE', -- DISPONIBLE, MANTENIMIENTO
    kilometraje INT DEFAULT 0
);


CREATE TABLE paquetes (
    id_paquete INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255),
    destino VARCHAR(100),
    estado VARCHAR(50) DEFAULT 'PENDIENTE', -- PENDIENTE, EN_RUTA, ENTREGADO
    id_chofer INT,
    id_vehiculo INT,
    FOREIGN KEY (id_chofer) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo)
);

DESCRIBE paquetes;

ALTER TABLE paquetes
ADD COLUMN id_cliente INT NOT NULL AFTER id_paquete;

ALTER TABLE paquetes
ADD CONSTRAINT fk_paquete_cliente
FOREIGN KEY (id_cliente) REFERENCES usuarios(id_usuario);
SELECT id_paquete, id_cliente FROM paquetes;


CREATE TABLE tracking (
    id_tracking INT AUTO_INCREMENT PRIMARY KEY,
    id_paquete INT,
    punto VARCHAR(100),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_paquete) REFERENCES paquetes(id_paquete)
);
-- SET SQL_SAFE_UPDATES = 0;

SELECT id_usuario, nombre, rol FROM usuarios WHERE rol = 'cliente';

ALTER TABLE paquetes MODIFY id_cliente INT NULL;

UPDATE paquetes
SET id_cliente = 10
WHERE id_cliente = 0 OR id_cliente IS NULL;

UPDATE paquetes
SET id_cliente = 7
WHERE id_cliente = 0 OR id_cliente IS NULL;

-- SET SQL_SAFE_UPDATES = 1;

ALTER TABLE paquetes
ADD CONSTRAINT fk_paquete_cliente
FOREIGN KEY (id_cliente) REFERENCES usuarios(id_usuario);


CREATE TABLE mantenimiento (
    id_mantenimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_vehiculo INT,
    descripcion VARCHAR(255),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50),
    FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo)
);

CREATE TABLE planilla (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    horas INT,
    entregas INT,
    estado VARCHAR(50),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE credenciales_admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100),
    clave VARCHAR(255),
    ultimo_acceso TIMESTAMP
);

CREATE TABLE configuracion_privada (
    id INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100),
    valor TEXT
);

CREATE TABLE logs_sistema_old (
    id INT AUTO_INCREMENT PRIMARY KEY,
    evento TEXT,
    fecha TIMESTAMP
);

CREATE TABLE tokens_sesion_archivados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255),
    fecha TIMESTAMP
);
-- tablas decoy (inicio)
INSERT INTO credenciales_admin (usuario, clave, ultimo_acceso) VALUES
('mserrano.ops', '$2a$10$F4k3HashSimulado1234567890abcd', '2025-11-12 08:14:22'),
('cnavarro.sec', '$2a$10$F4k3HashSimulado0987654321efgh', '2025-11-10 17:42:09'),
('rquiros.sys', '$2a$10$F4k3HashSimuladoABCDEF12345678', '2025-11-08 12:55:31');

INSERT INTO configuracion_privada (clave, valor) VALUES
('api_master_key', 'A12F9C0D88F4E1B2C3D4E5F60718293A'),
('smtp_root_pass', 'x9Kp!72LmQ#4'),
('internal_mode', 'legacy-compat'),
('backup_schedule', '03:00 AM - incremental');

INSERT INTO logs_sistema_old (evento, fecha) VALUES
('Deprecation warning: módulo auth_v1', '2024-06-14 11:22:10'),
('Migración incompleta detectada en tabla temp_cfg', '2024-06-15 09:18:44'),
('Intento de conexión fallido desde 10.0.45.12', '2024-06-16 03:41:02'),
('Servicio scheduler_old detenido', '2024-06-17 14:09:55');

INSERT INTO tokens_sesion_archivados (token, fecha) VALUES
('eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fakePayload123.fakeSignatureABC', '2025-10-22 10:12:55'),
('sess_98af12c0e7b34d5f9a1c2b3d4e5f6071', '2025-10-23 16:44:21'),
('jwt_test_4f9c8e1b2d3a7f6e5c4b1a0987d6e5f', '2025-10-24 07:33:09');
-- tablas decoy(fin)

-- hoenypot(inicio)
CREATE TABLE Secure_House (
    Code_Amenaza INT AUTO_INCREMENT PRIMARY KEY,
    UserId INT,
    Hash_Validacion VARCHAR(255),
    Token_Sesion VARCHAR(255),
    Ultima_IP VARCHAR(50),
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Audit_Trail (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    Code_Amenaza INT,
    UserId INT,
    Error_Detectado TEXT,
    Fecha_Hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Code_Amenaza) REFERENCES Secure_House(Code_Amenaza)
);

CREATE TABLE Quarantine_Log (
    QuarantineID INT AUTO_INCREMENT PRIMARY KEY,
    Code_Amenaza INT,
    UserId INT,
    Query_Sospechosa TEXT,
    Intento_Payload TEXT,
    Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Code_Amenaza) REFERENCES Secure_House(Code_Amenaza)
);
-- Honeypot (fin)
CREATE VIEW vw_monitoreo_paquetes AS
SELECT 
    p.id_paquete AS id,
    u.nombre AS chofer,
    (
        SELECT t.punto 
        FROM tracking t 
        WHERE t.id_paquete = p.id_paquete
        ORDER BY t.fecha DESC
        LIMIT 1
    ) AS ultimo_punto,
    p.estado
FROM paquetes p
LEFT JOIN usuarios u ON u.id_usuario = p.id_chofer;

CREATE TABLE ruta (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    id_chofer INT NOT NULL,
    id_vehiculo INT NOT NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'EN_RUTA'
);


CREATE TABLE roadbook_checkpoint (
    id_checkpoint INT AUTO_INCREMENT PRIMARY KEY,
    id_ruta INT NOT NULL,
    orden INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    latitud DOUBLE NOT NULL,
    longitud DOUBLE NOT NULL,
    distancia_km DOUBLE DEFAULT 0,
    tiempo_estimado INT DEFAULT 0,
    
    FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
        ON DELETE CASCADE
);

CREATE TABLE paquete_checkpoint (
    id_paquete INT NOT NULL,
    id_ruta INT NOT NULL,
    id_checkpoint INT NOT NULL,

    PRIMARY KEY (id_paquete, id_checkpoint),

    FOREIGN KEY (id_paquete) REFERENCES paquetes(id_paquete)
        ON DELETE CASCADE,

    FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
        ON DELETE CASCADE,

    FOREIGN KEY (id_checkpoint) REFERENCES roadbook_checkpoint(id_checkpoint)
        ON DELETE CASCADE
);

CREATE TABLE checkpoint_reportado (
    id_reporte INT AUTO_INCREMENT PRIMARY KEY,
    id_ruta INT NOT NULL,
    id_checkpoint INT NOT NULL,
    fecha_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(255),
    registrado_por VARCHAR(50),

    FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
        ON DELETE CASCADE,

    FOREIGN KEY (id_checkpoint) REFERENCES roadbook_checkpoint(id_checkpoint)
        ON DELETE CASCADE
);

ALTER TABLE paquetes
ADD COLUMN id_ruta INT NULL,
ADD FOREIGN KEY (id_ruta) REFERENCES ruta(id_ruta)
    ON DELETE SET NULL;

CREATE TABLE chofer (
    id_chofer INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    licencia VARCHAR(50),
    telefono VARCHAR(20),
    activo TINYINT DEFAULT 1,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

INSERT INTO chofer (id_usuario)
SELECT id_usuario
FROM usuarios
WHERE rol = 'chofer';

ALTER TABLE ruta ADD COLUMN id_chofer_nuevo INT NULL;
-- SET SQL_SAFE_UPDATES = 1;


UPDATE ruta r
JOIN chofer c ON c.id_usuario = r.id_chofer
SET r.id_chofer_nuevo = c.id_chofer;


-- usuarios
INSERT INTO usuarios (nombre, usuario, clave, correo, rol) VALUES
("Admin Sistema", "admin", "admin123", "admin@test.com", "admin"),
("Carlos Chofer", "chofer1", "1234", "chofer@test.com", "chofer"),
("Luis Despachador", "despacho1", "1234", "despacho@test.com", "despachador");

-- vehiculos
INSERT INTO vehiculos (placa, modelo, estado, kilometraje) VALUES
("ABC123", "Toyota Hilux", "DISPONIBLE", 120000),
("XYZ789", "Nissan Frontier", "DISPONIBLE", 95000);

-- paquetes
INSERT INTO paquetes (descripcion, destino, estado) VALUES
("Paquete 1 - Electrónicos", "San José", "PENDIENTE"),
("Paquete 2 - Documentos", "Heredia", "PENDIENTE");

-- tracking (ejemplo)
INSERT INTO tracking (id_paquete, punto) VALUES
(1, "San José Centro"),
(1, "San José Norte");

-- mantenimiento
INSERT INTO mantenimiento (id_vehiculo, descripcion, estado) VALUES
(1, "Cambio de aceite", "FINALIZADO");

-- planilla
INSERT INTO planilla (id_usuario, horas, entregas, estado) VALUES
(2, 8, 5, "ACTIVO");

SELECT nombre, usuario FROM usuarios WHERE rol = 'chofer';

INSERT INTO usuarios (nombre, usuario, clave, correo, rol) VALUES
('Andrés Chofer', 'chofer3', '1234', 'andres@test.com', 'chofer'),
('Beatriz Chofer', 'chofer4', '1234', 'beatriz@test.com', 'chofer'),
('Daniel Chofer', 'chofer5', '1234', 'daniel@test.com', 'chofer');

SELECT p.id_paquete, p.descripcion, u.usuario, p.id_chofer 
FROM paquetes p 
JOIN usuarios u ON p.id_chofer = u.id_usuario 
WHERE u.usuario = 'chofer3';

SELECT id_paquete, id_chofer FROM paquetes;

SELECT id_ruta, id_chofer FROM ruta;

SELECT * FROM chofer WHERE id_chofer = 2;

SELECT * FROM usuarios WHERE id_usuario = (
    SELECT id_usuario FROM chofer WHERE id_chofer = 2
);

SELECT * FROM chofer;
SHOW CREATE TABLE paquetes;
SELECT 
    CONSTRAINT_NAME, 
    TABLE_NAME, 
    COLUMN_NAME, 
    REFERENCED_TABLE_NAME, 
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'paquetes'
  AND CONSTRAINT_SCHEMA = 'tienda_app'
  AND REFERENCED_TABLE_NAME IS NOT NULL;

ALTER TABLE paquetes DROP FOREIGN KEY paquetes_ibfk_1;

ALTER TABLE paquetes
ADD CONSTRAINT fk_paquete_chofer
FOREIGN KEY (id_chofer)
REFERENCES chofer(id_chofer)
ON DELETE SET NULL
ON UPDATE CASCADE;

SELECT id_paquete, id_chofer
FROM paquetes
WHERE id_chofer IS NOT NULL
  AND id_chofer NOT IN (SELECT id_chofer FROM chofer);

UPDATE paquetes
SET id_chofer = NULL
WHERE id_chofer NOT IN (SELECT id_chofer FROM chofer);

ALTER TABLE paquetes
ADD CONSTRAINT fk_paquete_chofer
FOREIGN KEY (id_chofer)
REFERENCES chofer(id_chofer)
ON DELETE SET NULL
ON UPDATE CASCADE;

SELECT id_paquete, id_chofer
FROM paquetes
WHERE id_chofer IS NOT NULL
  AND id_chofer NOT IN (SELECT id_chofer FROM chofer);
SELECT id_paquete, id_chofer FROM paquetes;

SELECT c.id_chofer, u.id_usuario, u.nombre
FROM chofer c
JOIN usuarios u ON u.id_usuario = c.id_usuario
WHERE c.id_chofer = 4;
