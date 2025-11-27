package view;

import config.DatabaseConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * SplashScreen modern dengan proses loading dan persentase di background.
 * Diperbaiki agar komponen benar-benar muncul saat digunakan dengan JWindow.
 */
public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel lblStatus;
    private JLabel lblPercentage;
    private JButton btnClose;
    private Color primaryColor = new Color(52, 73, 94);
    private Color accentColor = new Color(41, 128, 185);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font statusFont = new Font("Segoe UI", Font.PLAIN, 14);

    public SplashScreen() {
        initComponents();
        setVisible(true); // ← Tampilkan setelah UI siap
    }

    private void initComponents() {
        // Ukuran tetap
        int width = 500;
        int height = 350;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);

        // Pastikan window berada di atas
        setAlwaysOnTop(true);

        // Panel utama dengan background gradien
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, accentColor, getWidth(), getHeight(), primaryColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // === Logo & Judul ===
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            URL imageUrl = SplashScreen.class.getResource("/img/logo.png");
            if (imageUrl != null) {
                ImageIcon logoIcon = new ImageIcon(ImageIO.read(imageUrl));
                Image scaledLogo = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                JLabel lblLogo = new JLabel(new ImageIcon(scaledLogo));
                lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
                titlePanel.add(lblLogo);
            } else {
                System.err.println("⚠️ File logo tidak ditemukan di /img/logo.png");
            }
        } catch (IOException e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }

        JLabel lblTitle = new JLabel("TOKO BANGUNAN WARNER");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Sistem Kasir & Inventory");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(220, 220, 220));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        // === Status & Progress Bar ===
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));

        lblStatus = new JLabel("Memulai aplikasi...", SwingConstants.CENTER);
        lblStatus.setFont(statusFont);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel progressContainer = new JPanel(new BorderLayout(5, 0));
        progressContainer.setOpaque(false);

        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true); // Mulai dengan animasi loading
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        progressBar.setPreferredSize(new Dimension(100, 20));

        lblPercentage = new JLabel("...", SwingConstants.RIGHT);
        lblPercentage.setFont(statusFont.deriveFont(Font.BOLD));
        lblPercentage.setForeground(Color.WHITE);
        lblPercentage.setPreferredSize(new Dimension(40, 20));

        progressContainer.add(progressBar, BorderLayout.CENTER);
        progressContainer.add(lblPercentage, BorderLayout.EAST);

        statusPanel.add(lblStatus);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(progressContainer);

        // === Tombol Close ===
        btnClose = new JButton("Tutup");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setBackground(new Color(231, 76, 60));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setVisible(false);
        btnClose.addActionListener(e -> System.exit(1));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnClose);

        mainPanel.add(titlePanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);

        add(mainPanel);

        // Pastikan UI dirender
        validate();
        repaint();
    }

    public void startLoading() {
        SwingWorker<Boolean, Object[]> worker = new SwingWorker<Boolean, Object[]>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                publish(new Object[]{"Menginisialisasi komponen...", -1});
                Thread.sleep(600);

                publish(new Object[]{"Menghubungkan ke database...", 20});
                Thread.sleep(800);

                boolean isConnected = DatabaseConfig.testConnection();

                if (isConnected) {
                    publish(new Object[]{"Koneksi database berhasil!", 50});
                    Thread.sleep(500);

                    publish(new Object[]{"Memuat pengaturan sistem...", 75});
                    Thread.sleep(600);

                    publish(new Object[]{"Menyiapkan antarmuka...", 90});
                    Thread.sleep(400);

                    publish(new Object[]{"Aplikasi siap digunakan!", 100});
                    Thread.sleep(300);
                } else {
                    publish(new Object[]{"Gagal terhubung ke database.", -2});
                }

                return isConnected;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                Object[] latest = chunks.get(chunks.size() - 1);
                String status = (String) latest[0];
                Integer progress = (Integer) latest[1];

                lblStatus.setText(status);

                if (progress == -1) {
                    // Mode indeterminate
                    progressBar.setIndeterminate(true);
                    lblPercentage.setText("...");
                } else if (progress == -2) {
                    // Error
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                    progressBar.setForeground(new Color(231, 76, 60));
                    lblPercentage.setText("❌");
                } else {
                    // Mode determinate
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(progress);
                    progressBar.setForeground(new Color(46, 204, 113));
                    lblPercentage.setText(progress + "%");
                }
            }

            @Override
            protected void done() {
                dispose();
                try {
                    if (get()) {
                        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
                    } else {
                        // Tampilkan pesan error dan tombol
                        progressBar.setForeground(new Color(231, 76, 60));
                        lblStatus.setText("<html><center>Gagal terhubung ke database.<br>Periksa koneksi dan konfigurasi.</center></html>");
                        btnClose.setVisible(true);
                    }
                } catch (Exception e) {
                    lblStatus.setText("<html><center>Error: " + e.getMessage() + "</center></html>");
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                    lblPercentage.setText("❌");
                    btnClose.setVisible(true);
                }
            }
        };
        worker.execute();
    }
}