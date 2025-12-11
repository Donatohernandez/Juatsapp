package com.juatsapp.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.MaskFormatter;

/**
 * Ventana para consultar y editar datos del usuario.
 */
public class ProfileFrame extends JDialog {

    private final UserDao userDao;
    private User currentUser;

    private JTextField txtPhone, txtAddress;
    private JFormattedTextField txtBirthDate;
    private JComboBox<String> cbSex;

    public ProfileFrame(Frame owner, User user) {
        super(owner, "Perfil de usuario", true);
        this.userDao = new UserDao();
        this.currentUser = user;
        initComponents();
        loadUserData();
    }

    private void style(JTextField f) {
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
    }

    private JFormattedTextField createDateField() {
        try {
            MaskFormatter mask = new MaskFormatter("####-##-##");
            mask.setPlaceholderCharacter('_');
            JFormattedTextField field = new JFormattedTextField(mask);
            style(field);
            return field;
        } catch (ParseException e) {
            JFormattedTextField f = new JFormattedTextField();
            style(f);
            return f;
        }
    }

    private void initComponents() {
        setSize(380, 400);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0xECE5DD));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Teléfono (solo lectura)
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Teléfono:"), gbc);
        txtPhone = new JTextField();
        txtPhone.setEditable(false);
        style(txtPhone);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
        row++;

        // Fecha nac
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Fecha nac. (yyyy-MM-dd):"), gbc);
        txtBirthDate = createDateField();
        gbc.gridx = 1;
        panel.add(txtBirthDate, gbc);
        row++;

        // Dirección
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Dirección:"), gbc);
        txtAddress = new JTextField();
        style(txtAddress);
        gbc.gridx = 1;
        panel.add(txtAddress, gbc);
        row++;

        // Sexo
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Sexo:"), gbc);
        cbSex = new JComboBox<>(new String[]{"masculino", "femenino", "robot", "ninja", "otro"});
        gbc.gridx = 1;
        panel.add(cbSex, gbc);
        row++;

        // Botones
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        JButton btnClose = new JButton("Cerrar");

        btnSave.addActionListener(this::onSave);
        btnClose.addActionListener(e -> dispose());

        buttons.add(btnSave);
        buttons.add(btnClose);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);

        setContentPane(panel);
    }

    private void loadUserData() {
        txtPhone.setText(currentUser.getPhone());

        if (currentUser.getBirthDate() != null) {
            txtBirthDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(currentUser.getBirthDate()));
        }
        txtAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        cbSex.setSelectedItem(currentUser.getSex());
    }

    private void onSave(ActionEvent e) {
        try {
            String birthStr = txtBirthDate.getText().trim();
            String address = txtAddress.getText().trim();
            String sex = (String) cbSex.getSelectedItem();

            if (birthStr.contains("_")) {
                JOptionPane.showMessageDialog(this, "Completa la fecha correctamente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthStr);

            currentUser.setBirthDate(birthDate);
            currentUser.setAddress(address);
            currentUser.setSex(sex);
            currentUser.setUpdatedAt(new Date());

            userDao.updateUser(currentUser);

            JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
