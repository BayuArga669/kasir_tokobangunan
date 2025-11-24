package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model dan DAO untuk Barang Masuk (Penambahan Stok)
 */
public class BarangMasuk {
    private int id;
    private int produkId;
    private String namaProduk;
    private String satuanDasar;
    private double jumlah;
    private double hargaBeliSatuan;
    private double totalHarga;
    private Date tanggalMasuk;
    private String keterangan;
    private int userId;
    private String namaUser;
    
    // Constructors
    public BarangMasuk() {}
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }
    
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    
    public String getSatuanDasar() { return satuanDasar; }
    public void setSatuanDasar(String satuanDasar) { this.satuanDasar = satuanDasar; }
    
    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }
    
    public double getHargaBeliSatuan() { return hargaBeliSatuan; }
    public void setHargaBeliSatuan(double hargaBeliSatuan) { this.hargaBeliSatuan = hargaBeliSatuan; }
    
    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }
    
    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }
    
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getNamaUser() { return namaUser; }
    public void setNamaUser(String namaUser) { this.namaUser = namaUser; }
    
    // DAO Methods
    
    /**
     * Tambah barang masuk (record penambahan stok) + UPDATE HARGA BELI PRODUK
     */
    public static boolean tambahBarangMasuk(int produkId, double jumlah, double hargaBeli, 
                                           double totalHarga, int userId, String keterangan) {
        Connection conn = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insert barang masuk
            String sqlInsert = "INSERT INTO barang_masuk (produk_id, jumlah, harga_beli_satuan, " +
                        "total_harga, user_id, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, produkId);
            psInsert.setDouble(2, jumlah);
            psInsert.setDouble(3, hargaBeli);
            psInsert.setDouble(4, totalHarga);
            psInsert.setInt(5, userId);
            psInsert.setString(6, keterangan.isEmpty() ? null : keterangan);
            
            int inserted = psInsert.executeUpdate();
            
            if (inserted > 0) {
                // 2. Update harga beli di tabel produk (harga beli terakhir)
                String sqlUpdate = "UPDATE produk SET harga_beli = ? WHERE id = ?";
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setDouble(1, hargaBeli);
                psUpdate.setInt(2, produkId);
                psUpdate.executeUpdate();
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rollback: " + ex.getMessage());
            }
            System.err.println("Error tambah barang masuk: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error set auto commit: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get semua history barang masuk
     */

    
    /**
     * Get semua history barang masuk
     */
    public static List<BarangMasuk> getAllBarangMasuk() {
        List<BarangMasuk> barangMasukList = new ArrayList<>();
        String sql = "SELECT bm.*, p.nama_produk, p.satuan_dasar, u.nama_lengkap " +
                    "FROM barang_masuk bm " +
                    "JOIN produk p ON bm.produk_id = p.id " +
                    "LEFT JOIN users u ON bm.user_id = u.id " +
                    "ORDER BY bm.tanggal_masuk DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                BarangMasuk bm = new BarangMasuk();
                bm.setId(rs.getInt("id"));
                bm.setProdukId(rs.getInt("produk_id"));
                bm.setNamaProduk(rs.getString("nama_produk"));
                bm.setSatuanDasar(rs.getString("satuan_dasar"));
                bm.setJumlah(rs.getDouble("jumlah"));
                bm.setHargaBeliSatuan(rs.getDouble("harga_beli_satuan"));
                bm.setTotalHarga(rs.getDouble("total_harga"));
                bm.setTanggalMasuk(rs.getTimestamp("tanggal_masuk"));
                bm.setKeterangan(rs.getString("keterangan"));
                bm.setUserId(rs.getInt("user_id"));
                bm.setNamaUser(rs.getString("nama_lengkap"));
                
                barangMasukList.add(bm);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get all barang masuk: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return barangMasukList;
    }
    
    /**
     * Get barang masuk by produk ID
     */
    public static List<BarangMasuk> getBarangMasukByProduk(int produkId) {
        List<BarangMasuk> barangMasukList = new ArrayList<>();
        String sql = "SELECT bm.*, p.nama_produk, p.satuan_dasar, u.nama_lengkap " +
                    "FROM barang_masuk bm " +
                    "JOIN produk p ON bm.produk_id = p.id " +
                    "LEFT JOIN users u ON bm.user_id = u.id " +
                    "WHERE bm.produk_id = ? " +
                    "ORDER BY bm.tanggal_masuk DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, produkId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BarangMasuk bm = new BarangMasuk();
                bm.setId(rs.getInt("id"));
                bm.setProdukId(rs.getInt("produk_id"));
                bm.setNamaProduk(rs.getString("nama_produk"));
                bm.setSatuanDasar(rs.getString("satuan_dasar"));
                bm.setJumlah(rs.getDouble("jumlah"));
                bm.setHargaBeliSatuan(rs.getDouble("harga_beli_satuan"));
                bm.setTotalHarga(rs.getDouble("total_harga"));
                bm.setTanggalMasuk(rs.getTimestamp("tanggal_masuk"));
                bm.setKeterangan(rs.getString("keterangan"));
                bm.setUserId(rs.getInt("user_id"));
                bm.setNamaUser(rs.getString("nama_lengkap"));
                
                barangMasukList.add(bm);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get barang masuk by produk: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return barangMasukList;
    }
    
    /**
     * Get barang masuk by periode
     */
    public static List<BarangMasuk> getBarangMasukByPeriode(Date dari, Date sampai) {
        List<BarangMasuk> barangMasukList = new ArrayList<>();
        String sql = "SELECT bm.*, p.nama_produk, p.satuan_dasar, u.nama_lengkap " +
                    "FROM barang_masuk bm " +
                    "JOIN produk p ON bm.produk_id = p.id " +
                    "LEFT JOIN users u ON bm.user_id = u.id " +
                    "WHERE DATE(bm.tanggal_masuk) BETWEEN ? AND ? " +
                    "ORDER BY bm.tanggal_masuk DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(dari.getTime()));
            ps.setDate(2, new java.sql.Date(sampai.getTime()));
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BarangMasuk bm = new BarangMasuk();
                bm.setId(rs.getInt("id"));
                bm.setProdukId(rs.getInt("produk_id"));
                bm.setNamaProduk(rs.getString("nama_produk"));
                bm.setSatuanDasar(rs.getString("satuan_dasar"));
                bm.setJumlah(rs.getDouble("jumlah"));
                bm.setHargaBeliSatuan(rs.getDouble("harga_beli_satuan"));
                bm.setTotalHarga(rs.getDouble("total_harga"));
                bm.setTanggalMasuk(rs.getTimestamp("tanggal_masuk"));
                bm.setKeterangan(rs.getString("keterangan"));
                bm.setUserId(rs.getInt("user_id"));
                bm.setNamaUser(rs.getString("nama_lengkap"));
                
                barangMasukList.add(bm);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get barang masuk by periode: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return barangMasukList;
    }
    
    /**
     * Get total nilai barang masuk by periode (untuk laporan)
     */
    public static double getTotalNilaiBarangMasuk(Date dari, Date sampai) {
        String sql = "SELECT COALESCE(SUM(total_harga), 0) as total " +
                    "FROM barang_masuk " +
                    "WHERE DATE(tanggal_masuk) BETWEEN ? AND ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(dari.getTime()));
            ps.setDate(2, new java.sql.Date(sampai.getTime()));
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error get total nilai barang masuk: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return 0;
    }
}