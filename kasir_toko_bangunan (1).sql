-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 24, 2025 at 06:27 AM
-- Server version: 10.4.32-MariaDB
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
(3, 2, 500.00, 5000.00, 2500000.00, '2025-11-24 02:58:10', 'Input: 100.00 Kaleng = 500.00 KG', 1);

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
(1, 1, 2, 3, 'Cat Tembok Avian 5kg', 'Kaleng', 1.00, 85000.00, 85000.00);

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
(1, 'Material Kasar', 'Semen, Pasir, Batu'),
(2, 'Cat', 'Cat Tembok, Cat Kayu'),
(3, 'Tools', 'Palu, Obeng, Tang'),
(4, 'Besi & Baja', 'Besi Beton, Hollow, Pipa'),
(5, 'Alat Tulis', 'test');

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
(1, 'Semen Tiga Roda', 1, 'KG', 1633.00, 10000.00, 0, '2025-11-27 07:50:33', '2025-11-24 02:50:32'),
(2, 'Cat Tembok Avian 5kg', 2, 'KG', 595.00, 5000.00, 0, '2025-11-27 07:50:33', '2025-11-24 02:58:10'),
(3, 'Paku', 3, 'PCS', 2000.00, 10.00, 0, '2025-11-24 00:01:21', '2025-11-24 02:50:32');

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
(1, 1, 'Sak', 50.00, 60000.00, '8991234567890'),
(2, 1, 'Kg', 1.00, 1500.00, NULL),
(3, 2, 'Kaleng', 5.00, 85000.00, NULL),
(4, 2, 'Kg', 1.00, 18000.00, NULL),
(5, 3, 'PCS', 1.00, 100.00, NULL),
(6, 3, 'Kotak', 100.00, 10000.00, NULL);

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
(1, 'TRX-20251124070932', '2025-11-24 00:09:44', 85000.00, 85000.00, 0.00, 'QRIS', 1);

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
(1, 'admin', 'admin123', 'admin', 'Administrator', 'sidoarjo', '0859425247858', '2025-11-27 07:50:33', 1),
(2, 'kasir1', 'kasir123', 'cashier', 'Kasir Satu', NULL, NULL, '2025-11-27 07:50:33', 1);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `satuan_jual`
--
ALTER TABLE `satuan_jual`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

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
