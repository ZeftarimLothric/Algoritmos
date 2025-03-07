package ProyectoPuntoVenta;

import com.formdev.flatlaf.FlatLightLaf;
import ProyectoPuntoVenta.Vistas.LoginView;

import java.io.File;

import javax.swing.*;

public class PuntoDeVentaApp {
    public static void main(String[] args) {
        // Establecer FlatLaf como el Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Intentar establecer la conexión con la base de datos
        ProductManager productoManager = null;
        boolean connected = false;
        while (!connected) {
            productoManager = new ProductManager();
            if (productoManager.isConnected()) {
                connected = true;
            } else {
                int option = JOptionPane.showOptionDialog(null,
                        "No se pudo conectar a la base de datos. ¿Desea volver a intentar?",
                        "Error de conexión",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        new Object[] { "Volver a intentar", "Cerrar" },
                        "Volver a intentar");
                if (option == JOptionPane.NO_OPTION) {
                    System.exit(1);
                }
            }
        }

        // Mostrar mensaje para seleccionar la ruta de guardado
        JOptionPane.showMessageDialog(null, "Selecciona la ruta de guardado de tickets digitales", "Información",
                JOptionPane.INFORMATION_MESSAGE);

        // Seleccionar la ruta de guardado al iniciar el programa
        JFrame frame = new JFrame("Punto de Venta");
        String rutaGuardado = seleccionarRutaGuardado(frame);
        if (rutaGuardado == null) {
            JOptionPane.showMessageDialog(frame, "Debe seleccionar una ruta de guardado para continuar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Mostrar la interfaz de login
        final ProductManager finalProductoManager = productoManager;
        SwingUtilities.invokeLater(() -> {
            new LoginView(finalProductoManager, rutaGuardado).setVisible(true);
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