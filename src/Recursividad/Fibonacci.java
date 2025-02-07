package Recursividad;

public class Fibonacci {
    public static int fibonacci(int n) {
        if (n <= 1) {
            return n; // Caso base: Fibonacci(0) = 0, Fibonacci(1) = 1
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2); // Llamada recursiva
        }
    }

    public static void main(String[] args) {
        int n = 6; // Calcular Fibonacci(6)
        System.out.println("Fibonacci(" + n + ") = " + fibonacci(n)); // Salida: 8
    }
}
