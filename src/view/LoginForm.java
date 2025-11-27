package view;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Form Login untuk Admin dan Kasir
 */
public class LoginForm extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblTitle;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JPanel panelMain;
    private JPanel panelForm;
    private JPanel panelButton;
    
    public LoginForm() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        // Setup Frame
        setTitle("Login - Kasir Toko Bangunan");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main Panel
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelMain.setBackground(new Color(240, 240, 240));
        
        // Title Panel
        JPanel panelTitle = new JPanel();
        panelTitle.setBackground(new Color(52, 73, 94));
        panelTitle.setPreferredSize(new Dimension(450, 80));
        
        lblTitle = new JLabel("TOKO BANGUNAN MAJU JAYA");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        panelTitle.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Sistem Kasir & Inventory");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(236, 240, 241));
        
        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setBackground(new Color(52, 73, 94));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleContainer.add(lblTitle);
        titleContainer.add(Box.createVerticalStrut(5));
        titleContainer.add(lblSubtitle);
        panelTitle.add(titleContainer);
        
        // Form Panel
        panelForm = new JPanel();
        panelForm.setLayout(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        panelForm.add(lblUsername, gbc);
        
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panelForm.add(txtUsername, gbc);
        
        // Password
        lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panelForm.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panelForm.add(txtPassword, gbc);
        
        // Button Panel
        panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelButton.setBackground(Color.WHITE);
        
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setPreferredSize(new Dimension(120, 40));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());
        
        btnExit = new JButton("KELUAR");
        btnExit.setFont(new Font("Arial", Font.BOLD, 13));
        btnExit.setPreferredSize(new Dimension(120, 40));
        btnExit.setBackground(new Color(231, 76, 60));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addActionListener(e -> System.exit(0));
        
        panelButton.add(btnLogin);
        panelButton.add(btnExit);
        
        // Add to Main Panel
        panelMain.add(panelTitle, BorderLayout.NORTH);
        panelMain.add(panelForm, BorderLayout.CENTER);
        panelMain.add(panelButton, BorderLayout.SOUTH);
        
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
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username tidak boleh kosong!", 
                "Validasi", 
                JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Password tidak boleh kosong!", 
                "Validasi", 
                JOptionPane.WARNING_MESSAGE);
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
                        JOptionPane.showMessageDialog(LoginForm.this,
                            "Login berhasil!\nSelamat datang, " + user.getNamaLengkap(),
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                        
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
                        JOptionPane.showMessageDialog(LoginForm.this,
                            "Username atau password salah!",
                            "Login Gagal",
                            JOptionPane.ERROR_MESSAGE);
                        
                        txtPassword.setText("");
                        txtUsername.requestFocus();
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");
                }
            }
        };
        
        worker.execute();
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