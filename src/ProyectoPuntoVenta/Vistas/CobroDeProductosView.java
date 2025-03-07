package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;
import ProyectoPuntoVenta.Clases.Usuario;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.Copies;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CobroDeProductosView extends JPanel {
    private DefaultListModel<String> carritoModel;
    private JLabel totalLabel;
    private Map<Producto, Integer> carrito;
    private DefaultListModel<Producto> productosModel;
    private VisualizarProductosView visualizarProductosView;
    @SuppressWarnings("unused")
    private ProductosMasVendidosView productosMasVendidosView;
    private ProductManager productoManager;
    private String rutaGuardado;
    private JList<Producto> productosList;
    private JList<String> carritoList;
    private JLabel productoInfoLabel;
    private JLabel usuarioLabel;
    private DecimalFormat decimalFormat;

    @SuppressWarnings("unused")
    public CobroDeProductosView(ProductManager productoManager, VisualizarProductosView visualizarProductosView,
            ProductosMasVendidosView productosMasVendidosView, String rutaGuardado) {
        this.productoManager = productoManager;
        this.visualizarProductosView = visualizarProductosView;
        this.productosMasVendidosView = productosMasVendidosView;
        this.rutaGuardado = rutaGuardado;
        this.decimalFormat = new DecimalFormat("#.00");

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        // Panel para mostrar productos disponibles
        JPanel productosPanel = new JPanel(new BorderLayout(10, 10));
        productosPanel.setBackground(new Color(245, 245, 245));

        productosModel = new DefaultListModel<>();
        for (Producto producto : productoManager.getProductosDisponibles()) {
            productosModel.addElement(producto);
        }
        productosList = new JList<>(productosModel);
        productosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosList.setFont(new Font("Arial", Font.PLAIN, 14));
        productosList.setCellRenderer(new DefaultListCellRenderer() {
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
        productosList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = productosList.locationToIndex(e.getPoint());
                    productosList.setSelectedIndex(index);
                    Producto producto = productosList.getSelectedValue();
                    if (producto != null) {
                        showPopupMenu(e, producto);
                    }
                } else if (e.getClickCount() == 2) {
                    Producto producto = productosList.getSelectedValue();
                    if (producto != null) {
                        agregarProductoAlCarrito(producto, 1);
                    }
                } else {
                    Producto producto = productosList.getSelectedValue();
                    if (producto != null) {
                        productoInfoLabel.setText(
                                "Precio: $" + producto.getPrecio() + " | Cantidad disponible: "
                                        + producto.getCantidad());
                    }
                }
            }
        });
        JScrollPane productosScrollPane = new JScrollPane(productosList);
        productosPanel.add(productosScrollPane, BorderLayout.CENTER);

        // Label para mostrar información adicional del producto
        productoInfoLabel = new JLabel("Seleccione un producto para ver más información.");
        productoInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        productosPanel.add(productoInfoLabel, BorderLayout.SOUTH);

        add(productosPanel, BorderLayout.WEST);

        // Panel para mostrar el carrito de compras
        carritoModel = new DefaultListModel<>();
        carritoList = new JList<>(carritoModel);
        carritoList.setBackground(new Color(255, 255, 255));
        carritoList.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        carritoList.setFont(new Font("Arial", Font.PLAIN, 14));
        carritoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = carritoList.locationToIndex(e.getPoint());
                    carritoList.setSelectedIndex(index);
                    Producto producto = (Producto) carrito.keySet().toArray()[index];
                    if (producto != null) {
                        showCarritoPopupMenu(e, producto);
                    }
                } else if (e.getClickCount() == 2) {
                    int index = carritoList.locationToIndex(e.getPoint());
                    Producto producto = (Producto) carrito.keySet().toArray()[index];
                    eliminarProductoDelCarrito(producto, 1);
                }
            }
        });
        JScrollPane carritoScrollPane = new JScrollPane(carritoList);
        add(carritoScrollPane, BorderLayout.CENTER);

        // Panel para mostrar el total y realizar el cobro
        JPanel cobroPanel = new JPanel(new BorderLayout());
        cobroPanel.setBackground(new Color(245, 245, 245));
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalLabel.setForeground(new Color(0, 123, 255));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cobroPanel.add(totalLabel, BorderLayout.NORTH);

        JButton cobrarButton = new JButton("Cobrar");
        cobrarButton.setBackground(new Color(40, 167, 69));
        cobrarButton.setForeground(Color.WHITE);
        cobrarButton.setFocusPainted(false);
        cobrarButton.setFont(new Font("Arial", Font.BOLD, 16));
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

        // Mostrar el nombre del usuario que inició sesión
        Usuario usuarioActual = productoManager.getUsuarioActual();
        if (usuarioActual != null) {
            usuarioLabel = new JLabel("Usuario: " + usuarioActual.getNombre());
            usuarioLabel.setFont(new Font("Arial", Font.BOLD, 14));
            usuarioLabel.setForeground(new Color(0, 123, 255));
            usuarioLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(usuarioLabel, BorderLayout.NORTH);
        }
    }

    @SuppressWarnings("unused")
    private void showPopupMenu(MouseEvent e, Producto producto) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addItem1 = new JMenuItem("Agregar 1");
        addItem1.addActionListener(event -> agregarProductoAlCarrito(producto, 1));
        popupMenu.add(addItem1);

        JMenuItem addItem2 = new JMenuItem("Agregar 2");
        addItem2.addActionListener(event -> agregarProductoAlCarrito(producto, 2));
        popupMenu.add(addItem2);

        JMenuItem addItem5 = new JMenuItem("Agregar 5");
        addItem5.addActionListener(event -> agregarProductoAlCarrito(producto, 5));
        popupMenu.add(addItem5);

        JMenuItem addItem10 = new JMenuItem("Agregar 10");
        addItem10.addActionListener(event -> agregarProductoAlCarrito(producto, 10));
        popupMenu.add(addItem10);

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    @SuppressWarnings("unused")
    private void showCarritoPopupMenu(MouseEvent e, Producto producto) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem removeItem1 = new JMenuItem("Quitar 1");
        removeItem1.addActionListener(event -> eliminarProductoDelCarrito(producto, 1));
        popupMenu.add(removeItem1);

        JMenuItem removeItem2 = new JMenuItem("Quitar 2");
        removeItem2.addActionListener(event -> eliminarProductoDelCarrito(producto, 2));
        popupMenu.add(removeItem2);

        JMenuItem removeItem5 = new JMenuItem("Quitar 5");
        removeItem5.addActionListener(event -> eliminarProductoDelCarrito(producto, 5));
        popupMenu.add(removeItem5);

        JMenuItem removeAll = new JMenuItem("Quitar todos");
        removeAll.addActionListener(event -> eliminarProductoDelCarrito(producto, carrito.get(producto)));
        popupMenu.add(removeAll);

        popupMenu.show(e.getComponent(), e.getX(), e.getY());
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

    private void eliminarProductoDelCarrito(Producto producto, int cantidad) {
        if (carrito.containsKey(producto)) {
            int cantidadEnCarrito = carrito.get(producto);
            if (cantidadEnCarrito > cantidad) {
                carrito.put(producto, cantidadEnCarrito - cantidad);
            } else {
                carrito.remove(producto);
            }
            producto.setCantidad(producto.getCantidad() + cantidad);
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
            productosModel.addElement(producto);
        }
    }

    private void actualizarTotal() {
        totalLabel.setText("Total: $" + decimalFormat.format(calcularTotal()));
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
        sb.append(fechaHora).append("\n");
        sb.append("Por: ").append(productoManager.getUsuarioActual().getNombre()).append("\n");
        sb.append("================================\n");
        for (Map.Entry<Producto, Integer> entry : carrito.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();
            String nombreProducto = producto.getNombre();
            String lineaProducto = String.format("%-20s %3d x %6.2f", nombreProducto, cantidad, producto.getPrecio());
            if (lineaProducto.length() > 32) {
                if (nombreProducto.length() > 15) {
                    nombreProducto = nombreProducto.substring(0, 15) + "...";
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