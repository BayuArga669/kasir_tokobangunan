package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model dan DAO untuk Kategori Produk
 */
public class Kategori {
    private int id;
    private String namaKategori;
    private String deskripsi;
    
    // Constructors
    public Kategori() {}
    
    public Kategori(int id, String namaKategori) {
        this.id = id;
        this.namaKategori = namaKategori;
    }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    // DAO Methods
    
    /**
     * Get semua kategori
     */
    public static List<Kategori> getAllKategori() {
        List<Kategori> kategoriList = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY nama_kategori";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Kategori k = new Kategori();
                k.setId(rs.getInt("id"));
                k.setNamaKategori(rs.getString("nama_kategori"));
                k.setDeskripsi(rs.getString("deskripsi"));
                kategoriList.add(k);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get all kategori: " + e.getMessage());
        }
        
        return kategoriList;
    }
    
    /**
     * Tambah kategori baru
     */
    public static boolean tambahKategori(String namaKategori, String deskripsi) {
        String sql = "INSERT INTO kategori (nama_kategori, deskripsi) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, namaKategori);
            ps.setString(2, deskripsi);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error tambah kategori: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update kategori
     * Method baru yang dibutuhkan FormKategori
     */
    public static boolean updateKategori(int id, String namaKategori, String deskripsi) {
        String sql = "UPDATE kategori SET nama_kategori = ?, deskripsi = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, namaKategori);
            ps.setString(2, deskripsi);
            ps.setInt(3, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update kategori: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Hapus kategori
     * Method baru yang dibutuhkan FormKategori
     */
    public static boolean deleteKategori(int id) {
        String sql = "DELETE FROM kategori WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete kategori: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get kategori by ID
     * Method baru yang dibutuhkan FormKategori
     */
    public static Kategori getKategoriById(int id) {
        String sql = "SELECT * FROM kategori WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Kategori k = new Kategori();
                k.setId(rs.getInt("id"));
                k.setNamaKategori(rs.getString("nama_kategori"));
                k.setDeskripsi(rs.getString("deskripsi"));
                return k;
            }
            
        } catch (SQLException e) {
            System.err.println("Error get kategori by id: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Cek duplikasi nama kategori
     * Method baru untuk validasi di FormKategori
     */
    public static boolean isDuplicate(String namaKategori, int excludeId) {
        String sql = "SELECT COUNT(*) as total FROM kategori WHERE nama_kategori = ? AND id != ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, namaKategori);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error check duplicate: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Hitung jumlah produk per kategori
     * Method baru untuk info di FormKategori
     */
    public static int countProdukByKategori(int kategoriId) {
        String sql = "SELECT COUNT(*) as total FROM produk WHERE kategori_id = ? AND is_deleted = FALSE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, kategoriId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error count produk: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Search kategori by keyword
     * Method baru untuk fitur search di FormKategori
     */
    public static List<Kategori> searchKategori(String keyword) {
        List<Kategori> kategoriList = new ArrayList<>();
        String sql = "SELECT * FROM kategori WHERE " +
                     "nama_kategori LIKE ? OR deskripsi LIKE ? " +
                     "ORDER BY nama_kategori";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Kategori k = new Kategori();
                k.setId(rs.getInt("id"));
                k.setNamaKategori(rs.getString("nama_kategori"));
                k.setDeskripsi(rs.getString("deskripsi"));
                kategoriList.add(k);
            }
            
        } catch (SQLException e) {
            System.err.println("Error search kategori: " + e.getMessage());
        }
        
        return kategoriList;
    }
    
    @Override
    public String toString() {
        return namaKategori;
    }
}