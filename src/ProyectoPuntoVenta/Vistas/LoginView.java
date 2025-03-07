package ProyectoPuntoVenta.Vistas;

import ProyectoPuntoVenta.ProductManager;
import ProyectoPuntoVenta.Clases.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("unused")
public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private ProductManager productManager;
    private String rutaGuardado;

    public LoginView(ProductManager productManager, String rutaGuardado) {
        this.productManager = productManager;
        this.rutaGuardado = rutaGuardado;
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Iniciar Sesión");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 123, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField();
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                Usuario usuario = productManager.loginUsuario(username, password);
                if (usuario != null) {
                    JOptionPane.showMessageDialog(LoginView.this, "Login exitoso", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new MainView(productManager, rutaGuardado).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, "Usuario o contraseña incorrectos", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(40, 167, 69));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridy++;
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterView(productManager).setVisible(true);
            }
        });
        panel.add(registerButton, gbc);

        add(panel);
    }
}