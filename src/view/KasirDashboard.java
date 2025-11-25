package view;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dashboard Kasir - Akses Terbatas (Transaksi & Laporan Shift)
 */
public class KasirDashboard extends JFrame {
    
    private User currentUser;
    private JMenuBar menuBar;
    private JPanel panelMain;
    private JLabel lblWelcome;
    private JLabel lblInfo;
    
    public KasirDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kasir Dashboard - Kasir Toko Bangunan");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Menu Bar
        createMenuBar();
        
        // Main Panel
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());
        
        // Header Panel
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(155, 89, 182));
        panelHeader.setPreferredSize(new Dimension(1000, 100));
        panelHeader.setLayout(new BorderLayout());
        
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(new Color(155, 89, 182));
        headerContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        lblWelcome = new JLabel("Selamat Datang, " + currentUser.getNamaLengkap());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        
        lblInfo = new JLabel("Role: Kasir | Akses: Transaksi & Laporan Shift");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfo.setForeground(new Color(236, 240, 241));
        
        headerContent.add(lblWelcome);
        headerContent.add(Box.createVerticalStrut(5));
        headerContent.add(lblInfo);
        
        panelHeader.add(headerContent, BorderLayout.WEST);
        
        // Content Panel - Dashboard Cards
        JPanel panelContent = new JPanel();
        panelContent.setLayout(new GridLayout(1, 2, 30, 30));
        panelContent.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panelContent.setBackground(new Color(236, 240, 241));
        
        // Card 1: Transaksi
        JPanel cardTransaksi = createDashboardCard(
            "TRANSAKSI",
            "Proses penjualan kasir",
            new Color(46, 204, 113),
            e -> openTransaksi()
        );
        
        // Card 2: Laporan Shift
//        JPanel cardLaporan = createDashboardCard(
//            "LAPORAN SHIFT",
//            "Lihat penjualan shift Anda",
//            new Color(52, 152, 219),
//            e -> openLaporanShift()
//        );
//        
        panelContent.add(cardTransaksi);
//        panelContent.add(cardLaporan);
        
        // Footer Panel
        JPanel panelFooter = new JPanel();
        panelFooter.setBackground(new Color(44, 62, 80));
        panelFooter.setPreferredSize(new Dimension(1000, 50));
        
        JLabel lblFooter = new JLabel("Toko Bangunan Maju Jaya | Sistem Kasir v1.0");
        lblFooter.setForeground(Color.WHITE);
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 12));
        panelFooter.add(lblFooter);
        
        // Add to main
        panelMain.add(panelHeader, BorderLayout.NORTH);
        panelMain.add(panelContent, BorderLayout.CENTER);
        panelMain.add(panelFooter, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Transaksi
        JMenu menuTransaksi = new JMenu("Transaksi");
        JMenuItem itemPenjualan = new JMenuItem("Penjualan Baru");
        JMenuItem itemRiwayat = new JMenuItem("Riwayat Transaksi Saya");
        itemPenjualan.addActionListener(e -> openTransaksi());
//        itemRiwayat.addActionListener(e -> openRiwayatTransaksiKasir());
        menuTransaksi.add(itemPenjualan);
        menuTransaksi.add(itemRiwayat);
        
        // Menu Laporan
        JMenu menuLaporan = new JMenu("Laporan");
        JMenuItem itemLapShift = new JMenuItem("Laporan Shift Saya");
//        itemLapShift.addActionListener(e -> openLaporanShift());
        menuLaporan.add(itemLapShift);
        
        // Menu Akun
        JMenu menuAkun = new JMenu("Akun");
        JMenuItem itemProfile = new JMenuItem("Profil Saya");
        JMenuItem itemGantiPassword = new JMenuItem("Ganti Password");
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemProfile.addActionListener(e -> openProfile());
//        itemGantiPassword.addActionListener(e -> openGantiPassword());
        itemLogout.addActionListener(e -> logout());
        menuAkun.add(itemProfile);
        menuAkun.add(itemGantiPassword);
        menuAkun.addSeparator();
        menuAkun.add(itemLogout);
        
        menuBar.add(menuTransaksi);
        menuBar.add(menuLaporan);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAkun);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createDashboardCard(String title, String desc, Color bgColor, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgColor);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 16));
        lblDesc.setForeground(new Color(236, 240, 241));
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(Box.createVerticalGlue());
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(15));
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
    private void openTransaksi() {
        new FormTransaksi(this, currentUser).setVisible(true);
    }
    
//    private void openRiwayatTransaksiKasir() {
//        new FormRiwayatTransaksi(this, currentUser).setVisible(true);
//    }
    
//    private void openLaporanShift() {
//        new FormLaporanShift(this, currentUser).setVisible(true);
//    }
    
    private void openProfile() {
        JOptionPane.showMessageDialog(this,
            "Nama: " + currentUser.getNamaLengkap() + "\n" +
            "Username: " + currentUser.getUsername() + "\n" +
            "Role: " + currentUser.getRole(),
            "Profil Saya",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
//    private void openGantiPassword() {
//        new FormGantiPassword(this, currentUser).setVisible(true);
//    }
    
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