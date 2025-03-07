package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class AgregarProductoView extends JPanel {
    private JComboBox<String> productosComboBox;
    private JTextField nombreField;
    private JTextField codigoField;
    private JTextField precioField;
    private JTextField cantidadField;
    private ProductManager productoManager;
    private VisualizarProductosView visualizarProductosView;
    private CobroDeProductosView agregarProductosYCobroView;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @SuppressWarnings("unused")
    public AgregarProductoView(ProductManager productoManager, VisualizarProductosView visualizarProductosView,
            CobroDeProductosView agregarProductosYCobroView) {
        this.productoManager = productoManager;
        this.visualizarProductosView = visualizarProductosView;
        this.agregarProductosYCobroView = agregarProductosYCobroView;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        add(new JLabel("Seleccionar Producto:"), gbc);
        productosComboBox = new JComboBox<>();
        productosComboBox.addItem("Nuevo Producto");
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosComboBox.addItem(producto.getNombre());
        }
        productosComboBox.addActionListener(e -> seleccionarProducto());
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(productosComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.5;
        add(new JLabel("Nombre del Producto:"), gbc);
        nombreField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(nombreField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.5;
        add(new JLabel("Código de Barras:"), gbc);
        codigoField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(codigoField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.5;
        add(new JLabel("Precio:"), gbc);
        precioField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(precioField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.5;
        add(new JLabel("Cantidad:"), gbc);
        cantidadField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(cantidadField, gbc);

        JButton agregarButton = new JButton("Agregar Producto");
        agregarButton.setBackground(new Color(0, 123, 255));
        agregarButton.setForeground(Color.WHITE);
        agregarButton.setFocusPainted(false);
        agregarButton.addActionListener(e -> agregarProducto());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(agregarButton, gbc);

        JButton eliminarButton = new JButton("Eliminar Producto");
        eliminarButton.setBackground(new Color(220, 53, 69));
        eliminarButton.setForeground(Color.WHITE);
        eliminarButton.setFocusPainted(false);
        eliminarButton.addActionListener(e -> eliminarProducto());
        gbc.gridy++;
        add(eliminarButton, gbc);
    }

    private void seleccionarProducto() {
        String nombreSeleccionado = (String) productosComboBox.getSelectedItem();
        if (nombreSeleccionado != null && !nombreSeleccionado.equals("Nuevo Producto")) {
            for (Producto producto : productoManager.getProductosDisponibles()) {
                if (producto.getNombre().equals(nombreSeleccionado)) {
                    nombreField.setText(producto.getNombre());
                    codigoField.setText(producto.getCodigoBarras());
                    precioField.setText(decimalFormat.format(producto.getPrecio()));
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