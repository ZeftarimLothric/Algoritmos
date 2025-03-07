package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.text.DecimalFormat;

public class VisualizarProductosView extends JPanel {
    private JTable table;
    private ProductosTableModel tableModel;

    public VisualizarProductosView(ProductManager productoManager) {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        tableModel = new ProductosTableModel(productoManager);
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actualizarTabla() {
        tableModel.fireTableDataChanged();
    }

    private static class ProductosTableModel extends AbstractTableModel {
        private final ProductManager productoManager;
        private final String[] columnNames = { "Nombre", "Código de Barras", "Precio", "Cantidad" };
        private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

        public ProductosTableModel(ProductManager productoManager) {
            this.productoManager = productoManager;
        }

        @Override
        public int getRowCount() {
            return productoManager.getProductosDisponibles().size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Producto producto = productoManager.getProductosDisponibles().get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return producto.getNombre();
                case 1:
                    return producto.getCodigoBarras();
                case 2:
                    return decimalFormat.format(producto.getPrecio());
                case 3:
                    return producto.getCantidad();
                default:
                    return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }
}