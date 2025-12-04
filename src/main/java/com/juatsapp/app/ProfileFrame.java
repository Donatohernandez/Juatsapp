package com.juatsapp.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ventana para consultar y editar los datos del usuario logueado.
 * No permite darse de baja, solo actualizar información básica.
 */
public class ProfileFrame extends JDialog {

    private final UserDao userDao;
    private User currentUser;

    private JTextField txtPhone;
    private JTextField txtBirthDate;
    private JTextField txtAddress;
    private JComboBox<String> cbSex;

    public ProfileFrame(Frame owner, User user) {
        super(owner, "Perfil de usuario", true);
        this.userDao = new UserDao();
        this.currentUser = user;
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setSize(380, 400);
        setLocationRelativeTo(getOwner());

        JPanel panel = new JPanel(new GridBagLayout());
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
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        JButton btnClose = new JButton("Cerrar");
        btnSave.addActionListener(this::onSave);
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClose);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
    }

    private void loadUserData() {
        txtPhone.setText(currentUser.getPhone());

        Date birthDate = currentUser.getBirthDate();
        if (birthDate != null) {
            txtBirthDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(birthDate));
        }
        txtAddress.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        String sex = currentUser.getSex();
        if (sex != null) {
            cbSex.setSelectedItem(sex);
        }
    }

    private void onSave(ActionEvent e) {
        String birthDateStr = txtBirthDate.getText().trim();
        String address = txtAddress.getText().trim();
        String sex = (String) cbSex.getSelectedItem();

        if (birthDateStr.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fecha de nacimiento y dirección son obligatorias.",
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

        currentUser.setBirthDate(birthDate);
        currentUser.setAddress(address);
        currentUser.setSex(sex);
        currentUser.setUpdatedAt(new Date());

        userDao.updateUser(currentUser);

        JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
