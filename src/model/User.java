package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model dan DAO untuk User (Admin & Kasir) - UPDATED WITH ADDRESS & PHONE
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String namaLengkap;
    private String alamat;        // NEW
    private String noTelepon;     // NEW
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
    
    // NEW
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    
    // NEW
    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // DAO Methods
    
    /**
     * Login user
     */
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = TRUE";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setAlamat(rs.getString("alamat"));         // NEW
                user.setNoTelepon(rs.getString("no_telepon"));  // NEW
                user.setActive(rs.getBoolean("is_active"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    /**
     * Tambah user baru (Admin only) - UPDATED
     */
    public static boolean tambahUser(String username, String password, String role, 
                                    String namaLengkap, String alamat, String noTelepon) {
        String sql = "INSERT INTO users (username, password, role, nama_lengkap, alamat, no_telepon) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setString(4, namaLengkap);
            ps.setString(5, alamat);
            ps.setString(6, noTelepon);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error tambah user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get semua user (Admin only) - UPDATED
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = TRUE ORDER BY id";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setAlamat(rs.getString("alamat"));         // NEW
                user.setNoTelepon(rs.getString("no_telepon"));  // NEW
                user.setActive(rs.getBoolean("is_active"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get all users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return users;
    }
    
    /**
     * Update user (tanpa password) - UPDATED
     */
    public static boolean updateUser(int userId, String username, String namaLengkap, 
                                     String role, String alamat, String noTelepon) {
        String sql = "UPDATE users SET username = ?, nama_lengkap = ?, role = ?, " +
                    "alamat = ?, no_telepon = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, username);
            ps.setString(2, namaLengkap);
            ps.setString(3, role);
            ps.setString(4, alamat);
            ps.setString(5, noTelepon);
            ps.setInt(6, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Update password user
     */
    public static boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update password: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Soft delete user
     */
    public static boolean deleteUser(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get user by ID - UPDATED
     */
    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ? AND is_active = TRUE";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setNamaLengkap(rs.getString("nama_lengkap"));
                user.setAlamat(rs.getString("alamat"));         // NEW
                user.setNoTelepon(rs.getString("no_telepon"));  // NEW
                user.setActive(rs.getBoolean("is_active"));
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error get user by id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return namaLengkap + " (" + role + ")";
    }
}