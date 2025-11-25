package view;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import javax.swing.border.Border;

/**
 * Form Login untuk Admin dan Kasir dengan UI Modern dan Fullscreen
 */
public class LoginForm extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblTitle;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblLogo;
    private JPanel panelMain;
    private JPanel panelForm;
    private JPanel panelButton;
    private JPanel panelTitle;
    
    public LoginForm() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Setup Frame untuk fullscreen
        setTitle("Login - Kasir Toko Bangunan");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Menghilangkan border default window
        
        // Main Panel dengan gradien background
        panelMain = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(41, 128, 185);
                Color color2 = new Color(109, 33, 79);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        panelMain.setLayout(new BorderLayout());
        
        // Center panel untuk form login
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        // Login Card Panel dengan rounded corners
        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        loginCard.setLayout(new BorderLayout());
        loginCard.setOpaque(false);
        loginCard.setPreferredSize(new Dimension(450, 500));
        loginCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title Panel dengan logo
        panelTitle = new JPanel();
        panelTitle.setLayout(new BoxLayout(panelTitle, BoxLayout.Y_AXIS));
        panelTitle.setOpaque(false);
        panelTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // --- PERBAIKAN PEMUATAN LOGO ---
        // Logo
        try {
            // Coba muat gambar sebagai resource dari classpath
            // Tanda '/' di awal berarti pencarian dimulai dari root classpath
            URL imageUrl = LoginForm.class.getResource("/img/logo.png");

            if (imageUrl != null) {
                ImageIcon logoIcon = new ImageIcon(ImageIO.read(imageUrl));
                Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                lblLogo = new JLabel(new ImageIcon(scaledLogo));
                lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelTitle.add(lblLogo);
                panelTitle.add(Box.createVerticalStrut(10));
            } else {
                // Jika gambar tidak ditemukan, cetak peringatan ke konsol
                System.err.println("Peringatan: Logo tidak ditemukan di /img/logo.png. Program akan berjalan tanpa logo.");
            }
        } catch (IOException e) {
            System.err.println("Error saat mencoba memuat logo: " + e.getMessage());
            e.printStackTrace();
        }
        
        lblTitle = new JLabel("TOKO BANGUNAN MAJU JAYA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 73, 94));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Sistem Kasir & Inventory");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(149, 165, 166));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelTitle.add(lblTitle);
        panelTitle.add(Box.createVerticalStrut(5));
        panelTitle.add(lblSubtitle);
        
        // Form Panel
        panelForm = new JPanel();
        panelForm.setLayout(new GridBagLayout());
        panelForm.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panelForm.add(lblUsername, gbc);
        
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(250, 40));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panelForm.add(txtUsername, gbc);
        
        // Password
        lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panelForm.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(250, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panelForm.add(txtPassword, gbc);
        
        // Button Panel
        panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panelButton.setOpaque(false);
        
        btnLogin = createModernButton("LOGIN", new Color(46, 204, 113));
        btnLogin.addActionListener(e -> handleLogin());
        
        btnExit = createModernButton("KELUAR", new Color(231, 76, 60));
        btnExit.addActionListener(e -> System.exit(0));
        
        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        
        // Add components to login card
        loginCard.add(panelTitle, BorderLayout.NORTH);
        loginCard.add(panelForm, BorderLayout.CENTER);
        loginCard.add(panelButton, BorderLayout.SOUTH);
        
        // Add login card to center panel
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerPanel.add(loginCard, centerGbc);
        
        // Add center panel to main panel
        panelMain.add(centerPanel, BorderLayout.CENTER);
        
        // Close button
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(closeButton);
        
        panelMain.add(topPanel, BorderLayout.NORTH);
        
        add(panelMain);
        
        // Enter key listener
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        
        // Focus on username
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtUsername.requestFocus();
            }
        });
        
        // Add mouse listener for dragging the window
        final Point[] dragStartPoint = new Point[1];
        panelMain.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStartPoint[0] = e.getPoint();
            }
        });
        
        panelMain.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
                setLocation(current.x - dragStartPoint[0].x, current.y - dragStartPoint[0].y);
            }
        });
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle
                g2d.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
            
            @Override
            public void setContentAreaFilled(boolean b) {
                // Do nothing
            }
            
            @Override
            public void setBorder(Border border) {
                // Do nothing
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty()) {
            showCustomDialog("Username tidak boleh kosong!", "Validasi");
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showCustomDialog("Password tidak boleh kosong!", "Validasi");
            txtPassword.requestFocus();
            return;
        }
        
        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Loading...");
        
        // Login process
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return User.login(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    if (user != null) {
                        // Login berhasil
                        showCustomDialog("Login berhasil!\nSelamat datang, " + user.getNamaLengkap(), "Sukses");
                        
                        // Buka dashboard sesuai role
                        if (user.getRole().equals("admin")) {
                            new AdminDashboard(user).setVisible(true);
                        } else {
                            new KasirDashboard(user).setVisible(true);
                        }
                        
                        // Tutup form login
                        dispose();
                        
                    } else {
                        // Login gagal
                        showCustomDialog("Username atau password salah!", "Login Gagal");
                        txtPassword.setText("");
                        txtUsername.requestFocus();
                    }
                    
                } catch (Exception e) {
                    showCustomDialog("Error: " + e.getMessage(), "Error");
                } finally {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showCustomDialog(String message, String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setUndecorated(true);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2));
        panel.setBackground(Color.WHITE);
        
        JLabel messageLabel = new JLabel("<html><div style='text-align: center; padding: 10px;'>" + message.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setBackground(new Color(52, 73, 94));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        okButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}