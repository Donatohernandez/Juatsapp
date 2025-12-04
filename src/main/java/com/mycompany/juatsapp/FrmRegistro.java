/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.juatsapp;
import javax.swing.*;
import org.bson.Document;

/**
 *
 * @author luisf.salido
 */
public class FrmRegistro extends JFrame {

    private JTextField txtTelefono, txtDireccion, txtNacimiento;
    private JPasswordField txtPassword;
    private JComboBox<String> cbSexo;
    private JButton btnGuardar;

    private UserDAO userDAO = new UserDAO();

    public FrmRegistro() {

        setTitle("Juatsapp - Registro");
        setSize(430, 380);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTel = new JLabel("Teléfono:");
        lblTel.setBounds(30, 30, 150, 25);
        add(lblTel);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(180, 30, 200, 25);
        add(txtTelefono);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(30, 70, 150, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180, 70, 200, 25);
        add(txtPassword);

        JLabel lblNac = new JLabel("Nacimiento (YYYY-MM-DD):");
        lblNac.setBounds(30, 110, 200, 25);
        add(lblNac);

        txtNacimiento = new JTextField();
        txtNacimiento.setBounds(230, 110, 150, 25);
        add(txtNacimiento);

        JLabel lblDir = new JLabel("Dirección:");
        lblDir.setBounds(30, 150, 150, 25);
        add(lblDir);

        txtDireccion = new JTextField();
        txtDireccion.setBounds(180, 150, 200, 25);
        add(txtDireccion);

        JLabel lblSexo = new JLabel("Sexo:");
        lblSexo.setBounds(30, 190, 150, 25);
        add(lblSexo);

        cbSexo = new JComboBox<>(new String[]{"Masculino", "Femenino", "Robot", "Ninja", "Otro"});
        cbSexo.setBounds(180, 190, 200, 25);
        add(cbSexo);

        btnGuardar = new JButton("Registrar");
        btnGuardar.setBounds(140, 250, 150, 40);
        add(btnGuardar);

        btnGuardar.addActionListener(e -> registrar());
    }

    private void registrar() {
        String tel = txtTelefono.getText();
        String pass = new String(txtPassword.getPassword());
        String nac = txtNacimiento.getText();
        String dir = txtDireccion.getText();
        String sexo = cbSexo.getSelectedItem().toString();

        if (!tel.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe ser de 10 dígitos.");
            return;
        }

        if (!pass.matches("^(?=.*[A-Z])(?=.*[0-9]).{8,}$")) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener:\n• 8 caracteres\n• 1 mayúscula\n• 1 número");
            return;
        }

        String passEnc = EncryptUtil.sha256(pass);

        userDAO.registrarUsuario(tel, passEnc, nac, dir, sexo);
        JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
        dispose();
    }
}