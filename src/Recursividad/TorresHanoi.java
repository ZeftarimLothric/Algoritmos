package Recursividad;

public class TorresHanoi {

    public static void hanoi(int n, char origen, char destino, char auxiliar) {
        if (n == 1) {
            System.out.println("Mover disco 1 de " + origen + " a " + destino);
        } else {
            hanoi(n - 1, origen, auxiliar, destino); // Mover n-1 discos a la torre auxiliar
            System.out.println("Mover disco " + n + " de " + origen + " a " + destino); // Mover el disco más grande
            hanoi(n - 1, auxiliar, destino, origen); // Mover n-1 discos al destino
        }
    }

    public static void main(String[] args) {
        int n = 3; // Número de discos
        hanoi(n, 'A', 'C', 'B'); // Llamada inicial
    }
}
