-- =====================================================================
--  Catálogo de videojuegos - UD3 Acceso a Datos
--  SGBD: PostgreSQL
--  Script re-ejecutable: borra las tablas si existen y las vuelve a crear.
-- =====================================================================

DROP TABLE IF EXISTS listas_videojuegos;
DROP TABLE IF EXISTS listas;
DROP TABLE IF EXISTS jugadores;
DROP TABLE IF EXISTS videojuegos;

-- ---------------------------------------------------------------------
--  Tablas
-- ---------------------------------------------------------------------

CREATE TABLE videojuegos (
    id      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo  VARCHAR(100)     NOT NULL,
    genero  VARCHAR(50)      NOT NULL,
    anio    INTEGER          NOT NULL,
    nota    DOUBLE PRECISION NOT NULL
);

CREATE TABLE jugadores (
    id_jugador INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre     VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE listas (
    id_lista   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre     VARCHAR(50) NOT NULL,
    id_jugador INTEGER     NOT NULL,
    CONSTRAINT fk_listas_jugador
        FOREIGN KEY (id_jugador) REFERENCES jugadores (id_jugador)
        ON DELETE CASCADE
);

CREATE TABLE listas_videojuegos (
    id_lista       INTEGER NOT NULL,
    id_videojuego  INTEGER NOT NULL,
    fecha_añadido  DATE    NOT NULL,
    CONSTRAINT pk_listas_videojuegos PRIMARY KEY (id_lista, id_videojuego),
    CONSTRAINT fk_lv_lista
        FOREIGN KEY (id_lista) REFERENCES listas (id_lista)
        ON DELETE CASCADE,
    CONSTRAINT fk_lv_videojuego
        FOREIGN KEY (id_videojuego) REFERENCES videojuegos (id)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
--  Datos de prueba
-- ---------------------------------------------------------------------

INSERT INTO videojuegos (titulo, genero, anio, nota) VALUES
    ('Zelda: Breath of the Wild', 'Aventura',   2017, 9.7),
    ('Red Dead Redemption 2',     'Acción',     2018, 9.5),
    ('Hades',                     'Roguelike',  2020, 9.3),
    ('Hollow Knight',             'Metroidvania', 2017, 9.0),
    ('Celeste',                   'Plataformas', 2018, 9.1);

INSERT INTO jugadores (nombre, email) VALUES
    ('Ana',    'ana@example.com'),
    ('Carlos', 'carlos@example.com'),
    ('Lucía',  'lucia@example.com');

-- Cada lista pertenece a un jugador distinto
INSERT INTO listas (nombre, id_jugador) VALUES
    ('Pendientes',  1),
    ('Completados', 2),
    ('Favoritos',   3);

INSERT INTO listas_videojuegos (id_lista, id_videojuego, fecha_añadido) VALUES
    (1, 3, '2026-11-01'),
    (1, 4, '2026-11-02'),
    (2, 1, '2026-10-15'),
    (3, 2, '2026-09-20'),
    (3, 5, '2026-11-10');
