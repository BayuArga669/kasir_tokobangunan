package main;

import view.SplashScreen;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Main Application Entry Point
 * Aplikasi Kasir Toko Bangunan
 * 
 * @author Toko Bangunan Maju Jaya
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Set Look and Feel modern (Nimbus)
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Error setting look and feel: " + ex.getMessage());
            }
        }
        
        // Jalankan SplashScreen — jangan panggil setVisible dari sini!
        SwingUtilities.invokeLater(() -> {
            new SplashScreen().startLoading();
        });
    }
}