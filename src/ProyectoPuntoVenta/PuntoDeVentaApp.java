package ProyectoPuntoVenta;

import com.formdev.flatlaf.FlatLightLaf;
import ProyectoPuntoVenta.Vistas.AgregarProductoView;
import ProyectoPuntoVenta.Vistas.AgregarProductosYCobroView;
import ProyectoPuntoVenta.Vistas.ProductosMasVendidosView;
import ProyectoPuntoVenta.Vistas.VisualizarProductosView;

import javax.swing.*;

public class PuntoDeVentaApp {
    public static void main(String[] args) {
        // Establecer FlatLaf como el Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Crear y mostrar la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Punto de Venta");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            ProductManager productoManager = new ProductManager();

            VisualizarProductosView visualizarProductosView = new VisualizarProductosView(productoManager);
            ProductosMasVendidosView productosMasVendidosView = new ProductosMasVendidosView(productoManager);
            AgregarProductosYCobroView agregarProductosYCobroView = new AgregarProductosYCobroView(productoManager, visualizarProductosView, productosMasVendidosView);
            AgregarProductoView agregarProductoView = new AgregarProductoView(productoManager, visualizarProductosView, agregarProductosYCobroView);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Agregar Producto", agregarProductoView);
            tabbedPane.addTab("Cobro", agregarProductosYCobroView);
            tabbedPane.addTab("Visualizar Productos", visualizarProductosView);
            tabbedPane.addTab("Productos Más Vendidos", productosMasVendidosView);

            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}