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
    
    @Override
    public String toString() {
        return namaKategori;
    }
}