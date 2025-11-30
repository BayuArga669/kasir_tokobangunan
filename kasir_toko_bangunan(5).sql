-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 30, 2025 at 09:03 PM
-- Server version: 10.4.32-MariaDB-log
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `kasir_toko_bangunan`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang_masuk`
--

CREATE TABLE `barang_masuk` (
  `id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `jumlah` decimal(10,2) NOT NULL,
  `harga_beli_satuan` decimal(10,2) NOT NULL,
  `total_harga` decimal(10,2) NOT NULL,
  `tanggal_masuk` timestamp NULL DEFAULT current_timestamp(),
  `keterangan` text DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `barang_masuk`
--

INSERT INTO `barang_masuk` (`id`, `produk_id`, `jumlah`, `harga_beli_satuan`, `total_harga`, `tanggal_masuk`, `keterangan`, `user_id`) VALUES
(1, 1, 133.00, 10000.00, 1330000.00, '2025-11-24 02:28:31', NULL, 1),
(2, 3, 1000.00, 10.00, 10000.00, '2025-11-24 02:33:11', 'Input: 10.00 Kotak = 1000.00 PCS', 1),
(3, 2, 500.00, 5000.00, 2500000.00, '2025-11-24 02:58:10', 'Input: 100.00 Kaleng = 500.00 KG', 1),
(4, 4, 1000.00, 2000.00, 2000000.00, '2025-11-24 23:41:18', 'Input: 1.00 Truk = 1000.00 PCS', 1),
(5, 7, 25.00, 4000.00, 100000.00, '2025-11-27 00:42:15', 'Input: 1.00 sak = 25.00 KG', 1),
(6, 11, 1000.00, 2000.00, 2000000.00, '2025-11-27 04:46:02', 'Input: 1000.00 METER', 1),
(7, 7, 500.00, 6000.00, 3000000.00, '2025-11-27 05:21:35', 'nambah stok | Input: 20.00 sak = 500.00 KG', 1),
(8, 9, 800.00, 3000.00, 2400000.00, '2025-11-27 06:02:19', 'Input: 2.00 Truk = 800.00 KG', 1),
(9, 13, 5.00, 11000.00, 55000.00, '2025-11-27 07:27:42', 'Input: 5.00 PCS', 1);

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `id` int(11) NOT NULL,
  `transaksi_id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `satuan_jual_id` int(11) NOT NULL,
  `nama_produk` varchar(100) NOT NULL,
  `nama_satuan` varchar(20) NOT NULL,
  `qty` decimal(10,2) NOT NULL,
  `harga_satuan` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detail_transaksi`
--

INSERT INTO `detail_transaksi` (`id`, `transaksi_id`, `produk_id`, `satuan_jual_id`, `nama_produk`, `nama_satuan`, `qty`, `harga_satuan`, `subtotal`) VALUES
(1, 1, 2, 3, 'Cat Tembok Avian 5kg', 'Kaleng', 1.00, 85000.00, 85000.00),
(2, 2, 1, 1, 'Semen Tiga Roda', 'Sak', 1.00, 60000.00, 60000.00),
(3, 2, 2, 3, 'Cat Tembok Avian 5kg', 'Kaleng', 1.00, 85000.00, 85000.00),
(4, 2, 4, 8, 'Batu Bata', 'Truk', 1.00, 3000000.00, 3000000.00),
(5, 3, 2, 3, 'Cat Tembok Avian 5kg', 'Kaleng', 1.00, 85000.00, 85000.00),
(6, 4, 3, 6, 'Paku', 'Kotak', 1.00, 10000.00, 10000.00),
(7, 4, 7, 12, 'Semen Gersik', 'sak', 1.00, 200000.00, 200000.00),
(8, 4, 4, 7, 'Batu Bata', 'PCS', 50.00, 3000.00, 150000.00),
(9, 5, 7, 12, 'Semen Gersik', 'sak', 1.00, 200000.00, 200000.00),
(10, 5, 3, 6, 'Paku', 'Kotak', 1.00, 10000.00, 10000.00),
(11, 6, 7, 12, 'Semen Gersik', 'sak', 1.00, 200000.00, 200000.00),
(12, 6, 4, 7, 'Batu Bata', 'PCS', 50.00, 3000.00, 150000.00),
(13, 6, 9, 14, 'Pasir Gunung', 'KG', 20.00, 10000.00, 200000.00),
(14, 7, 2, 3, 'Cat Tembok Avian', 'Kaleng', 1.00, 85000.00, 85000.00),
(15, 8, 9, 19, 'Pasir Gunung', 'Truk', 1.00, 4000000.00, 4000000.00),
(16, 9, 13, 22, 'Tang tong', 'PCS', 2.00, 13000.00, 26000.00),
(17, 9, 7, 11, 'Semen Gersik', 'KG', 1.00, 10000.00, 10000.00),
(18, 9, 10, 21, 'Pasir Batu', 'Pick up', 1.00, 1000000.00, 1000000.00),
(19, 10, 7, 12, 'Semen Gersik', 'Karung', 1.00, 200000.00, 200000.00),
(20, 11, 2, 4, 'Cat Tembok Avian', 'Kg', 1.00, 18000.00, 18000.00),
(21, 12, 1, 1, 'Semen Tiga Roda', 'Karung', 1.00, 60000.00, 60000.00),
(22, 13, 9, 14, 'Pasir Gunung', 'KG', 1.00, 10000.00, 10000.00),
(23, 14, 2, 3, 'Cat Tembok Avian', 'Kaleng', 1.00, 85000.00, 85000.00),
(24, 15, 10, 18, 'Pasir Batu', 'Truk', 1.00, 2000000.00, 2000000.00),
(25, 16, 4, 8, 'Batu Bata', 'Pick up', 1.00, 1500000.00, 1500000.00);

-- --------------------------------------------------------

--
-- Table structure for table `kategori`
--

CREATE TABLE `kategori` (
  `id` int(11) NOT NULL,
  `nama_kategori` varchar(50) NOT NULL,
  `deskripsi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kategori`
--

INSERT INTO `kategori` (`id`, `nama_kategori`, `deskripsi`) VALUES
(1, 'Material Kasar', 'Semen, Pasir'),
(2, 'Cat', 'Cat Tembok, Cat Kayu'),
(3, 'Tools', 'Palu, Obeng, Tang'),
(4, 'Besi & Baja', 'Besi Beton, Hollow, Pipa'),
(5, 'Alat listrik', 'Kabel, MCB, Steker');

-- --------------------------------------------------------

--
-- Table structure for table `pengaturan_toko`
--

CREATE TABLE `pengaturan_toko` (
  `id` int(11) NOT NULL,
  `nama_toko` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `no_hp` varchar(20) DEFAULT NULL,
  `footer_struk` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pengaturan_toko`
--

INSERT INTO `pengaturan_toko` (`id`, `nama_toko`, `alamat`, `no_hp`, `footer_struk`) VALUES
(1, 'Toko Bangunan Maju Jaya', 'Jl. Raya No. 123, Jakarta', '021-12345678', 'Barang yang sudah dibeli tidak dapat ditukar kecuali ada perjanjian.');

-- --------------------------------------------------------

--
-- Table structure for table `penyesuaian_stok`
--

CREATE TABLE `penyesuaian_stok` (
  `id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `jumlah_penyesuaian` decimal(10,2) NOT NULL,
  `tipe` enum('tambah','kurang') NOT NULL,
  `alasan` text DEFAULT NULL,
  `tanggal` timestamp NULL DEFAULT current_timestamp(),
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `produk`
--

CREATE TABLE `produk` (
  `id` int(11) NOT NULL,
  `nama_produk` varchar(100) NOT NULL,
  `kategori_id` int(11) DEFAULT NULL,
  `satuan_dasar` varchar(20) NOT NULL,
  `stok_dasar` decimal(10,2) DEFAULT 0.00,
  `harga_beli` decimal(10,2) DEFAULT 0.00 COMMENT 'Harga beli terakhir (per satuan dasar)',
  `is_deleted` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `produk`
--

INSERT INTO `produk` (`id`, `nama_produk`, `kategori_id`, `satuan_dasar`, `stok_dasar`, `harga_beli`, `is_deleted`, `created_at`, `updated_at`) VALUES
(1, 'Semen Tiga Roda', 1, 'KG', 1533.00, 10000.00, 0, '2025-11-27 07:50:33', '2025-11-27 09:00:12'),
(2, 'Cat Tembok Avian', 2, 'KG', 574.00, 5000.00, 0, '2025-11-27 07:50:33', '2025-11-30 13:19:02'),
(3, 'Paku', 3, 'PCS', 1800.00, 10.00, 1, '2025-11-24 00:01:21', '2025-11-27 03:52:44'),
(4, 'Batu Bata', 1, 'PCS', 9400.00, 2000.00, 0, '2025-11-24 23:37:51', '2025-11-30 14:02:15'),
(5, 'Semen Gersik', 1, 'KG', 500.00, 0.00, 1, '2025-11-24 23:50:59', '2025-11-24 23:51:12'),
(6, 'Semen Gersik', 1, 'KG', 5000.00, 0.00, 1, '2025-11-24 23:52:22', '2025-11-24 23:55:21'),
(7, 'Semen Gersik', 1, 'KG', 1424.00, 6000.00, 0, '2025-11-27 00:38:48', '2025-11-27 07:41:38'),
(8, 'Pasir Gunung', 1, 'KG', 1000.00, 0.00, 1, '2025-11-27 03:54:50', '2025-11-27 03:55:56'),
(9, 'Pasir Gunung', 1, 'KG', 1379.00, 3000.00, 0, '2025-11-27 03:56:13', '2025-11-27 09:00:20'),
(10, 'Pasir Batu', 1, 'KG', 400.00, 2000.00, 0, '2025-11-27 04:33:34', '2025-11-30 13:45:04'),
(11, 'Kabel', 5, 'METER', 2000.00, 2000.00, 1, '2025-11-27 04:45:13', '2025-11-27 05:11:41'),
(12, 'Staker', 5, 'PCS', 100.00, 3000.00, 0, '2025-11-27 05:13:03', '2025-11-27 05:13:03'),
(13, 'Tang tong', 3, 'PCS', 13.00, 11000.00, 0, '2025-11-27 07:25:29', '2025-11-27 07:31:55');

-- --------------------------------------------------------

--
-- Table structure for table `satuan_jual`
--

CREATE TABLE `satuan_jual` (
  `id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `nama_satuan` varchar(20) NOT NULL,
  `konversi_ke_dasar` decimal(10,2) NOT NULL,
  `harga_jual` decimal(10,2) NOT NULL,
  `barcode` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `satuan_jual`
--

INSERT INTO `satuan_jual` (`id`, `produk_id`, `nama_satuan`, `konversi_ke_dasar`, `harga_jual`, `barcode`) VALUES
(1, 1, 'Karung', 50.00, 60000.00, '8991234567890'),
(2, 1, 'KG', 1.00, 1500.00, NULL),
(3, 2, 'Kaleng', 5.00, 85000.00, NULL),
(4, 2, 'Kg', 1.00, 18000.00, NULL),
(5, 3, 'PCS', 1.00, 100.00, NULL),
(6, 3, 'Kotak', 100.00, 10000.00, NULL),
(7, 4, 'PCS', 1.00, 3000.00, NULL),
(8, 4, 'Pick up', 500.00, 1500000.00, NULL),
(9, 5, 'KG', 1.00, 0.00, NULL),
(10, 6, 'KG', 50.00, 80000.00, NULL),
(11, 7, 'KG', 1.00, 10000.00, NULL),
(12, 7, 'Karung', 25.00, 200000.00, NULL),
(13, 8, 'KG', 1.00, 3900.00, NULL),
(14, 9, 'KG', 1.00, 10000.00, NULL),
(15, 10, 'KG', 1.00, 5000.00, NULL),
(16, 11, 'METER', 1.00, 2600.00, NULL),
(17, 12, 'PCS', 1.00, 5000.00, NULL),
(18, 10, 'Truk', 400.00, 2000000.00, NULL),
(19, 9, 'Truk', 400.00, 4000000.00, NULL),
(20, 9, 'Pick up', 200.00, 2000000.00, NULL),
(21, 10, 'Pick up', 200.00, 1000000.00, NULL),
(22, 13, 'PCS', 1.00, 13000.00, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id` int(11) NOT NULL,
  `kode_transaksi` varchar(50) NOT NULL,
  `tanggal` timestamp NULL DEFAULT current_timestamp(),
  `total_belanja` decimal(10,2) NOT NULL,
  `bayar` decimal(10,2) NOT NULL,
  `kembalian` decimal(10,2) NOT NULL,
  `metode_pembayaran` varchar(20) DEFAULT 'Tunai',
  `kasir_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id`, `kode_transaksi`, `tanggal`, `total_belanja`, `bayar`, `kembalian`, `metode_pembayaran`, `kasir_id`) VALUES
(1, 'TRX-20251124070932', '2025-11-24 00:09:44', 85000.00, 85000.00, 0.00, 'QRIS', 1),
(2, 'TRX-20251125064125', '2025-11-24 23:42:18', 3145000.00, 3145000.00, 0.00, 'QRIS', 1),
(3, 'TRX-20251125080937', '2025-11-25 01:09:47', 85000.00, 85000.00, 0.00, 'QRIS', 1),
(4, 'TRX-20251127075139', '2025-11-27 00:54:51', 360000.00, 400000.00, 40000.00, 'Tunai', 2),
(5, 'TRX-20251127085059', '2025-11-27 01:51:19', 210000.00, 250000.00, 40000.00, 'Tunai', 2),
(6, 'TRX-20251127122427', '2025-11-27 05:26:29', 550000.00, 600000.00, 50000.00, 'Tunai', 2),
(7, 'TRX-20251127130331', '2025-11-27 06:03:48', 85000.00, 85000.00, 0.00, 'QRIS', 2),
(8, 'TRX-20251127130354', '2025-11-27 06:04:34', 4000000.00, 4000000.00, 0.00, 'Transfer', 2),
(9, 'TRX-20251127142956', '2025-11-27 07:31:55', 1036000.00, 1050000.00, 14000.00, 'Tunai', 2),
(10, 'TRX-20251127144109', '2025-11-27 07:41:38', 200000.00, 300000.00, 100000.00, 'Tunai', 2),
(11, 'TRX-20251127155931', '2025-11-27 08:59:51', 18000.00, 18000.00, 0.00, 'QRIS', 2),
(12, 'TRX-20251127155959', '2025-11-27 09:00:12', 60000.00, 60000.00, 0.00, 'QRIS', 2),
(13, 'TRX-20251127160013', '2025-11-27 09:00:20', 10000.00, 10000.00, 0.00, 'Transfer', 2),
(14, 'TRX-20251130201739', '2025-11-30 13:19:02', 85000.00, 85000.00, 0.00, 'QRIS', 2),
(15, 'TRX-20251130204457', '2025-11-30 13:45:04', 2000000.00, 2000000.00, 0.00, 'QRIS', 2),
(16, 'TRX-20251130210209', '2025-11-30 14:02:15', 1500000.00, 1500000.00, 0.00, 'QRIS', 2);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','cashier') NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `no_telepon` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `nama_lengkap`, `alamat`, `no_telepon`, `created_at`, `is_active`) VALUES
(1, 'admin', 'admin123', 'admin', 'Administrator', 'SIDOARJO', '0859425247858', '2025-11-27 07:50:33', 1),
(2, 'kasir1', 'kasir123', 'cashier', 'bayuyu', 'SIDOARJO', '0857654321', '2025-11-27 07:50:33', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang_masuk`
--
ALTER TABLE `barang_masuk`
  ADD PRIMARY KEY (`id`),
  ADD KEY `produk_id` (`produk_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `transaksi_id` (`transaksi_id`),
  ADD KEY `produk_id` (`produk_id`),
  ADD KEY `satuan_jual_id` (`satuan_jual_id`);

--
-- Indexes for table `kategori`
--
ALTER TABLE `kategori`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `pengaturan_toko`
--
ALTER TABLE `pengaturan_toko`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `penyesuaian_stok`
--
ALTER TABLE `penyesuaian_stok`
  ADD PRIMARY KEY (`id`),
  ADD KEY `produk_id` (`produk_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`id`),
  ADD KEY `kategori_id` (`kategori_id`);

--
-- Indexes for table `satuan_jual`
--
ALTER TABLE `satuan_jual`
  ADD PRIMARY KEY (`id`),
  ADD KEY `produk_id` (`produk_id`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode_transaksi` (`kode_transaksi`),
  ADD KEY `kasir_id` (`kasir_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `barang_masuk`
--
ALTER TABLE `barang_masuk`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `kategori`
--
ALTER TABLE `kategori`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `pengaturan_toko`
--
ALTER TABLE `pengaturan_toko`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `penyesuaian_stok`
--
ALTER TABLE `penyesuaian_stok`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `produk`
--
ALTER TABLE `produk`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `satuan_jual`
--
ALTER TABLE `satuan_jual`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `barang_masuk`
--
ALTER TABLE `barang_masuk`
  ADD CONSTRAINT `barang_masuk_ibfk_1` FOREIGN KEY (`produk_id`) REFERENCES `produk` (`id`),
  ADD CONSTRAINT `barang_masuk_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `detail_transaksi_ibfk_1` FOREIGN KEY (`transaksi_id`) REFERENCES `transaksi` (`id`),
  ADD CONSTRAINT `detail_transaksi_ibfk_2` FOREIGN KEY (`produk_id`) REFERENCES `produk` (`id`),
  ADD CONSTRAINT `detail_transaksi_ibfk_3` FOREIGN KEY (`satuan_jual_id`) REFERENCES `satuan_jual` (`id`);

--
-- Constraints for table `penyesuaian_stok`
--
ALTER TABLE `penyesuaian_stok`
  ADD CONSTRAINT `penyesuaian_stok_ibfk_1` FOREIGN KEY (`produk_id`) REFERENCES `produk` (`id`),
  ADD CONSTRAINT `penyesuaian_stok_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `produk`
--
ALTER TABLE `produk`
  ADD CONSTRAINT `produk_ibfk_1` FOREIGN KEY (`kategori_id`) REFERENCES `kategori` (`id`);

--
-- Constraints for table `satuan_jual`
--
ALTER TABLE `satuan_jual`
  ADD CONSTRAINT `satuan_jual_ibfk_1` FOREIGN KEY (`produk_id`) REFERENCES `produk` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`kasir_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
