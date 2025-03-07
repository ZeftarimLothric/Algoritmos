CREATE DATABASE punto_de_venta;

USE punto_de_venta;

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    codigo_barras VARCHAR(255) NOT NULL,
    precio DOUBLE NOT NULL,
    cantidad INT NOT NULL
);

CREATE TABLE productos_vendidos (
    producto_id INT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    cantidad_vendida INT NOT NULL,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

SELECT * FROM punto_de_venta.productos;
SELECT * FROM punto_de_venta.productos_vendidos;

ALTER TABLE productos AUTO_INCREMENT = 1;

INSERT INTO Productos (Nombre, codigo_barras, precio, cantidad) VALUES
('Camiseta Roja', '1234567890123', 20.99, 50),
('Jeans Azul', '1234567890124', 35.50, 30),
('Zapatillas Deportivas', '1234567890125', 45.75, 20),
('Gorra Deportiva', '1234567890126', 15.00, 100),
('Chaqueta de Invierno', '1234567890127', 70.99, 15),
('Botines de Cuero', '1234567890128', 60.00, 25),
('Mochila Escolar', '1234567890129', 22.50, 40),
('Pulsera de Cuero', '1234567890130', 12.00, 75),
('Reloj Deportivo', '1234567890131', 30.00, 60),
('Auriculares Bluetooth', '1234567890132', 25.99, 35);