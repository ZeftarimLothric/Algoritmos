package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.swing.*;
import java.awt.*;

public class AgregarProductoView extends JPanel {
    private JComboBox<String> productosComboBox;
    private JTextField nombreField;
    private JTextField codigoField;
    private JTextField precioField;
    private JTextField cantidadField;
    private ProductManager productoManager;
    private VisualizarProductosView visualizarProductosView;
    private AgregarProductosYCobroView agregarProductosYCobroView;

    @SuppressWarnings("unused")
    public AgregarProductoView(ProductManager productoManager, VisualizarProductosView visualizarProductosView, AgregarProductosYCobroView agregarProductosYCobroView) {
        this.productoManager = productoManager;
        this.visualizarProductosView = visualizarProductosView;
        this.agregarProductosYCobroView = agregarProductosYCobroView;

        setLayout(new GridLayout(7, 2));

        add(new JLabel("Seleccionar Producto:"));
        productosComboBox = new JComboBox<>();
        productosComboBox.addItem("Nuevo Producto");
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosComboBox.addItem(producto.getNombre());
        }
        productosComboBox.addActionListener(e -> seleccionarProducto());
        add(productosComboBox);

        add(new JLabel("Nombre del Producto:"));
        nombreField = new JTextField();
        add(nombreField);

        add(new JLabel("Código de Barras:"));
        codigoField = new JTextField();
        add(codigoField);

        add(new JLabel("Precio:"));
        precioField = new JTextField();
        add(precioField);

        add(new JLabel("Cantidad:"));
        cantidadField = new JTextField();
        add(cantidadField);

        JButton agregarButton = new JButton("Agregar Producto");
        agregarButton.addActionListener(e -> agregarProducto());
        add(agregarButton);

        JButton eliminarButton = new JButton("Eliminar Producto");
        eliminarButton.addActionListener(e -> eliminarProducto());
        add(eliminarButton);
    }

    private void seleccionarProducto() {
        String nombreSeleccionado = (String) productosComboBox.getSelectedItem();
        if (nombreSeleccionado != null && !nombreSeleccionado.equals("Nuevo Producto")) {
            for (Producto producto : productoManager.getProductosDisponibles()) {
                if (producto.getNombre().equals(nombreSeleccionado)) {
                    nombreField.setText(producto.getNombre());
                    codigoField.setText(producto.getCodigoBarras());
                    precioField.setText(String.valueOf(producto.getPrecio()));
                    cantidadField.setText(String.valueOf(producto.getCantidad()));
                    break;
                }
            }
        } else {
            nombreField.setText("");
            codigoField.setText("");
            precioField.setText("");
            cantidadField.setText("");
        }
    }

    private void agregarProducto() {
        String nombre = nombreField.getText();
        String codigo = codigoField.getText();
        double precio = Double.parseDouble(precioField.getText());
        int cantidad = Integer.parseInt(cantidadField.getText());

        Producto productoExistente = null;
        for (Producto producto : productoManager.getProductosDisponibles()) {
            if (producto.getNombre().equals(nombre)) {
                productoExistente = producto;
                break;
            }
        }

        if (productoExistente != null) {
            productoExistente.setCantidad(productoExistente.getCantidad() + cantidad);
            productoManager.actualizarProducto(productoExistente);
        } else {
            Producto nuevoProducto = new Producto(nombre, codigo, precio, cantidad);
            productoManager.agregarProducto(nuevoProducto);
        }

        visualizarProductosView.actualizarTabla();
        agregarProductosYCobroView.actualizarListaProductos();
        actualizarListaProductos();
        JOptionPane.showMessageDialog(this, "Producto agregado/actualizado con éxito.");
    }

    private void eliminarProducto() {
        String nombre = nombreField.getText();
        Producto productoExistente = null;
        for (Producto producto : productoManager.getProductosDisponibles()) {
            if (producto.getNombre().equals(nombre)) {
                productoExistente = producto;
                break;
            }
        }

        if (productoExistente != null) {
            productoManager.eliminarProducto(productoExistente);
            visualizarProductosView.actualizarTabla();
            agregarProductosYCobroView.actualizarListaProductos();
            actualizarListaProductos();
            JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.");
            nombreField.setText("");
            codigoField.setText("");
            precioField.setText("");
            cantidadField.setText("");

            // Reiniciar AUTO_INCREMENT si no hay productos
            if (productoManager.getProductosDisponibles().isEmpty()) {
                productoManager.reiniciarAutoIncrement();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
        }
    }

    private void actualizarListaProductos() {
        productosComboBox.removeAllItems();
        productosComboBox.addItem("Nuevo Producto");
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosComboBox.addItem(producto.getNombre());
        }
    }
}