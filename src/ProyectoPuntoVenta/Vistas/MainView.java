package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;

import javax.swing.*;

public class MainView extends JFrame {
    public MainView(ProductManager productManager, String rutaGuardado) {
        setTitle("Punto de Venta");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        VisualizarProductosView visualizarProductosView = new VisualizarProductosView(productManager);
        ProductosMasVendidosView productosMasVendidosView = new ProductosMasVendidosView(productManager);
        CobroDeProductosView agregarProductosYCobroView = new CobroDeProductosView(productManager,
                visualizarProductosView, productosMasVendidosView, rutaGuardado);
        AgregarProductoView agregarProductoView = new AgregarProductoView(productManager, visualizarProductosView,
                agregarProductosYCobroView);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Agregar Producto", agregarProductoView);
        tabbedPane.addTab("Cobro", agregarProductosYCobroView);
        tabbedPane.addTab("Visualizar Productos", visualizarProductosView);
        tabbedPane.addTab("Productos Más Vendidos", productosMasVendidosView);

        add(tabbedPane);
    }
}