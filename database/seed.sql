-- Datos de prueba. Las contraseñas se guardan como hash SHA-256.
-- directivo123, lider123 y voluntario123 son contraseñas de demostración.
SET NAMES utf8mb4;
USE fundacion_cana;

INSERT INTO roles (id_rol, nombre) VALUES
    (1, 'DIRECTIVO'),
    (2, 'LIDER_PROYECTO'),
    (3, 'VOLUNTARIO');

INSERT INTO usuarios (id_usuario, id_rol, nombre, email, password_hash) VALUES
    (1, 1, 'Oscar Rodriguez',  'oscar.directiva@cana.org',   '1705a369e59361e7b85a0d3b95c96b82725ff35a874916576ba3281c8d28048d'),
    (2, 2, 'Carlos Dominguez', 'carlos.lider@cana.org',      'f2499f5bd15335d3fd9ef979ea072a8e6eb32990f7c6fe54dd08cdd6792309af'),
    (3, 3, 'Ana Perez',        'ana.voluntaria@cana.org',    'fda3c5571f9554a4adff4617dfafef6be8401f2d279ec642b1522d50c388c916'),
    (4, 2, 'Lucia Fernandez',  'lucia.lider@cana.org',       'f2499f5bd15335d3fd9ef979ea072a8e6eb32990f7c6fe54dd08cdd6792309af'),
    (5, 3, 'Martin Gomez',     'martin.voluntario@cana.org', 'fda3c5571f9554a4adff4617dfafef6be8401f2d279ec642b1522d50c388c916'),
    (6, 3, 'Sofia Ruiz',       'sofia.voluntaria@cana.org',  'fda3c5571f9554a4adff4617dfafef6be8401f2d279ec642b1522d50c388c916');

INSERT INTO proyectos
    (id_proyecto, id_lider, nombre, objetivo, presupuesto_inicial, fecha_inicio, fecha_fin, estado)
VALUES
    (1, 2, 'Comedor Solidario', 'Brindar almuerzos a 500 niños durante el segundo semestre',
     150000.00, '2026-09-01', '2026-12-20', 'EN_PROGRESO'),
    (2, 4, 'Apoyo Escolar 2026', 'Acompañar a 80 estudiantes de nivel primario con clases de apoyo',
     80000.00, '2026-08-01', '2026-11-30', 'EN_PROGRESO'),
    (3, 2, 'Campaña de Invierno', 'Recolectar y entregar abrigos a familias de barrios vulnerables',
     60000.00, '2026-05-01', '2026-08-31', 'FINALIZADO'),
    (4, 4, 'Taller de Oficios', 'Capacitar a 40 jóvenes en oficios de construcción y electricidad',
     120000.00, '2026-11-01', '2027-03-31', 'PLANIFICACION');

INSERT INTO hitos
    (id_hito, id_proyecto, nombre, descripcion, fecha_limite, porcentaje_avance, estado)
VALUES
    (1, 1, 'Compra de insumos', 'Adquirir los insumos del primer mes', '2026-09-15', 100.00, 'COMPLETADO'),
    (2, 1, 'Distribución de viandas', 'Organizar la entrega a las personas beneficiarias', '2026-09-30', 35.00, 'EN_PROGRESO'),
    (3, 1, 'Relevamiento de familias', 'Actualizar el padrón de familias beneficiarias', '2026-11-15', 20.00, 'EN_PROGRESO'),
    (4, 1, 'Cierre y rendición', 'Rendir cuentas al Consejo Directivo', '2026-12-20', 0.00, 'PENDIENTE'),
    (5, 2, 'Inscripción de alumnos', 'Inscribir a los estudiantes y relevar necesidades', '2026-08-20', 100.00, 'COMPLETADO'),
    (6, 2, 'Compra de útiles', 'Adquirir útiles y material de lectura', '2026-09-10', 100.00, 'COMPLETADO'),
    (7, 2, 'Clases de apoyo primer ciclo', 'Dictar clases de apoyo de lengua y matemática', '2026-10-10', 70.00, 'EN_PROGRESO'),
    (8, 2, 'Evaluación final', 'Evaluar los resultados con las familias y docentes', '2026-11-30', 0.00, 'PENDIENTE'),
    (9, 3, 'Colecta de abrigos', 'Recolectar abrigos en parroquias y escuelas', '2026-06-15', 100.00, 'COMPLETADO'),
    (10, 3, 'Entrega de abrigos', 'Distribuir abrigos en cinco barrios', '2026-07-31', 100.00, 'COMPLETADO'),
    (11, 4, 'Convenio con instituciones', 'Firmar convenios con escuelas técnicas', '2026-11-20', 0.00, 'PENDIENTE');

INSERT INTO asignaciones (id_hito, id_usuario, fecha_asignacion) VALUES
    (1, 3, '2026-09-01'),
    (2, 3, '2026-09-01'),
    (3, 5, '2026-09-20'),
    (4, 6, '2026-09-20'),
    (5, 6, '2026-08-01'),
    (6, 5, '2026-08-01'),
    (7, 3, '2026-08-25'),
    (8, 6, '2026-08-25'),
    (9, 5, '2026-05-01'),
    (10, 3, '2026-05-01'),
    (11, 5, '2026-10-01');

INSERT INTO costos (id_hito, fecha, monto, descripcion) VALUES
    (1, '2026-09-10', 45000.00, 'Compra de insumos del primer mes'),
    (2, '2026-09-12', 12000.00, 'Logística de distribución'),
    (3, '2026-10-05', 3000.00, 'Movilidad para el relevamiento'),
    (5, '2026-08-05', 5000.00, 'Difusión de la inscripción'),
    (6, '2026-09-08', 22000.00, 'Útiles escolares'),
    (7, '2026-09-25', 9000.00, 'Material didáctico'),
    (9, '2026-06-10', 18000.00, 'Bolsas y logística de colecta'),
    (10, '2026-07-28', 50000.00, 'Traslado y depósito de abrigos');
