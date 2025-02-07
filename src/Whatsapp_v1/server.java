package Whatsapp_v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class server {
    private static ConcurrentHashMap<PrintWriter, String> clientWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 1232;
        try (ServerSocket servidor = new ServerSocket(port)) {
            System.out.println("Servidor esperando conexiones en el puerto " + port);
            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Cliente conectado.");
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter salida;
        private BufferedReader entrada;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(socket.getOutputStream(), true);

                // Leer el nombre de usuario
                salida.println("Ingrese su nombre de usuario:");
                username = entrada.readLine();
                clientWriters.put(salida, username);
                broadcastMessage("Servidor", username + " se ha unido al chat.");

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    System.out.println(username + " dice: " + mensaje);
                    if (mensaje.equalsIgnoreCase("salir")) {
                        break;
                    }
                    // Enviar el mensaje a todos los clientes excepto al remitente
                    broadcastMessage(username, mensaje);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (salida != null) {
                        clientWriters.remove(salida);
                        broadcastMessage("Servidor", username + " ha salido del chat.");
                    }
                    socket.close();
                    System.out.println("Conexión cerrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String sender, String message) {
            for (PrintWriter writer : clientWriters.keySet()) {
                if (writer != salida) {
                    writer.println(sender + ": " + message);
                }
            }
        }
    }
}
