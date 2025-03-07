package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.Copies;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CobroDeProductosView extends JPanel {
    private DefaultListModel<String> carritoModel;
    private JLabel totalLabel;
    private Map<Producto, Integer> carrito;
    private DefaultListModel<String> productosModel;
    private VisualizarProductosView visualizarProductosView;
    @SuppressWarnings("unused")
    private ProductosMasVendidosView productosMasVendidosView;
    private ProductManager productoManager;
    private String rutaGuardado;
    private JComboBox<Producto> productosComboBox;
    private JTextField cantidadField;
    private JLabel productoInfoLabel;

    @SuppressWarnings("unused")
    public CobroDeProductosView(ProductManager productoManager, VisualizarProductosView visualizarProductosView,
            ProductosMasVendidosView productosMasVendidosView, String rutaGuardado) {
        this.productoManager = productoManager;
        this.visualizarProductosView = visualizarProductosView;
        this.productosMasVendidosView = productosMasVendidosView;
        this.rutaGuardado = rutaGuardado;

        setLayout(new BorderLayout());

        // Panel para mostrar productos disponibles
        JPanel productosPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        productosModel = new DefaultListModel<>();
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosModel.addElement(producto.toString());
        }
        productosComboBox = new JComboBox<>();
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosComboBox.addItem(producto);
        }
        productosComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto) {
                    Producto producto = (Producto) value;
                    setText(producto.getNombre() + " - $" + producto.getPrecio());
                }
                return c;
            }
        });
        productosComboBox.setPreferredSize(new Dimension(200, 25)); // Ajustar la altura del JComboBox
        productosComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Producto producto = (Producto) productosComboBox.getSelectedItem();
                if (producto != null) {
                    productoInfoLabel.setText(
                            "Precio: $" + producto.getPrecio() + " | Cantidad disponible: " + producto.getCantidad());
                }
            }
        });
        productosPanel.add(new JLabel("Seleccionar Producto:"), gbc);
        gbc.gridy++;
        productosPanel.add(productosComboBox, gbc);

        // Campo de entrada para la cantidad
        gbc.gridy++;
        productosPanel.add(new JLabel("Cantidad:"), gbc);
        cantidadField = new JTextField();
        cantidadField.setPreferredSize(new Dimension(200, 25)); // Ajustar la altura del campo de entrada
        gbc.gridy++;
        productosPanel.add(cantidadField, gbc);

        // Botón para agregar el producto con la cantidad especificada
        JButton agregarButton = new JButton("Agregar");
        agregarButton.addActionListener(e -> {
            Producto producto = (Producto) productosComboBox.getSelectedItem();
            int cantidad;
            try {
                cantidad = Integer.parseInt(cantidadField.getText());
                if (cantidad <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una cantidad válida.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            agregarProductoAlCarrito(producto, cantidad);
        });
        gbc.gridy++;
        productosPanel.add(agregarButton, gbc);

        // Label para mostrar información adicional del producto
        productoInfoLabel = new JLabel("Seleccione un producto para ver más información.");
        gbc.gridy++;
        productosPanel.add(productoInfoLabel, gbc);

        add(productosPanel, BorderLayout.WEST);

        // Panel para mostrar el carrito de compras
        carritoModel = new DefaultListModel<>();
        JList<String> carritoList = new JList<>(carritoModel);
        carritoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = carritoList.locationToIndex(e.getPoint());
                    Producto producto = (Producto) carrito.keySet().toArray()[index];
                    eliminarProductoDelCarrito(producto);
                }
            }
        });
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
            String ticket = generarTicket();
            int respuesta = JOptionPane.showConfirmDialog(CobroDeProductosView.this,
                    "¿Desea generar el ticket?\n\n" + ticket, "Confirmación", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                imprimir(ticket);
                guardarTicketEnArchivo(ticket);
                JOptionPane.showMessageDialog(CobroDeProductosView.this,
                        "Cobro realizado con éxito. Total: $" + calcularTotal());
                actualizarProductosVendidos();
                carritoModel.clear();
                carrito.clear();
                actualizarTotal();
                visualizarProductosView.actualizarTabla();
                productosMasVendidosView.actualizarTabla();
            }
        });
    }

    private void agregarProductoAlCarrito(Producto producto, int cantidad) {
        if (producto.getCantidad() >= cantidad) {
            carrito.put(producto, carrito.getOrDefault(producto, 0) + cantidad);
            producto.reducirCantidad(cantidad);
            productoManager.actualizarProducto(producto);
            actualizarCarrito();
            actualizarProductosDisponibles();
            actualizarTotal();
            visualizarProductosView.actualizarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "No hay suficiente cantidad de " + producto.getNombre());
        }
    }

    private void eliminarProductoDelCarrito(Producto producto) {
        if (carrito.containsKey(producto)) {
            int cantidad = carrito.get(producto);
            if (cantidad > 1) {
                carrito.put(producto, cantidad - 1);
            } else {
                carrito.remove(producto);
            }
            producto.setCantidad(producto.getCantidad() + 1);
            productoManager.actualizarProducto(producto);
            actualizarCarrito();
            actualizarProductosDisponibles();
            actualizarTotal();
            visualizarProductosView.actualizarTabla();
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

    public String generarTicket() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String fechaHora = sdf.format(new Date());
        sb.append("================================\n");
        sb.append("          TICKET DE COMPRA      \n");
        sb.append("================================\n");
        sb.append("Fecha y Hora: ").append(fechaHora).append("\n");
        sb.append("================================\n");
        for (Map.Entry<Producto, Integer> entry : carrito.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            String nombreProducto = producto.getNombre();
            String lineaProducto = String.format("%-20s %3d x %6.2f", nombreProducto, cantidad, producto.getPrecio());
            if (lineaProducto.length() > 32) {
                if (nombreProducto.length() > 20) {
                    nombreProducto = nombreProducto.substring(0, 20) + "...";
                }
                lineaProducto = String.format("%-20s %3d x %6.2f", nombreProducto, cantidad, producto.getPrecio());
            }
            sb.append(lineaProducto).append("\n");
        }
        sb.append("================================\n");
        sb.append(String.format("TOTAL: $%6.2f\n", calcularTotal()));
        sb.append("================================\n");
        sb.append("       Gracias por su compra    \n");
        sb.append("================================\n\n");
        sb.append("       Final del ticket.\n\n\n\n");
        return sb.toString();
    }

    public void imprimir(String texto) {
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        if (service == null) {
            System.out.println("No se encontró una impresora.");
            return;
        }
        try {
            DocPrintJob job = service.createPrintJob();
            byte[] bytes = texto.getBytes("CP437");
            Doc doc = new SimpleDoc(bytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Copies(1));
            job.print(doc, attributes);
        } catch (UnsupportedEncodingException | PrintException e) {
            e.printStackTrace();
        }
    }

    public void guardarTicketEnArchivo(String ticket) {
        if (rutaGuardado == null || rutaGuardado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una ruta de guardado para los tickets.");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String fechaHora = sdf.format(new Date());
        String nombreArchivo = rutaGuardado + File.separator + "ticket_" + fechaHora + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            writer.write(ticket);
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}