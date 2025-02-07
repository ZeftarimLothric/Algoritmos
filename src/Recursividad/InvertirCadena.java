package Recursividad;

public class InvertirCadena {

    public static String invertirCadena(String cadena) {
        if (cadena.isEmpty()) {
            return cadena; // Caso base: si la cadena está vacía, devolverla
        } else {
            // Tomar el último carácter y concatenarlo con la inversión del resto
            return invertirCadena(cadena.substring(1)) + cadena.charAt(0);
        }
    }

    public static void main(String[] args) {
        String cadena = "Hola"; // Cadena a invertir
        System.out.println("Cadena original: " + cadena);
        System.out.println("Cadena invertida: " + invertirCadena(cadena)); // Salida: aloH
    }
}
