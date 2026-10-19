# Sistema de gestión de proyectos - Fundación María de Caná

Prototipo desarrollado para la materia **Seminario de Práctica de Informática** (Universidad Siglo 21).
Centraliza la gestión de proyectos solidarios, hitos, responsables, costos e indicadores de seguimiento
de la Fundación María de Caná (Rosario).

**Tecnologías:** Java 21, JavaFX 21, MySQL 8, JDBC (MySQL Connector/J), Maven, JUnit 5.
**Arquitectura:** Modelo-Vista-Controlador con una capa de acceso a datos (DAO).

## Funcionalidades implementadas (TP3)

| Requisito | Funcionalidad |
|---|---|
| RF01 / CU01 | Inicio de sesión con email y contraseña (hash SHA-256) y menú según el rol. |
| RF02-RF04 / CU02 | Alta, consulta, modificación y baja lógica (cancelación) de proyectos. |
| RF05-RF06 / CU03 | Alta, modificación y eliminación de hitos con asignación de responsable. |
| RF07 | Actualización del avance de un hito (el voluntario solo puede actualizar los propios). |
| RF08 | Registro y eliminación de costos por hito. |
| RF09-RF11 / CU04 | Tablero de control con indicadores, gráfico de presupuesto y alertas de atraso y desvío. |
| Extra | Búsqueda (binaria por código, lineal por nombre) y ordenamiento por inserción de proyectos. |

## Requisitos

- JDK 21 o superior.
- Maven 3.9 o superior (o la integración de Maven de IntelliJ IDEA / NetBeans / Eclipse).
- MySQL 8 local, o Docker.

## Puesta en marcha

### 1. Base de datos

Con Docker:

```bash
docker run --name fundacion-mysql -e MYSQL_ROOT_PASSWORD=tp3demo -p 3306:3306 -d mysql:8.0
docker exec -i fundacion-mysql mysql --default-character-set=utf8mb4 -uroot -ptp3demo < database/schema.sql
docker exec -i fundacion-mysql mysql --default-character-set=utf8mb4 -uroot -ptp3demo < database/seed.sql
```

Con un MySQL instalado localmente, ejecutar los mismos scripts con el cliente `mysql` o desde MySQL Workbench.

### 2. Configuración de la conexión

Copiar `db.properties.example` como `db.properties` en la raíz del proyecto y completar la contraseña.
También se pueden usar las variables de entorno `FUNDACION_DB_URL`, `FUNDACION_DB_USUARIO` y `FUNDACION_DB_PASSWORD`.

### 3. Compilar y ejecutar

```bash
mvn javafx:run                       # ejecuta la aplicación
mvn package                          # compila, ejecuta las pruebas y genera target/gestion-proyectos.jar
java -jar target/gestion-proyectos.jar
```

Desde un IDE: importar el proyecto como proyecto Maven y ejecutar la clase `com.fundacioncana.Launcher`.

## Usuarios de prueba

| Rol | Email | Contraseña |
|---|---|---|
| Consejo Directivo | oscar.directiva@cana.org | directivo123 |
| Líder de proyecto | carlos.lider@cana.org | lider123 |
| Líder de proyecto | lucia.lider@cana.org | lider123 |
| Voluntario | ana.voluntaria@cana.org | voluntario123 |
| Voluntario | martin.voluntario@cana.org | voluntario123 |
| Voluntario | sofia.voluntaria@cana.org | voluntario123 |

## Estructura del repositorio

```text
.
├── database/                 Scripts SQL (schema, seed, queries) y modelo dbdiagram
├── docs/
│   ├── capturas/             Capturas del prototipo en funcionamiento
│   └── diagramas/            Diagramas UML y DER (fuentes PlantUML .puml y PNG)
├── src/main/java/com/fundacioncana/
│   ├── App.java, Launcher.java, Contexto.java
│   ├── modelo/               Entidades, jerarquía de usuarios, alertas y enumeraciones
│   ├── controlador/          Lógica de aplicación y control de permisos
│   ├── dao/                  Acceso a datos con JDBC (interfaz CrudDao y DAOs)
│   ├── excepciones/          Excepciones propias del sistema
│   ├── util/                 Algoritmos de ordenación y búsqueda, formato y seguridad
│   └── vista/                Pantallas JavaFX
├── src/main/resources/css/   Hoja de estilos de la interfaz
├── src/test/java/            Pruebas unitarias (JUnit 5)
├── db.properties.example
└── pom.xml
```

## Autor

Matías Domínguez Alonso - Licenciatura en Informática, Universidad Siglo 21.
