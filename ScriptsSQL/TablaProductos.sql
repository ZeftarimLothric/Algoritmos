CREATE DATABASE punto_de_venta;

USE punto_de_venta;

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo_barras VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    cantidad INT NOT NULL
);