package view;

import model.Kategori;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Form Master Kategori dengan CRUD Lengkap
 */
public class FormKategori extends JFrame {
    
    private JFrame parent;
    
    // Form Components
    private JTextField txtId, txtNamaKategori, txtDeskripsi;
    private JButton btnSimpan, btnUpdate, btnBatal, btnHapus;
    
    // Table Components
    private JTable tableKategori;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnRefresh;
    
    private int selectedKategoriId = -1;
    
    public FormKategori(JFrame parent) {
        this.parent = parent;
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Master Kategori Produk");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // ========== HEADER PANEL ==========
        JPanel headerPanel = createHeaderPanel();
        
        // ========== FORM PANEL ==========
        JPanel formPanel = createFormPanel();
        
        // ========== TABLE PANEL ==========
        JPanel tablePanel = createTablePanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 188, 156));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("📁 MASTER KATEGORI PRODUK");
        lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("Kelola Kategori untuk Klasifikasi Produk");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 255, 240));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(26, 188, 156));
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(lblSubtitle);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(26, 188, 156), 2),
            "Form Input/Edit Kategori",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(26, 188, 156)
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 200));
        
        // Form Fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: ID (Hidden)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel lblId = new JLabel("ID:");
        lblId.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblId, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.8;
        txtId = new JTextField();
        txtId.setEnabled(false);
        txtId.setPreferredSize(new Dimension(200, 35));
        txtId.setBackground(new Color(240, 240, 240));
        fieldsPanel.add(txtId, gbc);
        
        // Row 2: Nama Kategori
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel lblNama = new JLabel("Nama Kategori:*");
        lblNama.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblNama, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.8;
        txtNamaKategori = new JTextField();
        txtNamaKategori.setPreferredSize(new Dimension(300, 35));
        txtNamaKategori.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldsPanel.add(txtNamaKategori, gbc);
        
        // Row 3: Deskripsi
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align top
        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        lblDeskripsi.setFont(new Font("Arial", Font.BOLD, 13));
        fieldsPanel.add(lblDeskripsi, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.CENTER; // Reset anchor
        txtDeskripsi = new JTextField();
        txtDeskripsi.setPreferredSize(new Dimension(600, 35)); // Lebih lebar
        txtDeskripsi.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldsPanel.add(txtDeskripsi, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSimpan = new JButton("✓ SIMPAN BARU");
        btnSimpan.setPreferredSize(new Dimension(140, 40));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 13));
        btnSimpan.addActionListener(e -> simpanKategori());
        
        btnUpdate = new JButton("✎ UPDATE");
        btnUpdate.setPreferredSize(new Dimension(120, 40));
        btnUpdate.setBackground(new Color(52, 152, 219));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 13));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> updateKategori());
        
        btnBatal = new JButton("✕ BATAL");
        btnBatal.setPreferredSize(new Dimension(100, 40));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.addActionListener(e -> clearForm());
        
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBatal);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Daftar Kategori",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 280));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("Cari Kategori:");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 12));
        
        txtSearch = new JTextField(30);
        txtSearch.setPreferredSize(new Dimension(300, 30));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // LIVE SEARCH: Key listener untuk pencarian otomatis
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchKategori(); // Otomatis search saat mengetik
            }
        });
        
        // Info label untuk live search
        JLabel lblSearchInfo = new JLabel("(Ketik untuk mencari otomatis)");
        lblSearchInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        lblSearchInfo.setForeground(Color.GRAY);
        
        btnRefresh = new JButton("🔄 REFRESH");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); // Clear search box
            loadData();
        });
        
        btnHapus = new JButton("🗑 HAPUS");
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusKategori());
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(lblSearchInfo);
        searchPanel.add(btnRefresh);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(btnHapus);
        
        // Table
        String[] columns = {"ID", "Nama Kategori", "Deskripsi", "Jumlah Produk"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableKategori = new JTable(tableModel);
        tableKategori.setRowHeight(32);
        tableKategori.setFont(new Font("Arial", Font.PLAIN, 12));
        tableKategori.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableKategori.getTableHeader().setBackground(new Color(236, 240, 241));
        tableKategori.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableKategori.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableKategori.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableKategori.getColumnModel().getColumn(2).setPreferredWidth(350);
        tableKategori.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        // Double click to edit
        tableKategori.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableKategori.getSelectedRow();
                    if (row != -1) {
                        loadKategoriToForm(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableKategori);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Kategori> kategoriList = Kategori.getAllKategori();
        
        for (Kategori k : kategoriList) {
            // Hitung jumlah produk per kategori
            int jumlahProduk = countProdukByKategori(k.getId());
            
            Object[] row = {
                k.getId(),
                k.getNamaKategori(),
                k.getDeskripsi() != null ? k.getDeskripsi() : "-",
                jumlahProduk + " produk"
            };
            tableModel.addRow(row);
        }
    }
    
    private int countProdukByKategori(int kategoriId) {
        // Query untuk menghitung jumlah produk
        try {
            java.sql.Connection conn = config.DatabaseConfig.getConnection();
            String sql = "SELECT COUNT(*) as total FROM produk WHERE kategori_id = ? AND is_deleted = FALSE";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, kategoriId);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("Error count produk: " + e.getMessage());
        }
        return 0;
    }
    
    private void searchKategori() {
        String keyword = txtSearch.getText().trim().toLowerCase(); // Live search: lowercase
        
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Kategori> kategoriList = Kategori.getAllKategori();
        
        for (Kategori k : kategoriList) {
            // Live search: cek nama kategori dan deskripsi
            boolean matchNama = k.getNamaKategori().toLowerCase().contains(keyword);
            boolean matchDeskripsi = k.getDeskripsi() != null && 
                                     k.getDeskripsi().toLowerCase().contains(keyword);
            
            if (matchNama || matchDeskripsi) {
                int jumlahProduk = countProdukByKategori(k.getId());
                
                Object[] row = {
                    k.getId(),
                    k.getNamaKategori(),
                    k.getDeskripsi() != null ? k.getDeskripsi() : "-",
                    jumlahProduk + " produk"
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void simpanKategori() {
        // Validasi
        if (txtNamaKategori.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nama kategori harus diisi!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }
        
        String namaKategori = txtNamaKategori.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        
        // Cek duplikasi nama kategori
        if (isDuplicateKategori(namaKategori, -1)) {
            JOptionPane.showMessageDialog(this,
                "Nama kategori sudah ada!\nGunakan nama yang berbeda.",
                "Duplikasi Data",
                JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }
        
        if (Kategori.tambahKategori(namaKategori, deskripsi.isEmpty() ? null : deskripsi)) {
            JOptionPane.showMessageDialog(this,
                "✓ Kategori berhasil ditambahkan!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal menambahkan kategori!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateKategori() {
        if (selectedKategoriId == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih kategori yang akan diupdate!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validasi
        if (txtNamaKategori.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nama kategori harus diisi!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }
        
        String namaKategori = txtNamaKategori.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        
        // Cek duplikasi nama kategori (kecuali kategori yang sedang diedit)
        if (isDuplicateKategori(namaKategori, selectedKategoriId)) {
            JOptionPane.showMessageDialog(this,
                "Nama kategori sudah ada!\nGunakan nama yang berbeda.",
                "Duplikasi Data",
                JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }
        
        if (updateKategoriData(selectedKategoriId, namaKategori, deskripsi.isEmpty() ? null : deskripsi)) {
            JOptionPane.showMessageDialog(this,
                "✓ Kategori berhasil diupdate!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate kategori!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean updateKategoriData(int id, String nama, String deskripsi) {
        try {
            java.sql.Connection conn = config.DatabaseConfig.getConnection();
            String sql = "UPDATE kategori SET nama_kategori = ?, deskripsi = ? WHERE id = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setString(2, deskripsi);
            ps.setInt(3, id);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error update kategori: " + e.getMessage());
            return false;
        }
    }
    
    private void hapusKategori() {
        int selectedRow = tableKategori.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih kategori yang akan dihapus!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int kategoriId = (int) tableModel.getValueAt(selectedRow, 0);
        String namaKategori = (String) tableModel.getValueAt(selectedRow, 1);
        int jumlahProduk = countProdukByKategori(kategoriId);
        
        // Cek apakah kategori masih digunakan
        if (jumlahProduk > 0) {
            JOptionPane.showMessageDialog(this,
                "Kategori tidak dapat dihapus!\n\n" +
                "Kategori '" + namaKategori + "' masih digunakan oleh " + jumlahProduk + " produk.\n" +
                "Hapus atau pindahkan produk tersebut terlebih dahulu.",
                "Tidak Dapat Dihapus",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus kategori:\n\n" +
            "'" + namaKategori + "' ?\n\n" +
            "Kategori ini tidak digunakan oleh produk manapun.",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (deleteKategori(kategoriId)) {
                JOptionPane.showMessageDialog(this,
                    "✓ Kategori berhasil dihapus!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus kategori!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean deleteKategori(int kategoriId) {
        try {
            java.sql.Connection conn = config.DatabaseConfig.getConnection();
            String sql = "DELETE FROM kategori WHERE id = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, kategoriId);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error delete kategori: " + e.getMessage());
            return false;
        }
    }
    
    private boolean isDuplicateKategori(String namaKategori, int excludeId) {
        try {
            java.sql.Connection conn = config.DatabaseConfig.getConnection();
            String sql = "SELECT COUNT(*) as total FROM kategori WHERE nama_kategori = ? AND id != ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, namaKategori);
            ps.setInt(2, excludeId);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            System.err.println("Error check duplicate: " + e.getMessage());
        }
        return false;
    }
    
    private void loadKategoriToForm(int row) {
        int kategoriId = (int) tableModel.getValueAt(row, 0);
        String namaKategori = (String) tableModel.getValueAt(row, 1);
        String deskripsi = (String) tableModel.getValueAt(row, 2);
        
        selectedKategoriId = kategoriId;
        txtId.setText(String.valueOf(kategoriId));
        txtNamaKategori.setText(namaKategori);
        txtDeskripsi.setText(deskripsi.equals("-") ? "" : deskripsi);
        
        // Enable update button
        btnSimpan.setEnabled(false);
        btnUpdate.setEnabled(true);
        
        txtNamaKategori.requestFocus();
    }
    
    private void clearForm() {
        selectedKategoriId = -1;
        txtId.setText("");
        txtNamaKategori.setText("");
        txtDeskripsi.setText("");
        
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        
        tableKategori.clearSelection();
        txtNamaKategori.requestFocus();
    }
}