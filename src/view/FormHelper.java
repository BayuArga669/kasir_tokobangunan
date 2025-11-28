package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Form Kategori
 */
class FormKategori extends JDialog {
    private JTable tableKategori;
    private DefaultTableModel tableModel;
    
    public FormKategori(JFrame parent) {
        super(parent, "Master Kategori", true);
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(600, 400);
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"ID", "Nama Kategori", "Deskripsi"};
        tableModel = new DefaultTableModel(columns, 0);
        tableKategori = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableKategori);
        
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTambah = new JButton("Tambah Kategori");
        btnTambah.addActionListener(e -> tambahKategori());
        panelButton.add(btnTambah);
        
        panelMain.add(scrollPane, BorderLayout.CENTER);
        panelMain.add(panelButton, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Kategori> kategoriList = Kategori.getAllKategori();
        for (Kategori k : kategoriList) {
            Object[] row = {k.getId(), k.getNamaKategori(), k.getDeskripsi()};
            tableModel.addRow(row);
        }
    }
    
    private void tambahKategori() {
        String nama = JOptionPane.showInputDialog(this, "Nama Kategori:");
        if (nama != null && !nama.trim().isEmpty()) {
            String deskripsi = JOptionPane.showInputDialog(this, "Deskripsi:");
            if (Kategori.tambahKategori(nama, deskripsi)) {
                JOptionPane.showMessageDialog(this, "Kategori berhasil ditambahkan!");
                loadData();
            }
        }
    }
}

/**
 * Form Barang Masuk (Restock)
 */
class FormBarangMasuk extends JDialog {
    private User currentUser;
    private JComboBox<Produk> cmbProduk;
    private JTextField txtJumlah, txtHargaBeli;
    
    public FormBarangMasuk(JFrame parent, User user) {
        super(parent, "Input Barang Masuk", true);
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 300);
        setLayout(new BorderLayout());
        
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panelForm.add(new JLabel("Produk:"));
        cmbProduk = new JComboBox<>();
        loadProduk();
        panelForm.add(cmbProduk);
        
        panelForm.add(new JLabel("Jumlah Masuk:"));
        txtJumlah = new JTextField();
        panelForm.add(txtJumlah);
        
        panelForm.add(new JLabel("Harga Beli/Unit:"));
        txtHargaBeli = new JTextField();
        panelForm.add(txtHargaBeli);
        
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnSimpan = new JButton("SIMPAN");
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanBarangMasuk());
        
        JButton btnBatal = new JButton("BATAL");
        btnBatal.addActionListener(e -> dispose());
        
        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);
        
        add(panelForm, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    private void loadProduk() {
        List<Produk> produkList = Produk.getAllProduk();
        for (Produk p : produkList) {
            cmbProduk.addItem(p);
        }
    }
    
    private void simpanBarangMasuk() {
        try {
            Produk produk = (Produk) cmbProduk.getSelectedItem();
            double jumlah = Double.parseDouble(txtJumlah.getText());
            double hargaBeli = Double.parseDouble(txtHargaBeli.getText());
            
            // Update stok
            double stokBaru = produk.getStokDasar() + jumlah;
            if (Produk.updateStok(produk.getId(), stokBaru)) {
                JOptionPane.showMessageDialog(this, "Barang masuk berhasil dicatat!");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data tidak valid!");
        }
    }
}

/**
 * Form Stok Opname
 */
class FormStokOpname extends JDialog {
    private User currentUser;
    private JComboBox<Produk> cmbProduk;
    private JComboBox<String> cmbTipe;
    private JTextField txtJumlah;
    private JTextArea txtAlasan;
    
    public FormStokOpname(JFrame parent, User user) {
        super(parent, "Stok Opname", true);
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 350);
        setLayout(new BorderLayout());
        
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panelForm.add(new JLabel("Produk:"));
        cmbProduk = new JComboBox<>();
        loadProduk();
        panelForm.add(cmbProduk);
        
        panelForm.add(new JLabel("Tipe:"));
        cmbTipe = new JComboBox<>(new String[]{"Tambah", "Kurang"});
        panelForm.add(cmbTipe);
        
        panelForm.add(new JLabel("Jumlah:"));
        txtJumlah = new JTextField();
        panelForm.add(txtJumlah);
        
        panelForm.add(new JLabel("Alasan:"));
        txtAlasan = new JTextArea(3, 20);
        JScrollPane scrollAlasan = new JScrollPane(txtAlasan);
        panelForm.add(scrollAlasan);
        
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnSimpan = new JButton("SIMPAN");
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpanOpname());
        
        JButton btnBatal = new JButton("BATAL");
        btnBatal.addActionListener(e -> dispose());
        
        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);
        
        add(panelForm, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    private void loadProduk() {
        List<Produk> produkList = Produk.getAllProduk();
        for (Produk p : produkList) {
            cmbProduk.addItem(p);
        }
    }
    
    private void simpanOpname() {
        try {
            Produk produk = (Produk) cmbProduk.getSelectedItem();
            String tipe = (String) cmbTipe.getSelectedItem();
            double jumlah = Double.parseDouble(txtJumlah.getText());
            String alasan = txtAlasan.getText();
            
            double stokBaru;
            if (tipe.equals("Tambah")) {
                stokBaru = produk.getStokDasar() + jumlah;
            } else {
                stokBaru = produk.getStokDasar() - jumlah;
            }
            
            if (stokBaru < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!");
                return;
            }
            
            if (Produk.updateStok(produk.getId(), stokBaru)) {
                JOptionPane.showMessageDialog(this, "Stok opname berhasil!");
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data tidak valid!");
        }
    }
}

/**
 * Form Lihat Stok
 */
class FormLihatStok extends JDialog {
    private JTable tableStok;
    private DefaultTableModel tableModel;
    
    public FormLihatStok(JFrame parent) {
        super(parent, "Lihat Stok Real-Time", true);
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(800, 500);
        setLayout(new BorderLayout());
        
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"ID", "Nama Produk", "Kategori", "Stok", "Satuan", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        tableStok = new JTable(tableModel);
        tableStok.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tableStok);
        
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadData());
        panelButton.add(btnRefresh);
        
        panelMain.add(scrollPane, BorderLayout.CENTER);
        panelMain.add(panelButton, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Produk> produkList = Produk.getAllProduk();
        
        for (Produk p : produkList) {
            String status = "Aman";
            if (p.getStokDasar() < 10) {
                status = "MENIPIS!";
            } else if (p.getStokDasar() < 50) {
                status = "Perlu Restock";
            }
            
            Object[] row = {
                p.getId(),
                p.getNamaProduk(),
                p.getNamaKategori(),
                String.format("%.2f", p.getStokDasar()),
                p.getSatuanDasar(),
                status
            };
            tableModel.addRow(row);
        }
    }
}

/**
 * Form Riwayat Transaksi
 */
class FormRiwayatTransaksi extends JDialog {
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;
    private User currentUser;
    
    public FormRiwayatTransaksi(JFrame parent) {
        this(parent, null);
    }
    
    public FormRiwayatTransaksi(JFrame parent, User user) {
        super(parent, "Riwayat Transaksi", true);
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(900, 500);
        setLayout(new BorderLayout());
        
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"Kode", "Tanggal", "Kasir", "Total", "Bayar", "Kembalian"};
        tableModel = new DefaultTableModel(columns, 0);
        tableTransaksi = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        panelMain.add(scrollPane, BorderLayout.CENTER);
        
        add(panelMain);
    }
}

/**
 * Form Laporan
 */
class FormLaporanPenjualan extends JDialog {
    public FormLaporanPenjualan(JFrame parent) {
        super(parent, "Laporan Penjualan", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        JOptionPane.showMessageDialog(this, "Laporan Penjualan");
    }
}

class FormLaporanLaba extends JDialog {
    public FormLaporanLaba(JFrame parent) {
        super(parent, "Laporan Laba", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        JOptionPane.showMessageDialog(this, "Laporan Laba");
    }
}

class FormLaporanStok extends JDialog {
    public FormLaporanStok(JFrame parent) {
        super(parent, "Laporan Stok", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        JOptionPane.showMessageDialog(this, "Laporan Stok Menipis");
    }
}

class FormLaporanShift extends JDialog {
    public FormLaporanShift(JFrame parent, User user) {
        super(parent, "Laporan Shift Saya", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        JOptionPane.showMessageDialog(this, "Laporan Shift: " + user.getNamaLengkap());
    }
}

/**
 * Form Manajemen User
 */
//class FormManajemenUser extends JDialog {
//    public FormManajemenUser(JFrame parent) {
//        super(parent, "Manajemen User", true);
//        setSize(700, 500);
//        setLocationRelativeTo(parent);
//        JOptionPane.showMessageDialog(this, "Manajemen User");
//    }
//}

/**
 * Form Info Toko
 */
class FormInfoToko extends JDialog {
    public FormInfoToko(JFrame parent) {
        super(parent, "Info Toko", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        JOptionPane.showMessageDialog(this, "Pengaturan Info Toko");
    }
}

/**
 * Form Ganti Password
 */
class FormGantiPassword extends JDialog {
    private User currentUser;
    private JPasswordField txtPasswordLama, txtPasswordBaru, txtKonfirmasi;
    
    public FormGantiPassword(JFrame parent, User user) {
        super(parent, "Ganti Password", true);
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(400, 300);
        setLayout(new BorderLayout());
        
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panelForm.add(new JLabel("Password Lama:"));
        txtPasswordLama = new JPasswordField();
        panelForm.add(txtPasswordLama);
        
        panelForm.add(new JLabel("Password Baru:"));
        txtPasswordBaru = new JPasswordField();
        panelForm.add(txtPasswordBaru);
        
        panelForm.add(new JLabel("Konfirmasi:"));
        txtKonfirmasi = new JPasswordField();
        panelForm.add(txtKonfirmasi);
        
        JPanel panelButton = new JPanel(new FlowLayout());
        JButton btnSimpan = new JButton("SIMPAN");
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> gantiPassword());
        
        JButton btnBatal = new JButton("BATAL");
        btnBatal.addActionListener(e -> dispose());
        
        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);
        
        add(panelForm, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    private void gantiPassword() {
        String passwordLama = new String(txtPasswordLama.getPassword());
        String passwordBaru = new String(txtPasswordBaru.getPassword());
        String konfirmasi = new String(txtKonfirmasi.getPassword());
        
        if (!passwordBaru.equals(konfirmasi)) {
            JOptionPane.showMessageDialog(this, "Konfirmasi password tidak cocok!");
            return;
        }
        
        if (User.updatePassword(currentUser.getId(), passwordBaru)) {
            JOptionPane.showMessageDialog(this, "Password berhasil diubah!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengubah password!");
        }
    }
}