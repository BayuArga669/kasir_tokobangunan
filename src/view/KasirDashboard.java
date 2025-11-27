package view;

import model.User;
import model.Transaksi;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Dashboard Kasir - UI Improved Version (No Box components)
 */
public class KasirDashboard extends JFrame {
    
    private User currentUser;
    private JMenuBar menuBar;
    private JPanel panelMain;
    private JLabel lblWelcome;
    private JLabel lblInfo;
    
    // Komponen untuk laporan hari ini
    private JLabel lblTotalTransaksi, lblTotalPenjualan, lblTotalKeuntungan;
    private JTable tableTransaksiHariIni;
    private DefaultTableModel tableModel;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public KasirDashboard(User user) {
        this.currentUser = user;
        initComponents();
        loadLaporanHariIni();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kasir Dashboard - Kasir Toko Bangunan");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Menu Bar
        createMenuBar();
        
        // Main Panel
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout(0, 0));
        panelMain.setBackground(new Color(245, 247, 250));
        
        // Header Panel
        JPanel panelHeader = createHeaderPanel();
        
        // Content Panel
        JPanel panelContent = new JPanel(new BorderLayout(0, 0));
        panelContent.setBackground(new Color(245, 247, 250));
        
        // Summary Cards (Top)
        JPanel summaryPanel = createSummaryPanel();
        
        // Bottom Section: Button + Table
        JPanel bottomSection = new JPanel(new BorderLayout(15, 0));
        bottomSection.setBackground(new Color(245, 247, 250));
        bottomSection.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        
        // Left: Transaction Buttons
        JPanel leftButtons = createTransaksiButtonPanel();
        leftButtons.setPreferredSize(new Dimension(350, 0));
        
        // Right: Transaction Table
        JPanel rightTable = createTableTransaksiHariIni();
        
        bottomSection.add(leftButtons, BorderLayout.WEST);
        bottomSection.add(rightTable, BorderLayout.CENTER);
        
        panelContent.add(summaryPanel, BorderLayout.NORTH);
        panelContent.add(bottomSection, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel panelFooter = createFooterPanel();
        
        // Add to main
        panelMain.add(panelHeader, BorderLayout.NORTH);
        panelMain.add(panelContent, BorderLayout.CENTER);
        panelMain.add(panelFooter, BorderLayout.SOUTH);
        
        add(panelMain);
        
        // Auto refresh setiap 30 detik
        startAutoRefresh();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(142, 68, 173));
        panel.setPreferredSize(new Dimension(0, 100));
        panel.setLayout(new BorderLayout());
        
        JPanel headerContent = new JPanel(new GridBagLayout());
        headerContent.setBackground(new Color(142, 68, 173));
        headerContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        
        lblWelcome = new JLabel("Selamat Datang, " + currentUser.getNamaLengkap());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);
        headerContent.add(lblWelcome, gbc);
        
        // Tanggal hari ini
        SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        lblInfo = new JLabel("Laporan Penjualan: " + sdfDate.format(new Date()));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblInfo.setForeground(new Color(230, 220, 240));
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        headerContent.add(lblInfo, gbc);
        
        panel.add(headerContent, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(new Color(245, 247, 250));
        
        // Card 1: Total Transaksi
        JPanel card1 = createSummaryCard(
            "Total Transaksi",
            "0 transaksi",
            new Color(52, 152, 219),
            "📝"
        );
        lblTotalTransaksi = findValueLabel(card1);
        
        // Card 2: Total Penjualan
        JPanel card2 = createSummaryCard(
            "Total Penjualan",
            "Rp 0",
            new Color(46, 204, 113),
            "💰"
        );
        lblTotalPenjualan = findValueLabel(card2);
        
        // Card 3: Total Keuntungan (estimasi)
        JPanel card3 = createSummaryCard(
            "Estimasi Keuntungan",
            "Rp 0",
            new Color(230, 126, 34),
            "📈"
        );
        lblTotalKeuntungan = findValueLabel(card3);
        
        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        
        wrapper.add(panel, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JLabel findValueLabel(JPanel card) {
        // Cari label value dalam card
        Component[] comps = ((JPanel)card.getComponent(0)).getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().getSize() == 28) {
                    return lbl;
                }
            }
        }
        return null;
    }
    
    private JPanel createSummaryCard(String title, String value, Color bgColor, String emoji) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        // Icon with colored background
        JLabel lblIconBg = new JLabel(emoji);
        lblIconBg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblIconBg.setOpaque(true);
        lblIconBg.setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 30));
        lblIconBg.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        content.add(lblIconBg, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(100, 110, 120));
        content.add(lblTitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(new Color(40, 50, 60));
        content.add(lblValue, gbc);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTransaksiButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 15));
        panel.setBackground(new Color(245, 247, 250));
        
        // Card Transaksi
        JPanel cardTransaksi = createActionCard(
            "TRANSAKSI BARU",
            "Proses penjualan produk",
            new Color(46, 204, 113),
            "🛒",
            e -> openTransaksi()
        );
        
        // Card Refresh
        JPanel cardRefresh = createActionCard(
            "REFRESH DATA",
            "Perbarui laporan terbaru",
            new Color(52, 152, 219),
            "🔄",
            e -> {
                loadLaporanHariIni();
                JOptionPane.showMessageDialog(KasirDashboard.this,
                    "Data berhasil diperbarui!",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        );
        
        panel.add(cardTransaksi);
        panel.add(cardRefresh);
        
        return panel;
    }
    
    private JPanel createActionCard(String title, String desc, Color bgColor, String emoji, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(bgColor);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        
        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblEmoji.setHorizontalAlignment(JLabel.CENTER);
        content.add(lblEmoji, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        content.add(lblTitle, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(240, 245, 250));
        lblDesc.setHorizontalAlignment(JLabel.CENTER);
        content.add(lblDesc, gbc);
        
        card.add(content, BorderLayout.CENTER);
        
        // Click listener with hover effect
        card.addMouseListener(new MouseAdapter() {
            private Color originalColor = bgColor;
            private Color hoverColor = bgColor.darker();
            
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverColor);
                content.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(originalColor);
                content.setBackground(originalColor);
            }
        });
        
        return card;
    }
    
    private JPanel createTableTransaksiHariIni() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblTableTitle = new JLabel("📋 Transaksi Hari Ini - " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(new Color(40, 50, 60));
        
        headerPanel.add(lblTableTitle, BorderLayout.WEST);
        
        // Table
        String[] columns = {"No", "Waktu", "Kode Transaksi", "Total", "Bayar", "Kembalian", "Metode"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableTransaksiHariIni = new JTable(tableModel);
        tableTransaksiHariIni.setRowHeight(35);
        tableTransaksiHariIni.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTransaksiHariIni.setGridColor(new Color(235, 238, 242));
        tableTransaksiHariIni.setSelectionBackground(new Color(52, 152, 219, 40));
        tableTransaksiHariIni.setSelectionForeground(new Color(40, 50, 60));
        
        // Header styling
        JTableHeader header = tableTransaksiHariIni.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(60, 70, 80));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 225, 230)));
        
        // Column widths
        tableTransaksiHariIni.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableTransaksiHariIni.getColumnModel().getColumn(1).setPreferredWidth(90);
        tableTransaksiHariIni.getColumnModel().getColumn(2).setPreferredWidth(170);
        tableTransaksiHariIni.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableTransaksiHariIni.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableTransaksiHariIni.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableTransaksiHariIni.getColumnModel().getColumn(6).setPreferredWidth(90);
        
        // Center alignment for specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableTransaksiHariIni.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableTransaksiHariIni.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tableTransaksiHariIni.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(tableTransaksiHariIni);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(0, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JLabel lblFooter = new JLabel("Toko Bangunan Maju Jaya | Sistem Kasir v1.0");
        lblFooter.setForeground(new Color(189, 195, 199));
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel lblUser = new JLabel("Kasir: " + currentUser.getNamaLengkap());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        panel.add(lblFooter, BorderLayout.WEST);
        panel.add(lblUser, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadLaporanHariIni() {
        // Get tanggal hari ini
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startOfDay = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endOfDay = cal.getTime();
        
        // Ambil transaksi kasir ini hari ini
        List<Transaksi> transaksiList = Transaksi.getTransaksiByKasir(
            currentUser.getId(), 
            startOfDay, 
            endOfDay
        );
        
        // Update tabel
        tableModel.setRowCount(0);
        double totalPenjualan = 0;
        double totalKeuntungan = 0;
        int no = 1;
        
        for (Transaksi t : transaksiList) {
            Object[] row = {
                no++,
                sdf.format(t.getTanggal()),
                t.getKodeTransaksi(),
                String.format("Rp %,d", (long)t.getTotalBelanja()),
                String.format("Rp %,d", (long)t.getBayar()),
                String.format("Rp %,d", (long)t.getKembalian()),
                t.getMetodePembayaran()
            };
            tableModel.addRow(row);
            
            totalPenjualan += t.getTotalBelanja();
            
            // Hitung estimasi keuntungan (asumsi margin 30%)
            totalKeuntungan += t.getTotalBelanja() * 0.3;
        }
        
        // Update summary cards
        if (lblTotalTransaksi != null) {
            lblTotalTransaksi.setText(String.valueOf(transaksiList.size()) + " transaksi");
        }
        if (lblTotalPenjualan != null) {
            lblTotalPenjualan.setText(String.format("Rp %,d", (long)totalPenjualan));
        }
        if (lblTotalKeuntungan != null) {
            lblTotalKeuntungan.setText(String.format("Rp %,d", (long)totalKeuntungan));
        }
    }
    
    private void startAutoRefresh() {
        // Timer untuk auto refresh setiap 30 detik
        Timer timer = new Timer(30000, e -> loadLaporanHariIni());
        timer.start();
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
        
        // Menu Transaksi
        JMenu menuTransaksi = new JMenu("Transaksi");
        menuTransaksi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem itemPenjualan = new JMenuItem("Penjualan Baru");
        JMenuItem itemRiwayat = new JMenuItem("Riwayat Transaksi Saya");
        itemPenjualan.addActionListener(e -> openTransaksi());
//        itemRiwayat.addActionListener(e -> openRiwayatTransaksiKasir());
        menuTransaksi.add(itemPenjualan);
        menuTransaksi.add(itemRiwayat);
        
        // Menu Laporan
        JMenu menuLaporan = new JMenu("Laporan");
        menuLaporan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem itemLapHariIni = new JMenuItem("Laporan Hari Ini");
        itemLapHariIni.addActionListener(e -> {
            loadLaporanHariIni();
            JOptionPane.showMessageDialog(this, "Data telah diperbarui!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
        });
        menuLaporan.add(itemLapHariIni);
        
        // Menu Akun
        JMenu menuAkun = new JMenu("Akun");
        menuAkun.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JMenuItem itemProfile = new JMenuItem("Profil Saya");
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemProfile.addActionListener(e -> openProfile());
        itemLogout.addActionListener(e -> logout());
        menuAkun.add(itemProfile);
        menuAkun.addSeparator();
        menuAkun.add(itemLogout);
        
        menuBar.add(menuTransaksi);
        menuBar.add(menuLaporan);
        menuBar.add(menuAkun);
        
        setJMenuBar(menuBar);
    }
    
    // Menu Actions
    private void openTransaksi() {
        new FormTransaksi(this, currentUser).setVisible(true);
        // Refresh data setelah transaksi ditutup
        loadLaporanHariIni();
    }
    
//    private void openRiwayatTransaksiKasir() {
//        new FormRiwayatTransaksi(this, currentUser).setVisible(true);
//    }
    
    private void openProfile() {
        JOptionPane.showMessageDialog(this,
            "Nama: " + currentUser.getNamaLengkap() + "\n" +
            "Username: " + currentUser.getUsername() + "\n" +
            "Role: " + currentUser.getRole() + "\n" +
            "Alamat: " + (currentUser.getAlamat() != null ? currentUser.getAlamat() : "-") + "\n" +
            "No. Telepon: " + (currentUser.getNoTelepon() != null ? currentUser.getNoTelepon() : "-"),
            "Profil Saya",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}