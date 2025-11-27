package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model dan DAO untuk Satuan Jual (Multi-satuan per produk)
 */
public class SatuanJual {
    private int id;
    private int produkId;
    private String namaSatuan;
    private double konversiKeDasar;
    private double hargaJual;
    private String barcode;
    
    // Constructors
    public SatuanJual() {}
    
    public SatuanJual(String namaSatuan, double konversiKeDasar, double hargaJual, String barcode) {
        this.namaSatuan = namaSatuan;
        this.konversiKeDasar = konversiKeDasar;
        this.hargaJual = hargaJual;
        this.barcode = barcode;
    }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }
    
    public String getNamaSatuan() { return namaSatuan; }
    public void setNamaSatuan(String namaSatuan) { this.namaSatuan = namaSatuan; }
    
    public double getKonversiKeDasar() { return konversiKeDasar; }
    public void setKonversiKeDasar(double konversiKeDasar) { this.konversiKeDasar = konversiKeDasar; }
    
    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }
    
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    
    // DAO Methods
    
    /**
     * Get satuan jual by produk ID
     */
    // Replace method getSatuanByProdukId() di file SatuanJual.java dengan yang ini:

/**
 * Get satuan jual by produk ID
 */
public static List<SatuanJual> getSatuanByProdukId(int produkId) {
    List<SatuanJual> satuanList = new ArrayList<>();
    String sql = "SELECT * FROM satuan_jual WHERE produk_id = ? ORDER BY konversi_ke_dasar DESC";
    
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
        conn = DatabaseConfig.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, produkId);
        rs = ps.executeQuery();
        
        while (rs.next()) {
            SatuanJual s = new SatuanJual();
            s.setId(rs.getInt("id"));
            s.setProdukId(rs.getInt("produk_id"));
            s.setNamaSatuan(rs.getString("nama_satuan"));
            s.setKonversiKeDasar(rs.getDouble("konversi_ke_dasar"));
            s.setHargaJual(rs.getDouble("harga_jual"));
            s.setBarcode(rs.getString("barcode"));
            satuanList.add(s);
        }
        
    } catch (SQLException e) {
        System.err.println("Error get satuan by produk: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            if (rs != null && !rs.isClosed()) rs.close();
            if (ps != null && !ps.isClosed()) ps.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
    
    return satuanList;
}

/**
 * Get satuan by ID
 */
public static SatuanJual getSatuanById(int id) {
    String sql = "SELECT * FROM satuan_jual WHERE id = ?";
    
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try {
        conn = DatabaseConfig.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        rs = ps.executeQuery();
        
        if (rs.next()) {
            SatuanJual s = new SatuanJual();
            s.setId(rs.getInt("id"));
            s.setProdukId(rs.getInt("produk_id"));
            s.setNamaSatuan(rs.getString("nama_satuan"));
            s.setKonversiKeDasar(rs.getDouble("konversi_ke_dasar"));
            s.setHargaJual(rs.getDouble("harga_jual"));
            s.setBarcode(rs.getString("barcode"));
            return s;
        }
        
    } catch (SQLException e) {
        System.err.println("Error get satuan by id: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            if (rs != null && !rs.isClosed()) rs.close();
            if (ps != null && !ps.isClosed()) ps.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
    
    return null;
}
    
    /**
     * Update harga satuan jual
     */
    public static boolean updateHarga(int satuanId, double hargaBaru) {
        String sql = "UPDATE satuan_jual SET harga_jual = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, hargaBaru);
            ps.setInt(2, satuanId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update harga: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete satuan jual
     */
    public static boolean deleteSatuan(int satuanId) {
        String sql = "DELETE FROM satuan_jual WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, satuanId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete satuan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tambah satuan jual baru ke produk existing
     */
    public static boolean tambahSatuan(SatuanJual satuan) {
        String sql = "INSERT INTO satuan_jual (produk_id, nama_satuan, konversi_ke_dasar, harga_jual, barcode) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, satuan.getProdukId());
            ps.setString(2, satuan.getNamaSatuan());
            ps.setDouble(3, satuan.getKonversiKeDasar());
            ps.setDouble(4, satuan.getHargaJual());
            ps.setString(5, satuan.getBarcode());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error tambah satuan: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return namaSatuan + " - Rp " + String.format("%,.0f", hargaJual);
    }
    
    public static boolean updateSatuan(SatuanJual satuan) {
    String sql = "UPDATE satuan_jual SET nama_satuan = ?, konversi_ke_dasar = ?, " +
                 "harga_jual = ?, barcode = ? WHERE id = ?";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, satuan.getNamaSatuan());
        ps.setDouble(2, satuan.getKonversiKeDasar());
        ps.setDouble(3, satuan.getHargaJual());
        ps.setString(4, satuan.getBarcode());
        ps.setInt(5, satuan.getId());
        
        return ps.executeUpdate() > 0;
        
    } catch (SQLException e) {
        System.err.println("Error update satuan: " + e.getMessage());
        return false;
        }
    }
}