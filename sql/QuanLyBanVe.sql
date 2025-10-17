-- --------------------------------------------------------
-- BƯỚC 1: KHỞI TẠO CƠ SỞ DỮ LIỆU
-- --------------------------------------------------------
-- Khởi tạo hoặc sử dụng CSDL
IF DB_ID(N'QuanLyBanVeTau') IS NULL
BEGIN
    CREATE DATABASE QuanLyBanVeTau;
END
GO

USE QuanLyBanVeTau;
GO

----------------------------------------------------------
-- BƯỚC 2: TẠO CÁC BẢNG VÀ KHÓA CHÍNH
----------------------------------------------------------

-- 1. Bảng KhuyenMai (Khuyến Mãi)
CREATE TABLE KhuyenMai (
    MaKM NVARCHAR(10) PRIMARY KEY,
    TenKM NVARCHAR(100) NOT NULL,
    NgayBatDau DATE,
    NgayKetThuc DATE
);

-- 2. Bảng Tau (Tàu)
CREATE TABLE Tau (
    MaTau NVARCHAR(10) PRIMARY KEY,
    SoHieu NVARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(50)
);

-- 3. Bảng NhanVien (Nhân Viên)
CREATE TABLE NhanVien (
    MaNV INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoCCCD NVARCHAR(100),
    NgaySinh DATE,
    Email NVARCHAR(100),
    SDT NVARCHAR(100),
    DiaChi NVARCHAR(255) NOT NULL,
    NgayVaoLam DATE,
    ChucVu NVARCHAR(100),
	GioTinh NVARCHAR(100)
);

-- 4. Bảng TaiKhoan (Tài Khoản) - Liên kết 1:1 với NhanVien
CREATE TABLE TaiKhoan (
    TenDangNhap NVARCHAR(50) PRIMARY KEY,
    MaNV INT UNIQUE NOT NULL, -- Khóa ngoại
    MatKhau NVARCHAR(255) NOT NULL,
    NgayTao DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(50)
);

-- 5. Bảng Toa (Toa Tàu) - Liên kết 1:N với Tau
CREATE TABLE Toa (
    MaToa NVARCHAR(10) PRIMARY KEY,
    MaTau NVARCHAR(10) NOT NULL, -- Khóa ngoại
    LoaiToa NVARCHAR(50) NOT NULL
);

-- 6. Bảng ChoDat (Chỗ đặt) - Liên kết 1:N với Toa
CREATE TABLE ChoDat (
    MaCho NVARCHAR(20) PRIMARY KEY,
    MaToa NVARCHAR(10) NOT NULL, -- Khóa ngoại
    SoCho NVARCHAR(10),
    LoaiCho NVARCHAR(50),
    Khoang INT,
    Tang INT,
    TrangThai NVARCHAR(50)
);

-- 7. Bảng KhachHang (Khách Hàng)
CREATE TABLE KhachHang (
    MaKhachHang INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    CCCD CHAR(12) UNIQUE,
    Tuoi INT,
    SoDienThoai VARCHAR(15),
    GioiTinh NVARCHAR(255)
);

-- 8. Bảng CaLamViec (Ca Làm Việc) - Liên kết 1:N với NhanVien
CREATE TABLE CaLamViec (
    MaCaLamViec INT IDENTITY(1,1) PRIMARY KEY,
    MaNV INT NOT NULL, -- Khóa ngoại
    ThoiGianBatDau DATETIME,
    ThoiGianKetThuc DATETIME
    -- Đã xóa MaChuyenTau
);

-- 9. Bảng ChuyenTau (Chuyến Tàu) - ĐÃ THÊM LIÊN KẾT VỚI NHÂN VIÊN
CREATE TABLE ChuyenTau (
    MaChuyenTau INT IDENTITY(1,1) PRIMARY KEY,
    MaTau NVARCHAR(10) NOT NULL, -- Khóa ngoại
    MaNhanVien INT, -- KHÓA NGOẠI MỚI: Nhân viên phụ trách/lái tàu
    NgayKhoiHanh DATE NOT NULL,
    GioKhoiHan TIME,
    NgayGienDuKien DATE,
    GioDenDuKien TIME,
    TrangThai NVARCHAR(50)
);

-- 10. Bảng Ve (Vé)
CREATE TABLE Ve (
    MaVe INT IDENTITY(1,1) PRIMARY KEY,
    MaChuyenTau INT NOT NULL, -- Khóa ngoại
    MaChoDat NVARCHAR(20) NOT NULL, -- Khóa ngoại
    MaKhachHang INT, -- Khóa ngoại
    GiaVe DECIMAL(18, 0),
    TrangThai NVARCHAR(50)
);

-- 11. Bảng HoaDon (Hóa Đơn) - Liên kết 1:N với NhanVien, KhuyenMai, CaLamViec
CREATE TABLE HoaDon (
    MaHD INT IDENTITY(1,1) PRIMARY KEY,
    MaKhachHang INT NOT NULL, -- Khóa ngoại
    MaNVLap INT NOT NULL, -- Khóa ngoại (NV Lập Hóa Đơn)
    MaKM NVARCHAR(10), -- Khóa ngoại (KM áp dụng cho tổng HD)
    MaCaLamViec INT, -- Khóa ngoại (Ca làm việc của NV lập HD)
    TongTien DECIMAL(18, 0),
    NgayLap DATETIME DEFAULT GETDATE(),
    PhuongThuc NVARCHAR(100),
    LoaiHoaDon NVARCHAR(100)
);

-- 12. Bảng ChiTietHoaDon (Chi Tiết Hóa Đơn) - Liên kết N:M giữa HoaDon và Ve
CREATE TABLE ChiTietHoaDon (
    MaHD INT NOT NULL, -- Khóa ngoại
    MaVe INT NOT NULL, -- Khóa ngoại
    SoLuong INT NOT NULL DEFAULT 1,
    PRIMARY KEY (MaHD, MaVe)
);


----------------------------------------------------------
-- BƯỚC 3: THIẾT LẬP KHÓA NGOẠI (FOREIGN KEYS)
----------------------------------------------------------

-- Khóa Ngoại cho Bảng TaiKhoan
ALTER TABLE TaiKhoan
ADD CONSTRAINT FK_TaiKhoan_NhanVien
FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

-- Khóa Ngoại cho Bảng Toa
ALTER TABLE Toa
ADD CONSTRAINT FK_Toa_Tau
FOREIGN KEY (MaTau) REFERENCES Tau(MaTau);

-- Khóa Ngoại cho Bảng ChoDat (Ghế cũ)
ALTER TABLE ChoDat
ADD CONSTRAINT FK_ChoDat_Toa
FOREIGN KEY (MaToa) REFERENCES Toa(MaToa);

-- Khóa Ngoại cho Bảng CaLamViec
ALTER TABLE CaLamViec
ADD CONSTRAINT FK_CLV_NhanVien
FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV);

-- Khóa Ngoại cho Bảng ChuyenTau
ALTER TABLE ChuyenTau
ADD CONSTRAINT FK_ChuyenTau_Tau
FOREIGN KEY (MaTau) REFERENCES Tau(MaTau);

-- KHÓA NGOẠI MỚI: ChuyenTau liên kết với NhanVien
ALTER TABLE ChuyenTau
ADD CONSTRAINT FK_ChuyenTau_NhanVien
FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNV);

-- Khóa Ngoại cho Bảng Ve
ALTER TABLE Ve
ADD CONSTRAINT FK_Ve_ChuyenTau
FOREIGN KEY (MaChuyenTau) REFERENCES ChuyenTau(MaChuyenTau);

ALTER TABLE Ve
ADD CONSTRAINT FK_Ve_ChoDat
FOREIGN KEY (MaChoDat) REFERENCES ChoDat(MaCho);

ALTER TABLE Ve
ADD CONSTRAINT FK_Ve_KhachHang
FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang);

-- Khóa Ngoại cho Bảng HoaDon
ALTER TABLE HoaDon
ADD CONSTRAINT FK_HoaDon_KhachHang
FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang);

ALTER TABLE HoaDon
ADD CONSTRAINT FK_HoaDon_NhanVienLap
FOREIGN KEY (MaNVLap) REFERENCES NhanVien(MaNV);

ALTER TABLE HoaDon
ADD CONSTRAINT FK_HoaDon_KhuyenMai
FOREIGN KEY (MaKM) REFERENCES KhuyenMai(MaKM);

ALTER TABLE HoaDon
ADD CONSTRAINT FK_HoaDon_CaLamViec
FOREIGN KEY (MaCaLamViec) REFERENCES CaLamViec(MaCaLamViec);

-- Khóa Ngoại cho Bảng ChiTietHoaDon
ALTER TABLE ChiTietHoaDon
ADD CONSTRAINT FK_CTHD_HoaDon
FOREIGN KEY (MaHD) REFERENCES HoaDon(MaHD);

ALTER TABLE ChiTietHoaDon
ADD CONSTRAINT FK_CTHD_Ve
FOREIGN KEY (MaVe) REFERENCES Ve(MaVe);