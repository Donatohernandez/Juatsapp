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
public class FrmLogin extends JFrame {

    private JTextField txtTelefono;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegistro;

    private UserDAO userDAO = new UserDAO();

    public FrmLogin() {
        setTitle("Juatsapp - Login");
        setSize(350, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblTel = new JLabel("Teléfono:");
        lblTel.setBounds(30, 40, 100, 25);
        add(lblTel);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(120, 40, 180, 25);
        add(txtTelefono);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(30, 80, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 80, 180, 25);
        add(txtPassword);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(100, 130, 150, 30);
        add(btnLogin);

        btnRegistro = new JButton("Crear cuenta");
        btnRegistro.setBounds(100, 170, 150, 30);
        add(btnRegistro);

        btnRegistro.addActionListener(e -> new FrmRegistro().setVisible(true));

        btnLogin.addActionListener(e -> login());
    }

    private void login() {
        String tel = txtTelefono.getText();
        String pass = EncryptUtil.sha256(new String(txtPassword.getPassword()));

        Document user = userDAO.iniciarSesion(tel, pass);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Datos incorrectos");
            return;
        }

        JOptionPane.showMessageDialog(this, "Bienvenido a Juatsapp!");
        new FrmChat().setVisible(true);
    }
}