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
    }

    private void connectToDatabase() {
        try {
            //La conexión a la base de datos se realiza con el usuario root y sin contraseña, y con el nonbre de la base de datos "punto_de_venta"
            //De formal local en la mayoria de los casos solo hay que cambiar el nombre de la base de datos, ya existe un script sql en el 
            //proyecto en la carpeta llamada ScriptSQL para crear la base de datos en MySQL
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
                String nombre = resultSet.getString("nombre");
                String codigoBarras = resultSet.getString("codigo_barras");
                double precio = resultSet.getDouble("precio");
                int cantidad = resultSet.getInt("cantidad");
                productosDisponibles.add(new Producto(nombre, codigoBarras, precio, cantidad));
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
                "UPDATE productos SET cantidad = ? WHERE codigo_barras = ?"
            );
            preparedStatement.setInt(1, producto.getCantidad());
            preparedStatement.setString(2, producto.getCodigoBarras());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void agregarProductoVendido(Producto producto, int cantidad) {
        productosVendidos.put(producto, productosVendidos.getOrDefault(producto, 0) + cantidad);
    }

    public void agregarProducto(Producto producto) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO productos (nombre, codigo_barras, precio, cantidad) VALUES (?, ?, ?, ?)"
            );
            preparedStatement.setString(1, producto.getNombre());
            preparedStatement.setString(2, producto.getCodigoBarras());
            preparedStatement.setDouble(3, producto.getPrecio());
            preparedStatement.setInt(4, producto.getCantidad());
            preparedStatement.executeUpdate();
            productosDisponibles.add(producto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarProducto(Producto producto) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM productos WHERE codigo_barras = ?"
            );
            preparedStatement.setString(1, producto.getCodigoBarras());
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
}