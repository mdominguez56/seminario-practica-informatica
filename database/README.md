# Base de datos del sistema

La base `fundacion_cana` utiliza MySQL 8 y el motor InnoDB.

## Archivos

| Archivo | Contenido |
|---|---|
| `schema.sql` | Crea la base, las tablas, las claves primarias y foráneas, las restricciones CHECK y los índices. **Borra la base si ya existe.** |
| `seed.sql` | Inserta roles, usuarios (contraseñas con hash SHA-256), proyectos, hitos, asignaciones y costos de prueba. |
| `queries.sql` | Consultas de referencia: las que usa la aplicación Java (Q1 a Q6) y consultas de control (tablero, alertas, reasignación, baja lógica y borrado). |
| `dbdiagram.dbml` | Modelo relacional para importar en [dbdiagram.io](https://dbdiagram.io/). |

El diagrama entidad-relación con tipos de datos, PK, FK y cardinalidades está en
[`../docs/diagramas/der.png`](../docs/diagramas/der.png) (fuente: `der.puml`).

## Orden de ejecución

```bash
mysql --default-character-set=utf8mb4 -uroot -p < schema.sql
mysql --default-character-set=utf8mb4 -uroot -p < seed.sql
```

`queries.sql` modifica datos (actualiza, inserta y borra registros): conviene ejecutarlo solo para verificar las consultas y luego volver a cargar `seed.sql`.
