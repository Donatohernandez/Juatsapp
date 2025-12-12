package com.juatsapp.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Pantalla de inicio de sesión.
 * Ahora solo pide teléfono y contraseña.
 */
public class AuthFrame extends JFrame {

    private final AuthService authService;

    private JTextField txtPhone;
    private JPasswordField txtPassword;

    public AuthFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0x128C7E));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
    }

    private void initComponents() {
        setTitle("Juatsapp - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0xECE5DD));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Título
        // LOGO
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        ImageIcon icon = new ImageIcon(getClass().getResource("/j_logo.png"));
        Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblLogo, gbc);
        row++;

        // Título
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(lblTitle, gbc);
        row++;

        gbc.gridwidth = 1;

        // Teléfono
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Teléfono:"), gbc);
        txtPhone = new JTextField();
        styleTextField(txtPhone);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
        row++;

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        row++;

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton btnLogin = createPrimaryButton("Iniciar sesión");
        JButton btnRegister = createPrimaryButton("Crear cuenta");

        btnLogin.addActionListener(this::onLogin);
        btnRegister.addActionListener(e -> {
            new RegisterFrame(AuthFrame.this).setVisible(true);
            setVisible(false);
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

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
            new MainChatFrame(user).setVisible(true);
            this.dispose();
        }
    }
}
