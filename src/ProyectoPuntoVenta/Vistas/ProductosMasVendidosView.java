package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class ProductosMasVendidosView extends JPanel {
    private JTable table;
    private ProductosVendidosTableModel tableModel;
    private ChartPanel chartPanel;

    public ProductosMasVendidosView(ProductManager productoManager) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        tableModel = new ProductosVendidosTableModel(productoManager);
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Crear el gráfico de barras
        DefaultCategoryDataset dataset = createDataset(productoManager);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Productos Más Vendidos",
                "Producto",
                "Cantidad Vendida",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 400));
        add(chartPanel, BorderLayout.SOUTH);
    }

    public void actualizarTabla() {
        tableModel.actualizarDatos();
        tableModel.fireTableDataChanged();
        actualizarGrafica();
    }

    private void actualizarGrafica() {
        DefaultCategoryDataset dataset = createDataset(tableModel.getProductoManager());
        JFreeChart barChart = ChartFactory.createBarChart(
                "Productos Más Vendidos",
                "Producto",
                "Cantidad Vendida",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        chartPanel.setChart(barChart);
    }

    private DefaultCategoryDataset createDataset(ProductManager productoManager) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Entry<Producto, Integer>> productosVendidosOrdenados = new ArrayList<>(
                productoManager.getProductosVendidos().entrySet());
        productosVendidosOrdenados.sort((e1, e2) -> Double.compare(
                e2.getKey().getPrecio() * e2.getValue(),
                e1.getKey().getPrecio() * e1.getValue()));
        for (Entry<Producto, Integer> entry : productosVendidosOrdenados) {
            Producto producto = entry.getKey();
            int cantidadVendida = entry.getValue();
            dataset.addValue(cantidadVendida, "Cantidad Vendida", producto.getNombre());
        }
        return dataset;
    }

    private static class ProductosVendidosTableModel extends AbstractTableModel {
        private final ProductManager productoManager;
        private final String[] columnNames = { "Nombre", "Cantidad Vendida", "Ingresos" };
        private List<Entry<Producto, Integer>> productosVendidosOrdenados;
        private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

        public ProductosVendidosTableModel(ProductManager productoManager) {
            this.productoManager = productoManager;
            actualizarDatos();
        }

        public void actualizarDatos() {
            productosVendidosOrdenados = new ArrayList<>(productoManager.getProductosVendidos().entrySet());
            productosVendidosOrdenados.sort((e1, e2) -> Double.compare(
                    e2.getKey().getPrecio() * e2.getValue(),
                    e1.getKey().getPrecio() * e1.getValue()));
        }

        @Override
        public int getRowCount() {
            return productosVendidosOrdenados.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Entry<Producto, Integer> entry = productosVendidosOrdenados.get(rowIndex);
            Producto producto = entry.getKey();
            int cantidadVendida = entry.getValue();
            switch (columnIndex) {
                case 0:
                    return producto.getNombre();
                case 1:
                    return cantidadVendida;
                case 2:
                    return decimalFormat.format(producto.getPrecio() * cantidadVendida);
                default:
                    return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        public ProductManager getProductoManager() {
            return productoManager;
        }
    }
}