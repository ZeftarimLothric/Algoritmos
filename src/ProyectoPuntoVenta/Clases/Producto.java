package ProyectoPuntoVenta.Clases;

public class Producto {
    private int id;
    private String nombre;
    private String codigoBarras;
    private double precio;
    private int cantidad;

    public Producto(int id, String nombre, String codigoBarras, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.codigoBarras = codigoBarras;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Producto(String nombre, String codigoBarras, double precio, int cantidad) {
        this(0, nombre, codigoBarras, precio, cantidad);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void reducirCantidad(int cantidad) {
        this.cantidad -= cantidad;
    }

    @Override
    public String toString() {
        return nombre + " - $" + precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Producto producto = (Producto) o;
        return id == producto.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}