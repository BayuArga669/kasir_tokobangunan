package model;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model dan DAO untuk Produk
 */
public class Produk {
    private int id;
    private String namaProduk;
    private int kategoriId;
    private String namaKategori;
    private String satuanDasar;
    private double stokDasar;
    private List<SatuanJual> daftarSatuan;
    private double hargaBeli;

    
    // Constructors
    public Produk() {
        this.daftarSatuan = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return namaProduk;
    }

    // Getters & Setters
    
    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    
    public int getKategoriId() { return kategoriId; }
    public void setKategoriId(int kategoriId) { this.kategoriId = kategoriId; }
    
    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }
    
    public String getSatuanDasar() { return satuanDasar; }
    public void setSatuanDasar(String satuanDasar) { this.satuanDasar = satuanDasar; }
    
    public double getStokDasar() { return stokDasar; }
    public void setStokDasar(double stokDasar) { this.stokDasar = stokDasar; }
    
    public List<SatuanJual> getDaftarSatuan() { return daftarSatuan; }
    public void setDaftarSatuan(List<SatuanJual> daftarSatuan) { this.daftarSatuan = daftarSatuan; }
    
    // DAO Methods
    
    /**
     * Tambah produk baru dengan satuan jualnya
     */
    public static boolean tambahProduk(Produk produk) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // PERBAIKAN: Tambahkan harga_beli ke query INSERT
            String sqlProduk = "INSERT INTO produk (nama_produk, kategori_id, satuan_dasar, stok_dasar, harga_beli) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psProduk = conn.prepareStatement(sqlProduk, Statement.RETURN_GENERATED_KEYS);
            psProduk.setString(1, produk.getNamaProduk());
            psProduk.setInt(2, produk.getKategoriId());
            psProduk.setString(3, produk.getSatuanDasar());
            psProduk.setDouble(4, produk.getStokDasar());
            psProduk.setDouble(5, produk.getHargaBeli()); // Tambahkan ini
            
            int affected = psProduk.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }
            
            // Get generated ID
            ResultSet rs = psProduk.getGeneratedKeys();
            int produkId = 0;
            if (rs.next()) {
                produkId = rs.getInt(1);
            }
            
            // Insert satuan jual
            String sqlSatuan = "INSERT INTO satuan_jual (produk_id, nama_satuan, konversi_ke_dasar, harga_jual, barcode) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psSatuan = conn.prepareStatement(sqlSatuan);
            
            for (SatuanJual satuan : produk.getDaftarSatuan()) {
                psSatuan.setInt(1, produkId);
                psSatuan.setString(2, satuan.getNamaSatuan());
                psSatuan.setDouble(3, satuan.getKonversiKeDasar());
                psSatuan.setDouble(4, satuan.getHargaJual());
                psSatuan.setString(5, satuan.getBarcode());
                psSatuan.addBatch();
            }
            
            psSatuan.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rollback: " + ex.getMessage());
            }
            System.err.println("Error tambah produk: " + e.getMessage());
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
     * Get semua produk dengan join kategori
     */
    public static List<Produk> getAllProduk() {
        List<Produk> produkList = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori FROM produk p " +
                     "LEFT JOIN kategori k ON p.kategori_id = k.id " +
                     "WHERE p.is_deleted = FALSE ORDER BY p.id";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Produk p = new Produk();
                p.setId(rs.getInt("id"));
                p.setNamaProduk(rs.getString("nama_produk"));
                p.setKategoriId(rs.getInt("kategori_id"));
                p.setNamaKategori(rs.getString("nama_kategori"));
                p.setSatuanDasar(rs.getString("satuan_dasar"));
                p.setStokDasar(rs.getDouble("stok_dasar"));
                p.setHargaBeli(rs.getDouble("harga_beli")); // Ini sudah benar
                
                produkList.add(p);
            }
            
            rs.close();
            stmt.close();
            
            for (Produk p : produkList) {
                p.setDaftarSatuan(SatuanJual.getSatuanByProdukId(p.getId()));
            }
            
        } catch (SQLException e) {
            System.err.println("Error get all produk: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
                if (stmt != null && !stmt.isClosed()) stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return produkList;
    }
    
    // PERBAIKAN: Update metode updateProduk untuk menerima dan menyimpan harga_beli
    public static boolean updateProduk(int produkId, String namaProduk, int kategoriId, 
                                       String satuanDasar, double stokDasar, double hargaBeli) {
        String sql = "UPDATE produk SET nama_produk = ?, kategori_id = ?, " +
                     "satuan_dasar = ?, stok_dasar = ?, harga_beli = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, namaProduk);
            ps.setInt(2, kategoriId);
            ps.setString(3, satuanDasar);
            ps.setDouble(4, stokDasar);
            ps.setDouble(5, hargaBeli); // Tambahkan ini
            ps.setInt(6, produkId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update produk: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get produk by ID
     */
    public static Produk getProdukById(int id) {
        String sql = "SELECT p.*, k.nama_kategori FROM produk p " +
                     "LEFT JOIN kategori k ON p.kategori_id = k.id " +
                     "WHERE p.id = ? AND p.is_deleted = FALSE";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Produk p = new Produk();
                p.setId(rs.getInt("id"));
                p.setNamaProduk(rs.getString("nama_produk"));
                p.setKategoriId(rs.getInt("kategori_id"));
                p.setNamaKategori(rs.getString("nama_kategori"));
                p.setSatuanDasar(rs.getString("satuan_dasar"));
                p.setStokDasar(rs.getDouble("stok_dasar"));
                p.setHargaBeli(rs.getDouble("harga_beli")); // Ini sudah benar
                
                rs.close();
                ps.close();
                
                p.setDaftarSatuan(SatuanJual.getSatuanByProdukId(p.getId()));
                
                return p;
            }
            
        } catch (SQLException e) {
            System.err.println("Error get produk by id: " + e.getMessage());
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
     * Search produk by nama
     */
    public static List<Produk> searchProduk(String keyword) {
        List<Produk> produkList = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori FROM produk p " +
                     "LEFT JOIN kategori k ON p.kategori_id = k.id " +
                     "WHERE p.is_deleted = FALSE AND p.nama_produk LIKE ? " +
                     "ORDER BY p.nama_produk";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Produk p = new Produk();
                p.setId(rs.getInt("id"));
                p.setNamaProduk(rs.getString("nama_produk"));
                p.setKategoriId(rs.getInt("kategori_id"));
                p.setNamaKategori(rs.getString("nama_kategori"));
                p.setSatuanDasar(rs.getString("satuan_dasar"));
                p.setStokDasar(rs.getDouble("stok_dasar"));
                p.setHargaBeli(rs.getDouble("harga_beli")); // Ini sudah benar
                
                produkList.add(p);
            }
            
            rs.close();
            ps.close();
            
            for (Produk p : produkList) {
                p.setDaftarSatuan(SatuanJual.getSatuanByProdukId(p.getId()));
            }
            
        } catch (SQLException e) {
            System.err.println("Error search produk: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null && !rs.isClosed()) rs.close();
                if (ps != null && !ps.isClosed()) ps.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        return produkList;
    }
    
    /**
     * Update stok produk
     */
    public static boolean updateStok(int produkId, double stokBaru) {
        String sql = "UPDATE produk SET stok_dasar = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, stokBaru);
            ps.setInt(2, produkId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error update stok: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Soft delete produk
     */
    public static boolean deleteProduk(int produkId) {
        String sql = "UPDATE produk SET is_deleted = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, produkId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error delete produk: " + e.getMessage());
            return false;
        }
    }
}