package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog untuk memilih satuan dan jumlah produk
 */
class DialogPilihSatuan extends JDialog {
    private FormTransaksi parent;
    private Produk produk;
    private JComboBox<SatuanJual> cmbSatuan;
    private JTextField txtQty;
    private JButton btnTambah, btnBatal;

    public DialogPilihSatuan(FormTransaksi parent, Produk produk) {
        super(parent, "Pilih Satuan - " + produk.getNamaProduk(), true);
        this.parent = parent;
        this.produk = produk;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(350, 200);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Produk
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Produk:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel(produk.getNamaProduk()), gbc);

        // Satuan
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Satuan:"), gbc);
        gbc.gridx = 1;
        cmbSatuan = new JComboBox<>();
        for (SatuanJual s : produk.getDaftarSatuan()) {
            cmbSatuan.addItem(s);
        }
        formPanel.add(cmbSatuan, gbc);

        // Qty
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Jumlah:"), gbc);
        gbc.gridx = 1;
        txtQty = new JTextField("1");
        formPanel.add(txtQty, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Panel Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnTambah = new JButton("TAMBAH");
        btnTambah.setBackground(new Color(46, 204, 113));
        btnTambah.setForeground(Color.WHITE);
        btnTambah.setFocusPainted(false);
        btnTambah.addActionListener(e -> tambahProduk());

        btnBatal = new JButton("BATAL");
        btnBatal.setBackground(new Color(231, 76, 60));
        btnBatal.setForeground(Color.WHITE);
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(e -> dispose());

        buttonPanel.add(btnBatal);
        buttonPanel.add(btnTambah);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void tambahProduk() {
        try {
            SatuanJual satuan = (SatuanJual) cmbSatuan.getSelectedItem();
            double qty = Double.parseDouble(txtQty.getText());

            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cek stok (opsional, tapi disarankan)
            double stokDiperlukan = qty * satuan.getKonversiKeDasar();
            if (stokDiperlukan > produk.getStokDasar()) {
                JOptionPane.showMessageDialog(this,
                    "Stok tidak cukup!\nTersedia: " + produk.getStokDasar() + " " + produk.getSatuanDasar(),
                    "Stok Kurang", JOptionPane.WARNING_MESSAGE);
                return;
            }

            parent.tambahKeKeranjang(produk, satuan, qty);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format jumlah tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}