package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class VisualizarProductosView extends JPanel {
    private JTable table;
    private ProductosTableModel tableModel;

    public VisualizarProductosView(ProductManager productoManager) {
        setLayout(new BorderLayout());

        tableModel = new ProductosTableModel(productoManager);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actualizarTabla() {
        tableModel.fireTableDataChanged();
    }

    private static class ProductosTableModel extends AbstractTableModel {
        private final ProductManager productoManager;
        private final String[] columnNames = {"Nombre", "Código de Barras", "Precio", "Cantidad"};

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
                    return producto.getPrecio();
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