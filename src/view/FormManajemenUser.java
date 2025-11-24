package view;

import model.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Form Manajemen User dengan Alamat & No Telepon
 */
class FormManajemenUser extends JDialog {
    private JTable tableUser;
    private DefaultTableModel tableModel;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;
    
    public FormManajemenUser(JFrame parent) {
        super(parent, "Manajemen User", true);
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel lblTitle = new JLabel("KELOLA DATA USER");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Table
        String[] columns = {"ID", "Username", "Nama Lengkap", "Alamat", "No Telepon", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableUser = new JTable(tableModel);
        tableUser.setRowHeight(30);
        tableUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUser.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableUser.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableUser.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableUser.getColumnModel().getColumn(3).setPreferredWidth(200);
        tableUser.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableUser.getColumnModel().getColumn(5).setPreferredWidth(80);
        tableUser.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tableUser);
        
        // Button Panel
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        btnTambah = new JButton("+ Tambah User");
        btnTambah.setBackground(new Color(46, 204, 113));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> tambahUser());
        
        btnEdit = new JButton("✎ Edit User");
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.addActionListener(e -> editUser());
        
        btnHapus = new JButton("✕ Hapus User");
        btnHapus.setBackground(new Color(231, 76, 60));
        btnHapus.setForeground(Color.WHITE);
        btnHapus.setFocusPainted(false);
        btnHapus.addActionListener(e -> hapusUser());
        
        btnRefresh = new JButton("⟳ Refresh");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadData());
        
        panelButton.add(btnTambah);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(btnRefresh);
        
        panelMain.add(lblTitle, BorderLayout.NORTH);
        panelMain.add(scrollPane, BorderLayout.CENTER);
        panelMain.add(panelButton, BorderLayout.SOUTH);
        
        add(panelMain);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<User> userList = User.getAllUsers();
        
        for (User u : userList) {
            Object[] row = {
                u.getId(),
                u.getUsername(),
                u.getNamaLengkap(),
                u.getAlamat() != null ? u.getAlamat() : "-",
                u.getNoTelepon() != null ? u.getNoTelepon() : "-",
                u.getRole(),
                u.isActive() ? "Aktif" : "Non-aktif"
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahUser() {
        new DialogTambahUser(this).setVisible(true);
        loadData();
    }
    
    private void editUser() {
        int selectedRow = tableUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih user yang akan diedit!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        new DialogEditUser(this, userId).setVisible(true);
        loadData();
    }
    
    private void hapusUser() {
        int selectedRow = tableUser.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih user yang akan dihapus!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus user: " + username + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (User.deleteUser(userId)) {
                JOptionPane.showMessageDialog(this,
                    "User berhasil dihapus!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus user!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/**
 * Dialog Tambah User
 */
class DialogTambahUser extends JDialog {
    private JTextField txtUsername, txtNamaLengkap, txtAlamat, txtNoTelepon;
    private JPasswordField txtPassword, txtKonfirmPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSimpan, btnBatal;
    
    public DialogTambahUser(JDialog parent) {
        super(parent, "Tambah User Baru", true);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 550);
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panelMain.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Username
        JLabel lblUsername = new JLabel("Username:*");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblUsername, gbc);
        
        txtUsername = new JTextField(20);
        txtUsername.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtUsername, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblPassword = new JLabel("Password:*");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtPassword, gbc);
        
        // Konfirmasi Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblKonfirm = new JLabel("Konfirmasi Password:*");
        lblKonfirm.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblKonfirm, gbc);
        
        txtKonfirmPassword = new JPasswordField(20);
        txtKonfirmPassword.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtKonfirmPassword, gbc);
        
        // Nama Lengkap
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblNama = new JLabel("Nama Lengkap:*");
        lblNama.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblNama, gbc);
        
        txtNamaLengkap = new JTextField(20);
        txtNamaLengkap.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtNamaLengkap, gbc);
        
        // Alamat
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblAlamat, gbc);
        
        txtAlamat = new JTextField(20);
        txtAlamat.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtAlamat, gbc);
        
        // No Telepon
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblTelepon = new JLabel("No Telepon:");
        lblTelepon.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblTelepon, gbc);
        
        txtNoTelepon = new JTextField(20);
        txtNoTelepon.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtNoTelepon, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblRole = new JLabel("Role:*");
        lblRole.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblRole, gbc);
        
        cmbRole = new JComboBox<>(new String[]{"admin", "cashier"});
        cmbRole.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(cmbRole, gbc);
        
        // Button Panel
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panelButton.setBackground(Color.WHITE);
        
        btnSimpan = new JButton("✓ SIMPAN");
        btnSimpan.setPreferredSize(new Dimension(130, 40));
        btnSimpan.setBackground(new Color(46, 204, 113));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 13));
        btnSimpan.addActionListener(e -> simpan());
        
        btnBatal = new JButton("✕ BATAL");
        btnBatal.setPreferredSize(new Dimension(130, 40));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.addActionListener(e -> dispose());
        
        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);
        
        add(panelMain, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    private void simpan() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String konfirmPassword = new String(txtKonfirmPassword.getPassword());
        String namaLengkap = txtNamaLengkap.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String noTelepon = txtNoTelepon.getText().trim();
        String role = cmbRole.getSelectedItem().toString();
        
        // Validasi
        if (username.isEmpty() || password.isEmpty() || namaLengkap.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username, Password, dan Nama Lengkap harus diisi!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(konfirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Password dan Konfirmasi Password tidak sama!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.length() < 5) {
            JOptionPane.showMessageDialog(this,
                "Password minimal 5 karakter!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Simpan ke database
        if (User.tambahUser(username, password, role, namaLengkap, 
                alamat.isEmpty() ? null : alamat, 
                noTelepon.isEmpty() ? null : noTelepon)) {
            JOptionPane.showMessageDialog(this,
                "✓ User berhasil ditambahkan!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal menambahkan user!\nUsername mungkin sudah digunakan.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

/**
 * Dialog Edit User
 */
class DialogEditUser extends JDialog {
    private int userId;
    private JTextField txtUsername, txtNamaLengkap, txtAlamat, txtNoTelepon;
    private JPasswordField txtPasswordBaru, txtKonfirmPassword;
    private JComboBox<String> cmbRole;
    private JCheckBox chkGantiPassword;
    private JButton btnSimpan, btnBatal;
    
    public DialogEditUser(JDialog parent, int userId) {
        super(parent, "Edit User", true);
        this.userId = userId;
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(500, 600);
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panelMain.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Username
        JLabel lblUsername = new JLabel("Username:*");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblUsername, gbc);
        
        txtUsername = new JTextField(20);
        txtUsername.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtUsername, gbc);
        
        // Nama Lengkap
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblNama = new JLabel("Nama Lengkap:*");
        lblNama.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblNama, gbc);
        
        txtNamaLengkap = new JTextField(20);
        txtNamaLengkap.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtNamaLengkap, gbc);
        
        // Alamat
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblAlamat, gbc);
        
        txtAlamat = new JTextField(20);
        txtAlamat.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtAlamat, gbc);
        
        // No Telepon
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblTelepon = new JLabel("No Telepon:");
        lblTelepon.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblTelepon, gbc);
        
        txtNoTelepon = new JTextField(20);
        txtNoTelepon.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(txtNoTelepon, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblRole = new JLabel("Role:*");
        lblRole.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblRole, gbc);
        
        cmbRole = new JComboBox<>(new String[]{"admin", "cashier"});
        cmbRole.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        panelMain.add(cmbRole, gbc);
        
        // Separator
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JSeparator separator = new JSeparator();
        gbc.insets = new Insets(15, 5, 15, 5);
        panelMain.add(separator, gbc);
        
        // Checkbox Ganti Password
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        chkGantiPassword = new JCheckBox("🔒 Ganti Password");
        chkGantiPassword.setFont(new Font("Arial", Font.BOLD, 13));
        chkGantiPassword.setBackground(Color.WHITE);
        chkGantiPassword.addActionListener(e -> togglePasswordFields());
        panelMain.add(chkGantiPassword, gbc);
        
        // Password Baru
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(8, 5, 8, 5);
        JLabel lblPasswordBaru = new JLabel("Password Baru:");
        lblPasswordBaru.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblPasswordBaru, gbc);
        
        txtPasswordBaru = new JPasswordField(20);
        txtPasswordBaru.setPreferredSize(new Dimension(250, 35));
        txtPasswordBaru.setEnabled(false);
        txtPasswordBaru.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panelMain.add(txtPasswordBaru, gbc);
        
        // Konfirmasi Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblKonfirm = new JLabel("Konfirmasi Password:");
        lblKonfirm.setFont(new Font("Arial", Font.BOLD, 13));
        panelMain.add(lblKonfirm, gbc);
        
        txtKonfirmPassword = new JPasswordField(20);
        txtKonfirmPassword.setPreferredSize(new Dimension(250, 35));
        txtKonfirmPassword.setEnabled(false);
        txtKonfirmPassword.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panelMain.add(txtKonfirmPassword, gbc);
        
        // Button Panel
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panelButton.setBackground(Color.WHITE);
        
        btnSimpan = new JButton("✓ UPDATE");
        btnSimpan.setPreferredSize(new Dimension(130, 40));
        btnSimpan.setBackground(new Color(52, 152, 219));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setFont(new Font("Arial", Font.BOLD, 13));
        btnSimpan.addActionListener(e -> simpan());
        
        btnBatal = new JButton("✕ BATAL");
        btnBatal.setPreferredSize(new Dimension(130, 40));
        btnBatal.setBackground(new Color(149, 165, 166));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.setFont(new Font("Arial", Font.BOLD, 13));
        btnBatal.addActionListener(e -> dispose());
        
        panelButton.add(btnSimpan);
        panelButton.add(btnBatal);
        
        add(panelMain, BorderLayout.CENTER);
        add(panelButton, BorderLayout.SOUTH);
    }
    
    private void togglePasswordFields() {
        boolean enabled = chkGantiPassword.isSelected();
        txtPasswordBaru.setEnabled(enabled);
        txtKonfirmPassword.setEnabled(enabled);
        if (enabled) {
            txtPasswordBaru.setBackground(Color.WHITE);
            txtKonfirmPassword.setBackground(Color.WHITE);
        } else {
            txtPasswordBaru.setBackground(new Color(240, 240, 240));
            txtKonfirmPassword.setBackground(new Color(240, 240, 240));
            txtPasswordBaru.setText("");
            txtKonfirmPassword.setText("");
        }
    }
    
    private void loadData() {
        User user = User.getUserById(userId);
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtNamaLengkap.setText(user.getNamaLengkap());
            txtAlamat.setText(user.getAlamat() != null ? user.getAlamat() : "");
            txtNoTelepon.setText(user.getNoTelepon() != null ? user.getNoTelepon() : "");
            cmbRole.setSelectedItem(user.getRole());
        }
    }
    
    private void simpan() {
        String username = txtUsername.getText().trim();
        String namaLengkap = txtNamaLengkap.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String noTelepon = txtNoTelepon.getText().trim();
        String role = cmbRole.getSelectedItem().toString();
        
        // Validasi
        if (username.isEmpty() || namaLengkap.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan Nama Lengkap harus diisi!",
                "Validasi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Jika ganti password
        if (chkGantiPassword.isSelected()) {
            String password = new String(txtPasswordBaru.getPassword());
            String konfirmPassword = new String(txtKonfirmPassword.getPassword());
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Password baru harus diisi!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (password.length() < 5) {
                JOptionPane.showMessageDialog(this,
                    "Password minimal 5 karakter!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!password.equals(konfirmPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Password dan Konfirmasi Password tidak sama!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Update dengan password baru
            if (User.updatePassword(userId, password)) {
                // Kemudian update data lainnya
                updateDataUser(username, namaLengkap, role, alamat, noTelepon);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Gagal mengupdate password!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Update tanpa password
            updateDataUser(username, namaLengkap, role, alamat, noTelepon);
        }
    }
    
    private void updateDataUser(String username, String namaLengkap, String role, String alamat, String noTelepon) {
        if (User.updateUser(userId, username, namaLengkap, role,
                alamat.isEmpty() ? null : alamat,
                noTelepon.isEmpty() ? null : noTelepon)) {
            JOptionPane.showMessageDialog(this,
                "✓ User berhasil diupdate!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate user!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}