package com.juatsapp.app;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ventana de autenticación principal de Juatsapp.
 * Permite registrar nuevos usuarios y realizar el inicio de sesión
 * aplicando validaciones básicas sobre los campos capturados.
 */
public class AuthFrame extends JFrame {

    private final AuthService authService;

    private JTextField txtPhone;
    private JPasswordField txtPassword;
    private JTextField txtBirthDate;
    private JTextField txtAddress;
    private JComboBox<String> cbSex;

    /**
     * Crea una nueva instancia de la ventana de autenticación
     * inicializando el servicio de autenticación y los componentes
     * gráficos.
     */
    public AuthFrame() {
        this.authService = new AuthService();
        initLookAndFeel();
        initComponents();
    }

    /**
     * Inicializa el Look and Feel de la aplicación usando FlatLaf.
     */
    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Error al inicializar FlatLaf: " + ex.getMessage());
        }
    }

    /**
     * Inicializa y acomoda los componentes de la ventana.
     */
    private void initComponents() {
        setTitle("Juatsapp - Autenticación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Tamaño tipo pantalla de teléfono (vertical)
        setSize(420, 720);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // Más espacio entre campos para que se vea más limpio
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Teléfono:"), gbc);
        txtPhone = new JTextField();
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
        row++;

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField();
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        row++;

        // Fecha de nacimiento
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Fecha nac. (yyyy-MM-dd):"), gbc);
        txtBirthDate = new JTextField();
        gbc.gridx = 1;
        panel.add(txtBirthDate, gbc);
        row++;

        // Dirección
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Dirección:"), gbc);
        txtAddress = new JTextField();
        gbc.gridx = 1;
        panel.add(txtAddress, gbc);
        row++;

        // Sexo
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Sexo:"), gbc);
        cbSex = new JComboBox<>(new String[]{
                "masculino", "femenino", "robot", "ninja", "otro"
        });
        gbc.gridx = 1;
        panel.add(cbSex, gbc);
        row++;

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnRegister = new JButton("Registrar");
        JButton btnLogin = new JButton("Iniciar sesión");
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnLogin);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);

        // Listeners
        btnRegister.addActionListener(this::onRegister);
        btnLogin.addActionListener(this::onLogin);
    }

    /**
     * Maneja el flujo de registro de un nuevo usuario desde la GUI.
     */
    private void onRegister(ActionEvent e) {
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        String birthDateStr = txtBirthDate.getText().trim();
        String address = txtAddress.getText().trim();
        String sex = (String) cbSex.getSelectedItem();

        if (phone.isEmpty() || password.isEmpty() || birthDateStr.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios para registrarse.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar formato básico de teléfono (solo dígitos y longitud razonable)
        if (!phone.matches("\\d{8,15}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo dígitos y tener entre 8 y 15 caracteres.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date birthDate;
        try {
            birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDateStr);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-MM-dd.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = authService.register(phone, password, birthDate, address, sex);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese teléfono.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Maneja el flujo de inicio de sesión desde la GUI.
     */
    private void onLogin(ActionEvent e) {
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese teléfono y contraseña.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phone.matches("\\d{8,15}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe contener solo dígitos y tener entre 8 y 15 caracteres.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authService.login(phone, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Bienvenido, " + user.getPhone() + "!",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            MainChatFrame mainChatFrame = new MainChatFrame(user);
            mainChatFrame.setVisible(true);
            this.dispose();
        }
    }

    /**
     * Punto de entrada de la aplicación cuando se ejecuta la ventana
     * de autenticación directamente.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthFrame frame = new AuthFrame();
            frame.setVisible(true);
        });
    }
}
