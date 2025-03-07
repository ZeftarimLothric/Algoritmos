package ProyectoPuntoVenta;

import com.formdev.flatlaf.FlatLightLaf;
import ProyectoPuntoVenta.Vistas.AgregarProductoView;
import ProyectoPuntoVenta.Vistas.CobroDeProductosView;
import ProyectoPuntoVenta.Vistas.ProductosMasVendidosView;
import ProyectoPuntoVenta.Vistas.VisualizarProductosView;

import javax.swing.*;
import java.io.File;

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

            // Seleccionar la ruta de guardado al iniciar el programa
            String rutaGuardado = seleccionarRutaGuardado(frame);
            if (rutaGuardado == null) {
                JOptionPane.showMessageDialog(frame, "Debe seleccionar una ruta de guardado para continuar.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            ProductManager productoManager = new ProductManager();

            VisualizarProductosView visualizarProductosView = new VisualizarProductosView(productoManager);
            ProductosMasVendidosView productosMasVendidosView = new ProductosMasVendidosView(productoManager);
            CobroDeProductosView agregarProductosYCobroView = new CobroDeProductosView(productoManager,
                    visualizarProductosView, productosMasVendidosView, rutaGuardado);
            AgregarProductoView agregarProductoView = new AgregarProductoView(productoManager, visualizarProductosView,
                    agregarProductosYCobroView);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Agregar Producto", agregarProductoView);
            tabbedPane.addTab("Cobro", agregarProductosYCobroView);
            tabbedPane.addTab("Visualizar Productos", visualizarProductosView);
            tabbedPane.addTab("Productos Más Vendidos", productosMasVendidosView);

            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }

    private static String seleccionarRutaGuardado(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            return selectedDirectory.getAbsolutePath();
        }
        return null;
    }
}