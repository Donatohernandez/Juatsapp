package com.juatsapp.app;



import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada principal de Juatsapp.
 * Configura el Look & Feel y abre la ventana de autenticación.
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Look & Feel moderno
                FlatLightLaf.setup();

                // Bordes redondeados globales
                UIManager.put("Button.arc", 12);
                UIManager.put("Component.arc", 12);
                UIManager.put("TextComponent.arc", 12);
            } catch (Exception ex) {
                System.err.println("Error al inicializar FlatLaf: " + ex.getMessage());
            }

            
            AuthFrame frame = new AuthFrame();
            frame.setVisible(true);
        });
    }
}
