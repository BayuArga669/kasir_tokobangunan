package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Form Master Produk dengan:
 * ✅ LIVE SEARCH (otomatis saat ketik)
 * ✅ INPUT ANGKA ONLY (Stok, Harga Beli, Konversi, Harga Jual)
 * ✅ Harga beli disimpan & ditampilkan
 */
public class FormProduk extends JFrame {
    
    private JFrame parent;
    private User currentUser;
    
    // Form Components
    private JTextField txtId, txtNamaProduk, txtStok, txtHargaBeli;
    private JComboBox<Kategori> cmbKategori;
    private JComboBox<String> cmbSatuanDasar;
    private JButton btnSimpan, btnUpdate, btnBatal, btnHapus;
    
    // Table Components
    private JTable tableProduk;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnRefresh, btnKelolaHarga;
    
    private int selectedProdukId = -1;

    // ======== DEBOUNCE TIMER (untuk live search) ========
    private Timer searchTimer;
    private static final int SEARCH_DELAY = 300;

    public FormProduk(JFrame parent, User user) {
        this.parent = parent;
        this.currentUser = user;
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Master Produk");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ========== FORM PANEL (TOP) ==========
        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BorderLayout(10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Form Input/Edit Produk",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        panelForm.setBackground(Color.WHITE);
        
        // Form Fields
        JPanel panelFields = new JPanel(new GridBagLayout());
        panelFields.setBackground(Color.WHITE);
        panelFields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: ID (Hidden) & Nama Produk
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        panelFields.add(new JLabel("ID:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.2;
        txtId = new JTextField();
        txtId.setEnabled(false);
        txtId.setPreferredSize(new Dimension(80, 30));
        panelFields.add(txtId, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        JLabel lblNama = new JLabel("Nama Produk:*");
        lblNama.setFont(new Font("Arial", Font.BOLD, 12));
        panelFields.add(lblNama, gbc);
        
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.6;
        txtNamaProduk = new JTextField();
        txtNamaProduk.setPreferredSize(new Dimension(300, 30));
        panelFields.add(txtNamaProduk, gbc);
        
        // Row 2: Kategori & Satuan Dasar
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        panelFields.add(new JLabel("Kategori:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.2; gbc.gridwidth = 3;
        cmbKategori = new JComboBox<>();
        cmbKategori.setPreferredSize(new Dimension(200, 30));
        loadKategori();
        panelFields.add(cmbKategori, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 4; gbc.gridy = 1; gbc.weightx = 0.1;
        panelFields.add(new JLabel("Satuan Dasar:*"), gbc);
        
        gbc.gridx = 5; gbc.gridy = 1; gbc.weightx = 0.2;
        cmbSatuanDasar = new JComboBox<>(new String[]{"PCS", "KG", "METER", "LITER", "UNIT", "BOX", "LEMBAR"});
        cmbSatuanDasar.setPreferredSize(new Dimension(120, 30));
        panelFields.add(cmbSatuanDasar, gbc);
        
        // Row 3: Stok Awal & Harga Beli
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        panelFields.add(new JLabel("Stok Awal:*"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.2;
        txtStok = new JTextField("0");
        txtStok.setPreferredSize(new Dimension(150, 30));
        // ✅ Numeric only (desimal)
        NumericDocumentFilter.applyTo(txtStok, true);
        panelFields.add(txtStok, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.1;
        JLabel lblHargaBeli = new JLabel("Harga Beli (Rp):*");
        lblHargaBeli.setFont(new Font("Arial", Font.BOLD, 12));
        panelFields.add(lblHargaBeli, gbc);
        
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 0.3;
        txtHargaBeli = new JTextField("0");
        txtHargaBeli.setPreferredSize(new Dimension(150, 30));
        // ✅ Numeric only (desimal)
        NumericDocumentFilter.applyTo(txtHargaBeli, true);
        panelFields.add(txtHargaBeli, gbc);
        
        // Info label untuk harga beli
        gbc.gridx = 4; gbc.gridy = 2; gbc.weightx = 0.3; gbc.gridwidth = 2;
        JLabel lblInfoHargaBeli = new JLabel("(Per satuan dasar)");
        lblInfoHargaBeli.setFont(new Font("Arial", Font.ITALIC, 11));
        lblInfoHargaBeli.setForeground(Color.GRAY);
        panelFields.add(lblInfoHargaBeli, gbc);
        
        // Form Buttons
        JPanel panelFormButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFormButtons.setBackground(Color.WHITE);
        
        btnSimpan = new JButton("SIMPAN BARU");
        btnSimpan.setPreferredSize(new Dimension(130, 35));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 12));
        btnSimpan.addActionListener(e -> simpanProduk());
        
        btnUpdate = new JButton("UPDATE");
        btnUpdate.setPreferredSize(new Dimension(130, 35));
        btnUpdate.setBackground(new Color(52, 152, 219));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 12));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> updateProduk());
        
        btnBatal = new JButton("BATAL");
        btnBatal.setPreferredSize(new Dimension(100, 35));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 12));
        btnBatal.addActionListener(e -> clearForm());
        
        btnKelolaHarga = new JButton("KELOLA HARGA");
        btnKelolaHarga.setPreferredSize(new Dimension(140, 35));
        btnKelolaHarga.setBackground(new Color(230, 126, 34));
        btnKelolaHarga.setForeground(Color.WHITE);
        btnKelolaHarga.setFocusPainted(false);
        btnKelolaHarga.setFont(new Font("Arial", Font.BOLD, 12));
        btnKelolaHarga.setEnabled(false);
        btnKelolaHarga.addActionListener(e -> kelolaHarga());
        
        panelFormButtons.add(btnSimpan);
        panelFormButtons.add(btnUpdate);
        panelFormButtons.add(btnKelolaHarga);
        panelFormButtons.add(btnBatal);
        
        panelForm.add(panelFields, BorderLayout.CENTER);
        panelForm.add(panelFormButtons, BorderLayout.SOUTH);
        
        // ========== TABLE PANEL (CENTER) ==========
        JPanel panelTable = new JPanel(new BorderLayout(10, 10));
        
        // Search Panel
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSearch.setBackground(new Color(236, 240, 241));
        
        JLabel lblSearch = new JLabel("Cari Produk:");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 12));
        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(250, 30));
        
        // ✅ LIVE SEARCH
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { triggerSearch(); }
        });

        btnRefresh = new JButton("↺");
        btnRefresh.setToolTipText("Refresh data");
        btnRefresh.setPreferredSize(new Dimension(40, 30));
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });
        
        btnHapus = new JButton("HAPUS");
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusProduk());
        
        panelSearch.add(lblSearch);
        panelSearch.add(txtSearch);
        panelSearch.add(btnRefresh);
        panelSearch.add(Box.createHorizontalStrut(10));
        panelSearch.add(btnHapus);
        
        // Table
        String[] columns = {"ID", "Nama Produk", "Kategori", "Satuan Dasar", "Stok", "Harga Beli", "Jumlah Satuan"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProduk = new JTable(tableModel);
        tableProduk.setRowHeight(28);
        tableProduk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProduk.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableProduk.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableProduk.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableProduk.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableProduk.getColumnModel().getColumn(4).setPreferredWidth(80);
        tableProduk.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableProduk.getColumnModel().getColumn(6).setPreferredWidth(100);

        tableProduk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableProduk.getSelectedRow();
                    if (row != -1) {
                        loadProdukToForm(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableProduk);
        
        panelTable.add(panelSearch, BorderLayout.NORTH);
        panelTable.add(scrollPane, BorderLayout.CENTER);
        
        panelMain.add(panelForm, BorderLayout.NORTH);
        panelMain.add(panelTable, BorderLayout.CENTER);
        
        add(panelMain);
        
        // Timer untuk debounce live search
        searchTimer = new Timer(SEARCH_DELAY, e -> performSearch());
        searchTimer.setRepeats(false);
    }

    private void triggerSearch() {
        searchTimer.restart();
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
        } else {
            tableModel.setRowCount(0);
            List<Produk> produkList = Produk.searchProduk(keyword);
            for (Produk p : produkList) {
                Object[] row = {
                    p.getId(),
                    p.getNamaProduk(),
                    p.getNamaKategori(),
                    p.getSatuanDasar(),
                    String.format("%.2f", p.getStokDasar()),
                    String.format("Rp %,.0f", p.getHargaBeli()),
                    p.getDaftarSatuan().size() + " satuan"
                };
                tableModel.addRow(row);
            }
        }
    }

    private void loadKategori() {
        cmbKategori.removeAllItems();
        List<Kategori> kategoriList = Kategori.getAllKategori();
        for (Kategori k : kategoriList) {
            cmbKategori.addItem(k);
        }
    }
    
    public void loadData() {
        tableModel.setRowCount(0);
        List<Produk> produkList = Produk.getAllProduk();

        for (Produk p : produkList) {
            Object[] row = {
                p.getId(),
                p.getNamaProduk(),
                p.getNamaKategori(),
                p.getSatuanDasar(),
                String.format("%.2f", p.getStokDasar()),
                String.format("Rp %,.0f", p.getHargaBeli()),
                p.getDaftarSatuan().size() + " satuan"
            };
            tableModel.addRow(row);
        }
    }

    private void simpanProduk() {
        if (txtNamaProduk.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama produk harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNamaProduk.requestFocus();
            return;
        }
        
        if (cmbKategori.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double stok = parseNumericField(txtStok.getText());
            double hargaBeli = parseNumericField(txtHargaBeli.getText());
            
            if (stok < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (hargaBeli < 0) {
                JOptionPane.showMessageDialog(this, "Harga beli tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Produk produk = new Produk();
            produk.setNamaProduk(txtNamaProduk.getText().trim());
            produk.setKategoriId(((Kategori) cmbKategori.getSelectedItem()).getId());
            produk.setSatuanDasar((String) cmbSatuanDasar.getSelectedItem());
            produk.setStokDasar(stok);
            produk.setHargaBeli(hargaBeli);
            
            List<SatuanJual> satuanList = new ArrayList<>();
            SatuanJual satuanDefault = new SatuanJual();
            satuanDefault.setNamaSatuan((String) cmbSatuanDasar.getSelectedItem());
            satuanDefault.setKonversiKeDasar(1.0);
            satuanDefault.setHargaJual(hargaBeli * 1.3);
            satuanDefault.setBarcode(null);
            satuanList.add(satuanDefault);
            produk.setDaftarSatuan(satuanList);
            
            if (Produk.tambahProduk(produk)) {
                JOptionPane.showMessageDialog(this,
                    "Produk berhasil ditambahkan!\nHarga Beli: Rp " + String.format("%,.0f", hargaBeli),
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan produk!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProduk() {
        if (selectedProdukId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan diupdate!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtNamaProduk.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama produk harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cmbKategori.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double stok = parseNumericField(txtStok.getText());
            double hargaBeli = parseNumericField(txtHargaBeli.getText());
            
            if (stok < 0) {
                JOptionPane.showMessageDialog(this, "Stok tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (hargaBeli < 0) {
                JOptionPane.showMessageDialog(this, "Harga beli tidak boleh negatif!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (Produk.updateProduk(selectedProdukId, 
                    txtNamaProduk.getText().trim(),
                    ((Kategori) cmbKategori.getSelectedItem()).getId(),
                    (String) cmbSatuanDasar.getSelectedItem(),
                    stok,
                    hargaBeli)) {
                
                JOptionPane.showMessageDialog(this, "Produk dan harga beli berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengupdate produk!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusProduk() {
        int selectedRow = tableProduk.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang akan dihapus!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus produk ini?\nData satuan jual juga akan terhapus.",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int produkId = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (Produk.deleteProduk(produkId)) {
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus produk!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadProdukToForm(int row) {
        int produkId = (int) tableModel.getValueAt(row, 0);
        Produk produk = Produk.getProdukById(produkId);
        
        if (produk != null) {
            selectedProdukId = produk.getId();
            txtId.setText(String.valueOf(produk.getId()));
            txtNamaProduk.setText(produk.getNamaProduk());
            txtStok.setText(formatNumberForField(produk.getStokDasar()));
            txtHargaBeli.setText(formatNumberForField(produk.getHargaBeli()));
            
            for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                Kategori k = cmbKategori.getItemAt(i);
                if (k.getId() == produk.getKategoriId()) {
                    cmbKategori.setSelectedIndex(i);
                    break;
                }
            }
            cmbSatuanDasar.setSelectedItem(produk.getSatuanDasar());
            
            btnSimpan.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnKelolaHarga.setEnabled(true);
            txtNamaProduk.requestFocus();
        }
    }
    
    private void kelolaHarga() {
        if (selectedProdukId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Produk produk = Produk.getProdukById(selectedProdukId);
        new DialogKelolaHarga(this, produk).setVisible(true);
        loadData();
    }
    
    private void clearForm() {
        selectedProdukId = -1;
        txtId.setText("");
        txtNamaProduk.setText("");
        txtStok.setText("0");
        txtHargaBeli.setText("0");
        cmbKategori.setSelectedIndex(0);
        cmbSatuanDasar.setSelectedIndex(0);
        
        btnSimpan.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnKelolaHarga.setEnabled(false);
        tableProduk.clearSelection();
        txtNamaProduk.requestFocus();
    }

    // Helper: parsing input user (handle pemisah ribuan & desimal)
    private double parseNumericField(String text) {
        if (text == null || text.trim().isEmpty()) return 0.0;
        String clean = text.replaceAll("[^0-9.,]", "")
                           .replace(",", "."); // normalisasi ke .
        if (clean.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Helper: format angka ke string tanpa koma ribuan, hanya titik desimal
    private String formatNumberForField(double value) {
        DecimalFormat df = new DecimalFormat("0.##");
        df.setDecimalSeparatorAlwaysShown(false);
        return df.format(value);
    }
}

// =====================================================
// 🔢 NumericDocumentFilter — Only allow digits & single dot
// =====================================================
class NumericDocumentFilter extends PlainDocument {
    private final boolean allowDecimal;

    public NumericDocumentFilter(boolean allowDecimal) {
        this.allowDecimal = allowDecimal;
    }

    public static void applyTo(JTextField field, boolean allowDecimal) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (isValid(fb.getDocument(), offset, string)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (isValid(fb.getDocument(), offset, text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isValid(Document doc, int offset, String newText) {
                try {
                    String currentText = doc.getText(0, doc.getLength());
                    String pendingText = new StringBuilder(currentText)
                            .insert(offset, newText)
                            .toString();

                    if (pendingText.isEmpty()) return true;

                    if (!pendingText.matches("[0-9.,]*")) return false;

                    String normalized = pendingText.replace(",", ".");

                    if (allowDecimal && normalized.chars().filter(ch -> ch == '.').count() > 1) {
                        return false;
                    }

                    if (allowDecimal && normalized.startsWith(".") && normalized.length() == 1) return false;
                    if (allowDecimal && normalized.endsWith(".") && normalized.length() > 1) return false;

                    if (!allowDecimal && normalized.contains(".")) return false;

                    if (!normalized.isEmpty()) {
                        Double.parseDouble(normalized);
                    }
                    return true;

                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    // ✅ METHOD INI YANG HARUS ADA!
    public static double parseDouble(String text) throws NumberFormatException {
        if (text == null || text.trim().isEmpty()) return 0.0;
        String clean = text.replaceAll("[^0-9.,]", "").replace(",", ".");
        if (clean.isEmpty()) return 0.0;
        return Double.parseDouble(clean);
    }
}

// =====================================================
// Dialog Kelola Harga — tetap sama, tapi input numerik diperbaiki
// =====================================================
class DialogKelolaHarga extends JDialog {
    private Produk produk;
    private JTable tableSatuan;
    private DefaultTableModel tableSatuanModel;

    public DialogKelolaHarga(JFrame parent, Produk produk) {
        super(parent, "Kelola Harga - " + produk.getNamaProduk(), true);
        this.produk = produk;
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(750, 500);
        setLayout(new BorderLayout(10, 10));

        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel panelInfo = new JPanel(new GridLayout(4, 2, 10, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Info Produk"));
        panelInfo.add(new JLabel("Produk:"));
        panelInfo.add(new JLabel(produk.getNamaProduk()));
        panelInfo.add(new JLabel("Kategori:"));
        panelInfo.add(new JLabel(produk.getNamaKategori()));
        panelInfo.add(new JLabel("Satuan Dasar:"));
        panelInfo.add(new JLabel(produk.getSatuanDasar() + " (Stok: " + String.format("%.2f", produk.getStokDasar()) + ")"));
        panelInfo.add(new JLabel("Harga Beli:"));
        panelInfo.add(new JLabel(String.format("Rp %,.0f", produk.getHargaBeli())));

        String[] columns = {"ID", "Nama Satuan", "Konversi ke Dasar", "Harga Jual", "Barcode"};
        tableSatuanModel = new DefaultTableModel(columns, 0);
        tableSatuan = new JTable(tableSatuanModel);
        tableSatuan.setRowHeight(28);
        tableSatuan.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableSatuan.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableSatuan.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableSatuan.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableSatuan.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tableSatuan);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton btnTambah = new JButton("Tambah Satuan");
        btnTambah.setBackground(new Color(46, 204, 113));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> tambahSatuan());

        JButton btnEdit = new JButton("Edit Satuan");
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.addActionListener(e -> editSatuan());

        JButton btnHapus = new JButton("Hapus Satuan");
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusSatuan());

        JButton btnTutup = new JButton("Tutup");
        btnTutup.addActionListener(e -> dispose());

        panelButton.add(btnTambah);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(Box.createHorizontalStrut(20));
        panelButton.add(btnTutup);

        panelMain.add(panelInfo, BorderLayout.NORTH);
        panelMain.add(scrollPane, BorderLayout.CENTER);
        panelMain.add(panelButton, BorderLayout.SOUTH);

        add(panelMain);
    }

    private void loadData() {
        tableSatuanModel.setRowCount(0);
        List<SatuanJual> satuanList = SatuanJual.getSatuanByProdukId(produk.getId());
        for (SatuanJual s : satuanList) {
            Object[] row = {
                s.getId(),
                s.getNamaSatuan(),
                String.format("%.2f", s.getKonversiKeDasar()),
                String.format("Rp %,.0f", s.getHargaJual()),
                s.getBarcode() != null ? s.getBarcode() : "-"
            };
            tableSatuanModel.addRow(row);
        }
    }

    private void tambahSatuan() {
        new DialogInputSatuan(this, produk, null).setVisible(true);
        loadData();
    }

    private void editSatuan() {
        int selectedRow = tableSatuan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satuan yang akan diedit!");
            return;
        }
        int satuanId = (int) tableSatuanModel.getValueAt(selectedRow, 0);
        SatuanJual satuan = SatuanJual.getSatuanById(satuanId);
        new DialogInputSatuan(this, produk, satuan).setVisible(true);
        loadData();
    }

    private void hapusSatuan() {
        int selectedRow = tableSatuan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih satuan yang akan dihapus!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus satuan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int satuanId = (int) tableSatuanModel.getValueAt(selectedRow, 0);
            if (SatuanJual.deleteSatuan(satuanId)) {
                JOptionPane.showMessageDialog(this, "Satuan berhasil dihapus!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus satuan!");
            }
        }
    }
}

// =====================================================
// Dialog Input Satuan — dengan input numerik
// =====================================================
class DialogInputSatuan extends JDialog {
    private Produk produk;
    private SatuanJual satuan;
    private JTextField txtNamaSatuan, txtKonversi, txtHarga, txtBarcode;
    private boolean isEdit;

    public DialogInputSatuan(JDialog parent, Produk produk, SatuanJual satuan) {
        super(parent, satuan == null ? "Tambah Satuan Jual" : "Edit Satuan Jual", true);
        this.produk = produk;
        this.satuan = satuan;
        this.isEdit = (satuan != null);
        initComponents();
        if (isEdit) {
            loadDataToForm();
        }
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 350);
        setLayout(new BorderLayout(10, 10));

        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("Produk: " + produk.getNamaProduk() + " (Dasar: " + produk.getSatuanDasar() + ")");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 12));
        panelMain.add(lblInfo, gbc);

        gbc.gridy = 1;
        JLabel lblInfoHarga = new JLabel("Harga Beli: " + String.format("Rp %,.0f", produk.getHargaBeli()));
        lblInfoHarga.setFont(new Font("Arial", Font.PLAIN, 11));
        panelMain.add(lblInfoHarga, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblNama = new JLabel("Nama Satuan:*");
        lblNama.setFont(new Font("Arial", Font.BOLD, 11));
        panelMain.add(lblNama, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        txtNamaSatuan = new JTextField();
        txtNamaSatuan.setPreferredSize(new Dimension(200, 30));
        panelMain.add(txtNamaSatuan, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblKonversi = new JLabel("Konversi ke Dasar:*");
        lblKonversi.setFont(new Font("Arial", Font.BOLD, 11));
        panelMain.add(lblKonversi, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        txtKonversi = new JTextField("1");
        txtKonversi.setPreferredSize(new Dimension(200, 30));
        // ✅ Numeric only (desimal)
        NumericDocumentFilter.applyTo(txtKonversi, true);
        panelMain.add(txtKonversi, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        JLabel lblKonversiInfo = new JLabel("(1 satuan ini = berapa " + produk.getSatuanDasar() + ")");
        lblKonversiInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        lblKonversiInfo.setForeground(Color.GRAY);
        panelMain.add(lblKonversiInfo, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        JLabel lblHarga = new JLabel("Harga Jual (Rp):*");
        lblHarga.setFont(new Font("Arial", Font.BOLD, 11));
        panelMain.add(lblHarga, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        txtHarga = new JTextField("0");
        txtHarga.setPreferredSize(new Dimension(200, 30));
        // ✅ Numeric only (desimal)
        NumericDocumentFilter.applyTo(txtHarga, true);
        panelMain.add(txtHarga, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.3;
        panelMain.add(new JLabel("Barcode:"), gbc);

        gbc.gridx = 1; gbc.gridy = 6; gbc.weightx = 0.7;
        txtBarcode = new JTextField();
        txtBarcode.setPreferredSize(new Dimension(200, 30));
        panelMain.add(txtBarcode, gbc);

        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnSimpan = new JButton(isEdit ? "UPDATE" : "SIMPAN");
        btnSimpan.setPreferredSize(new Dimension(120, 35));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 12));
        btnSimpan.addActionListener(e -> simpan());

        JButton btnBatal = new JButton("BATAL");
        btnBatal.setPreferredSize(new Dimension(100, 35));
        btnBatal.addActionListener(e -> dispose());

        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);

        add(panelMain, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }

    private void loadDataToForm() {
        txtNamaSatuan.setText(satuan.getNamaSatuan());
        txtKonversi.setText(new DecimalFormat("0.##").format(satuan.getKonversiKeDasar()));
        txtHarga.setText(new DecimalFormat("0.##").format(satuan.getHargaJual()));
        txtBarcode.setText(satuan.getBarcode() != null ? satuan.getBarcode() : "");
    }

    private void simpan() {
        if (txtNamaSatuan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama satuan harus diisi!");
            txtNamaSatuan.requestFocus();
            return;
        }

        try {
            double konversi = NumericDocumentFilter.parseDouble(txtKonversi.getText());
            double harga = NumericDocumentFilter.parseDouble(txtHarga.getText());

            if (konversi <= 0) {
                JOptionPane.showMessageDialog(this, "Konversi harus lebih dari 0!");
                return;
            }

            if (harga < 0) {
                JOptionPane.showMessageDialog(this, "Harga tidak boleh negatif!");
                return;
            }

            if (isEdit) {
                satuan.setNamaSatuan(txtNamaSatuan.getText().trim());
                satuan.setKonversiKeDasar(konversi);
                satuan.setHargaJual(harga);
                satuan.setBarcode(txtBarcode.getText().trim().isEmpty() ? null : txtBarcode.getText().trim());

                if (SatuanJual.updateSatuan(satuan)) {
                    JOptionPane.showMessageDialog(this, "Satuan berhasil diupdate!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengupdate satuan!");
                }
            } else {
                SatuanJual satuanBaru = new SatuanJual();
                satuanBaru.setProdukId(produk.getId());
                satuanBaru.setNamaSatuan(txtNamaSatuan.getText().trim());
                satuanBaru.setKonversiKeDasar(konversi);
                satuanBaru.setHargaJual(harga);
                satuanBaru.setBarcode(txtBarcode.getText().trim().isEmpty() ? null : txtBarcode.getText().trim());

                if (SatuanJual.tambahSatuan(satuanBaru)) {
                    JOptionPane.showMessageDialog(this, "Satuan berhasil ditambahkan!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan satuan!");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format angka tidak valid!");
        }
    }
}

// Tambahkan helper di NumericDocumentFilter untuk parse (opsional, tapi rapi)
