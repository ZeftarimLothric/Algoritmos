package Recursividad;

public class Node<T, K> {
    Node <T,K>izq;
    Node <T,K>der;
    T dato;

    public Node(T dato) {
        this.izq = null;
        this.der = null;
        this.dato = dato;
    }

    @Override
    public String toString() {
        return "Nodo(" + "dato=" + dato +")";
    }
}
