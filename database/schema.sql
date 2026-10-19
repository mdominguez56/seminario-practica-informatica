-- Esquema de la base fundacion_cana (MySQL 8, InnoDB).
SET NAMES utf8mb4;
DROP DATABASE IF EXISTS fundacion_cana;

CREATE DATABASE fundacion_cana
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fundacion_cana;

CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usuarios_roles
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
) ENGINE = InnoDB;

CREATE TABLE proyectos (
    id_proyecto INT AUTO_INCREMENT PRIMARY KEY,
    id_lider INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    objetivo TEXT NOT NULL,
    presupuesto_inicial DECIMAL(12, 2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    estado VARCHAR(30) NOT NULL DEFAULT 'PLANIFICACION',
    CONSTRAINT ck_proyectos_presupuesto CHECK (presupuesto_inicial >= 0),
    CONSTRAINT ck_proyectos_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT ck_proyectos_estado
        CHECK (estado IN ('PLANIFICACION', 'EN_PROGRESO', 'FINALIZADO', 'CANCELADO')),
    CONSTRAINT fk_proyectos_lider
        FOREIGN KEY (id_lider) REFERENCES usuarios (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE hitos (
    id_hito INT AUTO_INCREMENT PRIMARY KEY,
    id_proyecto INT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_limite DATE NOT NULL,
    porcentaje_avance DECIMAL(5, 2) NOT NULL DEFAULT 0,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT ck_hitos_avance CHECK (porcentaje_avance BETWEEN 0 AND 100),
    CONSTRAINT ck_hitos_estado CHECK (estado IN ('PENDIENTE', 'EN_PROGRESO', 'COMPLETADO')),
    CONSTRAINT fk_hitos_proyecto
        FOREIGN KEY (id_proyecto) REFERENCES proyectos (id_proyecto)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE asignaciones (
    id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
    id_hito INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_asignacion DATE NOT NULL,
    fecha_desasignacion DATE,
    CONSTRAINT ck_asignaciones_fechas CHECK (
        fecha_desasignacion IS NULL OR fecha_desasignacion >= fecha_asignacion
    ),
    CONSTRAINT uq_asignaciones_hito_usuario UNIQUE (id_hito, id_usuario),
    CONSTRAINT fk_asignaciones_hito
        FOREIGN KEY (id_hito) REFERENCES hitos (id_hito)
        ON DELETE CASCADE,
    CONSTRAINT fk_asignaciones_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
) ENGINE = InnoDB;

CREATE TABLE costos (
    id_costo INT AUTO_INCREMENT PRIMARY KEY,
    id_hito INT NOT NULL,
    fecha DATE NOT NULL,
    monto DECIMAL(12, 2) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    CONSTRAINT ck_costos_monto CHECK (monto > 0),
    CONSTRAINT fk_costos_hito
        FOREIGN KEY (id_hito) REFERENCES hitos (id_hito)
        ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE INDEX idx_proyectos_estado ON proyectos (estado);
CREATE INDEX idx_hitos_fecha_estado ON hitos (fecha_limite, estado);
CREATE INDEX idx_costos_fecha ON costos (fecha);
