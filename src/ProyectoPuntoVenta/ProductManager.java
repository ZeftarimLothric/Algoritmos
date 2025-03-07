package ProyectoPuntoVenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ProyectoPuntoVenta.Clases.Producto;

public class ProductManager {
    private List<Producto> productosDisponibles;
    private Map<Producto, Integer> productosVendidos;
    private Connection connection;

    public ProductManager() {
        productosDisponibles = new ArrayList<>();
        productosVendidos = new HashMap<>();
        connectToDatabase();
        loadProductosFromDatabase();
        loadProductosVendidosFromDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/punto_de_venta", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProductosFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM productos");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String codigoBarras = resultSet.getString("codigo_barras");
                double precio = resultSet.getDouble("precio");
                int cantidad = resultSet.getInt("cantidad");
                Producto producto = new Producto(id, nombre, codigoBarras, precio, cantidad);
                productosDisponibles.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadProductosVendidosFromDatabase() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM productos_vendidos");
            while (resultSet.next()) {
                int productoId = resultSet.getInt("producto_id");
                @SuppressWarnings("unused")
                String nombre = resultSet.getString("nombre");
                int cantidadVendida = resultSet.getInt("cantidad_vendida");
                Producto producto = getProductoPorId(productoId);
                if (producto != null) {
                    productosVendidos.put(producto, cantidadVendida);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public Map<Producto, Integer> getProductosVendidos() {
        return productosVendidos;
    }

    public void actualizarProducto(Producto producto) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE productos SET cantidad = ? WHERE id = ?");
            preparedStatement.setInt(1, producto.getCantidad());
            preparedStatement.setInt(2, producto.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void agregarProductoVendido(Producto producto, int cantidad) {
        productosVendidos.put(producto, productosVendidos.getOrDefault(producto, 0) + cantidad);
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO productos_vendidos (producto_id, nombre, cantidad_vendida) VALUES (?, ?, ?) "
                            + "ON DUPLICATE KEY UPDATE cantidad_vendida = cantidad_vendida + ?");
            preparedStatement.setInt(1, producto.getId());
            preparedStatement.setString(2, producto.getNombre());
            preparedStatement.setInt(3, cantidad);
            preparedStatement.setInt(4, cantidad);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void agregarProducto(Producto producto) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO productos (nombre, codigo_barras, precio, cantidad) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, producto.getNombre());
            preparedStatement.setString(2, producto.getCodigoBarras());
            preparedStatement.setDouble(3, producto.getPrecio());
            preparedStatement.setInt(4, producto.getCantidad());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                producto.setId(generatedKeys.getInt(1));
            }
            productosDisponibles.add(producto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarProducto(Producto producto) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM productos WHERE id = ?");
            preparedStatement.setInt(1, producto.getId());
            preparedStatement.executeUpdate();
            productosDisponibles.remove(producto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reiniciarAutoIncrement() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE productos AUTO_INCREMENT = 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener un producto por su ID
    public Producto getProductoPorId(int id) {
        for (Producto producto : productosDisponibles) {
            if (producto.getId() == id) {
                return producto;
            }
        }
        return null; // Retorna null si no se encuentra el producto
    }

    // Método para obtener un producto por su nombre
    public Producto getProductoPorNombre(String nombre) {
        for (Producto producto : productosDisponibles) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }
        return null; // Retorna null si no se encuentra el producto
    }
}