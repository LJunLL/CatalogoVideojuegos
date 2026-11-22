# Catálogo de Videojuegos (UD3 – Persistencia en BD)

Aplicación de consola en Kotlin que gestiona un catálogo de videojuegos, jugadores y sus listas
(Pendientes, Completados, Favoritos…). Usa JDBC con el patrón DAO sobre PostgreSQL.

## Estructura de la base de datos

```
jugadores (id_jugador PK, nombre, email UNIQUE)
    │ 1
    │
    │ N
listas (id_lista PK, nombre, id_jugador FK → jugadores)
    │ 1
    │
    │ N
listas_videojuegos (id_lista PK/FK → listas, id_videojuego PK/FK → videojuegos, fecha_añadido)
    │ N
    │
    │ 1
videojuegos (id PK, titulo, genero, anio, nota)
```

- Un jugador tiene varias listas; cada lista pertenece a un único jugador.
- `listas_videojuegos` resuelve la relación N:M entre listas y videojuegos.
  Su clave primaria es compuesta (`id_lista`, `id_videojuego`), así un juego no puede repetirse en la misma lista.
- Script completo (tablas + datos de prueba): [`datos/videojuegos.sql`](datos/videojuegos.sql)

## Instrucciones de ejecución

**Requisitos:** JDK 21, Docker (o un PostgreSQL 16 instalado).

1. Arrancar la base de datos (crea la BD `videojuegos` y ejecuta el script automáticamente):
   ```bash
   docker compose up -d
   ```
   Sin Docker: crear una BD `videojuegos` con usuario/contraseña `videojuegos` y ejecutar
   `datos/videojuegos.sql` desde la herramienta Database de IntelliJ o con `psql`.

2. La conexión se configura en `src/main/kotlin/ConexionBD.kt`
   (por defecto `localhost:5433`, BD `videojuegos`, usuario y contraseña `videojuegos`).

3. Abrir el proyecto en IntelliJ (proyecto Gradle) y ejecutar `Main.kt`.

## Decisiones de diseño

- **PostgreSQL** en lugar de MySQL: es el SGBD relacional open source más usado actualmente en proyectos nuevos
  y respeta el estándar SQL de forma más estricta. Para los IDs se usa `GENERATED ALWAYS AS IDENTITY`
  (equivalente estándar de `AUTOINCREMENT` / `AUTO_INCREMENT`).
- **Campos de `jugadores`:** `nombre` y `email`; el email es `UNIQUE` para que no se repitan jugadores.
- **Integridad referencial:** todas las FK usan `ON DELETE CASCADE`. Al borrar un jugador se borran sus listas,
  y al borrar una lista o un videojuego se borran sus filas en `listas_videojuegos`. Así nunca quedan
  referencias huérfanas.
- **Crear lista con un jugador inexistente:** la FK impide el INSERT y se muestra un mensaje de error.
- **Añadir videojuego a una lista (opción 10):** usa `anyadirALista`, que en una única transacción comprueba
  que el videojuego existe e inserta el registro. Si algo falla, se hace `rollback`.
- **`nota` como `DOUBLE PRECISION`:** corresponde exactamente al `Double` de Kotlin.
