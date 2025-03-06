import java.util.Random;

public class ordenamiento {

    public int[] createArray(int limit) {
        int[] array = new int[limit];
        Random r = new Random();
        for (int i = 0; i < limit; i++) {
            array[i] = r.nextInt(2000000); // Genera números aleatorios
        }
        ordenando(array);
        return array;
    }

    private void ordenando(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            // Encuentra el índice de partición
            int pivotIndex = partition(array, low, high);

            // Ordena la parte izquierda y derecha
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        // Tomamos el último elemento como pivote
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                // Intercambiamos los elementos
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        // Colocamos el pivote en su lugar
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        return i + 1;
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime(); // Tiempo de inicio

        ordenamiento ord = new ordenamiento();
        int[] sortedArray = ord.createArray(2000000); // Crea un array de 2 millones de elementos

        long endTime = System.nanoTime(); // Tiempo de fin
        long duration = (endTime - startTime) / 1000000; // Convertir de nanosegundos a milisegundos

        // Mostrar el tiempo de ejecución
        System.out.println("Tiempo de ordenamiento: " + duration + " milisegundos.");

        // Opcional: Mostrar los primeros 10 y últimos 10 elementos para verificar
        System.out.println("Primeros 10 elementos ordenados:");
        for (int i = 0; i < 2000000; i++) {
            System.out.println(sortedArray[i]);
        }

        // Descomentar esta parte si realmente deseas ver todo el array (no recomendado)
        // System.out.println("\nArray completo ordenado:");
        // for (int i = 0; i < sortedArray.length; i++) {
        // System.out.println(sortedArray[i]);
        // }
    }
}
