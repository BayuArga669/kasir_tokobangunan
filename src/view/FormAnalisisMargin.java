package view;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Form Analisis Margin/Laba per Produk
 */
public class FormAnalisisMargin extends JFrame {
    
    private JTable tableAnalisis;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnSearch, btnRefresh;
    private JLabel lblTotalMargin;
    
    public FormAnalisisMargin(JFrame parent) {
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setTitle("Analisis Margin & Laba Produk");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Analisis Margin & Profitabilitas Produk");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSubtitle = new JLabel("Perbandingan Harga Beli vs Harga Jual");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(230, 240, 255));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(lblSubtitle);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblSearch = new JLabel("Cari Produk:");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 12));
        
        txtSearch = new JTextField(25);
        txtSearch.setPreferredSize(new Dimension(250, 30));
        
        btnSearch = new JButton("CARI");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> searchProduk());
        
        btnRefresh = new JButton("🔄 REFRESH");
        btnRefresh.setBackground(new Color(46, 204, 113));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadData());
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        
        // Table
        String[] columns = {
            "ID", "Produk", "Satuan Jual", "Harga Beli", 
            "Harga Jual", "Margin", "Profit %", "Status"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableAnalisis = new JTable(tableModel);
        tableAnalisis.setRowHeight(32);
        tableAnalisis.setFont(new Font("Arial", Font.PLAIN, 12));
        tableAnalisis.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableAnalisis.getTableHeader().setBackground(new Color(236, 240, 241));
        
        tableAnalisis.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableAnalisis.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableAnalisis.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableAnalisis.getColumnModel().getColumn(3).setPreferredWidth(110);
        tableAnalisis.getColumnModel().getColumn(4).setPreferredWidth(110);
        tableAnalisis.getColumnModel().getColumn(5).setPreferredWidth(110);
        tableAnalisis.getColumnModel().getColumn(6).setPreferredWidth(90);
        tableAnalisis.getColumnModel().getColumn(7).setPreferredWidth(120);
        
        // Custom renderer untuk kolom profit
        tableAnalisis.getColumnModel().getColumn(6).setCellRenderer(new ProfitRenderer());
        tableAnalisis.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        
        JScrollPane scrollPane = new JScrollPane(tableAnalisis);
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        summaryPanel.setBackground(new Color(236, 240, 241));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblSummaryLabel = new JLabel("Total Item Analisis:");
        lblSummaryLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        lblTotalMargin = new JLabel("0 item");
        lblTotalMargin.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalMargin.setForeground(new Color(52, 152, 219));
        
        summaryPanel.add(lblSummaryLabel);
        summaryPanel.add(lblTotalMargin);
        
        // Legend Panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createTitledBorder("Keterangan Status:"));
        
        addLegend(legendPanel, new Color(46, 204, 113), "Sangat Baik (> 40%)");
        addLegend(legendPanel, new Color(52, 152, 219), "Baik (20% - 40%)");
        addLegend(legendPanel, new Color(241, 196, 15), "Normal (10% - 20%)");
        addLegend(legendPanel, new Color(230, 126, 34), "Kurang (5% - 10%)");
        addLegend(legendPanel, new Color(231, 76, 60), "Rugi (< 5%)");
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(legendPanel, BorderLayout.CENTER);
        bottomPanel.add(summaryPanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void addLegend(JPanel panel, Color color, String text) {
        JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        legendItem.setBackground(Color.WHITE);
        
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        
        legendItem.add(colorBox);
        legendItem.add(label);
        panel.add(legendItem);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Produk> produkList = Produk.getAllProduk();
        int totalItems = 0;
        
        for (Produk p : produkList) {
            double hargaBeli = p.getHargaBeli();
            
            // Analisis per satuan jual
            for (SatuanJual s : p.getDaftarSatuan()) {
                double hargaJual = s.getHargaJual();
                double hargaBeliPerSatuan = hargaBeli * s.getKonversiKeDasar();
                double margin = hargaJual - hargaBeliPerSatuan;
                double profitPersen = hargaBeliPerSatuan > 0 ? 
                    (margin / hargaBeliPerSatuan) * 100 : 0;
                
                String status = getStatus(profitPersen);
                
                Object[] row = {
                    p.getId(),
                    p.getNamaProduk(),
                    s.getNamaSatuan(),
                    String.format("Rp %,.0f", hargaBeliPerSatuan),
                    String.format("Rp %,.0f", hargaJual),
                    String.format("Rp %,.0f", margin),
                    String.format("%.1f%%", profitPersen),
                    status
                };
                tableModel.addRow(row);
                totalItems++;
            }
        }
        
        lblTotalMargin.setText(totalItems + " item");
    }
    
    private void searchProduk() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Produk> produkList = Produk.searchProduk(keyword);
        int totalItems = 0;
        
        for (Produk p : produkList) {
            double hargaBeli = p.getHargaBeli();
            
            for (SatuanJual s : p.getDaftarSatuan()) {
                double hargaJual = s.getHargaJual();
                double hargaBeliPerSatuan = hargaBeli * s.getKonversiKeDasar();
                double margin = hargaJual - hargaBeliPerSatuan;
                double profitPersen = hargaBeliPerSatuan > 0 ? 
                    (margin / hargaBeliPerSatuan) * 100 : 0;
                
                String status = getStatus(profitPersen);
                
                Object[] row = {
                    p.getId(),
                    p.getNamaProduk(),
                    s.getNamaSatuan(),
                    String.format("Rp %,.0f", hargaBeliPerSatuan),
                    String.format("Rp %,.0f", hargaJual),
                    String.format("Rp %,.0f", margin),
                    String.format("%.1f%%", profitPersen),
                    status
                };
                tableModel.addRow(row);
                totalItems++;
            }
        }
        
        lblTotalMargin.setText(totalItems + " item");
    }
    
    private String getStatus(double profitPersen) {
        if (profitPersen > 40) return "Sangat Baik";
        else if (profitPersen > 20) return "Baik";
        else if (profitPersen > 10) return "Normal";
        else if (profitPersen > 5) return "Kurang";
        else return "Rugi";
    }
    
    // Custom Renderer untuk kolom Profit %
    class ProfitRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            
            if (value != null) {
                String profitStr = value.toString().replace("%", "");
                try {
                    double profit = Double.parseDouble(profitStr);
                    
                    if (!isSelected) {
                        if (profit > 40) {
                            c.setForeground(new Color(39, 174, 96));
                        } else if (profit > 20) {
                            c.setForeground(new Color(41, 128, 185));
                        } else if (profit > 10) {
                            c.setForeground(new Color(243, 156, 18));
                        } else if (profit > 5) {
                            c.setForeground(new Color(211, 84, 0));
                        } else {
                            c.setForeground(new Color(192, 57, 43));
                        }
                        setFont(new Font("Arial", Font.BOLD, 12));
                    }
                } catch (Exception e) {
                    c.setForeground(Color.BLACK);
                }
            }
            
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
    
    // Custom Renderer untuk kolom Status
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            
            if (value != null && !isSelected) {
                String status = value.toString();
                
                switch (status) {
                    case "Sangat Baik":
                        c.setBackground(new Color(212, 239, 223));
                        c.setForeground(new Color(39, 174, 96));
                        break;
                    case "Baik":
                        c.setBackground(new Color(214, 234, 248));
                        c.setForeground(new Color(41, 128, 185));
                        break;
                    case "Normal":
                        c.setBackground(new Color(254, 245, 231));
                        c.setForeground(new Color(243, 156, 18));
                        break;
                    case "Kurang":
                        c.setBackground(new Color(253, 237, 236));
                        c.setForeground(new Color(211, 84, 0));
                        break;
                    case "Rugi":
                        c.setBackground(new Color(242, 215, 213));
                        c.setForeground(new Color(192, 57, 43));
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
                
                setFont(new Font("Arial", Font.BOLD, 11));
            } else if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
}