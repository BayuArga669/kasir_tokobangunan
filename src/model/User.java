package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model dan DAO untuk User (Admin & Kasir)
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String namaLengkap;
    private boolean isActive;
    
    // Constructors
    public User() {}
    
    public User(int id, String username, String role, String namaLengkap) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.namaLengkap = namaLengkap;
    }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // DAO Methods
    
    /**
     * Login user
     */
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setActive(rs.getBoolean("is_active"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Tambah user baru (Admin only)
     */
    public static boolean tambahUser(String username, String password, String role, String namaLengkap) {
        String sql = "INSERT INTO users (username, password, role, nama_lengkap) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, namaLengkap);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error tambah user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get semua user (Admin only)
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY id";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Update password user
     */
    public static boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Soft delete user
     */
    public static boolean deleteUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return namaLengkap + " (" + role + ")";
    }
}