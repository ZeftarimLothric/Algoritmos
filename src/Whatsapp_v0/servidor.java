package Whatsapp_v0;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class servidor {
    private static final String AES_KEY = "1234567890123456"; // Clave de 16 bytes para AES
    private static final int PUERTO = 1232;
    public static void main(String[] args) {
        iniciarServidor();
    }

    /**
     * Inicia el servidor en el puerto especificado y espera conexiones de clientes.
     */
    private static void iniciarServidor() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado");
                manejarCliente(clienteSocket);
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    /**
     * Maneja la comunicación con un cliente conectado.
     */
    private static void manejarCliente(Socket clienteSocket) {
        try (
            PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            // Hilo para leer mensajes entrantes del cliente
            Thread lector = new Thread(() -> leerMensajesCliente(entrada));
            lector.start();

            // Leer mensajes desde la consola y enviarlos al cliente
            enviarMensajesServidor(salida, scanner);
        } catch (IOException e) {
            System.err.println("Error con el cliente: " + e.getMessage());
        }
    }

    /**
     * Lee los mensajes enviados por el cliente y los desencripta.
     */
    private static void leerMensajesCliente(BufferedReader entrada) {
        try {
            String mensajeCliente;
            while ((mensajeCliente = entrada.readLine()) != null) {
                String mensajeDesencriptado = desencriptarAES(mensajeCliente);
                System.out.println("Cliente: " + mensajeDesencriptado);
            }
        } catch (IOException e) {
            System.err.println("Error al leer mensaje del cliente: " + e.getMessage());
        }
    }

    /**
     * Envía mensajes escritos en la consola al cliente.
     */
    private static void enviarMensajesServidor(PrintWriter salida, Scanner scanner) {
        String mensajeServidor;
        while (!(mensajeServidor = scanner.nextLine()).equalsIgnoreCase("salir")) {
            String mensajeEncriptado = encriptarAES(mensajeServidor);
            salida.println(mensajeEncriptado);
        }
    }

    /**
     * Encripta un mensaje usando AES.
     */
    private static String encriptarAES(String data) {
        try {
            Key aesKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.err.println("Error al encriptar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Desencripta un mensaje usando AES.
     */
    private static String desencriptarAES(String data) {
        try {
            Key aesKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decoded = Base64.getDecoder().decode(data);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            System.err.println("Error al desencriptar: " + e.getMessage());
            return null;
        }
    }
}
