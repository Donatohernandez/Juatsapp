package com.juatsapp.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.MaskFormatter;

/**
 * Pantalla de registro de usuarios.
 * Después de registrar, regresa a la pantalla de login.
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService;
    private final JFrame loginFrame;

    private JTextField txtPhone, txtAddress;
    private JPasswordField txtPassword;
    private JFormattedTextField txtBirthDate;
    private JComboBox<String> cbSex;

    public RegisterFrame(JFrame loginFrame) {
        this.authService = new AuthService();
        this.loginFrame = loginFrame;
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

    private void style(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
    }

    /** Campo de fecha con máscara yyyy-MM-dd */
    private JFormattedTextField createDateField() {
        try {
            MaskFormatter mask = new MaskFormatter("####-##-##");
            mask.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(mask);
            style(field);
            return field;
        } catch (ParseException e) {
            JFormattedTextField fallback = new JFormattedTextField();
            style(fallback);
            return fallback;
        }
    }

    private void initComponents() {
        setTitle("Juatsapp - Registro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0xECE5DD));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Título
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Crear cuenta", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        txtPhone = new JTextField(); style(txtPhone);
        txtPassword = new JPasswordField(); style(txtPassword);
        txtBirthDate = createDateField();
        txtAddress = new JTextField(); style(txtAddress);
        cbSex = new JComboBox<>(new String[]{"masculino", "femenino", "robot", "ninja", "otro"});

        addField(panel, gbc, row++, "Teléfono:", txtPhone);
        addField(panel, gbc, row++, "Contraseña:", txtPassword);
        addField(panel, gbc, row++, "Fecha nac. (yyyy-MM-dd):", txtBirthDate);
        addField(panel, gbc, row++, "Dirección:", txtAddress);
        addField(panel, gbc, row++, "Sexo:", cbSex);

        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);

        JButton btnBack = new JButton("Regresar");
        btnBack.setBackground(new Color(0xCCCCCC));
        btnBack.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnBack.addActionListener(e -> {
            loginFrame.setVisible(true);
            dispose();
        });

        JButton btnSave = createPrimaryButton("Registrar");
        btnSave.addActionListener(this::onRegister);

        buttons.add(btnBack);
        buttons.add(btnSave);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttons, gbc);

        add(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void onRegister(ActionEvent e) {
        try {
            String phone = txtPhone.getText().trim();
            String password = new String(txtPassword.getPassword());
            String birthStr = txtBirthDate.getText().trim();
            String address = txtAddress.getText().trim();
            String sex = (String) cbSex.getSelectedItem();

            if (phone.isEmpty() || password.isEmpty() || birthStr.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (birthStr.contains("_")) {
                JOptionPane.showMessageDialog(this, "Completa la fecha de nacimiento.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "El teléfono debe ser de 10 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthStr);

            boolean ok = authService.register(phone, password, birthDate, address, sex);

            if (!ok) {
                JOptionPane.showMessageDialog(this, "Ese teléfono ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            loginFrame.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
