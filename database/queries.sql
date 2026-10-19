-- Consultas de referencia. Las consultas Q1 a Q6 son las que utiliza la aplicación Java
-- (con parámetros "?" en PreparedStatement); aquí se muestran con valores de ejemplo.
SET NAMES utf8mb4;
USE fundacion_cana;

-- Q1. Inicio de sesión (UsuarioDao.autenticar).
SELECT u.id_usuario, u.nombre, u.email, u.activo, r.nombre AS rol
FROM usuarios u
JOIN roles r ON r.id_rol = u.id_rol
WHERE u.email = 'carlos.lider@cana.org'
  AND u.password_hash = SHA2('lider123', 256)
  AND u.activo = TRUE;

-- Q2. Proyectos con su líder (ProyectoDao.listar).
SELECT p.id_proyecto, p.id_lider, u.nombre AS lider, p.nombre, p.objetivo,
       p.presupuesto_inicial, p.fecha_inicio, p.fecha_fin, p.estado
FROM proyectos p
JOIN usuarios u ON u.id_usuario = p.id_lider
ORDER BY p.id_proyecto;

-- Q3. Hitos de un proyecto con su responsable vigente (HitoDao.listarPorProyecto).
SELECT h.id_hito, h.id_proyecto, h.nombre, h.descripcion, h.fecha_limite,
       h.porcentaje_avance, h.estado, a.id_usuario AS id_responsable, u.nombre AS responsable
FROM hitos h
LEFT JOIN asignaciones a ON a.id_hito = h.id_hito AND a.fecha_desasignacion IS NULL
LEFT JOIN usuarios u ON u.id_usuario = a.id_usuario
WHERE h.id_proyecto = 1
ORDER BY h.fecha_limite;

-- Q4. Hitos asignados a un voluntario (HitoDao.listarPorResponsable).
SELECT h.id_hito, p.nombre AS proyecto, h.nombre AS hito, h.fecha_limite,
       h.porcentaje_avance, h.estado
FROM asignaciones a
JOIN hitos h ON h.id_hito = a.id_hito
JOIN proyectos p ON p.id_proyecto = h.id_proyecto
WHERE a.id_usuario = 3 AND a.fecha_desasignacion IS NULL
ORDER BY h.fecha_limite;

-- Q5. Actualización del avance de un hito (HitoDao.actualizarAvance).
UPDATE hitos
SET porcentaje_avance = 60.00,
    estado = 'EN_PROGRESO'
WHERE id_hito = 2;

-- Q6. Registro de un costo (CostoDao.insertar).
INSERT INTO costos (id_hito, fecha, monto, descripcion)
VALUES (2, CURRENT_DATE, 3500.00, 'Materiales de distribución');

-- Q7. Tablero: avance promedio y costo ejecutado por proyecto (equivalente SQL de los
-- cálculos que la aplicación realiza en Proyecto.calcularAvance y calcularCostoEjecutado).
SELECT p.id_proyecto, p.nombre, p.estado, u.nombre AS lider,
       COALESCE((SELECT ROUND(AVG(h.porcentaje_avance), 2)
                 FROM hitos h WHERE h.id_proyecto = p.id_proyecto), 0) AS avance_promedio,
       p.presupuesto_inicial,
       COALESCE((SELECT SUM(c.monto)
                 FROM costos c JOIN hitos h ON h.id_hito = c.id_hito
                 WHERE h.id_proyecto = p.id_proyecto), 0) AS costo_ejecutado
FROM proyectos p
JOIN usuarios u ON u.id_usuario = p.id_lider;

-- Q8. Alertas de atraso: hitos vencidos que no están completados.
SELECT p.nombre AS proyecto, h.nombre AS hito, h.fecha_limite,
       DATEDIFF(CURRENT_DATE, h.fecha_limite) AS dias_atraso
FROM hitos h
JOIN proyectos p ON p.id_proyecto = h.id_proyecto
WHERE h.fecha_limite < CURRENT_DATE AND h.estado <> 'COMPLETADO';

-- Q9. Reasignación de responsable (HitoDao.asignarResponsable, dentro de una transacción).
UPDATE asignaciones SET fecha_desasignacion = CURRENT_DATE
WHERE id_hito = 3 AND fecha_desasignacion IS NULL;
INSERT INTO asignaciones (id_hito, id_usuario, fecha_asignacion)
VALUES (3, 6, CURRENT_DATE)
ON DUPLICATE KEY UPDATE fecha_asignacion = CURRENT_DATE, fecha_desasignacion = NULL;

-- Q10. Baja lógica de un proyecto: no se borra, se cambia el estado (regla de negocio).
UPDATE proyectos SET estado = 'CANCELADO' WHERE id_proyecto = 4;

-- Q11. Borrado controlado de un costo cargado por error y verificación.
DELETE FROM costos WHERE id_costo = 9;
SELECT COUNT(*) AS costos_restantes FROM costos;
