package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Producto;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class ProductosMasVendidosView extends JPanel {
    private JTable table;
    private ProductosVendidosTableModel tableModel;

    public ProductosMasVendidosView(ProductManager productoManager) {
        setLayout(new BorderLayout());

        tableModel = new ProductosVendidosTableModel(productoManager);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actualizarTabla() {
        tableModel.actualizarDatos();
        tableModel.fireTableDataChanged();
    }

    private static class ProductosVendidosTableModel extends AbstractTableModel {
        private final ProductManager productoManager;
        private final String[] columnNames = {"Nombre", "Cantidad Vendida", "Ingresos"};
        private List<Entry<Producto, Integer>> productosVendidosOrdenados;

        public ProductosVendidosTableModel(ProductManager productoManager) {
            this.productoManager = productoManager;
            actualizarDatos();
        }

        public void actualizarDatos() {
            productosVendidosOrdenados = new ArrayList<>(productoManager.getProductosVendidos().entrySet());
            productosVendidosOrdenados.sort((e1, e2) -> Double.compare(
                    e2.getKey().getPrecio() * e2.getValue(),
                    e1.getKey().getPrecio() * e1.getValue()
            ));
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
                    return producto.getPrecio() * cantidadVendida;
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