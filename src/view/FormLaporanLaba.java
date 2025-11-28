package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JDateChooser;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;

/**
 * Form Laporan Laba Rugi - Analisis Keuntungan dengan Grafik
 */
public class FormLaporanLaba extends JFrame {
    
    private JDateChooser dateFrom, dateTo;
    private JButton btnTampilkan, btnExport, btnDetail, btnChartType;
    private JTable tableLaba;
    private DefaultTableModel tableLabaModel;
    private JLabel lblTotalPendapatan, lblTotalModal, lblLabaBersih, lblMarginPersen;
    private JProgressBar progressMargin;
    private JPanel chartPanel;
    private ChartPanel currentChartPanel;
    private boolean isPieChart = false;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private Map<Integer, ProdukLaba> currentProdukLabaMap;
    
    public FormLaporanLaba(JFrame parent) {
        initComponents();
        setDefaultPeriod();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Laporan Laba Rugi & Keuntungan");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Filter
        JPanel filterPanel = createFilterPanel();
        
        // Summary Cards
        JPanel summaryPanel = createSummaryPanel();
        
        // Table
        JPanel tablePanel = createTablePanel();
        
        // Chart Panel
        chartPanel = createChartPanel();
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(chartPanel);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 204, 113));
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("💰 LAPORAN LABA RUGI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("Analisis Keuntungan & Margin Profit");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 255, 240));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(46, 204, 113));
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(lblSubtitle);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
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
        
        btnTampilkan = new JButton("🔍 ANALISIS");
        btnTampilkan.setPreferredSize(new Dimension(140, 35));
        btnTampilkan.setBackground(new Color(46, 204, 113));
        btnTampilkan.setForeground(Color.WHITE);
        btnTampilkan.setFocusPainted(false);
        btnTampilkan.setFont(new Font("Arial", Font.BOLD, 12));
        btnTampilkan.addActionListener(e -> loadLaporan());
        
        btnDetail = new JButton("📊 ANALISIS MARGIN");
        btnDetail.setPreferredSize(new Dimension(160, 35));
        btnDetail.setBackground(new Color(52, 152, 219));
        btnDetail.setForeground(Color.WHITE);
        btnDetail.setFocusPainted(false);
        btnDetail.setFont(new Font("Arial", Font.BOLD, 12));
        btnDetail.addActionListener(e -> openAnalisisMargin());
        
        btnExport = new JButton("📄 EXPORT");
        btnExport.setPreferredSize(new Dimension(120, 35));
        btnExport.setBackground(new Color(230, 126, 34));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setFont(new Font("Arial", Font.BOLD, 12));
        btnExport.addActionListener(e -> exportReport());
        
        panel.add(lblPeriode);
        panel.add(dateFrom);
        panel.add(lblSampai);
        panel.add(dateTo);
        panel.add(btnTampilkan);
        panel.add(btnDetail);
        panel.add(btnExport);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        panel.setBackground(new Color(240, 242, 245));
        panel.setPreferredSize(new Dimension(0, 120));
        
        // Card 1: Total Pendapatan
        JPanel card1 = createSummaryCard("Total Pendapatan", "Rp 0", new Color(52, 152, 219), "💵");
        lblTotalPendapatan = (JLabel) ((JPanel)card1.getComponent(0)).getComponent(2);
        
        // Card 2: Total Modal
        JPanel card2 = createSummaryCard("Total Modal", "Rp 0", new Color(231, 76, 60), "💸");
        lblTotalModal = (JLabel) ((JPanel)card2.getComponent(0)).getComponent(2);
        
        // Card 3: Laba Bersih
        JPanel card3 = createSummaryCard("Laba Bersih", "Rp 0", new Color(46, 204, 113), "💰");
        lblLabaBersih = (JLabel) ((JPanel)card3.getComponent(0)).getComponent(2);
        
        // Card 4: Margin Profit
        JPanel card4 = createMarginCard();
        
        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(color);
        
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 32));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(230, 240, 255));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 22));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        content.add(lblIcon);
        content.add(Box.createVerticalStrut(5));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(lblValue);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMarginCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(155, 89, 182));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(155, 89, 182));
        
        JLabel lblIcon = new JLabel("📈");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 32));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel("Margin Profit");
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(230, 240, 255));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblMarginPersen = new JLabel("0%");
        lblMarginPersen.setFont(new Font("Arial", Font.BOLD, 22));
        lblMarginPersen.setForeground(Color.WHITE);
        lblMarginPersen.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        progressMargin = new JProgressBar(0, 100);
        progressMargin.setValue(0);
        progressMargin.setStringPainted(false);
        progressMargin.setPreferredSize(new Dimension(150, 10));
        progressMargin.setMaximumSize(new Dimension(150, 10));
        progressMargin.setForeground(new Color(46, 204, 113));
        progressMargin.setBackground(new Color(142, 68, 173));
        
        content.add(lblIcon);
        content.add(Box.createVerticalStrut(5));
        content.add(lblTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(lblMarginPersen);
        content.add(Box.createVerticalStrut(8));
        content.add(progressMargin);
        
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Detail Laba per Produk",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(52, 152, 219)
        ));
        
        String[] columns = {"Produk", "Terjual", "Satuan", "Pendapatan", "Modal", "Laba", "Margin %"};
        tableLabaModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableLaba = new JTable(tableLabaModel);
        tableLaba.setRowHeight(30);
        tableLaba.setFont(new Font("Arial", Font.PLAIN, 12));
        tableLaba.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableLaba.getTableHeader().setBackground(new Color(236, 240, 241));
        
        tableLaba.getColumnModel().getColumn(0).setPreferredWidth(200);
        tableLaba.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableLaba.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableLaba.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableLaba.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableLaba.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableLaba.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Custom renderer untuk kolom Margin
        tableLaba.getColumnModel().getColumn(6).setCellRenderer(new MarginRenderer());
        
        JScrollPane scrollPane = new JScrollPane(tableLaba);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Grafik Keuntungan",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            new Color(46, 204, 113)
        ));
        panel.setBackground(Color.WHITE);
        
        // Tombol ganti tipe chart
        btnChartType = new JButton("🔄 Ganti ke Pie Chart");
        btnChartType.setFont(new Font("Arial", Font.BOLD, 11));
        btnChartType.setBackground(new Color(52, 152, 219));
        btnChartType.setForeground(Color.WHITE);
        btnChartType.setFocusPainted(false);
        btnChartType.addActionListener(e -> toggleChartType());
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnChartType);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        
        // Default placeholder
        JLabel lblPlaceholder = new JLabel("<html><center>" +
            "📊<br><br>" +
            "Klik tombol ANALISIS<br>" +
            "untuk menampilkan grafik</center></html>",
            SwingConstants.CENTER);
        lblPlaceholder.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPlaceholder.setForeground(Color.GRAY);
        
        panel.add(lblPlaceholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setDefaultPeriod() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateFrom.setDate(cal.getTime());
        dateTo.setDate(new Date());
    }
    
    private void loadLaporan() {
        if (dateFrom.getDate() == null || dateTo.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                "Pilih periode tanggal!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Date dari = dateFrom.getDate();
        Date sampai = dateTo.getDate();
        
        tableLabaModel.setRowCount(0);
        
        // Ambil semua transaksi dalam periode
        List<Transaksi> transaksiList = Transaksi.getTransaksiByPeriode(dari, sampai);
        
        // Map untuk menyimpan data per produk
        currentProdukLabaMap = new HashMap<>();
        
        double totalPendapatan = 0;
        double totalModal = 0;
        
        for (Transaksi t : transaksiList) {
            List<DetailTransaksi> details = Transaksi.getDetailTransaksi(t.getId());
            
            for (DetailTransaksi detail : details) {
                int produkId = detail.getProdukId();
                
                if (!currentProdukLabaMap.containsKey(produkId)) {
                    currentProdukLabaMap.put(produkId, new ProdukLaba(detail.getNamaProduk()));
                }
                
                ProdukLaba pl = currentProdukLabaMap.get(produkId);
                
                // Ambil data produk untuk mendapatkan harga beli
                Produk produk = Produk.getProdukById(produkId);
                SatuanJual satuan = SatuanJual.getSatuanById(detail.getSatuanJualId());
                
                double hargaBeliPerSatuan = produk.getHargaBeli() * satuan.getKonversiKeDasar();
                double modalItem = detail.getQty() * hargaBeliPerSatuan;
                
                pl.qty += detail.getQty();
                pl.satuan = detail.getNamaSatuan();
                pl.pendapatan += detail.getSubtotal();
                pl.modal += modalItem;
                
                totalPendapatan += detail.getSubtotal();
                totalModal += modalItem;
            }
        }
        
        // Tampilkan di tabel
        for (ProdukLaba pl : currentProdukLabaMap.values()) {
            double laba = pl.pendapatan - pl.modal;
            double marginPersen = pl.modal > 0 ? (laba / pl.modal) * 100 : 0;
            
            Object[] row = {
                pl.namaProduk,
                String.format("%.0f", pl.qty),
                pl.satuan,
                String.format("Rp %,d", (long)pl.pendapatan),
                String.format("Rp %,d", (long)pl.modal),
                String.format("Rp %,d", (long)laba),
                String.format("%.1f%%", marginPersen)
            };
            tableLabaModel.addRow(row);
        }
        
        // Update summary
        double labaBersih = totalPendapatan - totalModal;
        double marginTotal = totalModal > 0 ? (labaBersih / totalModal) * 100 : 0;
        
        lblTotalPendapatan.setText(String.format("Rp %,d", (long)totalPendapatan));
        lblTotalModal.setText(String.format("Rp %,d", (long)totalModal));
        lblLabaBersih.setText(String.format("Rp %,d", (long)labaBersih));
        lblMarginPersen.setText(String.format("%.1f%%", marginTotal));
        progressMargin.setValue((int)Math.min(marginTotal, 100));
        
        // Update warna progress bar berdasarkan margin
        if (marginTotal < 10) {
            progressMargin.setForeground(new Color(231, 76, 60));
        } else if (marginTotal < 20) {
            progressMargin.setForeground(new Color(230, 126, 34));
        } else {
            progressMargin.setForeground(new Color(46, 204, 113));
        }
        
        // Update chart
        updateChart();
    }
    
    private void updateChart() {
        if (currentProdukLabaMap == null || currentProdukLabaMap.isEmpty()) {
            return;
        }
        
        // Remove semua komponen dari chartPanel kecuali button
        Component[] components = chartPanel.getComponents();
        for (Component comp : components) {
            if (!(comp instanceof JPanel && ((JPanel)comp).getComponentCount() > 0 
                  && ((JPanel)comp).getComponent(0) instanceof JButton)) {
                chartPanel.remove(comp);
            }
        }
        
        if (currentChartPanel != null) {
            chartPanel.remove(currentChartPanel);
        }
        
        // Buat chart sesuai tipe
        JFreeChart chart;
        if (isPieChart) {
            chart = createPieChart();
        } else {
            chart = createBarChart();
        }
        
        currentChartPanel = new ChartPanel(chart);
        currentChartPanel.setPreferredSize(new Dimension(450, 500));
        
        chartPanel.add(currentChartPanel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    private JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Sort berdasarkan laba tertinggi (ambil top 10)
        List<ProdukLaba> sortedList = new ArrayList<>(currentProdukLabaMap.values());
        sortedList.sort((a, b) -> Double.compare(b.pendapatan - b.modal, a.pendapatan - a.modal));
        
        int count = 0;
        for (ProdukLaba pl : sortedList) {
            if (count >= 10) break;
            
            double laba = pl.pendapatan - pl.modal;
            String namaProduk = pl.namaProduk.length() > 15 ? 
                pl.namaProduk.substring(0, 15) + "..." : pl.namaProduk;
            
            dataset.addValue(pl.pendapatan / 1000, "Pendapatan", namaProduk);
            dataset.addValue(pl.modal / 1000, "Modal", namaProduk);
            dataset.addValue(laba / 1000, "Laba", namaProduk);
            
            count++;
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Top 10 Produk - Analisis Keuntungan",
            "Produk",
            "Nilai (Ribu Rupiah)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Styling
        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setRangeGridlinePaint(Color.GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219));  // Pendapatan - Biru
        renderer.setSeriesPaint(1, new Color(231, 76, 60));   // Modal - Merah
        renderer.setSeriesPaint(2, new Color(46, 204, 113));  // Laba - Hijau
        
        // Rotate label kategori
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        return chart;
    }
    
    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Sort berdasarkan laba tertinggi (ambil top 8 untuk pie chart)
        List<ProdukLaba> sortedList = new ArrayList<>(currentProdukLabaMap.values());
        sortedList.sort((a, b) -> Double.compare(b.pendapatan - b.modal, a.pendapatan - a.modal));
        
        int count = 0;
        double otherLaba = 0;
        
        for (ProdukLaba pl : sortedList) {
            double laba = pl.pendapatan - pl.modal;
            
            if (count < 8) {
                String label = pl.namaProduk.length() > 15 ? 
                    pl.namaProduk.substring(0, 15) + "..." : pl.namaProduk;
                dataset.setValue(label, laba);
            } else {
                otherLaba += laba;
            }
            count++;
        }
        
        if (otherLaba > 0) {
            dataset.setValue("Lainnya", otherLaba);
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Kontribusi Laba per Produk",
            dataset,
            true,
            true,
            false
        );
        
        // Styling
        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(248, 249, 250));
        plot.setOutlinePaint(null);
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 11));
        
        // Custom colors
        Color[] colors = {
            new Color(46, 204, 113),
            new Color(52, 152, 219),
            new Color(155, 89, 182),
            new Color(230, 126, 34),
            new Color(231, 76, 60),
            new Color(26, 188, 156),
            new Color(241, 196, 15),
            new Color(149, 165, 166),
            new Color(127, 140, 141)
        };
        
        int i = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable) key, colors[i % colors.length]);
            i++;
        }
        
        return chart;
    }
    
    private void toggleChartType() {
        isPieChart = !isPieChart;
        
        if (isPieChart) {
            btnChartType.setText("🔄 Ganti ke Bar Chart");
        } else {
            btnChartType.setText("🔄 Ganti ke Pie Chart");
        }
        
        updateChart();
    }
    
    private void openAnalisisMargin() {
        new FormAnalisisMargin(this).setVisible(true);
    }
    
    private void exportReport() {
        JOptionPane.showMessageDialog(this,
            "Fitur Export akan segera hadir!",
            "Info",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Helper class
    class ProdukLaba {
        String namaProduk;
        double qty = 0;
        String satuan = "";
        double pendapatan = 0;
        double modal = 0;
        
        ProdukLaba(String nama) {
            this.namaProduk = nama;
        }
    }
    
    // Custom renderer untuk margin
    class MarginRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            
            if (value != null) {
                String marginStr = value.toString().replace("%", "");
                try {
                    double margin = Double.parseDouble(marginStr);
                    
                    if (!isSelected) {
                        if (margin > 40) {
                            c.setForeground(new Color(39, 174, 96));
                            c.setFont(new Font("Arial", Font.BOLD, 12));
                        } else if (margin > 20) {
                            c.setForeground(new Color(41, 128, 185));
                        } else if (margin > 10) {
                            c.setForeground(new Color(243, 156, 18));
                        } else {
                            c.setForeground(new Color(192, 57, 43));
                            c.setFont(new Font("Arial", Font.BOLD, 12));
                        }
                    }
                } catch (Exception e) {
                    c.setForeground(Color.BLACK);
                }
            }
            
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
}