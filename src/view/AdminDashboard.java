package view;

import model.User;
import model.Produk;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dashboard Admin - Akses Penuh
 */
public class AdminDashboard extends JFrame {
    
    private User currentUser;
    private JMenuBar menuBar;
    private JPanel panelMain;
    private JLabel lblWelcome;
    private JLabel lblInfo;
    
    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Admin Dashboard - Kasir Toko Bangunan");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Menu Bar
        createMenuBar();
        
        // Main Panel
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());
        
        // Header Panel
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(41, 128, 185));
        panelHeader.setPreferredSize(new Dimension(1200, 100));
        panelHeader.setLayout(new BorderLayout());
        
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(new Color(41, 128, 185));
        headerContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        lblWelcome = new JLabel("Selamat Datang, " + currentUser.getNamaLengkap());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        
        lblInfo = new JLabel("Role: Administrator | Akses: Semua Menu");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(236, 240, 241));
        
        headerContent.add(lblWelcome);
        headerContent.add(Box.createVerticalStrut(5));
        headerContent.add(lblInfo);
        
        panelHeader.add(headerContent, BorderLayout.WEST);
        
        // Content Panel - Dashboard Cards
        JPanel panelContent = new JPanel();
        panelContent.setLayout(new GridLayout(2, 3, 20, 20));
        panelContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panelContent.setBackground(new Color(236, 240, 241));
        
        // Card 1: Master Produk
        JPanel cardProduk = createDashboardCard(
            "Master Produk",
            "Kelola data produk & satuan jual",
            new Color(52, 152, 219),
            e -> openMasterProduk()
        );
        
        // Card 2: Manajemen Stok
        JPanel cardStok = createDashboardCard(
            "Manajemen Stok",
            "Restock & stok opname",
            new Color(46, 204, 113),
            e -> openManajemenStok()
        );
        
        // Card 3: Transaksi
        JPanel cardTransaksi = createDashboardCard(
            "Transaksi",
            "Proses penjualan kasir",
            new Color(155, 89, 182),
            e -> openTransaksi()
        );
        
        // Card 4: Laporan
        JPanel cardLaporan = createDashboardCard(
            "Laporan",
            "Penjualan, laba & stok",
            new Color(230, 126, 34),
            e -> openLaporan()
        );
        
        // Card 5: Kategori
        JPanel cardKategori = createDashboardCard(
            "Kategori",
            "Kelola kategori produk",
            new Color(26, 188, 156),
            e -> openKategori()
        );
        
        // Card 6: Pengaturan
        JPanel cardPengaturan = createDashboardCard(
            "Pengaturan",
            "User & info toko",
            new Color(52, 73, 94),
            e -> openPengaturan()
        );
        
        panelContent.add(cardProduk);
        panelContent.add(cardStok);
        panelContent.add(cardTransaksi);
        panelContent.add(cardLaporan);
        panelContent.add(cardKategori);
        panelContent.add(cardPengaturan);
        
        // Add to main
        panelMain.add(panelHeader, BorderLayout.NORTH);
        panelMain.add(panelContent, BorderLayout.CENTER);
        
        add(panelMain);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Master Data
        JMenu menuMaster = new JMenu("Master Data");
        JMenuItem itemProduk = new JMenuItem("Produk");
        JMenuItem itemKategori = new JMenuItem("Kategori");
        itemProduk.addActionListener(e -> openMasterProduk());
        itemKategori.addActionListener(e -> openKategori());
        menuMaster.add(itemProduk);
        menuMaster.add(itemKategori);
        
        // Menu Stok
        JMenu menuStok = new JMenu("Manajemen Stok");
        JMenuItem itemBarangMasuk = new JMenuItem("Barang Masuk");
        JMenuItem itemStokOpname = new JMenuItem("Stok Opname");
        JMenuItem itemLihatStok = new JMenuItem("Lihat Stok");
        itemBarangMasuk.addActionListener(e -> openBarangMasuk());
        itemStokOpname.addActionListener(e -> openStokOpname());
        itemLihatStok.addActionListener(e -> openLihatStok());
        menuStok.add(itemBarangMasuk);
        menuStok.add(itemStokOpname);
        menuStok.addSeparator();
        menuStok.add(itemLihatStok);
        
        // Menu Transaksi
        JMenu menuTransaksi = new JMenu("Transaksi");
        JMenuItem itemPenjualan = new JMenuItem("Penjualan Baru");
        JMenuItem itemRiwayat = new JMenuItem("Riwayat Transaksi");
        itemPenjualan.addActionListener(e -> openTransaksi());
        itemRiwayat.addActionListener(e -> openRiwayatTransaksi());
        menuTransaksi.add(itemPenjualan);
        menuTransaksi.add(itemRiwayat);
        
        // Menu Laporan
        JMenu menuLaporan = new JMenu("Laporan");
        JMenuItem itemLapPenjualan = new JMenuItem("Laporan Penjualan");
        JMenuItem itemLapLaba = new JMenuItem("Laporan Laba");
        JMenuItem itemLapStok = new JMenuItem("Laporan Stok");
        itemLapPenjualan.addActionListener(e -> openLaporanPenjualan());
        itemLapLaba.addActionListener(e -> openLaporanLaba());
        itemLapStok.addActionListener(e -> openLaporanStok());
        menuLaporan.add(itemLapPenjualan);
        menuLaporan.add(itemLapLaba);
        menuLaporan.add(itemLapStok);
        
        // Menu Pengaturan
        JMenu menuPengaturan = new JMenu("Pengaturan");
        JMenuItem itemUser = new JMenuItem("Manajemen User");
        JMenuItem itemToko = new JMenuItem("Info Toko");
        itemUser.addActionListener(e -> openManajemenUser());
        itemToko.addActionListener(e -> openInfoToko());
        menuPengaturan.add(itemUser);
        menuPengaturan.add(itemToko);
        
        // Menu Akun
        JMenu menuAkun = new JMenu("Akun");
        JMenuItem itemProfile = new JMenuItem("Profil Saya");
        JMenuItem itemGantiPassword = new JMenuItem("Ganti Password");
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemProfile.addActionListener(e -> openProfile());
        itemGantiPassword.addActionListener(e -> openGantiPassword());
        itemLogout.addActionListener(e -> logout());
        menuAkun.add(itemProfile);
        menuAkun.add(itemGantiPassword);
        menuAkun.addSeparator();
        menuAkun.add(itemLogout);
        
        menuBar.add(menuMaster);
        menuBar.add(menuStok);
        menuBar.add(menuTransaksi);
        menuBar.add(menuLaporan);
        menuBar.add(menuPengaturan);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAkun);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createDashboardCard(String title, String desc, Color bgColor, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgColor);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(236, 240, 241));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(Box.createVerticalGlue());
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(10));
        content.add(lblDesc);
        content.add(Box.createVerticalGlue());
        
        card.add(content, BorderLayout.CENTER);
        
        // Click listener
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(bgColor.darker());
                content.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(bgColor);
                content.setBackground(bgColor);
            }
        });
        
        return card;
    }
    
    // Menu Actions
    private void openMasterProduk() {
        new FormProduk(this, currentUser).setVisible(true);
    }
    
    private void openKategori() {
        new FormKategori(this).setVisible(true);
    }
    
    private void openManajemenStok() {
        new FormTambahStok(this, currentUser).setVisible(true);
    }
    
    private void openBarangMasuk() {
        new FormTambahStok(this, currentUser).setVisible(true);
    }

    
    private void openStokOpname() {
        new FormStokOpname(this, currentUser).setVisible(true);
    }
    
    private void openLihatStok() {
        new FormLihatStok(this).setVisible(true);
    }
    
    private void openTransaksi() {
        new FormTransaksi(this, currentUser).setVisible(true);
    }
    
    private void openRiwayatTransaksi() {
        new FormRiwayatTransaksi(this).setVisible(true);
    }
    
    private void openLaporan() {
        new FormLaporanLaba(this).setVisible(true);
    }
    
    private void openLaporanPenjualan() {
        new FormLaporanPenjualan(this).setVisible(true);
    }
    
    private void openLaporanLaba() {
        new FormLaporanLaba(this).setVisible(true);
    }
    
    private void openLaporanStok() {
        new FormLaporanStok(this).setVisible(true);
    }
    
    private void openPengaturan() {
      new FormManajemenUser(this).setVisible(true);
  }

    private void openManajemenUser() {
        new FormManajemenUser(this).setVisible(true);
    }
    
    private void openInfoToko() {
        new FormInfoToko(this).setVisible(true);
    }
    
    private void openProfile() {
        JOptionPane.showMessageDialog(this,
            "Nama: " + currentUser.getNamaLengkap() + "\n" +
            "Username: " + currentUser.getUsername() + "\n" +
            "Role: " + currentUser.getRole(),
            "Profil Saya",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openGantiPassword() {
        new FormGantiPassword(this, currentUser).setVisible(true);
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