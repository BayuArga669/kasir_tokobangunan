package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.border.TitledBorder;

public class FormLaporanPenjualan extends JFrame {

    private JDateChooser dateFrom, dateTo;
    private JButton btnTampilkan;
    private JTable tableTransaksi, tableDetail;
    private DefaultTableModel tableTransaksiModel, tableDetailModel;
    private JLabel lblTotalTransaksi, lblTotalPenjualan, lblRataRata;
    private JLabel lblTotalTunai, lblTotalQRIS, lblTotalTransfer;
    private JComboBox<String> cmbMetodePembayaran;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public FormLaporanPenjualan(JFrame parent) {
        setTitle("Laporan Penjualan");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        initComponents();
        setDefaultPeriod();
        setupDateRestrictions(); // Tambahkan setup date restrictions
        
        // Load data after window is visible
        SwingUtilities.invokeLater(this::loadLaporan);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("📊 LAPORAN PENJUALAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));

        filterPanel.add(new JLabel("Periode:"));
        dateFrom = new JDateChooser();
        dateFrom.setPreferredSize(new Dimension(120, 30));
        dateFrom.setDateFormatString("dd/MM/yyyy");
        filterPanel.add(dateFrom);

        filterPanel.add(new JLabel("s/d"));
        dateTo = new JDateChooser();
        dateTo.setPreferredSize(new Dimension(120, 30));
        dateTo.setDateFormatString("dd/MM/yyyy");
        filterPanel.add(dateTo);

        filterPanel.add(new JLabel("Metode:"));
        cmbMetodePembayaran = new JComboBox<>(new String[]{"Semua", "Tunai", "QRIS", "Transfer"});
        cmbMetodePembayaran.setPreferredSize(new Dimension(100, 30));
        filterPanel.add(cmbMetodePembayaran);

        btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.addActionListener(e -> loadLaporan());
        filterPanel.add(btnTampilkan);

        // Summary Panel - Moved to top
        JPanel summaryPanel = createSummaryPanel();

        // Tables
        tableTransaksiModel = new DefaultTableModel(new String[]{"Kode", "Tanggal", "Kasir", "Metode", "Total", "Bayar", "Kembalian", "Item"}, 0);
        tableTransaksi = new JTable(tableTransaksiModel);
        JScrollPane scrollTransaksi = new JScrollPane(tableTransaksi);
        scrollTransaksi.setBorder(BorderFactory.createTitledBorder("Daftar Transaksi"));

        tableDetailModel = new DefaultTableModel(new String[]{"Produk", "Satuan", "Qty", "Harga", "Subtotal"}, 0);
        tableDetail = new JTable(tableDetailModel);
        JScrollPane scrollDetail = new JScrollPane(tableDetail);
        scrollDetail.setBorder(BorderFactory.createTitledBorder("Detail Item"));

        // Add mouse listener for detail
        tableTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableTransaksi.getSelectedRow();
                if (row != -1) {
                    String kode = (String) tableTransaksiModel.getValueAt(row, 0);
                    loadDetail(kode);
                }
            }
        });

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTransaksi, scrollDetail);
        splitPane.setDividerLocation(400);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(summaryPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Create summary cards
        lblTotalTransaksi = createCard(panel, "TOTAL TRANSAKSI", new Color(52, 152, 219));
        lblTotalPenjualan = createCard(panel, "TOTAL PENJUALAN", new Color(46, 204, 113));
        lblRataRata = createCard(panel, "RATA-RATA", new Color(230, 126, 34));
        lblTotalTunai = createCard(panel, "TOTAL TUNAI", new Color(155, 89, 182));
        lblTotalQRIS = createCard(panel, "TOTAL QRIS", new Color(243, 156, 18));
        lblTotalTransfer = createCard(panel, "TOTAL TRANSFER", new Color(52, 73, 94));

        return panel;
    }

    private JLabel createCard(JPanel parent, String title, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        parent.add(card);

        return valueLabel;
    }

    private void setDefaultPeriod() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateFrom.setDate(cal.getTime());
        dateTo.setDate(new Date());
    }

    // Tambahkan method untuk setup date restrictions
    private void setupDateRestrictions() {
        // Listener untuk dateFrom (tanggal awal)
        dateFrom.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null) {
                    Date selectedDate = (Date) evt.getNewValue();
                    // Set minimal selectable date untuk dateTo
                    dateTo.setMinSelectableDate(selectedDate);
                    
                    // Jika dateTo saat ini lebih awal dari dateFrom, update dateTo
                    if (dateTo.getDate() != null && dateTo.getDate().before(selectedDate)) {
                        dateTo.setDate(selectedDate);
                    }
                }
            }
        });

        // Listener untuk dateTo (tanggal akhir)
        dateTo.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null) {
                    Date selectedDate = (Date) evt.getNewValue();
                    // Set maximal selectable date untuk dateFrom
                    dateFrom.setMaxSelectableDate(selectedDate);
                }
            }
        });

        // Set initial restrictions
        if (dateFrom.getDate() != null) {
            dateTo.setMinSelectableDate(dateFrom.getDate());
        }
        if (dateTo.getDate() != null) {
            dateFrom.setMaxSelectableDate(dateTo.getDate());
        }
    }

    private void loadLaporan() {
        if (dateFrom.getDate() == null || dateTo.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal terlebih dahulu");
            return;
        }

        Date dari = dateFrom.getDate();
        Date sampai = dateTo.getDate();

        // Clear tables
        tableTransaksiModel.setRowCount(0);
        tableDetailModel.setRowCount(0);

        // Get transactions
        List<Transaksi> allTransaksi = Transaksi.getTransaksiByPeriode(dari, sampai);
        
        // Calculate totals
        double totalTunai = 0, totalQRIS = 0, totalTransfer = 0;
        for (Transaksi t : allTransaksi) {
            String metode = t.getMetodePembayaran().trim().toLowerCase();
            if (metode.equals("tunai")) {
                totalTunai += t.getTotalBelanja();
            } else if (metode.equals("qris")) {
                totalQRIS += t.getTotalBelanja();
            } else if (metode.equals("transfer")) {
                totalTransfer += t.getTotalBelanja();
            }
        }

        // Filter by payment method
        List<Transaksi> transaksiList = new ArrayList<>(allTransaksi);
        String metodeFilter = (String) cmbMetodePembayaran.getSelectedItem();
        if (metodeFilter != null && !metodeFilter.equals("Semua")) {
            transaksiList.removeIf(t -> !t.getMetodePembayaran().equals(metodeFilter));
        }

        // Display transactions
        double totalPenjualan = 0;
        for (Transaksi t : transaksiList) {
            List<DetailTransaksi> details = Transaksi.getDetailTransaksi(t.getId());
            
            Object[] row = {
                t.getKodeTransaksi(),
                sdfFull.format(t.getTanggal()),
                t.getNamaKasir(),
                t.getMetodePembayaran(),
                String.format("Rp %,.0f", t.getTotalBelanja()),
                String.format("Rp %,.0f", t.getBayar()),
                String.format("Rp %,.0f", t.getKembalian()),
                details.size() + " item"
            };
            tableTransaksiModel.addRow(row);
            totalPenjualan += t.getTotalBelanja();
        }

        // Update summary labels
        lblTotalTransaksi.setText(String.valueOf(transaksiList.size()));
        lblTotalPenjualan.setText(String.format("Rp %,.0f", totalPenjualan));
        
        if (transaksiList.size() > 0) {
            double rataRata = totalPenjualan / transaksiList.size();
            lblRataRata.setText(String.format("Rp %,.0f", rataRata));
        } else {
            lblRataRata.setText("Rp 0");
        }
        
        lblTotalTunai.setText(String.format("Rp %,.0f", totalTunai));
        lblTotalQRIS.setText(String.format("Rp %,.0f", totalQRIS));
        lblTotalTransfer.setText(String.format("Rp %,.0f", totalTransfer));
    }

    private void loadDetail(String kodeTransaksi) {
        tableDetailModel.setRowCount(0);
        
        Date dari = dateFrom.getDate();
        Date sampai = dateTo.getDate();
        List<Transaksi> transaksiList = Transaksi.getTransaksiByPeriode(dari, sampai);
        
        for (Transaksi t : transaksiList) {
            if (t.getKodeTransaksi().equals(kodeTransaksi)) {
                List<DetailTransaksi> details = Transaksi.getDetailTransaksi(t.getId());
                
                for (DetailTransaksi d : details) {
                    Object[] row = {
                        d.getNamaProduk(),
                        d.getNamaSatuan(),
                        String.format("%.0f", d.getQty()),
                        String.format("Rp %,.0f", d.getHargaSatuan()),
                        String.format("Rp %,.0f", d.getSubtotal())
                    };
                    tableDetailModel.addRow(row);
                }
                break;
            }
        }
    }
}