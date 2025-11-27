package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Form Laporan Penjualan dengan Filter Periode
 */
public class FormLaporanPenjualan extends JFrame {
    
    private JDateChooser dateFrom, dateTo;
    private JButton btnTampilkan, btnExport, btnPrint;
    private JTable tableTransaksi, tableDetail;
    private DefaultTableModel tableTransaksiModel, tableDetailModel;
    private JLabel lblTotalTransaksi, lblTotalPenjualan, lblRataRata;
    private JComboBox<String> cmbMetodePembayaran;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public FormLaporanPenjualan(JFrame parent) {
        initComponents();
        setDefaultPeriod();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Laporan Penjualan");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        
        // Content Panel (Split: Transaksi & Detail)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setTopComponent(createTransaksiPanel());
        splitPane.setBottomComponent(createDetailPanel());
        
        // Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("📊 LAPORAN PENJUALAN");
        lblTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("Analisis Transaksi & Pendapatan");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 240, 255));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(lblSubtitle);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblPeriode = new JLabel("Periode:");
        lblPeriode.setFont(new Font("Arial", Font.BOLD, 13));
        
        dateFrom = new JDateChooser();
        dateFrom.setPreferredSize(new Dimension(140, 30));
        dateFrom.setDateFormatString("dd/MM/yyyy");
        
        JLabel lblSampai = new JLabel("s/d");
        lblSampai.setFont(new Font("Arial", Font.BOLD, 12));
        
        dateTo = new JDateChooser();
        dateTo.setPreferredSize(new Dimension(140, 30));
        dateTo.setDateFormatString("dd/MM/yyyy");
        
        JLabel lblMetode = new JLabel("Metode Bayar:");
        lblMetode.setFont(new Font("Arial", Font.BOLD, 13));
        
        cmbMetodePembayaran = new JComboBox<>(new String[]{"Semua", "Tunai", "QRIS", "Transfer"});
        cmbMetodePembayaran.setPreferredSize(new Dimension(120, 30));
        
        btnTampilkan = new JButton("🔍 TAMPILKAN");
        btnTampilkan.setPreferredSize(new Dimension(140, 35));
        btnTampilkan.setBackground(new Color(46, 204, 113));
        btnTampilkan.setForeground(Color.WHITE);
        btnTampilkan.setFocusPainted(false);
        btnTampilkan.setFont(new Font("Arial", Font.BOLD, 12));
        btnTampilkan.addActionListener(e -> loadLaporan());
        
        btnExport = new JButton("📄 EXPORT");
        btnExport.setPreferredSize(new Dimension(120, 35));
        btnExport.setBackground(new Color(52, 152, 219));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setFont(new Font("Arial", Font.BOLD, 12));
        btnExport.addActionListener(e -> exportToExcel());
        
        btnPrint = new JButton("🖨 CETAK");
        btnPrint.setPreferredSize(new Dimension(120, 35));
        btnPrint.setBackground(new Color(230, 126, 34));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setFont(new Font("Arial", Font.BOLD, 12));
        btnPrint.addActionListener(e -> printLaporan());
        
        panel.add(lblPeriode);
        panel.add(dateFrom);
        panel.add(lblSampai);
        panel.add(dateTo);
        panel.add(lblMetode);
        panel.add(cmbMetodePembayaran);
        panel.add(btnTampilkan);
        panel.add(btnExport);
        panel.add(btnPrint);
        
        return panel;
    }
    
    private JPanel createTransaksiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Daftar Transaksi",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(52, 152, 219)
        ));
        
        String[] columns = {"Kode", "Tanggal", "Kasir", "Metode", "Total", "Bayar", "Kembalian", "Item"};
        tableTransaksiModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableTransaksi = new JTable(tableTransaksiModel);
        tableTransaksi.setRowHeight(28);
        tableTransaksi.setFont(new Font("Arial", Font.PLAIN, 12));
        tableTransaksi.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableTransaksi.getTableHeader().setBackground(new Color(236, 240, 241));
        
        tableTransaksi.getColumnModel().getColumn(0).setPreferredWidth(130);
        tableTransaksi.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableTransaksi.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableTransaksi.getColumnModel().getColumn(3).setPreferredWidth(80);
        tableTransaksi.getColumnModel().getColumn(4).setPreferredWidth(110);
        tableTransaksi.getColumnModel().getColumn(5).setPreferredWidth(110);
        tableTransaksi.getColumnModel().getColumn(6).setPreferredWidth(110);
        tableTransaksi.getColumnModel().getColumn(7).setPreferredWidth(60);
        
        // Click listener untuk menampilkan detail
        tableTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableTransaksi.getSelectedRow();
                if (row != -1) {
                    String kodeTransaksi = (String) tableTransaksiModel.getValueAt(row, 0);
                    loadDetailTransaksi(kodeTransaksi);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Detail Item Transaksi (Klik transaksi untuk melihat)",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(46, 204, 113)
        ));
        
        String[] columns = {"Produk", "Satuan", "Qty", "Harga", "Subtotal"};
        tableDetailModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableDetail = new JTable(tableDetailModel);
        tableDetail.setRowHeight(28);
        tableDetail.setFont(new Font("Arial", Font.PLAIN, 12));
        tableDetail.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableDetail.getTableHeader().setBackground(new Color(236, 240, 241));
        
        tableDetail.getColumnModel().getColumn(0).setPreferredWidth(250);
        tableDetail.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableDetail.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableDetail.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableDetail.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tableDetail);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setBackground(new Color(240, 242, 245));
        panel.setPreferredSize(new Dimension(0, 100));
        
        // Card 1: Total Transaksi
        JPanel card1 = createSummaryCard("Total Transaksi", "0", new Color(52, 152, 219));
        lblTotalTransaksi = (JLabel) ((JPanel)card1.getComponent(0)).getComponent(1);
        
        // Card 2: Total Penjualan
        JPanel card2 = createSummaryCard("Total Penjualan", "Rp 0", new Color(46, 204, 113));
        lblTotalPenjualan = (JLabel) ((JPanel)card2.getComponent(0)).getComponent(1);
        
        // Card 3: Rata-rata per Transaksi
        JPanel card3 = createSummaryCard("Rata-rata/Transaksi", "Rp 0", new Color(230, 126, 34));
        lblRataRata = (JLabel) ((JPanel)card3.getComponent(0)).getComponent(1);
        
        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(color);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(230, 240, 255));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(10));
        content.add(lblValue);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private void setDefaultPeriod() {
        // Set periode default: bulan ini
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateFrom.setDate(cal.getTime());
        dateTo.setDate(new Date());
    }
    
    private void loadLaporan() {
        if (dateFrom.getDate() == null || dateTo.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                "Pilih periode tanggal terlebih dahulu!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Date dari = dateFrom.getDate();
        Date sampai = dateTo.getDate();
        
        if (dari.after(sampai)) {
            JOptionPane.showMessageDialog(this,
                "Tanggal 'Dari' tidak boleh lebih besar dari 'Sampai'!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableTransaksiModel.setRowCount(0);
        tableDetailModel.setRowCount(0);
        
        List<Transaksi> transaksiList = Transaksi.getTransaksiByPeriode(dari, sampai);
        
        // Filter berdasarkan metode pembayaran
        String metodeFilter = (String) cmbMetodePembayaran.getSelectedItem();
        if (!metodeFilter.equals("Semua")) {
            transaksiList.removeIf(t -> !t.getMetodePembayaran().equals(metodeFilter));
        }
        
        double totalPenjualan = 0;
        int jumlahItem = 0;
        
        for (Transaksi t : transaksiList) {
            // Hitung jumlah item
            List<DetailTransaksi> details = Transaksi.getDetailTransaksi(t.getId());
            jumlahItem = details.size();
            
            Object[] row = {
                t.getKodeTransaksi(),
                sdfFull.format(t.getTanggal()),
                t.getNamaKasir(),
                t.getMetodePembayaran(),
                String.format("Rp %,d", (long)t.getTotalBelanja()),
                String.format("Rp %,d", (long)t.getBayar()),
                String.format("Rp %,d", (long)t.getKembalian()),
                jumlahItem + " item"
            };
            tableTransaksiModel.addRow(row);
            
            totalPenjualan += t.getTotalBelanja();
        }
        
        // Update summary
        lblTotalTransaksi.setText(String.valueOf(transaksiList.size()));
        lblTotalPenjualan.setText(String.format("Rp %,d", (long)totalPenjualan));
        
        if (transaksiList.size() > 0) {
            double rataRata = totalPenjualan / transaksiList.size();
            lblRataRata.setText(String.format("Rp %,d", (long)rataRata));
        } else {
            lblRataRata.setText("Rp 0");
        }
    }
    
    private void loadDetailTransaksi(String kodeTransaksi) {
        tableDetailModel.setRowCount(0);
        
        // Cari transaksi berdasarkan kode
        Date dari = dateFrom.getDate();
        Date sampai = dateTo.getDate();
        List<Transaksi> transaksiList = Transaksi.getTransaksiByPeriode(dari, sampai);
        
        Transaksi transaksi = null;
        for (Transaksi t : transaksiList) {
            if (t.getKodeTransaksi().equals(kodeTransaksi)) {
                transaksi = t;
                break;
            }
        }
        
        if (transaksi != null) {
            List<DetailTransaksi> details = Transaksi.getDetailTransaksi(transaksi.getId());
            
            for (DetailTransaksi d : details) {
                Object[] row = {
                    d.getNamaProduk(),
                    d.getNamaSatuan(),
                    String.format("%.0f", d.getQty()),
                    String.format("Rp %,d", (long)d.getHargaSatuan()),
                    String.format("Rp %,d", (long)d.getSubtotal())
                };
                tableDetailModel.addRow(row);
            }
        }
    }
    
    private void exportToExcel() {
        JOptionPane.showMessageDialog(this,
            "Fitur Export ke Excel akan segera hadir!\n\n" +
            "Untuk saat ini, Anda bisa:\n" +
            "1. Copy data dari tabel\n" +
            "2. Paste ke Excel/Spreadsheet",
            "Info",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printLaporan() {
        try {
            // Print tabel transaksi
            boolean complete = tableTransaksi.print(
                JTable.PrintMode.FIT_WIDTH,
                null,
                null
            );
            
            if (complete) {
                JOptionPane.showMessageDialog(this,
                    "Laporan berhasil dicetak!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mencetak laporan!\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}