DROP TABLE persona IF EXISTS;

CREATE TABLE persona (
ID BIGINT IDENTITY NOT NULL PRIMARY KEY,
    nombre VARCHAR(20),
    apellido VARCHAR(20),
    telefono VARCHAR(20)
    );