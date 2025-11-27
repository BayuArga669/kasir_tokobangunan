package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Konfigurasi koneksi database MySQL
 */
public class DatabaseConfig {
    
    private static final String DB_URL = "jdbc:mysql://localhost:8889/kasir_toko_bangunan";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    
    private static Connection connection;
    
    /**
     * Mendapatkan koneksi database
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Driver MySQL tidak ditemukan!\n" + e.getMessage(), 
                "Error Database", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Gagal koneksi ke database!\nPastikan MySQL sudah berjalan.\n" + e.getMessage(), 
                "Error Database", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error menutup koneksi: " + e.getMessage());
        }
    }
    
    /**
     * Test koneksi database
     * @return true jika berhasil connect
     */
    public static boolean testConnection() {
        Connection conn = getConnection();
        return conn != null;
    }
}