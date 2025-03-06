package ProyectoPuntoVenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ProyectoPuntoVenta.Vistas.AgregarProductosYCobroView;

public class ProductManager {
    private List<AgregarProductosYCobroView.Producto> productosDisponibles;
    private Map<AgregarProductosYCobroView.Producto, Integer> productosVendidos;
    private Connection connection;

    public ProductManager() {
        productosDisponibles = new ArrayList<>();
        productosVendidos = new HashMap<>();
        connectToDatabase();
        loadProductosFromDatabase();
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
                String nombre = resultSet.getString("nombre");
                String codigoBarras = resultSet.getString("codigo_barras");
                double precio = resultSet.getDouble("precio");
                int cantidad = resultSet.getInt("cantidad");
                productosDisponibles.add(new AgregarProductosYCobroView.Producto(nombre, codigoBarras, precio, cantidad));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<AgregarProductosYCobroView.Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public Map<AgregarProductosYCobroView.Producto, Integer> getProductosVendidos() {
        return productosVendidos;
    }

    public void actualizarProducto(AgregarProductosYCobroView.Producto producto) {
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

    public void agregarProductoVendido(AgregarProductosYCobroView.Producto producto, int cantidad) {
        productosVendidos.put(producto, productosVendidos.getOrDefault(producto, 0) + cantidad);
    }

    public void agregarProducto(AgregarProductosYCobroView.Producto producto) {
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

    public void eliminarProducto(AgregarProductosYCobroView.Producto producto) {
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
}