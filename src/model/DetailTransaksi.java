package model;

public class DetailTransaksi {
    private int id;
    private int transaksiId;
    private int produkId;
    private int satuanJualId;
    private String namaProduk;
    private String namaSatuan;
    private double qty;
    private double hargaSatuan;
    private double subtotal;
    
    // Constructor
    public DetailTransaksi() {}

    public DetailTransaksi(int transaksiId, int produkId, int satuanJualId, double qty, double hargaSatuan, double subtotal) {
        this.transaksiId = transaksiId;
        this.produkId = produkId;
        this.satuanJualId = satuanJualId;
        this.qty = qty;
        this.hargaSatuan = hargaSatuan;
        this.subtotal = subtotal;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransaksiId() { return transaksiId; }
    public void setTransaksiId(int transaksiId) { this.transaksiId = transaksiId; }

    public int getProdukId() { return produkId; }
    public void setProdukId(int produkId) { this.produkId = produkId; }

    public int getSatuanJualId() { return satuanJualId; }
    public void setSatuanJualId(int satuanJualId) { this.satuanJualId = satuanJualId; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public String getNamaSatuan() { return namaSatuan; }
    public void setNamaSatuan(String namaSatuan) { this.namaSatuan = namaSatuan; }

    public double getQty() { return qty; }
    public void setQty(double qty) { this.qty = qty; }

    public double getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(double hargaSatuan) { this.hargaSatuan = hargaSatuan; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}