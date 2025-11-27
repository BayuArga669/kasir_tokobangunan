package main;

import view.LoginForm;
import config.DatabaseConfig;
import javax.swing.*;

/**
 * Main Application Entry Point
 * Aplikasi Kasir Toko Bangunan
 * 
 * @author Toko Bangunan Maju Jaya
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Set Look and Feel to System default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }
        
        // Test database connection
        System.out.println("=================================");
        System.out.println("APLIKASI KASIR TOKO BANGUNAN");
        System.out.println("=================================");
        System.out.println("Testing database connection...");
        
        if (DatabaseConfig.testConnection()) {
            System.out.println("✓ Database connected successfully!");
            System.out.println("=================================\n");
            
            // Launch Login Form
            SwingUtilities.invokeLater(() -> {
                new LoginForm().setVisible(true);
            });
            
        } else {
            System.err.println("✗ Database connection failed!");
            System.err.println("Please check:");
            System.err.println("1. MySQL Server is running");
            System.err.println("2. Database 'kasir_toko_bangunan' exists");
            System.err.println("3. Username and password in DatabaseConfig.java");
            System.err.println("=================================\n");
            
            // Show error dialog
            JOptionPane.showMessageDialog(null,
                "Gagal koneksi ke database!\n\n" +
                "Pastikan:\n" +
                "1. MySQL Server sudah berjalan\n" +
                "2. Database 'kasir_toko_bangunan' sudah dibuat\n" +
                "3. Username dan password di DatabaseConfig.java benar\n\n" +
                "Aplikasi akan ditutup.",
                "Error Database",
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
}