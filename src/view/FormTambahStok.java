package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

/**
 * Form Tambah Stok Produk dengan History
 */
public class FormTambahStok extends JFrame {
    
    private User currentUser;
    
    // Form Components
    private JComboBox<Produk> cmbProduk;
    private JComboBox<SatuanJual> cmbSatuan;
    private JTextField txtJumlah, txtHargaBeli, txtKeterangan;
    private JLabel lblStokSekarang, lblSatuanDasar, lblTotalHarga, lblKonversiInfo;
    private JButton btnSimpan, btnBatal, btnRefresh;
    
    // Table Components
    private JTable tableHistory;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFilterProduk;
    
    public FormTambahStok(JFrame parent, User user) {
        this.currentUser = user;
        initComponents();
        loadProduk();
        loadHistory();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Tambah Stok Produk");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // ========== FORM PANEL (TOP) ==========
        JPanel formPanel = createFormPanel();
        
        // ========== HISTORY PANEL (CENTER) ==========
        JPanel historyPanel = createHistoryPanel();
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(historyPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Form Tambah Stok",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(46, 204, 113)
        ));
        panel.setBackground(Color.WHITE);
        
        // Form Fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: Produk
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel lblProduk = new JLabel("Pilih Produk:*");
        lblProduk.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblProduk, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8; gbc.gridwidth = 3;
        cmbProduk = new JComboBox<>();
        cmbProduk.setPreferredSize(new Dimension(400, 35));
        cmbProduk.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbProduk.addActionListener(e -> {
            updateStokInfo();
            loadSatuanJual();
        });
        fieldsPanel.add(cmbProduk, gbc);
        
        // Row 2: Satuan Jual
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel lblSatuan = new JLabel("Satuan:*");
        lblSatuan.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblSatuan, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.3;
        cmbSatuan = new JComboBox<>();
        cmbSatuan.setPreferredSize(new Dimension(150, 35));
        cmbSatuan.setFont(new Font("Arial", Font.PLAIN, 13));
        cmbSatuan.addActionListener(e -> updateKonversiInfo());
        fieldsPanel.add(cmbSatuan, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.5; gbc.gridwidth = 2;
        lblKonversiInfo = new JLabel("");
        lblKonversiInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblKonversiInfo.setForeground(new Color(52, 152, 219));
        fieldsPanel.add(lblKonversiInfo, gbc);
        
        // Row 3: Info Stok Sekarang
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        JLabel lblInfoStok = new JLabel("Stok Sekarang:");
        lblInfoStok.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblInfoStok, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.3;
        lblStokSekarang = new JLabel("-");
        lblStokSekarang.setFont(new Font("Arial", Font.BOLD, 16));
        lblStokSekarang.setForeground(new Color(52, 152, 219));
        fieldsPanel.add(lblStokSekarang, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.2;
        lblSatuanDasar = new JLabel("");
        lblSatuanDasar.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSatuanDasar.setForeground(Color.GRAY);
        fieldsPanel.add(lblSatuanDasar, gbc);
        
        // Row 4: Jumlah Tambah & Harga Beli
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        JLabel lblJumlah = new JLabel("Jumlah Tambah:*");
        lblJumlah.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblJumlah, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.3;
        txtJumlah = new JTextField();
        txtJumlah.setPreferredSize(new Dimension(150, 35));
        txtJumlah.setFont(new Font("Arial", Font.PLAIN, 14));
        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
            }
        });
        fieldsPanel.add(txtJumlah, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0.2;
        JLabel lblHargaBeli = new JLabel("Harga Beli/Unit:*");
        lblHargaBeli.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblHargaBeli, gbc);
        
        gbc.gridx = 3; gbc.gridy = 3; gbc.weightx = 0.3;
        txtHargaBeli = new JTextField();
        txtHargaBeli.setPreferredSize(new Dimension(150, 35));
        txtHargaBeli.setFont(new Font("Arial", Font.PLAIN, 14));
        txtHargaBeli.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
            }
        });
        fieldsPanel.add(txtHargaBeli, gbc);
        
        // Row 5: Total Harga
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.2;
        JLabel lblTotalLabel = new JLabel("Total Harga:");
        lblTotalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblTotalLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.3; gbc.gridwidth = 2;
        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalHarga.setForeground(new Color(231, 76, 60));
        fieldsPanel.add(lblTotalHarga, gbc);
        
        // Row 6: Keterangan
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.2;
        JLabel lblKeterangan = new JLabel("Keterangan:");
        lblKeterangan.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblKeterangan, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.8; gbc.gridwidth = 3;
        txtKeterangan = new JTextField();
        txtKeterangan.setPreferredSize(new Dimension(400, 35));
        txtKeterangan.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldsPanel.add(txtKeterangan, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSimpan = new JButton("✓ SIMPAN & TAMBAH STOK");
        btnSimpan.setPreferredSize(new Dimension(200, 40));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 13));
        btnSimpan.addActionListener(e -> simpanTambahStok());
        
        btnBatal = new JButton("✕ BATAL");
        btnBatal.setPreferredSize(new Dimension(120, 40));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.addActionListener(e -> clearForm());
        
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnBatal);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "History Penambahan Stok",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        panel.setBackground(Color.WHITE);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel lblFilter = new JLabel("Filter Produk:");
        lblFilter.setFont(new Font("Arial", Font.BOLD, 12));
        
        cmbFilterProduk = new JComboBox<>();
        cmbFilterProduk.addItem("-- Semua Produk --");
        cmbFilterProduk.setPreferredSize(new Dimension(250, 30));
        cmbFilterProduk.addActionListener(e -> filterHistory());
        
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadHistory());
        
        filterPanel.add(lblFilter);
        filterPanel.add(cmbFilterProduk);
        filterPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "Tanggal", "Produk", "Jumlah", "Satuan", "Harga Beli", "Total", "User", "Keterangan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableHistory = new JTable(tableModel);
        tableHistory.setRowHeight(30);
        tableHistory.setFont(new Font("Arial", Font.PLAIN, 12));
        tableHistory.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableHistory.getTableHeader().setBackground(new Color(236, 240, 241));
        
        tableHistory.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableHistory.getColumnModel().getColumn(1).setPreferredWidth(130);
        tableHistory.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableHistory.getColumnModel().getColumn(3).setPreferredWidth(80);
        tableHistory.getColumnModel().getColumn(4).setPreferredWidth(70);
        tableHistory.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableHistory.getColumnModel().getColumn(6).setPreferredWidth(100);
        tableHistory.getColumnModel().getColumn(7).setPreferredWidth(120);
        tableHistory.getColumnModel().getColumn(8).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tableHistory);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadProduk() {
        cmbProduk.removeAllItems();
        cmbFilterProduk.removeAllItems();
        cmbFilterProduk.addItem("-- Semua Produk --");
        
        List<Produk> produkList = Produk.getAllProduk();
        for (Produk p : produkList) {
            cmbProduk.addItem(p);
            cmbFilterProduk.addItem(p.getNamaProduk());
        }
        
        if (cmbProduk.getItemCount() > 0) {
            cmbProduk.setSelectedIndex(0);
            updateStokInfo();
        }
    }
    
    private void updateStokInfo() {
        Produk produk = (Produk) cmbProduk.getSelectedItem();
        if (produk != null) {
            lblStokSekarang.setText(String.format("%.2f", produk.getStokDasar()));
            lblSatuanDasar.setText(produk.getSatuanDasar());
        } else {
            lblStokSekarang.setText("-");
            lblSatuanDasar.setText("");
        }
    }
    
    private void loadSatuanJual() {
        cmbSatuan.removeAllItems();
        
        Produk produk = (Produk) cmbProduk.getSelectedItem();
        if (produk != null) {
            List<SatuanJual> satuanList = SatuanJual.getSatuanByProdukId(produk.getId());
            for (SatuanJual s : satuanList) {
                cmbSatuan.addItem(s);
            }
            
            if (cmbSatuan.getItemCount() > 0) {
                cmbSatuan.setSelectedIndex(0);
                updateKonversiInfo();
            }
        }
    }
    
    private void updateKonversiInfo() {
        SatuanJual satuan = (SatuanJual) cmbSatuan.getSelectedItem();
        Produk produk = (Produk) cmbProduk.getSelectedItem();
        
        if (satuan != null && produk != null) {
            if (satuan.getKonversiKeDasar() == 1.0) {
                lblKonversiInfo.setText("(Satuan Dasar)");
            } else {
                lblKonversiInfo.setText(String.format("(1 %s = %.2f %s)", 
                    satuan.getNamaSatuan(), 
                    satuan.getKonversiKeDasar(), 
                    produk.getSatuanDasar()));
            }
        } else {
            lblKonversiInfo.setText("");
        }
    }
    
    private void hitungTotalHarga() {
        try {
            String jumlahStr = txtJumlah.getText().replaceAll("[^0-9.]", "");
            String hargaStr = txtHargaBeli.getText().replaceAll("[^0-9.]", "");
            
            if (!jumlahStr.isEmpty() && !hargaStr.isEmpty()) {
                double jumlah = Double.parseDouble(jumlahStr);
                double harga = Double.parseDouble(hargaStr);
                double total = jumlah * harga;
                
                lblTotalHarga.setText(String.format("Rp %,.0f", total));
            } else {
                lblTotalHarga.setText("Rp 0");
            }
        } catch (Exception e) {
            lblTotalHarga.setText("Rp 0");
        }
    }
    
    private void simpanTambahStok() {
        // Validasi
        if (cmbProduk.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Pilih produk terlebih dahulu!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cmbSatuan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Pilih satuan terlebih dahulu!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtJumlah.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Masukkan jumlah tambah stok!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus();
            return;
        }
        
        if (txtHargaBeli.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Masukkan harga beli per unit!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            txtHargaBeli.requestFocus();
            return;
        }
        
        try {
            Produk produk = (Produk) cmbProduk.getSelectedItem();
            SatuanJual satuan = (SatuanJual) cmbSatuan.getSelectedItem();
            double jumlah = Double.parseDouble(txtJumlah.getText().trim());
            double hargaBeli = Double.parseDouble(txtHargaBeli.getText().trim());
            String keterangan = txtKeterangan.getText().trim();
            
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Jumlah harus lebih dari 0!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (hargaBeli < 0) {
                JOptionPane.showMessageDialog(this,
                    "Harga beli tidak boleh negatif!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Konversi ke satuan dasar
            double jumlahDasar = jumlah * satuan.getKonversiKeDasar();
            
            // Hitung stok baru
            double stokBaru = produk.getStokDasar() + jumlahDasar;
            double totalHarga = jumlah * hargaBeli;
            
            // Konfirmasi
            String konfirmasiMsg = "Konfirmasi Penambahan Stok:\n\n" +
                "Produk: " + produk.getNamaProduk() + "\n" +
                "Satuan Input: " + satuan.getNamaSatuan() + "\n" +
                "Jumlah Input: " + String.format("%.2f", jumlah) + " " + satuan.getNamaSatuan() + "\n";
            
            if (satuan.getKonversiKeDasar() != 1.0) {
                konfirmasiMsg += "Konversi: " + String.format("%.2f", jumlahDasar) + " " + produk.getSatuanDasar() + "\n";
            }
            
            konfirmasiMsg += "\nStok Sekarang: " + String.format("%.2f", produk.getStokDasar()) + " " + produk.getSatuanDasar() + "\n" +
                "Stok Baru: " + String.format("%.2f", stokBaru) + " " + produk.getSatuanDasar() + "\n\n" +
                "Harga Beli: Rp " + String.format("%,.0f", hargaBeli) + " per " + satuan.getNamaSatuan() + "\n" +
                "Total Harga: Rp " + String.format("%,.0f", totalHarga) + "\n\n" +
                "Lanjutkan?";
            
            int confirm = JOptionPane.showConfirmDialog(this,
                konfirmasiMsg,
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Buat keterangan lengkap dengan info satuan
                String keteranganLengkap = keterangan;
                if (!keterangan.isEmpty()) {
                    keteranganLengkap += " | ";
                }
                keteranganLengkap += "Input: " + String.format("%.2f", jumlah) + " " + satuan.getNamaSatuan();
                if (satuan.getKonversiKeDasar() != 1.0) {
                    keteranganLengkap += " = " + String.format("%.2f", jumlahDasar) + " " + produk.getSatuanDasar();
                }
                
                // Simpan ke database (gunakan jumlah dalam satuan dasar)
                if (BarangMasuk.tambahBarangMasuk(
                        produk.getId(),
                        jumlahDasar,  // Simpan dalam satuan dasar
                        hargaBeli / satuan.getKonversiKeDasar(),  // Harga per satuan dasar
                        totalHarga,
                        currentUser.getId(),
                        keteranganLengkap)) {
                    
                    // Update stok produk
                    if (Produk.updateStok(produk.getId(), stokBaru)) {
                        JOptionPane.showMessageDialog(this,
                            "✓ Stok berhasil ditambahkan!\n\n" +
                            "Input: " + String.format("%.2f", jumlah) + " " + satuan.getNamaSatuan() + "\n" +
                            (satuan.getKonversiKeDasar() != 1.0 ? 
                                "Konversi: " + String.format("%.2f", jumlahDasar) + " " + produk.getSatuanDasar() + "\n" : "") +
                            "Stok Baru: " + String.format("%.2f", stokBaru) + " " + produk.getSatuanDasar(),
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh data
                        loadProduk();
                        loadHistory();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Gagal mengupdate stok produk!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Gagal menyimpan data barang masuk!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Format angka tidak valid!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadHistory() {
        tableModel.setRowCount(0);
        List<BarangMasuk> historyList = BarangMasuk.getAllBarangMasuk();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (BarangMasuk bm : historyList) {
            Object[] row = {
                bm.getId(),
                sdf.format(bm.getTanggalMasuk()),
                bm.getNamaProduk(),
                String.format("%.2f", bm.getJumlah()),
                bm.getSatuanDasar(),
                String.format("Rp %,.0f", bm.getHargaBeliSatuan()),
                String.format("Rp %,.0f", bm.getTotalHarga()),
                bm.getNamaUser(),
                bm.getKeterangan() != null ? bm.getKeterangan() : "-"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterHistory() {
        String selected = (String) cmbFilterProduk.getSelectedItem();
        
        if (selected == null || selected.equals("-- Semua Produk --")) {
            loadHistory();
            return;
        }
        
        tableModel.setRowCount(0);
        List<BarangMasuk> historyList = BarangMasuk.getAllBarangMasuk();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (BarangMasuk bm : historyList) {
            if (bm.getNamaProduk().equals(selected)) {
                Object[] row = {
                    bm.getId(),
                    sdf.format(bm.getTanggalMasuk()),
                    bm.getNamaProduk(),
                    String.format("%.2f", bm.getJumlah()),
                    bm.getSatuanDasar(),
                    String.format("Rp %,.0f", bm.getHargaBeliSatuan()),
                    String.format("Rp %,.0f", bm.getTotalHarga()),
                    bm.getNamaUser(),
                    bm.getKeterangan() != null ? bm.getKeterangan() : "-"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void clearForm() {
        if (cmbProduk.getItemCount() > 0) {
            cmbProduk.setSelectedIndex(0);
        }
        if (cmbSatuan.getItemCount() > 0) {
            cmbSatuan.setSelectedIndex(0);
        }
        txtJumlah.setText("");
        txtHargaBeli.setText("");
        txtKeterangan.setText("");
        lblTotalHarga.setText("Rp 0");
        updateStokInfo();
        updateKonversiInfo();
        txtJumlah.requestFocus();
    }
}