package ProyectoPuntoVenta.Clases;

public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String password;
    private double importeTotal;

    public Usuario(int id, String nombre, String username, String password, double importeTotal) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.importeTotal = importeTotal;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }
}