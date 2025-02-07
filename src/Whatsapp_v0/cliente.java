package Whatsapp_v0;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import com.formdev.flatlaf.FlatDarculaLaf;

public class cliente {
    private static final String AES_KEY = "1234567890123456"; // Clave AES para cifrado
    private static JTextPane areaMensajes;
    private static JTextField campoMensaje;
    private static PrintWriter salida;
    private static Socket socket;
    private static BufferedReader entrada;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf()); // Aplicar tema oscuro
        } catch (Exception e) {
            e.printStackTrace();
        }

        String servidor = "localhost"; // Dirección del servidor
        int puerto = 1232; // Puerto del servidor

        // Crear ventana principal
        JFrame frame = new JFrame("Chat Cliente");
        frame.setSize(420, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Área de mensajes donde se mostrarán los chats
        areaMensajes = new JTextPane();
        areaMensajes.setEditable(false);
        areaMensajes.setBackground(new Color(43, 43, 43)); // Fondo oscuro
        areaMensajes.setForeground(Color.WHITE);
        areaMensajes.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(areaMensajes);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Campo de entrada de texto
        campoMensaje = new JTextField();
        campoMensaje.setPreferredSize(new Dimension(250, 35));
        campoMensaje.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Botón para enviar mensaje
        JButton botonEnviar = new JButton("Enviar");
        botonEnviar.setFocusPainted(false);
        botonEnviar.setBorderPainted(false);
        botonEnviar.setBackground(new Color(0, 120, 215)); // Color azul
        botonEnviar.setForeground(Color.WHITE);
        botonEnviar.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Acción de enviar mensaje
        ActionListener enviarMensaje = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mensajeCliente = campoMensaje.getText();
                if (!mensajeCliente.isEmpty()) {
                    String mensajeEncriptado = encryptAES(mensajeCliente, AES_KEY); // Cifrar mensaje
                    salida.println(mensajeEncriptado); // Enviar mensaje cifrado al servidor
                    appendMessage("Cliente: " + mensajeCliente, new Color(0, 150, 250), StyleConstants.ALIGN_RIGHT);
                    campoMensaje.setText(""); // Limpiar campo de texto
                }
            }
        };
        
        // Asignar acción al botón
        botonEnviar.addActionListener(enviarMensaje);
        
        // Enviar mensaje al presionar Enter
        campoMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje.actionPerformed(null);
                }
            }
        });

        // Panel inferior con campo de texto y botón
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toolBar.add(campoMensaje);
        toolBar.add(Box.createHorizontalStrut(5)); // Espaciador
        toolBar.add(botonEnviar);

        // Agregar componentes a la ventana
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Conectar con el servidor
        try {
            socket = new Socket(servidor, puerto);
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Hilo para recibir mensajes del servidor
            Thread lector = new Thread(() -> {
                try {
                    String mensajeServidor;
                    while ((mensajeServidor = entrada.readLine()) != null) {
                        String mensajeDesencriptado = decryptAES(mensajeServidor, AES_KEY);
                        appendMessage("Servidor: " + mensajeDesencriptado, Color.WHITE, StyleConstants.ALIGN_LEFT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (entrada != null) entrada.close();
                        if (salida != null) salida.close();
                        if (socket != null) socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            lector.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para encriptar usando AES
    public static String encryptAES(String data, String key) {
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para desencriptar usando AES
    public static String decryptAES(String data, String key) {
        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decoded = Base64.getDecoder().decode(data);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Método para mostrar mensajes en la ventana
    private static void appendMessage(String message, Color color, int alignment) {
        StyledDocument doc = areaMensajes.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        StyleConstants.setAlignment(attr, alignment);
        StyleConstants.setFontFamily(attr, "Arial");
        StyleConstants.setFontSize(attr, 14);
        StyleConstants.setBold(attr, true);
        try {
            int length = doc.getLength();
            doc.insertString(length, message + "\n", attr);
            doc.setParagraphAttributes(length, message.length(), attr, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
