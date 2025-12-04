/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.juatsapp;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 *
 * @author luisf.salido
 */
public class FrmChat extends JFrame {

    private JTextPane txtChat;
    private JTextField txtMensaje;
    private JButton btnEnviar;

    public FrmChat() {
        setTitle("Juatsapp - Chat");
        setSize(420, 520);
        setLocationRelativeTo(null);
        setLayout(null);

        txtChat = new JTextPane();
        txtChat.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtChat);
        scroll.setBounds(20, 20, 360, 350);
        add(scroll);

        txtMensaje = new JTextField();
        txtMensaje.setBounds(20, 390, 250, 35);
        add(txtMensaje);

        btnEnviar = new JButton("Enviar");
        btnEnviar.setBounds(280, 390, 100, 35);
        add(btnEnviar);

        btnEnviar.addActionListener(e -> enviarMensaje());
    }

    private void enviarMensaje() {
        String msj = txtMensaje.getText();

        if (msj.isBlank()) return;

        appendMessage("Tú: " + msj, Color.BLUE);
        txtMensaje.setText("");
    }

    private void appendMessage(String msg, Color color) {
        StyledDocument doc = txtChat.getStyledDocument();
        Style style = txtChat.addStyle("Style", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), msg + "\n", style);
            txtChat.setCaretPosition(doc.getLength());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}