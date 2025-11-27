package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Form Transaksi Penjualan (POS) dengan UI Grid Produk - Full Screen Version
 * Stok ditampilkan jelas di bawah harga produk.
 * 🔍 Fitur Live Search ditambahkan.
 * ✅ Perbaikan: Stok produk langsung berkurang di UI setelah transaksi disimpan.
 * ✅ Perbaikan: Input "Jumlah Bayar" hanya menerima angka.
 */
public class FormTransaksi extends JFrame {
    private User currentUser;
    private JPanel productGridPanel;
    private JTable tableKeranjang;
    private DefaultTableModel tableKeranjangModel;
    private JTextField txtBayar, txtSearch;
    private JLabel lblTotal, lblKembalian, lblKodeTransaksi;
    private JButton btnBatal, btnSimpan;
    private JRadioButton rbTunai, rbQRIS, rbTransfer;
    private ButtonGroup paymentGroup;
    private double totalBelanja = 0;
    private List<ItemKeranjang> keranjang = new ArrayList<>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private String metodePembayaran = "Tunai";
    private List<Produk> allProducts;

    // Modern color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color LIGHT_COLOR = new Color(248, 249, 250);
    private final Color DARK_COLOR = new Color(44, 62, 80);
    private final Color BACKGROUND_COLOR = new Color(240, 242, 245);

    public FormTransaksi(JFrame parent, User user) {
        this.currentUser = user;
        initComponents();
        loadAllProducts();
        displayProducts(allProducts);
        generateKodeTransaksi();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setTitle("Point of Sale - Transaksi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(mainPanel);

        // Panel Pencarian
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.setPreferredSize(new Dimension(300, 40));
        txtSearch.setForeground(DARK_COLOR);
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setCaretColor(DARK_COLOR);
        txtSearch.setToolTipText("Cari produk berdasarkan nama");

        JLabel lblSearchIcon = new JLabel("🔍");
        lblSearchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblSearchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        searchWrapper.add(lblSearchIcon, BorderLayout.WEST);
        searchWrapper.add(txtSearch, BorderLayout.CENTER);

        searchPanel.add(searchWrapper, BorderLayout.WEST);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterProducts(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterProducts(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterProducts(); }
        });

        // Panel Grid Produk
        productGridPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        productGridPanel.setBackground(Color.WHITE);
        productGridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scrollProduct = new JScrollPane(productGridPanel);
        scrollProduct.setBorder(null);
        scrollProduct.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollProduct.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollProduct.getVerticalScrollBar().setUnitIncrement(16);

        scrollProduct.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                productGridPanel.revalidate();
                productGridPanel.repaint();
            }
        });

        // Panel Kanan: Keranjang + Bayar
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(750, 0));
        rightPanel.setBackground(LIGHT_COLOR);

        rightPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        rightPanel.add(createCartPanel(), BorderLayout.CENTER);
        rightPanel.add(createPaymentPanel(), BorderLayout.SOUTH);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollProduct, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
    }

    private void loadAllProducts() {
        allProducts = Produk.getAllProduk();
    }

    private void filterProducts() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            displayProducts(allProducts);
        } else {
            List<Produk> filtered = allProducts.stream()
                .filter(p -> p.getNamaProduk().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
            displayProducts(filtered);
        }
    }

    private void displayProducts(List<Produk> productsToShow) {
        productGridPanel.removeAll();
        for (Produk produk : productsToShow) {
            productGridPanel.add(new ProductCard(produk));
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel leftInfo = new JPanel(new GridLayout(2, 1, 0, 5));
        leftInfo.setBackground(PRIMARY_COLOR);
        JLabel lblKasir = new JLabel("Kasir: " + currentUser.getNamaLengkap());
        lblKasir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKasir.setForeground(Color.WHITE);
        lblKodeTransaksi = new JLabel("Kode: -");
        lblKodeTransaksi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblKodeTransaksi.setForeground(new Color(230, 240, 255));
        leftInfo.add(lblKasir);
        leftInfo.add(lblKodeTransaksi);
        panel.add(leftInfo, BorderLayout.WEST);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Produk", "Satuan", "Qty", "Harga", "Subtotal"};
        tableKeranjangModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableKeranjang = new JTable(tableKeranjangModel);
        tableKeranjang.setRowHeight(35);
        tableKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableKeranjang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableKeranjang.getTableHeader().setBackground(LIGHT_COLOR);
        tableKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKeranjang.getColumnModel().getColumn(0).setPreferredWidth(220);
        tableKeranjang.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableKeranjang.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableKeranjang.getColumnModel().getColumn(3).setPreferredWidth(110);
        tableKeranjang.getColumnModel().getColumn(4).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(tableKeranjang);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        controlPanel.setBackground(new Color(236, 240, 241));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton btnPlus = new JButton("+ TAMBAH QTY");
        btnPlus.setPreferredSize(new Dimension(140, 45));
        btnPlus.setFont(new Font("Arial", Font.BOLD, 13));
        btnPlus.setBackground(SUCCESS_COLOR);
        btnPlus.setForeground(Color.WHITE);
        btnPlus.setFocusPainted(false);
        btnPlus.setBorderPainted(false);
        btnPlus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPlus.addActionListener(e -> tambahQtyItem());

        JButton btnMinus = new JButton("- KURANG QTY");
        btnMinus.setPreferredSize(new Dimension(140, 45));
        btnMinus.setFont(new Font("Arial", Font.BOLD, 13));
        btnMinus.setBackground(new Color(230, 126, 34));
        btnMinus.setForeground(Color.WHITE);
        btnMinus.setFocusPainted(false);
        btnMinus.setBorderPainted(false);
        btnMinus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMinus.addActionListener(e -> kurangiQtyItem());

        JButton btnDelete = new JButton("✖ HAPUS ITEM");
        btnDelete.setPreferredSize(new Dimension(140, 45));
        btnDelete.setFont(new Font("Arial", Font.BOLD, 13));
        btnDelete.setBackground(DANGER_COLOR);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> hapusItem());

        controlPanel.add(btnPlus);
        controlPanel.add(btnMinus);
        controlPanel.add(btnDelete);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void tambahQtyItem() {
        int selectedRow = tableKeranjang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item di tabel terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ItemKeranjang item = keranjang.get(selectedRow);
        double stokDiperlukan = (item.qty + 1) * item.satuan.getKonversiKeDasar();
        if (stokDiperlukan > item.produk.getStokDasar()) {
            JOptionPane.showMessageDialog(this,
                "Stok tidak cukup!\nTersedia: " + item.produk.getStokDasar() + " " + item.produk.getSatuanDasar(),
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        item.qty += 1;
        item.subtotal = item.qty * item.satuan.getHargaJual();
        refreshKeranjangTable();
        updateTotal();
        if (selectedRow < tableKeranjang.getRowCount()) {
            tableKeranjang.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void kurangiQtyItem() {
        int selectedRow = tableKeranjang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item di tabel terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ItemKeranjang item = keranjang.get(selectedRow);
        if (item.qty > 1) {
            item.qty -= 1;
            item.subtotal = item.qty * item.satuan.getHargaJual();
            refreshKeranjangTable();
            updateTotal();
            if (selectedRow < tableKeranjang.getRowCount()) {
                tableKeranjang.setRowSelectionInterval(selectedRow, selectedRow);
            }
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Quantity sudah 1. Hapus item ini dari keranjang?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hapusItem();
            }
        }
    }

    private void hapusItem() {
        int selectedRow = tableKeranjang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item di tabel terlebih dahulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus item ini dari keranjang?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            keranjang.remove(selectedRow);
            refreshKeranjangTable();
            updateTotal();
        }
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Total
        JPanel totalPanel = new JPanel(new BorderLayout(10, 0));
        totalPanel.setBackground(LIGHT_COLOR);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel lblTotalLabel = new JLabel("TOTAL");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotal.setForeground(DANGER_COLOR);
        totalPanel.add(lblTotalLabel, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.CENTER);
        panel.add(totalPanel);
        panel.add(Box.createVerticalStrut(15));

        // Metode Bayar
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        methodPanel.setBackground(LIGHT_COLOR);
        methodPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lblMethod = new JLabel("Metode Bayar:");
        lblMethod.setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentGroup = new ButtonGroup();
        rbTunai = new JRadioButton("Tunai");
        rbQRIS = new JRadioButton("QRIS");
        rbTransfer = new JRadioButton("Transfer");
        rbTunai.setSelected(true);
        rbTunai.setBackground(LIGHT_COLOR);
        rbQRIS.setBackground(LIGHT_COLOR);
        rbTransfer.setBackground(LIGHT_COLOR);
        rbTunai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rbQRIS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rbTransfer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentGroup.add(rbTunai);
        paymentGroup.add(rbQRIS);
        paymentGroup.add(rbTransfer);

        rbTunai.addActionListener(e -> {
            metodePembayaran = "Tunai";
            txtBayar.setEnabled(true);
            txtBayar.setText("");
            hitungKembalian();
        });
        rbQRIS.addActionListener(e -> {
            metodePembayaran = "QRIS";
            txtBayar.setText(String.valueOf((long)totalBelanja));
            txtBayar.setEnabled(false);
            hitungKembalian();
        });
        rbTransfer.addActionListener(e -> {
            metodePembayaran = "Transfer";
            txtBayar.setText(String.valueOf((long)totalBelanja));
            txtBayar.setEnabled(false);
            hitungKembalian();
        });

        methodPanel.add(lblMethod);
        methodPanel.add(rbTunai);
        methodPanel.add(rbQRIS);
        methodPanel.add(rbTransfer);
        panel.add(methodPanel);
        panel.add(Box.createVerticalStrut(10));

        // Input Bayar (HANYA ANGKA)
        JPanel bayarPanel = new JPanel(new BorderLayout(10, 5));
        bayarPanel.setBackground(LIGHT_COLOR);
        bayarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel lblBayarLabel = new JLabel("Jumlah Bayar");
        lblBayarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBayar = new JTextField();
        txtBayar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtBayar.setPreferredSize(new Dimension(0, 50));
        txtBayar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // 🔒 FILTER: HANYA ANGKA DIIZINKAN
        ((AbstractDocument) txtBayar.getDocument()).setDocumentFilter(new NumericDocumentFilter());

        txtBayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungKembalian();
            }
        });
        bayarPanel.add(lblBayarLabel, BorderLayout.NORTH);
        bayarPanel.add(txtBayar, BorderLayout.CENTER);
        panel.add(bayarPanel);
        panel.add(Box.createVerticalStrut(15));

        // Kembalian
        JPanel kembalianPanel = new JPanel(new BorderLayout(10, 0));
        kembalianPanel.setBackground(LIGHT_COLOR);
        kembalianPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel lblKembalianLabel = new JLabel("Kembalian");
        lblKembalianLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKembalian = new JLabel("Rp 0", SwingConstants.RIGHT);
        lblKembalian.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblKembalian.setForeground(SUCCESS_COLOR);
        kembalianPanel.add(lblKembalianLabel, BorderLayout.WEST);
        kembalianPanel.add(lblKembalian, BorderLayout.CENTER);
        panel.add(kembalianPanel);
        panel.add(Box.createVerticalStrut(25));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        buttonPanel.setBackground(LIGHT_COLOR);
        btnBatal = createModernButton("BATAL", DANGER_COLOR);
        btnBatal.addActionListener(e -> batalTransaksi());
        btnSimpan = createModernButton("SIMPAN & CETAK", SUCCESS_COLOR);
        btnSimpan.addActionListener(e -> simpanTransaksi());
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnSimpan);
        panel.add(buttonPanel);
        return panel;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(bgColor);
        return button;
    }

    public void tambahKeKeranjang(Produk produk, SatuanJual satuan, double qty) {
        double kebutuhanStok = qty * satuan.getKonversiKeDasar();
        if (kebutuhanStok > produk.getStokDasar()) {
            JOptionPane.showMessageDialog(this,
                "<html>Stok tidak cukup!<br>Tersedia: <b>" + String.format("%.0f", produk.getStokDasar()) + 
                " " + produk.getSatuanDasar() + "</b></html>",
                "Stok Tidak Cukup",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (ItemKeranjang item : keranjang) {
            if (item.produk.getId() == produk.getId() && item.satuan.getId() == satuan.getId()) {
                double totalQty = item.qty + qty;
                double totalKebutuhan = totalQty * satuan.getKonversiKeDasar();
                if (totalKebutuhan > produk.getStokDasar()) {
                    JOptionPane.showMessageDialog(this,
                        "<html>Stok tidak cukup!<br>Tersedia: <b>" + String.format("%.0f", produk.getStokDasar()) + 
                        " " + produk.getSatuanDasar() + "</b></html>",
                        "Stok Tidak Cukup",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item.qty = totalQty;
                item.subtotal = item.qty * satuan.getHargaJual();
                refreshKeranjangTable();
                updateTotal();
                return;
            }
        }

        ItemKeranjang item = new ItemKeranjang();
        item.produk = produk;
        item.satuan = satuan;
        item.qty = qty;
        item.subtotal = qty * satuan.getHargaJual();
        keranjang.add(item);
        refreshKeranjangTable();
        updateTotal();
    }

    private void refreshKeranjangTable() {
        tableKeranjangModel.setRowCount(0);
        for (ItemKeranjang item : keranjang) {
            Object[] row = {
                item.produk.getNamaProduk(),
                item.satuan.getNamaSatuan(),
                String.format("%.0f", item.qty),
                currencyFormat.format(item.satuan.getHargaJual()),
                currencyFormat.format(item.subtotal)
            };
            tableKeranjangModel.addRow(row);
        }
    }

    private void updateTotal() {
        totalBelanja = 0;
        for (ItemKeranjang item : keranjang) {
            totalBelanja += item.subtotal;
        }
        lblTotal.setText(currencyFormat.format(totalBelanja));
        if ("QRIS".equals(metodePembayaran) || "Transfer".equals(metodePembayaran)) {
            txtBayar.setText(String.valueOf((long)totalBelanja));
        }
        hitungKembalian();
    }

    private void hitungKembalian() {
        try {
            String bayarStr = txtBayar.getText().replaceAll("[^0-9]", "");
            if (bayarStr.isEmpty()) {
                lblKembalian.setText(currencyFormat.format(0));
                return;
            }
            double bayar = Double.parseDouble(bayarStr);
            double kembalian = bayar - totalBelanja;
            if ("QRIS".equals(metodePembayaran) || "Transfer".equals(metodePembayaran)) {
                kembalian = 0;
            }
            lblKembalian.setText(currencyFormat.format(kembalian));
            lblKembalian.setForeground(kembalian < 0 ? DANGER_COLOR : SUCCESS_COLOR);
        } catch (Exception e) {
            lblKembalian.setText(currencyFormat.format(0));
        }
    }

    private void generateKodeTransaksi() {
        String kode = Transaksi.generateKodeTransaksi();
        lblKodeTransaksi.setText("Kode: " + kode);
    }

    private void simpanTransaksi() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String bayarStr = txtBayar.getText().replaceAll("[^0-9]", "");
            if (bayarStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah bayar!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtBayar.requestFocus();
                return;
            }
            double bayar = Double.parseDouble(bayarStr);
            double kembalian = bayar - totalBelanja;
            if ("QRIS".equals(metodePembayaran) || "Transfer".equals(metodePembayaran)) {
                kembalian = 0;
            } else if (kembalian < 0) {
                JOptionPane.showMessageDialog(this, "Uang bayar kurang!", "Validasi", JOptionPane.WARNING_MESSAGE);
                txtBayar.requestFocus();
                return;
            }

            Transaksi transaksi = new Transaksi();
            transaksi.setKodeTransaksi(lblKodeTransaksi.getText().replace("Kode: ", ""));
            transaksi.setTotalBelanja(totalBelanja);
            transaksi.setBayar(bayar);
            transaksi.setKembalian(kembalian);
            transaksi.setKasirId(currentUser.getId());
            transaksi.setMetodePembayaran(metodePembayaran);

            List<model.DetailTransaksi> detailList = new ArrayList<>();
            for (ItemKeranjang item : keranjang) {
                model.DetailTransaksi detail = new model.DetailTransaksi();
                detail.setProdukId(item.produk.getId());
                detail.setSatuanJualId(item.satuan.getId());
                detail.setNamaProduk(item.produk.getNamaProduk());
                detail.setNamaSatuan(item.satuan.getNamaSatuan());
                detail.setQty(item.qty);
                detail.setHargaSatuan(item.satuan.getHargaJual());
                detail.setSubtotal(item.subtotal);
                detailList.add(detail);
            }
            transaksi.setDetailList(detailList);

            if (Transaksi.simpanTransaksi(transaksi)) {
                tampilkanStruk(transaksi, bayar, kembalian);
                keranjang.clear();
                refreshKeranjangTable();
                txtBayar.setText("");
                rbTunai.setSelected(true);
                metodePembayaran = "Tunai";
                txtBayar.setEnabled(true);
                updateTotal();
                generateKodeTransaksi();
                txtSearch.setText("");

                // ✅ PERBAIKAN UTAMA: Refresh data produk agar stok langsung berkurang di UI
                loadAllProducts();
                filterProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void batalTransaksi() {
        if (keranjang.isEmpty()) {
            dispose();
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Batalkan transaksi?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            keranjang.clear();
            refreshKeranjangTable();
            txtBayar.setText("");
            rbTunai.setSelected(true);
            metodePembayaran = "Tunai";
            txtBayar.setEnabled(true);
            updateTotal();
            generateKodeTransaksi();
            txtSearch.setText("");
        }
    }

    private void tampilkanStruk(Transaksi transaksi, double bayar, double kembalian) {
        JDialog strukDialog = new JDialog(this, "Struk Pembayaran", true);
        strukDialog.setSize(450, 650);
        strukDialog.setLayout(new BorderLayout());
        strukDialog.setLocationRelativeTo(this);

        JPanel strukPanel = new JPanel();
        strukPanel.setLayout(new BoxLayout(strukPanel, BoxLayout.Y_AXIS));
        strukPanel.setBackground(Color.WHITE);
        strukPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblNamaToko = new JLabel("TOKO BANGUNAN WARNER");
        lblNamaToko.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblNamaToko.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblAlamat = new JLabel("Jl. Raya No. 123, Jakarta");
        lblAlamat.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblAlamat.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblTelp = new JLabel("Telp: 021-12345678");
        lblTelp.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblTelp.setAlignmentX(Component.CENTER_ALIGNMENT);
        strukPanel.add(lblNamaToko);
        strukPanel.add(Box.createVerticalStrut(5));
        strukPanel.add(lblAlamat);
        strukPanel.add(lblTelp);
        strukPanel.add(Box.createVerticalStrut(15));

        strukPanel.add(new JSeparator());
        strukPanel.add(Box.createVerticalStrut(10));

        JLabel lblKode = new JLabel("No: " + transaksi.getKodeTransaksi());
        lblKode.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblKode.setAlignmentX(Component.CENTER_ALIGNMENT);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JLabel lblTanggal = new JLabel("Tanggal: " + sdf.format(new Date()));
        lblTanggal.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblTanggal.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblKasir = new JLabel("Kasir: " + currentUser.getNamaLengkap());
        lblKasir.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblKasir.setAlignmentX(Component.CENTER_ALIGNMENT);
        strukPanel.add(lblKode);
        strukPanel.add(lblTanggal);
        strukPanel.add(lblKasir);
        strukPanel.add(Box.createVerticalStrut(10));
        strukPanel.add(new JSeparator());
        strukPanel.add(Box.createVerticalStrut(15));

        JTextArea txtDetail = new JTextArea();
        txtDetail.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtDetail.setEditable(false);
        txtDetail.setBackground(Color.WHITE);
        txtDetail.setBorder(null);
        StringBuilder detail = new StringBuilder();
        for (DetailTransaksi item : transaksi.getDetailList()) {
            detail.append(item.getNamaProduk()).append("\n");
            detail.append(String.format("  %,.0f x %s @ %s\n", 
                item.getQty(),
                item.getNamaSatuan(),
                currencyFormat.format(item.getHargaSatuan())));
            detail.append(String.format("%38s\n", currencyFormat.format(item.getSubtotal())));
        }
        txtDetail.setText(detail.toString());
        strukPanel.add(txtDetail);
        strukPanel.add(Box.createVerticalStrut(10));
        strukPanel.add(new JSeparator());
        strukPanel.add(Box.createVerticalStrut(10));

        JPanel totalPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setMaximumSize(new Dimension(400, 100));
        JLabel lblTotalLabel = new JLabel("TOTAL:");
        lblTotalLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        JLabel lblTotalValue = new JLabel(currencyFormat.format(totalBelanja));
        lblTotalValue.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblTotalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblMetodeLabel = new JLabel("Metode:");
        lblMetodeLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JLabel lblMetodeValue = new JLabel(metodePembayaran);
        lblMetodeValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblMetodeValue.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblBayarLabel = new JLabel("Bayar:");
        lblBayarLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JLabel lblBayarValue = new JLabel(currencyFormat.format(bayar));
        lblBayarValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblBayarValue.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblKembaliLabel = new JLabel("Kembalian:");
        lblKembaliLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JLabel lblKembaliValue = new JLabel(currencyFormat.format(kembalian));
        lblKembaliValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblKembaliValue.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPanel.add(lblTotalLabel);
        totalPanel.add(lblTotalValue);
        totalPanel.add(lblMetodeLabel);
        totalPanel.add(lblMetodeValue);
        totalPanel.add(lblBayarLabel);
        totalPanel.add(lblBayarValue);
        totalPanel.add(lblKembaliLabel);
        totalPanel.add(lblKembaliValue);
        strukPanel.add(totalPanel);
        strukPanel.add(Box.createVerticalStrut(15));
        strukPanel.add(new JSeparator());
        strukPanel.add(Box.createVerticalStrut(10));

        JLabel lblFooter = new JLabel("Terima Kasih Atas Kunjungan Anda");
        lblFooter.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblFooter2 = new JLabel("Barang yang sudah dibeli tidak dapat ditukar");
        lblFooter2.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lblFooter2.setAlignmentX(Component.CENTER_ALIGNMENT);
        strukPanel.add(lblFooter);
        strukPanel.add(Box.createVerticalStrut(5));
        strukPanel.add(lblFooter2);

        JScrollPane scrollStruk = new JScrollPane(strukPanel);
        scrollStruk.setBorder(null);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(LIGHT_COLOR);
        JButton btnCetak = new JButton("🖨 CETAK");
        btnCetak.setPreferredSize(new Dimension(150, 45));
        btnCetak.setFont(new Font("Arial", Font.BOLD, 14));
        btnCetak.setBackground(PRIMARY_COLOR);
        btnCetak.setForeground(Color.WHITE);
        btnCetak.setFocusPainted(false);
        btnCetak.setBorderPainted(false);
        btnCetak.addActionListener(e -> {
            JOptionPane.showMessageDialog(strukDialog, 
                "Fitur cetak ke printer akan segera hadir!\nUntuk saat ini, Anda bisa screenshot struk ini.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
        });
        JButton btnTutup = new JButton("TUTUP");
        btnTutup.setPreferredSize(new Dimension(150, 45));
        btnTutup.setFont(new Font("Arial", Font.BOLD, 14));
        btnTutup.setBackground(new Color(149, 165, 166));
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFocusPainted(false);
        btnTutup.setBorderPainted(false);
        btnTutup.addActionListener(e -> strukDialog.dispose());
        buttonPanel.add(btnCetak);
        buttonPanel.add(btnTutup);

        strukDialog.add(scrollStruk, BorderLayout.CENTER);
        strukDialog.add(buttonPanel, BorderLayout.SOUTH);
        strukDialog.setVisible(true);
    }

    // === Kelas Bantuan ===

    class ProductCard extends JPanel {
        private Produk produk;

        public ProductCard(Produk produk) {
            this.produk = produk;
            setPreferredSize(new Dimension(180, 150));
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
            setBackground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

            JLabel nameLabel = new JLabel("<html><center>" + produk.getNamaProduk() + "</center></html>");
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(8));

            String hargaText = "Rp 0";
            if (!produk.getDaftarSatuan().isEmpty()) {
                double harga = produk.getDaftarSatuan().get(0).getHargaJual();
                hargaText = String.format("Rp %,.0f", harga);
            }
            JLabel priceLabel = new JLabel(hargaText);
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            priceLabel.setForeground(PRIMARY_COLOR);
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(priceLabel);
            infoPanel.add(Box.createVerticalStrut(6));

            double stok = produk.getStokDasar();
            String stokText = String.format("Stok: %.0f %s", stok, produk.getSatuanDasar());
            JLabel stokLabel = new JLabel(stokText);
            stokLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            stokLabel.setForeground(stok <= 5 ? DANGER_COLOR : Color.GRAY);
            stokLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(stokLabel);

            add(infoPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new DialogPilihSatuan(FormTransaksi.this, produk).setVisible(true);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(245, 250, 255));
                    setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                    setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
                    repaint();
                }
            });
        }
    }

    // ✅ FILTER UNTUK FIELD ANGKA SAJA
    private class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (isNumeric(string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (isNumeric(text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isNumeric(String str) {
            if (str == null) return false;
            return str.matches("\\d*");
        }
    }
    
    // === Kelas WrapLayout untuk Layout Grid Fleksibel ===
class WrapLayout extends FlowLayout {
    public WrapLayout() { super(); }
    public WrapLayout(int align) { super(align); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension min = layoutSize(target, false);
        min.width -= getHgap() * 2;
        return min;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            Container container = target;
            while (container.getParent() != null) {
                container = container.getParent();
                if (container instanceof JViewport) {
                    targetWidth = container.getWidth();
                    break;
                }
            }
            if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap(), vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;
            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0, rowHeight = 0;

            for (int i = 0; i < target.getComponentCount(); i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }
                    if (rowWidth > 0) rowWidth += hgap;
                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
            addRow(dim, rowWidth, rowHeight);
            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;
            return dim;
        }
    }

    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);
        if (dim.height > 0) dim.height += getVgap();
        dim.height += rowHeight;
    }
}
}

// === Kelas Data Pendukung (tetap di luar kelas utama) ===

class ItemKeranjang {
    Produk produk;
    SatuanJual satuan;
    double qty;
    double subtotal;
}