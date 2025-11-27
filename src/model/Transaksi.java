package model;

import config.DatabaseConfig;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model dan DAO untuk Transaksi - UPDATED with Payment Method
 */
public class Transaksi {
    private int id;
    private String kodeTransaksi;
    private Date tanggal;
    private double totalBelanja;
    private double bayar;
    private double kembalian;
    private int kasirId;
    private String namaKasir;
    private String metodePembayaran; // NEW
    private List<DetailTransaksi> detailList;
    
    // Constructors
    public Transaksi() {
        this.detailList = new ArrayList<>();
        this.metodePembayaran = "Tunai"; // Default
    }
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getKodeTransaksi() { return kodeTransaksi; }
    public void setKodeTransaksi(String kodeTransaksi) { this.kodeTransaksi = kodeTransaksi; }
    
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    
    public double getTotalBelanja() { return totalBelanja; }
    public void setTotalBelanja(double totalBelanja) { this.totalBelanja = totalBelanja; }
    
    public double getBayar() { return bayar; }
    public void setBayar(double bayar) { this.bayar = bayar; }
    
    public double getKembalian() { return kembalian; }
    public void setKembalian(double kembalian) { this.kembalian = kembalian; }
    
    public int getKasirId() { return kasirId; }
    public void setKasirId(int kasirId) { this.kasirId = kasirId; }
    
    public String getNamaKasir() { return namaKasir; }
    public void setNamaKasir(String namaKasir) { this.namaKasir = namaKasir; }
    
    // NEW
    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    
    public List<DetailTransaksi> getDetailList() { 
        return detailList; 
    }
    public void setDetailList(List<DetailTransaksi> detailList) { 
        this.detailList = detailList; 
    }
    
    // DAO Methods
    
    /**
     * Generate kode transaksi unik
     */
    public static String generateKodeTransaksi() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "TRX-" + sdf.format(new Date());
    }
    
    /**
     * Simpan transaksi baru (dengan detail dan update stok) - UPDATED
     */
    public static boolean simpanTransaksi(Transaksi transaksi) {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // Insert transaksi header - UPDATED dengan metode_pembayaran
            String sqlHeader = "INSERT INTO transaksi (kode_transaksi, total_belanja, bayar, kembalian, kasir_id, metode_pembayaran) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psHeader = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            
            psHeader.setString(1, transaksi.getKodeTransaksi());
            psHeader.setDouble(2, transaksi.getTotalBelanja());
            psHeader.setDouble(3, transaksi.getBayar());
            psHeader.setDouble(4, transaksi.getKembalian());
            psHeader.setInt(5, transaksi.getKasirId());
            psHeader.setString(6, transaksi.getMetodePembayaran()); // NEW
            
            int affected = psHeader.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }
            
            // Get generated ID
            ResultSet rs = psHeader.getGeneratedKeys();
            int transaksiId = 0;
            if (rs.next()) {
                transaksiId = rs.getInt(1);
            }
            
            // Insert detail transaksi
            String sqlDetail = "INSERT INTO detail_transaksi (transaksi_id, produk_id, satuan_jual_id, nama_produk, nama_satuan, qty, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
            
            // Update stok
            String sqlUpdateStok = "UPDATE produk SET stok_dasar = stok_dasar - ? WHERE id = ?";
            PreparedStatement psStok = conn.prepareStatement(sqlUpdateStok);
            
            for (DetailTransaksi detail : transaksi.getDetailList()) {
                // Insert detail
                psDetail.setInt(1, transaksiId);
                psDetail.setInt(2, detail.getProdukId());
                psDetail.setInt(3, detail.getSatuanJualId());
                psDetail.setString(4, detail.getNamaProduk());
                psDetail.setString(5, detail.getNamaSatuan());
                psDetail.setDouble(6, detail.getQty());
                psDetail.setDouble(7, detail.getHargaSatuan());
                psDetail.setDouble(8, detail.getSubtotal());
                psDetail.addBatch();
                
                // Update stok (konversi ke satuan dasar)
                SatuanJual satuan = SatuanJual.getSatuanById(detail.getSatuanJualId());
                double stokKurang = detail.getQty() * satuan.getKonversiKeDasar();
                
                psStok.setDouble(1, stokKurang);
                psStok.setInt(2, detail.getProdukId());
                psStok.addBatch();
            }
            
            psDetail.executeBatch();
            psStok.executeBatch();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rollback: " + ex.getMessage());
            }
            System.err.println("Error simpan transaksi: " + e.getMessage());
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
     * Get transaksi by periode - UPDATED
     */
    public static List<Transaksi> getTransaksiByPeriode(Date dari, Date sampai) {
        List<Transaksi> transaksiList = new ArrayList<>();
        String sql = "SELECT t.*, u.nama_lengkap FROM transaksi t " +
                     "JOIN users u ON t.kasir_id = u.id " +
                     "WHERE DATE(t.tanggal) BETWEEN ? AND ? " +
                     "ORDER BY t.tanggal DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(dari.getTime()));
            ps.setDate(2, new java.sql.Date(sampai.getTime()));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setId(rs.getInt("id"));
                t.setKodeTransaksi(rs.getString("kode_transaksi"));
                t.setTanggal(rs.getTimestamp("tanggal"));
                t.setTotalBelanja(rs.getDouble("total_belanja"));
                t.setBayar(rs.getDouble("bayar"));
                t.setKembalian(rs.getDouble("kembalian"));
                t.setKasirId(rs.getInt("kasir_id"));
                t.setNamaKasir(rs.getString("nama_lengkap"));
                t.setMetodePembayaran(rs.getString("metode_pembayaran")); // NEW
                transaksiList.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get transaksi by periode: " + e.getMessage());
        }
        
        return transaksiList;
    }
    
    /**
     * Get transaksi by ID - NEW
     */
    public static Transaksi getTransaksiById(int transaksiId) {
        String sql = "SELECT t.*, u.nama_lengkap FROM transaksi t " +
                     "JOIN users u ON t.kasir_id = u.id " +
                     "WHERE t.id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, transaksiId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Transaksi t = new Transaksi();
                t.setId(rs.getInt("id"));
                t.setKodeTransaksi(rs.getString("kode_transaksi"));
                t.setTanggal(rs.getTimestamp("tanggal"));
                t.setTotalBelanja(rs.getDouble("total_belanja"));
                t.setBayar(rs.getDouble("bayar"));
                t.setKembalian(rs.getDouble("kembalian"));
                t.setKasirId(rs.getInt("kasir_id"));
                t.setNamaKasir(rs.getString("nama_lengkap"));
                t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                
                // Load detail
                t.setDetailList(getDetailTransaksi(transaksiId));
                
                return t;
            }
            
        } catch (SQLException e) {
            System.err.println("Error get transaksi by id: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get detail transaksi by transaksi ID
     */
    public static List<DetailTransaksi> getDetailTransaksi(int transaksiId) {
        List<DetailTransaksi> detailList = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi WHERE transaksi_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, transaksiId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                DetailTransaksi d = new DetailTransaksi();
                d.setId(rs.getInt("id"));
                d.setTransaksiId(rs.getInt("transaksi_id"));
                d.setProdukId(rs.getInt("produk_id"));
                d.setSatuanJualId(rs.getInt("satuan_jual_id"));
                d.setNamaProduk(rs.getString("nama_produk"));
                d.setNamaSatuan(rs.getString("nama_satuan"));
                d.setQty(rs.getDouble("qty"));
                d.setHargaSatuan(rs.getDouble("harga_satuan"));
                d.setSubtotal(rs.getDouble("subtotal"));
                detailList.add(d);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get detail transaksi: " + e.getMessage());
        }
        
        return detailList;
    }
    
    /**
     * Get transaksi by kasir - NEW
     */
    public static List<Transaksi> getTransaksiByKasir(int kasirId, Date dari, Date sampai) {
        List<Transaksi> transaksiList = new ArrayList<>();
        String sql = "SELECT t.*, u.nama_lengkap FROM transaksi t " +
                     "JOIN users u ON t.kasir_id = u.id " +
                     "WHERE t.kasir_id = ? AND DATE(t.tanggal) BETWEEN ? AND ? " +
                     "ORDER BY t.tanggal DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, kasirId);
            ps.setDate(2, new java.sql.Date(dari.getTime()));
            ps.setDate(3, new java.sql.Date(sampai.getTime()));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaksi t = new Transaksi();
                t.setId(rs.getInt("id"));
                t.setKodeTransaksi(rs.getString("kode_transaksi"));
                t.setTanggal(rs.getTimestamp("tanggal"));
                t.setTotalBelanja(rs.getDouble("total_belanja"));
                t.setBayar(rs.getDouble("bayar"));
                t.setKembalian(rs.getDouble("kembalian"));
                t.setKasirId(rs.getInt("kasir_id"));
                t.setNamaKasir(rs.getString("nama_lengkap"));
                t.setMetodePembayaran(rs.getString("metode_pembayaran"));
                transaksiList.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("Error get transaksi by kasir: " + e.getMessage());
        }
        
        return transaksiList;
    }
}