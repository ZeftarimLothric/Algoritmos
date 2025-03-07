package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class AgregarProductosYCobroView extends JPanel {
    private DefaultListModel<String> carritoModel;
    private JLabel totalLabel;
    private Map<Producto, Integer> carrito;
    private DefaultListModel<String> productosModel;
    private VisualizarProductosView visualizarProductosView;
    @SuppressWarnings("unused")
    private ProductosMasVendidosView productosMasVendidosView;
    private ProductManager productoManager;

    @SuppressWarnings("unused")
    public AgregarProductosYCobroView(ProductManager productoManager, VisualizarProductosView visualizarProductosView, ProductosMasVendidosView productosMasVendidosView) {
        this.productoManager = productoManager;
        this.visualizarProductosView = visualizarProductosView;
        this.productosMasVendidosView = productosMasVendidosView;

        setLayout(new BorderLayout());

        // Panel para mostrar productos disponibles
        JPanel productosPanel = new JPanel(new BorderLayout());
        productosModel = new DefaultListModel<>();
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosModel.addElement(producto.toString());
        }
        JList<String> productosList = new JList<>(productosModel);
        productosList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = productosList.locationToIndex(e.getPoint());
                    Producto producto = productoManager.getProductosDisponibles().get(index);
                    agregarProductoAlCarrito(producto);
                }
            }
        });
        JScrollPane productosScrollPane = new JScrollPane(productosList);
        productosPanel.add(new JLabel("Productos Disponibles"), BorderLayout.NORTH);
        productosPanel.add(productosScrollPane, BorderLayout.CENTER);
        add(productosPanel, BorderLayout.WEST);

        // Panel para mostrar el carrito de compras
        carritoModel = new DefaultListModel<>();
        JList<String> carritoList = new JList<>(carritoModel);
        JScrollPane carritoScrollPane = new JScrollPane(carritoList);
        add(carritoScrollPane, BorderLayout.CENTER);

        // Panel para mostrar el total y realizar el cobro
        JPanel cobroPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Total: $0.00");
        cobroPanel.add(totalLabel, BorderLayout.NORTH);

        JButton cobrarButton = new JButton("Cobrar");
        cobroPanel.add(cobrarButton, BorderLayout.SOUTH);

        add(cobroPanel, BorderLayout.SOUTH);

        carrito = new HashMap<>();

        // Acción para realizar el cobro
        cobrarButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(AgregarProductosYCobroView.this, "Cobro realizado con éxito. Total: $" + calcularTotal());
            actualizarProductosVendidos();
            carritoModel.clear();
            carrito.clear();
            actualizarTotal();
            visualizarProductosView.actualizarTabla();
            productosMasVendidosView.actualizarTabla();
        });
    }

    private void agregarProductoAlCarrito(Producto producto) {
        if (producto.getCantidad() > 0) {
            carrito.put(producto, carrito.getOrDefault(producto, 0) + 1);
            producto.reducirCantidad(1);
            productoManager.actualizarProducto(producto);
            actualizarCarrito();
            actualizarProductosDisponibles();
            actualizarTotal();
            visualizarProductosView.actualizarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No hay suficiente cantidad de " + producto.getNombre());
        }
    }

    private void actualizarCarrito() {
        carritoModel.clear();
        for (Map.Entry<Producto, Integer> entry : carrito.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            carritoModel.addElement(producto.getNombre() + " - $" + producto.getPrecio() + " x" + cantidad);
        }
    }

    private void actualizarProductosDisponibles() {
        productosModel.clear();
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosModel.addElement(producto.toString());
        }
    }

    private void actualizarTotal() {
        totalLabel.setText("Total: $" + calcularTotal());
    }

    private double calcularTotal() {
        double total = 0;
        for (Map.Entry<Producto, Integer> entry : carrito.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            total += producto.getPrecio() * cantidad;
        }
        return total;
    }

    private void actualizarProductosVendidos() {
        for (Map.Entry<Producto, Integer> entry : carrito.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            productoManager.agregarProductoVendido(producto, cantidad);
        }
    }

    public void actualizarListaProductos() {
        actualizarProductosDisponibles();
    }
}