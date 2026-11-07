-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 07, 2026 at 09:52 AM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `brinkmikro`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_pengguna` int NOT NULL,
  `cakupan_tugas` varchar(100) NOT NULL DEFAULT 'Administrasi Sistem',
  `keterangan_detail` varchar(200) DEFAULT '-'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_pengguna`, `cakupan_tugas`, `keterangan_detail`) VALUES
(2, 'Operator Sistem Cabang', 'Pengawasan Akun Petugas Loket'),
(25, 'Administrasi Sistem', '-'),
(26, 'Administrasi Sistem', '-'),
(27, 'Administrasi Sistem', '-');

-- --------------------------------------------------------

--
-- Table structure for table `angsuran`
--

CREATE TABLE `angsuran` (
  `id_angsuran` int NOT NULL,
  `angsuran_ke` int NOT NULL,
  `jumlah_bayar` decimal(15,2) NOT NULL,
  `tanggal_bayar` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `denda` decimal(15,2) NOT NULL DEFAULT '0.00',
  `id_pembiayaan` int NOT NULL,
  `teller_id_pengguna` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `angsuran`
--

INSERT INTO `angsuran` (`id_angsuran`, `angsuran_ke`, `jumlah_bayar`, `tanggal_bayar`, `denda`, `id_pembiayaan`, `teller_id_pengguna`) VALUES
(1, 1, '5150000.00', '2026-05-02 09:30:00', '0.00', 1, 3),
(2, 2, '5150000.00', '2026-08-02 10:15:00', '0.00', 1, 4),
(3, 1, '1060000.00', '2026-03-02 09:00:00', '0.00', 2, 3),
(4, 2, '1060000.00', '2026-04-02 10:00:00', '0.00', 2, 5),
(5, 3, '1060000.00', '2026-05-02 11:30:00', '0.00', 2, 5),
(6, 4, '1060000.00', '2026-06-03 14:00:00', '25000.00', 2, 6),
(7, 5, '1060000.00', '2026-07-02 09:45:00', '0.00', 2, 7),
(8, 6, '1060000.00', '2026-08-02 10:20:00', '0.00', 2, 7),
(9, 7, '1060000.00', '2026-09-02 13:10:00', '0.00', 2, 8),
(10, 1, '1030000.00', '2026-03-03 08:45:00', '0.00', 3, 3),
(11, 2, '1030000.00', '2026-04-03 09:15:00', '0.00', 3, 5),
(12, 3, '1030000.00', '2026-05-03 10:30:00', '0.00', 3, 5),
(13, 4, '1030000.00', '2026-06-03 11:00:00', '0.00', 3, 7),
(14, 5, '1030000.00', '2026-07-03 14:15:00', '0.00', 3, 8),
(15, 6, '1030000.00', '2026-08-03 15:00:00', '0.00', 3, 9),
(16, 1, '1373333.00', '2026-03-08 09:00:00', '0.00', 8, 3),
(17, 2, '1373333.00', '2026-04-08 09:30:00', '0.00', 8, 5),
(18, 3, '1373333.00', '2026-05-08 10:00:00', '0.00', 8, 7),
(19, 4, '1373333.00', '2026-06-08 11:15:00', '0.00', 8, 9),
(20, 5, '1373333.00', '2026-07-08 13:30:00', '0.00', 8, 11),
(21, 6, '1373335.00', '2026-08-08 14:00:00', '0.00', 8, 11),
(22, 1, '1015000.00', '2026-03-12 09:30:00', '0.00', 12, 3),
(23, 2, '1015000.00', '2026-04-12 10:15:00', '0.00', 12, 5),
(24, 3, '1015000.00', '2026-05-12 11:00:00', '0.00', 12, 7),
(28, 2, '200000.00', '2026-11-06 23:34:20', '0.00', 16, 3),
(29, 3, '25000000.00', '2026-11-06 23:35:18', '0.00', 16, 3),
(32, 8, '100000.00', '2026-11-07 16:21:04', '0.00', 2, 3);

-- --------------------------------------------------------

--
-- Table structure for table `manajer`
--

CREATE TABLE `manajer` (
  `id_pengguna` int NOT NULL,
  `batas_persetujuan` decimal(15,2) NOT NULL,
  `wilayah_kerja` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `manajer`
--

INSERT INTO `manajer` (`id_pengguna`, `batas_persetujuan`, `wilayah_kerja`) VALUES
(17, '100000000.00', 'Wilayah Samarinda Kota'),
(18, '75000000.00', 'Wilayah Samarinda Utara'),
(19, '50000000.00', 'Wilayah Samarinda Seberang'),
(20, '50000000.00', 'Wilayah Palaran & Sambutan'),
(23, '50000000.00', 'Wilayah Samarinda Kota');

-- --------------------------------------------------------

--
-- Table structure for table `nasabah`
--

CREATE TABLE `nasabah` (
  `nik` varchar(16) NOT NULL,
  `nama_nasabah` varchar(100) NOT NULL,
  `alamat` varchar(200) NOT NULL,
  `no_telepon` varchar(20) NOT NULL,
  `jenis_nasabah` enum('Petani','UMKM') NOT NULL,
  `tanggal_daftar` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `nasabah`
--

INSERT INTO `nasabah` (`nik`, `nama_nasabah`, `alamat`, `no_telepon`, `jenis_nasabah`, `tanggal_daftar`) VALUES
('1234567890123456', 'ikan', 'ikan', '123456789011', 'Petani', '2026-11-07'),
('62123456789', 'Syauqi', 'Samarinda', '0858208033078', 'Petani', '2026-11-06'),
('6212345678901234', 'Sultan', 'Samarinda', '081234567890', 'UMKM', '2026-11-07'),
('6472010406900011', 'Marlina', 'Kios Pasar Segiri Blok B No. 12', '08215550011', 'UMKM', '2026-01-15'),
('6472011001850001', 'Supardi', 'Jl. Makroman RT 04, Sambutan', '08125550001', 'Petani', '2026-01-05'),
('6472011503820002', 'Joko Prayitno', 'Jl. Kebon Agung RT 12, Lempake', '08125550002', 'Petani', '2026-01-06'),
('6472021407870012', 'Syamsuddin', 'Pasar Pagi Lantai 2 Blok C-05', '08225550012', 'UMKM', '2026-01-16'),
('6472022005880003', 'H. Arsyad', 'Jl. Giri Rejo RT 02, Samarinda Utara', '08135550003', 'Petani', '2026-01-07'),
('6472031108790004', 'Wayan Sudana', 'Desa Kerta Buana, Muara Badak', '08135550004', 'Petani', '2026-01-08'),
('6472032209920013', 'Ratna Sari', 'Jl. Pemuda No. 45, Sungai Pinang', '08525550013', 'UMKM', '2026-01-17'),
('6472041811860014', 'Deddy Prasetyo', 'Jl. Cipto Mangunkusumo No. 88, Harapan Baru', '08535550014', 'UMKM', '2026-01-18'),
('6472042512830005', 'Hasan Basri', 'Jl. Poros Samarinda-Bontang KM 24', '08125550005', 'Petani', '2026-01-09'),
('6472050904940015', 'Fitri Handayani', 'Jl. KH. Harun Nafsi Gg. Barokah, Loa Janan', '08215550015', 'UMKM', '2026-01-19'),
('6472051202860006', 'Ketut Suardika', 'Jl. Sukorejo RT 18, Lempake', '08135550006', 'Petani', '2026-01-10'),
('6472061807840007', 'Slamet Riyadi', 'Jl. Saliki Trans Kalimantan', '08125550007', 'Petani', '2026-01-11'),
('6472062705910016', 'Budi Harsono', 'Pasar Kedondong Kios No. 08', '08225550016', 'UMKM', '2026-01-20'),
('6472071908880017', 'Endang Triastuti', 'Jl. Bung Tomo RT 09, Samarinda Seberang', '08525550017', 'UMKM', '2026-01-21'),
('6472072209800008', 'Mansyur Said', 'Desa Teluk Dalam, Kutai Kartanegara', '08135550008', 'Petani', '2026-01-12'),
('6472080310850018', 'Rusli Thamrin', 'Jl. Otto Iskandardinata No. 23', '08535550018', 'UMKM', '2026-01-22'),
('6472081404890009', 'Kuswanto', 'Jl. Berambai RT 05, Sempaja Utara', '08125550009', 'Petani', '2026-01-13'),
('6472091512930019', 'Dewi Anggraini', 'Pasar Sungai Dama Blok A-02', '08215550019', 'UMKM', '2026-01-23'),
('6472093011810010', 'Syahril Efendi', 'Jl. Karang Tunggal Blok B', '08135550010', 'Petani', '2026-01-14'),
('6472102801900020', 'M. Fadli Rahman', 'Jl. DI Panjaitan Ruko No. 14', '08225550020', 'Petani', '2026-01-24');

-- --------------------------------------------------------

--
-- Table structure for table `pedagang_umkm`
--

CREATE TABLE `pedagang_umkm` (
  `nik` varchar(16) NOT NULL,
  `jenis_usaha` varchar(100) NOT NULL,
  `lokasi_usaha` varchar(200) NOT NULL,
  `omzet_bulanan` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pedagang_umkm`
--

INSERT INTO `pedagang_umkm` (`nik`, `jenis_usaha`, `lokasi_usaha`, `omzet_bulanan`) VALUES
('6472010406900011', 'Sembako & Bahan Pokok', 'Pasar Segiri Blok B', '35000000.00'),
('6472021407870012', 'Konveksi & Pakaian Muslim', 'Pasar Pagi Lantai 2', '28000000.00'),
('6472032209920013', 'Rumah Makan & Kuliner', 'Jl. Pemuda', '42000000.00'),
('6472041811860014', 'Bengkel & Toko Sparepart', 'Jl. Cipto Mangunkusumo', '50000000.00'),
('6472050904940015', 'Produksi Kue Basah & Jajanan', 'Jl. KH. Harun Nafsi', '18000000.00'),
('6472062705910016', 'Pedagang Buah Musiman', 'Pasar Kedondong', '22000000.00'),
('6472071908880017', 'Warung Madura 24 Jam', 'Jl. Bung Tomo', '32000000.00'),
('6472080310850018', 'Toko Plastik & Kemasan', 'Jl. Otto Iskandardinata', '40000000.00'),
('6472091512930019', 'Lapak Daging Ayam Potong', 'Pasar Sungai Dama', '45000000.00'),
('6472102801900020', 'Percetakan & Fotokopi', 'Jl. DI Panjaitan', '25000000.00');

-- --------------------------------------------------------

--
-- Table structure for table `pembiayaan`
--

CREATE TABLE `pembiayaan` (
  `id_pembiayaan` int NOT NULL,
  `jumlah_pengajuan` decimal(15,2) NOT NULL,
  `jangka_waktu_bulan` int NOT NULL,
  `suku_bunga_persen` decimal(5,2) NOT NULL DEFAULT '6.00',
  `total_kewajiban` decimal(15,2) NOT NULL,
  `skema_pembiayaan` enum('Mingguan','Bulanan','Musiman Panen') NOT NULL,
  `skor_kelayakan` int DEFAULT NULL,
  `status_pembiayaan` enum('Diajukan','Disetujui','Ditolak','Lunas') NOT NULL DEFAULT 'Diajukan',
  `tanggal_pengajuan` date NOT NULL,
  `nasabah_nik` varchar(16) NOT NULL,
  `manajer_id_pengguna` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pembiayaan`
--

INSERT INTO `pembiayaan` (`id_pembiayaan`, `jumlah_pengajuan`, `jangka_waktu_bulan`, `suku_bunga_persen`, `total_kewajiban`, `skema_pembiayaan`, `skor_kelayakan`, `status_pembiayaan`, `tanggal_pengajuan`, `nasabah_nik`, `manajer_id_pengguna`) VALUES
(1, '10000000.00', 6, '6.00', '10300000.00', 'Musiman Panen', 88, 'Lunas', '2026-02-01', '6472011001850001', 17),
(2, '12000000.00', 12, '6.00', '12720000.00', 'Bulanan', 85, 'Disetujui', '2026-02-02', '6472010406900011', 17),
(3, '6000000.00', 6, '6.00', '6180000.00', 'Bulanan', 78, 'Lunas', '2026-02-03', '6472022005880003', 18),
(4, '20000000.00', 12, '6.00', '21200000.00', 'Bulanan', 92, 'Disetujui', '2026-02-04', '6472041811860014', 19),
(5, '15000000.00', 6, '6.00', '15450000.00', 'Musiman Panen', 82, 'Disetujui', '2026-02-05', '6472011503820002', 18),
(6, '10000000.00', 6, '6.00', '10300000.00', 'Mingguan', 45, 'Ditolak', '2026-02-06', '6472050904940015', 19),
(7, '18000000.00', 6, '6.00', '18540000.00', 'Musiman Panen', 90, 'Disetujui', '2026-02-07', '6472031108790004', 17),
(8, '8000000.00', 6, '6.00', '8240000.00', 'Bulanan', 80, 'Lunas', '2026-02-08', '6472021407870012', 17),
(9, '10000000.00', 12, '6.00', '10600000.00', 'Bulanan', 75, 'Disetujui', '2026-02-09', '6472042512830005', 18),
(10, '15000000.00', 12, '6.00', '15900000.00', 'Bulanan', 86, 'Disetujui', '2026-02-10', '6472032209920013', 17),
(11, '5000000.00', 6, '6.00', '5150000.00', 'Bulanan', 72, 'Disetujui', '2026-02-11', '6472051202860006', 18),
(12, '4000000.00', 3, '6.00', '4060000.00', 'Mingguan', 84, 'Lunas', '2026-02-12', '6472062705910016', 17),
(13, '20000000.00', 6, '6.00', '20600000.00', 'Musiman Panen', 89, 'Disetujui', '2026-02-13', '6472061807840007', 18),
(14, '6000000.00', 6, '6.00', '6180000.00', 'Bulanan', 76, 'Disetujui', '2026-02-14', '6472071908880017', 19),
(15, '12000000.00', 12, '6.00', '12720000.00', 'Bulanan', 79, 'Disetujui', '2026-02-15', '6472072209800008', 20),
(16, '25000000.00', 12, '6.00', '26500000.00', 'Bulanan', 91, 'Disetujui', '2026-02-16', '6472080310850018', 17),
(17, '8000000.00', 6, '6.00', '8240000.00', 'Musiman Panen', 70, 'Diajukan', '2026-03-01', '6472081404890009', NULL),
(18, '10000000.00', 6, '6.00', '10300000.00', 'Bulanan', 75, 'Diajukan', '2026-03-02', '6472091512930019', NULL),
(19, '15000000.00', 12, '6.00', '15900000.00', 'Bulanan', 48, 'Ditolak', '2026-03-03', '6472093011810010', 18),
(20, '12000000.00', 12, '6.00', '12720000.00', 'Bulanan', 81, 'Diajukan', '2026-03-04', '6472102801900020', NULL),
(21, '1000000.00', 5, '6.00', '1060000.00', 'Mingguan', NULL, 'Diajukan', '2026-11-06', '6472102801900020', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `pengguna`
--

CREATE TABLE `pengguna` (
  `id_pengguna` int NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `peran` enum('Admin','Teller','Manajer') NOT NULL,
  `status_aktif` enum('Aktif','Terkunci','Nonaktif') DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `pengguna`
--

INSERT INTO `pengguna` (`id_pengguna`, `nama_lengkap`, `username`, `password`, `peran`, `status_aktif`) VALUES
(2, 'Dimas Arya Pratama', 'admin_dimas', '$2a$10$Cs54ylEu1/xVhzHYVF8znu1a1/Or3I7ns8XWk0.yxrWP9ObB0xTYa', 'Admin', 'Terkunci'),
(3, 'Siti Nurhaliza', 'teller_siti', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(4, 'Ahmad Zulkarnain', 'teller_ahmad', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(5, 'Rina Anggraini', 'teller_rina', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(6, 'Fajar Nugraha', 'teller_fajar', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(7, 'Dewi Lestari', 'teller_dewi', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(8, 'Bayu Setiawan', 'teller_bayu', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(9, 'Indah Permatasari', 'teller_indah', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(10, 'Reza Rahardian', 'teller_reza', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(11, 'Nadia Utami', 'teller_nadia', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(12, 'Hendra Gunawan', 'teller_hendra', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(13, 'Maya Kartika', 'teller_maya', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(14, 'Doni Kusuma', 'teller_doni', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(15, 'Putri Wulandari', 'teller_putri', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(16, 'Eko Wahyudi', 'teller_eko', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Teller', 'Aktif'),
(17, 'Bambang Sudarmono, M.M.', 'manajer_bambang', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Manajer', 'Aktif'),
(18, 'Sri Wahyuningsih, S.E.', 'manajer_sri', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Manajer', 'Aktif'),
(19, 'Agus Firmansyah, S.E.', 'manajer_agus', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Manajer', 'Aktif'),
(20, 'Irwan Hidayat, M.Ak.', 'manajer_irwan', '$2y$10$wT8K8U9rEwSjK1M2N3O4PeX8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M', 'Manajer', 'Aktif'),
(21, 'Super Admin', 'admin', 'admin123', 'Admin', 'Aktif'),
(23, 'Manajer Pusat', 'manajer', 'manajer123', 'Manajer', 'Aktif'),
(25, 'isan', 'isan123', 'admin897e98q6', 'Admin', 'Aktif'),
(26, 'rias', 'riaser', '12345678', 'Admin', 'Aktif'),
(27, 'Mikhel Febian', 'Mikheladmin', 'adminmikhel', 'Admin', 'Aktif');

-- --------------------------------------------------------

--
-- Table structure for table `petani`
--

CREATE TABLE `petani` (
  `nik` varchar(16) NOT NULL,
  `luas_lahan` decimal(10,2) NOT NULL,
  `komoditas_utama` varchar(100) NOT NULL,
  `pendapatan_tahunan` decimal(15,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `petani`
--

INSERT INTO `petani` (`nik`, `luas_lahan`, `komoditas_utama`, `pendapatan_tahunan`) VALUES
('6472011001850001', '2.50', 'Padi Sawah', '48000000.00'),
('6472011503820002', '4.00', 'Kelapa Sawit', '96000000.00'),
('6472022005880003', '1.80', 'Jagung Hibrida', '36000000.00'),
('6472031108790004', '3.20', 'Karet Alam', '65000000.00'),
('6472042512830005', '2.00', 'Lada Putih', '55000000.00'),
('6472051202860006', '1.50', 'Sayur Mayur Hortikultura', '42000000.00'),
('6472061807840007', '3.50', 'Kelapa Sawit', '85000000.00'),
('6472072209800008', '2.20', 'Padi Sawah', '45000000.00'),
('6472081404890009', '1.00', 'Cabai Rawit', '38000000.00'),
('6472093011810010', '2.80', 'Karet Alam', '58000000.00');

-- --------------------------------------------------------

--
-- Table structure for table `rekening_tabungan`
--

CREATE TABLE `rekening_tabungan` (
  `nomor_rekening` varchar(30) NOT NULL,
  `saldo` decimal(15,2) NOT NULL DEFAULT '0.00',
  `tanggal_pembukaan` date NOT NULL,
  `status_rekening` enum('Aktif','Perlu Verifikasi','Nonaktif','Ditutup') NOT NULL DEFAULT 'Perlu Verifikasi',
  `nasabah_nik` varchar(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `rekening_tabungan`
--

INSERT INTO `rekening_tabungan` (`nomor_rekening`, `saldo`, `tanggal_pembukaan`, `status_rekening`, `nasabah_nik`) VALUES
('10012026001', '6500000.00', '2026-01-10', 'Aktif', '6472011001850001'),
('10012026002', '12000000.00', '2026-01-11', 'Aktif', '6472011503820002'),
('10012026003', '4200000.00', '2026-01-12', 'Aktif', '6472022005880003'),
('10012026004', '158500000.00', '2026-01-13', 'Aktif', '6472031108790004'),
('10012026005', '3000000.00', '2026-01-14', 'Aktif', '6472042512830005'),
('10012026006', '5000000.00', '2026-01-15', 'Aktif', '6472051202860006'),
('10012026007', '7500000.00', '2026-01-16', 'Aktif', '6472061807840007'),
('10012026008', '3500000.00', '2026-01-17', 'Aktif', '6472072209800008'),
('10012026009', '4000000.00', '2026-01-18', 'Aktif', '6472081404890009'),
('10012026010', '6000000.00', '2026-01-19', 'Aktif', '6472093011810010'),
('10012026011', '7500000.00', '2026-01-20', 'Aktif', '6472010406900011'),
('10012026012', '5000000.00', '2026-01-21', 'Aktif', '6472021407870012'),
('100120260128', '978938790.00', '2026-11-06', 'Ditutup', '62123456789'),
('10012026013', '9000000.00', '2026-01-22', 'Aktif', '6472032209920013'),
('10012026014', '15000000.00', '2026-01-23', 'Aktif', '6472041811860014'),
('10012026015', '2500000.00', '2026-01-24', 'Aktif', '6472050904940015'),
('10012026016', '4500000.00', '2026-01-25', 'Aktif', '6472062705910016'),
('10012026017', '6200000.00', '2026-01-26', 'Aktif', '6472071908880017'),
('10012026018', '8800000.00', '2026-01-27', 'Aktif', '6472080310850018'),
('10012026019', '11000000.00', '2026-01-28', 'Aktif', '6472091512930019'),
('10012026020', '10199901.00', '2026-01-29', 'Aktif', '6472102801900020');

-- --------------------------------------------------------

--
-- Table structure for table `teller`
--

CREATE TABLE `teller` (
  `id_pengguna` int NOT NULL,
  `loket` varchar(20) NOT NULL,
  `shift_teller` enum('Pagi','Siang') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `teller`
--

INSERT INTO `teller` (`id_pengguna`, `loket`, `shift_teller`) VALUES
(3, 'Loket 01', 'Pagi'),
(4, 'Loket 01', 'Siang'),
(5, 'Loket 02', 'Pagi'),
(6, 'Loket 02', 'Siang'),
(7, 'Loket 03', 'Pagi'),
(8, 'Loket 03', 'Siang'),
(9, 'Loket 04', 'Pagi'),
(10, 'Loket 04', 'Siang'),
(11, 'Loket 05', 'Pagi'),
(12, 'Loket 05', 'Siang'),
(13, 'Loket 06', 'Pagi'),
(14, 'Loket 06', 'Siang'),
(15, 'Loket 07', 'Pagi'),
(16, 'Loket 07', 'Siang');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi_tabungan`
--

CREATE TABLE `transaksi_tabungan` (
  `id_transaksi` int NOT NULL,
  `tanggal_transaksi` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `jenis_transaksi` enum('Setor Tunai','Tarik Tunai') NOT NULL,
  `jumlah` decimal(15,2) NOT NULL,
  `nomor_rekening` varchar(30) NOT NULL,
  `teller_id_pengguna` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `transaksi_tabungan`
--

INSERT INTO `transaksi_tabungan` (`id_transaksi`, `tanggal_transaksi`, `jenis_transaksi`, `jumlah`, `nomor_rekening`, `teller_id_pengguna`) VALUES
(1, '2026-01-10 08:30:00', 'Setor Tunai', '5000000.00', '10012026001', 3),
(2, '2026-01-11 09:15:00', 'Setor Tunai', '15000000.00', '10012026002', 3),
(3, '2026-01-12 10:00:00', 'Setor Tunai', '4200000.00', '10012026003', 5),
(4, '2026-01-13 08:45:00', 'Setor Tunai', '10000000.00', '10012026004', 5),
(6, '2026-01-14 09:00:00', 'Setor Tunai', '4000000.00', '10012026005', 7),
(7, '2026-01-14 14:15:00', 'Tarik Tunai', '1000000.00', '10012026005', 8),
(8, '2026-01-15 10:30:00', 'Setor Tunai', '5000000.00', '10012026006', 7),
(9, '2026-01-16 09:20:00', 'Setor Tunai', '7500000.00', '10012026007', 9),
(10, '2026-01-17 11:00:00', 'Setor Tunai', '3500000.00', '10012026008', 9),
(11, '2026-01-18 08:50:00', 'Setor Tunai', '4000000.00', '10012026009', 11),
(12, '2026-01-19 10:15:00', 'Setor Tunai', '6000000.00', '10012026010', 11),
(13, '2026-01-20 08:40:00', 'Setor Tunai', '8000000.00', '10012026011', 3),
(14, '2026-01-20 14:00:00', 'Tarik Tunai', '500000.00', '10012026011', 4),
(15, '2026-01-21 09:10:00', 'Setor Tunai', '5000000.00', '10012026012', 3),
(16, '2026-01-22 10:30:00', 'Setor Tunai', '10000000.00', '10012026013', 5),
(17, '2026-01-22 15:00:00', 'Tarik Tunai', '1000000.00', '10012026013', 6),
(18, '2026-01-23 08:30:00', 'Setor Tunai', '15000000.00', '10012026014', 7),
(19, '2026-01-24 09:45:00', 'Setor Tunai', '3000000.00', '10012026015', 9),
(20, '2026-01-24 13:45:00', 'Tarik Tunai', '500000.00', '10012026015', 10),
(21, '2026-01-25 10:00:00', 'Setor Tunai', '4500000.00', '10012026016', 11),
(22, '2026-01-26 11:15:00', 'Setor Tunai', '6200000.00', '10012026017', 13),
(23, '2026-01-27 08:50:00', 'Setor Tunai', '8800000.00', '10012026018', 13),
(24, '2026-01-28 09:30:00', 'Setor Tunai', '11000000.00', '10012026019', 15),
(25, '2026-01-29 10:45:00', 'Setor Tunai', '3800000.00', '10012026020', 15),
(28, '2026-11-06 13:47:52', 'Setor Tunai', '200000.00', '10012026020', 3),
(29, '2026-11-06 13:50:17', 'Tarik Tunai', '3000000.00', '10012026020', 3),
(30, '2026-11-06 13:52:27', 'Tarik Tunai', '500000.00', '10012026020', 3),
(31, '2026-11-06 13:53:18', 'Setor Tunai', '10000000.00', '10012026020', 3),
(34, '2026-11-07 11:03:30', 'Setor Tunai', '1234568.00', '100120260128', 3),
(37, '2026-11-07 11:14:01', 'Setor Tunai', '12345678.00', '100120260128', 3),
(38, '2026-11-07 11:14:10', 'Tarik Tunai', '123456789.00', '100120260128', 3),
(39, '2026-11-07 11:15:11', 'Tarik Tunai', '123456789.00', '100120260128', 3),
(40, '2026-11-07 15:36:07', 'Setor Tunai', '50000.00', '100120260128', 4),
(41, '2026-11-07 15:36:16', 'Tarik Tunai', '100000.00', '100120260128', 4),
(42, '2026-11-07 16:14:59', 'Tarik Tunai', '300000.00', '10012026020', 4);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_pengguna`);

--
-- Indexes for table `angsuran`
--
ALTER TABLE `angsuran`
  ADD PRIMARY KEY (`id_angsuran`),
  ADD KEY `fk_angsuran_pembiayaan` (`id_pembiayaan`),
  ADD KEY `fk_angsuran_teller` (`teller_id_pengguna`);

--
-- Indexes for table `manajer`
--
ALTER TABLE `manajer`
  ADD PRIMARY KEY (`id_pengguna`);

--
-- Indexes for table `nasabah`
--
ALTER TABLE `nasabah`
  ADD PRIMARY KEY (`nik`);

--
-- Indexes for table `pedagang_umkm`
--
ALTER TABLE `pedagang_umkm`
  ADD PRIMARY KEY (`nik`);

--
-- Indexes for table `pembiayaan`
--
ALTER TABLE `pembiayaan`
  ADD PRIMARY KEY (`id_pembiayaan`),
  ADD KEY `fk_pembiayaan_nasabah` (`nasabah_nik`),
  ADD KEY `fk_pembiayaan_manajer` (`manajer_id_pengguna`);

--
-- Indexes for table `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id_pengguna`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `petani`
--
ALTER TABLE `petani`
  ADD PRIMARY KEY (`nik`);

--
-- Indexes for table `rekening_tabungan`
--
ALTER TABLE `rekening_tabungan`
  ADD PRIMARY KEY (`nomor_rekening`),
  ADD UNIQUE KEY `uq_rekening_nasabah` (`nasabah_nik`);

--
-- Indexes for table `teller`
--
ALTER TABLE `teller`
  ADD PRIMARY KEY (`id_pengguna`);

--
-- Indexes for table `transaksi_tabungan`
--
ALTER TABLE `transaksi_tabungan`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `fk_transaksi_rekening` (`nomor_rekening`),
  ADD KEY `fk_transaksi_teller` (`teller_id_pengguna`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `angsuran`
--
ALTER TABLE `angsuran`
  MODIFY `id_angsuran` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `pembiayaan`
--
ALTER TABLE `pembiayaan`
  MODIFY `id_pembiayaan` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `pengguna`
--
ALTER TABLE `pengguna`
  MODIFY `id_pengguna` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `transaksi_tabungan`
--
ALTER TABLE `transaksi_tabungan`
  MODIFY `id_transaksi` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `fk_admin_pengguna` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`) ON DELETE CASCADE;

--
-- Constraints for table `angsuran`
--
ALTER TABLE `angsuran`
  ADD CONSTRAINT `fk_angsuran_pembiayaan` FOREIGN KEY (`id_pembiayaan`) REFERENCES `pembiayaan` (`id_pembiayaan`) ON DELETE RESTRICT,
  ADD CONSTRAINT `fk_angsuran_teller` FOREIGN KEY (`teller_id_pengguna`) REFERENCES `teller` (`id_pengguna`) ON DELETE RESTRICT;

--
-- Constraints for table `manajer`
--
ALTER TABLE `manajer`
  ADD CONSTRAINT `fk_manajer_pengguna` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`) ON DELETE CASCADE;

--
-- Constraints for table `pedagang_umkm`
--
ALTER TABLE `pedagang_umkm`
  ADD CONSTRAINT `fk_umkm_nasabah` FOREIGN KEY (`nik`) REFERENCES `nasabah` (`nik`) ON DELETE CASCADE;

--
-- Constraints for table `pembiayaan`
--
ALTER TABLE `pembiayaan`
  ADD CONSTRAINT `fk_pembiayaan_manajer` FOREIGN KEY (`manajer_id_pengguna`) REFERENCES `manajer` (`id_pengguna`) ON DELETE RESTRICT,
  ADD CONSTRAINT `fk_pembiayaan_nasabah` FOREIGN KEY (`nasabah_nik`) REFERENCES `nasabah` (`nik`) ON DELETE RESTRICT;

--
-- Constraints for table `petani`
--
ALTER TABLE `petani`
  ADD CONSTRAINT `fk_petani_nasabah` FOREIGN KEY (`nik`) REFERENCES `nasabah` (`nik`) ON DELETE CASCADE;

--
-- Constraints for table `rekening_tabungan`
--
ALTER TABLE `rekening_tabungan`
  ADD CONSTRAINT `fk_rekening_nasabah` FOREIGN KEY (`nasabah_nik`) REFERENCES `nasabah` (`nik`) ON DELETE RESTRICT;

--
-- Constraints for table `teller`
--
ALTER TABLE `teller`
  ADD CONSTRAINT `fk_teller_pengguna` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`) ON DELETE CASCADE;

--
-- Constraints for table `transaksi_tabungan`
--
ALTER TABLE `transaksi_tabungan`
  ADD CONSTRAINT `fk_transaksi_rekening` FOREIGN KEY (`nomor_rekening`) REFERENCES `rekening_tabungan` (`nomor_rekening`) ON DELETE RESTRICT,
  ADD CONSTRAINT `fk_transaksi_teller` FOREIGN KEY (`teller_id_pengguna`) REFERENCES `teller` (`id_pengguna`) ON DELETE RESTRICT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
