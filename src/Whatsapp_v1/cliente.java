package Whatsapp_v1;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class cliente {
    private JFrame frame;
    private JTextPane textPane;
    private JTextField textField;
    private JButton sendButton;
    private PrintWriter salida;
    private BufferedReader entrada;
    private StyledDocument doc;
    private Style styleSent;
    private Style styleReceived;
    private static final int SHIFT = 3; // Desplazamiento para el cifrado César
    private String username;

    // Colores para los nombres de los usuarios
    private Color[] nameColors = {
        new Color(30, 144, 255), // Dodger Blue
        new Color(34, 139, 34),   // Forest Green
        new Color(255, 140, 0),   // Dark Orange
        new Color(138, 43, 226),  // Blue Violet
        new Color(255, 0, 0),     // Red
        new Color(255, 20, 147),  // Deep Pink
        new Color(128, 0, 128),   // Purple
        new Color(0, 128, 128)    // Teal
    };
    private Map<String, Style> userNameStyles = new HashMap<>();
    private int colorCounter = 0;

    @SuppressWarnings({ "resource", "unused" })
    public cliente() {
        // Solicitar el nombre de usuario
        username = JOptionPane.showInputDialog(null, "Ingrese su nombre de usuario:", "Nombre de Usuario", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre de usuario es obligatorio. La aplicación se cerrará.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Configurar la interfaz gráfica
        frame = new JFrame("Chat de Mensajes - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Panel superior con título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 144, 255));
        JLabel titleLabel = new JLabel("Chat de Mensajes");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Área de mensajes
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(new Color(245, 245, 245));
        textPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doc = textPane.getStyledDocument();

        // Definir estilos de mensajes enviados
        styleSent = doc.addStyle("Sent", null);
        StyleConstants.setAlignment(styleSent, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(styleSent, new Color(30, 144, 255));
        StyleConstants.setFontSize(styleSent, 14);
        StyleConstants.setFontFamily(styleSent, "Segoe UI");

        // Definir estilos de mensajes recibidos
        styleReceived = doc.addStyle("Received", null);
        StyleConstants.setAlignment(styleReceived, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(styleReceived, Color.DARK_GRAY);
        StyleConstants.setFontSize(styleReceived, 14);
        StyleConstants.setFontFamily(styleReceived, "Segoe UI");

        JScrollPane scrollPane = new JScrollPane(textPane);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para enviar mensajes
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(220, 220, 220));
        inputPanel.setLayout(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255)));
        inputPanel.add(textField, BorderLayout.CENTER);

        sendButton = new JButton("Enviar");
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(new Color(30, 144, 255));
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Conectar al servidor
        String servidor = "localhost";
        int puerto = 1232;

        try {
            Socket socket = new Socket(servidor, puerto);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar el nombre de usuario al servidor
            String serverPrompt = entrada.readLine(); // Leer "Ingrese su nombre de usuario:"
            salida.println(username);

            appendMessage("Servidor", "Conectado al servidor.\n");

            // Hilo para escuchar mensajes del servidor
            new Thread(new IncomingReader()).start();
        } catch (IOException e) {
            appendMessage("Servidor", "Error al conectar al servidor.\n");
            e.printStackTrace();
        }

        // Acción del botón enviar
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        // Acción al presionar Enter en el campo de texto
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });
    }

    private void enviarMensaje() {
        String mensaje = textField.getText();
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            String mensajeCifrado = cifrarCesar(mensaje);
            salida.println(mensajeCifrado);
            appendMessage("Tú", mensaje);
            textField.setText("");
            if (mensaje.equalsIgnoreCase("salir")) {
                try {
                    salida.close();
                    entrada.close();
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void appendMessage(String sender, String message) {
        try {
            // Asignar un estilo único al usuario si no existe
            if (!userNameStyles.containsKey(sender)) {
                Style style = doc.addStyle(sender, null);
                StyleConstants.setForeground(style, nameColors[colorCounter % nameColors.length]);
                StyleConstants.setBold(style, true);
                userNameStyles.put(sender, style);
                colorCounter++;
            }

            // Estilo para el contenido del mensaje
            Style styleMessage = doc.addStyle("Message", null);
            StyleConstants.setForeground(styleMessage, Color.DARK_GRAY);
            StyleConstants.setFontSize(styleMessage, 14);
            StyleConstants.setFontFamily(styleMessage, "Segoe UI");

            // Insertar el nombre del remitente con su estilo
            doc.insertString(doc.getLength(), sender + ": ", userNameStyles.get(sender));

            // Insertar el contenido del mensaje con estilo estándar
            doc.insertString(doc.getLength(), message + "\n", styleMessage);

            // Ajustar la alineación del párrafo
            if (sender.equals("Tú")) {
                doc.setParagraphAttributes(doc.getLength() - message.length() - sender.length() - 2, sender.length() + message.length() + 2,
                    styleSent, false);
            } else {
                doc.setParagraphAttributes(doc.getLength() - message.length() - sender.length() - 2, sender.length() + message.length() + 2,
                    styleReceived, false);
            }

            // Desplazar el cursor al final del documento
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Método para cifrar usando el cifrado César
    private String cifrarCesar(String texto) {
        StringBuilder cifrado = new StringBuilder();
        for (char caracter : texto.toCharArray()) {
            if (Character.isLetter(caracter)) {
                char base = Character.isLowerCase(caracter) ? 'a' : 'A';
                cifrado.append((char) ((caracter - base + SHIFT) % 26 + base));
            } else {
                cifrado.append(caracter);
            }
        }
        return cifrado.toString();
    }

    // Método para descifrar usando el cifrado César
    private String descifrarCesar(String texto) {
        StringBuilder descifrado = new StringBuilder();
        for (char caracter : texto.toCharArray()) {
            if (Character.isLetter(caracter)) {
                char base = Character.isLowerCase(caracter) ? 'a' : 'A';
                descifrado.append((char) ((caracter - base - SHIFT + 26) % 26 + base));
            } else {
                descifrado.append(caracter);
            }
        }
        return descifrado.toString();
    }

    private class IncomingReader implements Runnable {
        public void run() {
            String mensaje;
            try {
                while ((mensaje = entrada.readLine()) != null) {
                    // Dividir el mensaje en "sender: encryptedMessage"
                    if (mensaje.contains(":")) {
                        String[] parts = mensaje.split(":", 2);
                        String sender = parts[0].trim();
                        String encryptedMessage = parts[1].trim();
                        
                        if (sender.equals("Servidor")) {
                            // Mensaje del servidor, no encriptado
                            appendMessage(sender, encryptedMessage);
                        } else {
                            // Mensaje de otro usuario, encriptado
                            String mensajeDescifrado = descifrarCesar(encryptedMessage);
                            appendMessage(sender, mensajeDescifrado);
                        }
                    } else {
                        // Si no hay ":", descifrar todo el mensaje
                        String mensajeDescifrado = descifrarCesar(mensaje);
                        appendMessage("Tú", mensajeDescifrado);
                    }
                }
            } catch (IOException e) {
                appendMessage("Servidor", "Conexión perdida.\n");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new cliente());
    }
}
