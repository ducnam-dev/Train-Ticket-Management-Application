USE [master]
GO

-- Xóa cơ sở dữ liệu nếu đã tồn tại
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QuanLyBanVeTau')
BEGIN
    ALTER DATABASE [QuanLyBanVeTau] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [QuanLyBanVeTau];
END
GO

-- TẠO CƠ SỞ DỮ LIỆU MỚI
CREATE DATABASE [QuanLyBanVeTau];
GO

USE [QuanLyBanVeTau]
GO

----------------------------------------------------------------------
-- 1. TẠO CẤU TRÚC BẢNG (TABLE SCHEMA)
----------------------------------------------------------------------

CREATE TABLE [dbo].[Ga](
	[MaGa] [nvarchar](10) NOT NULL PRIMARY KEY,
	[TenGa] [nvarchar](100) NOT NULL,
	[DiaChi] [nvarchar](255) NULL
);

CREATE TABLE [dbo].[Tau](
	[MaTau] [nvarchar](10) NOT NULL PRIMARY KEY,
	[SoHieu] [nvarchar](50) NOT NULL,
	[TrangThai] [nvarchar](50) NULL
);

CREATE TABLE [dbo].[Toa](
	[MaToa] [nvarchar](10) NOT NULL PRIMARY KEY,
	[MaTau] [nvarchar](10) NOT NULL FOREIGN KEY REFERENCES [dbo].[Tau](MaTau),
	[LoaiToa] [nvarchar](50) NOT NULL
);

CREATE TABLE [dbo].[ChoDat](
	[MaCho] [nvarchar](20) NOT NULL PRIMARY KEY,
	[MaToa] [nvarchar](10) NOT NULL FOREIGN KEY REFERENCES [dbo].[Toa](MaToa),
	[SoCho] [nvarchar](10) NULL,
	[LoaiCho] [nvarchar](50) NULL,
	[Khoang] [int] NULL,
	[Tang] [int] NULL
);

CREATE TABLE [dbo].[NhanVien](
	[MaNV] [nvarchar](50) NOT NULL PRIMARY KEY,
	[HoTen] [nvarchar](100) NOT NULL,
	[SoCCCD] [nvarchar](100) NULL UNIQUE,
	[NgaySinh] [date] NULL,
	[Email] [nvarchar](100) NULL,
	[SDT] [nvarchar](100) NULL,
	[GioiTinh] [nvarchar](50) NULL,
	[DiaChi] [nvarchar](255) NOT NULL,
	[NgayVaoLam] [date] NULL,
	[ChucVu] [nvarchar](100) NULL
);

CREATE TABLE [dbo].[TaiKhoan](
	[TenDangNhap] [nvarchar](50) NOT NULL PRIMARY KEY,
	[MaNV] [nvarchar](50) NOT NULL UNIQUE FOREIGN KEY REFERENCES [dbo].[NhanVien](MaNV),
	[MatKhau] [nvarchar](255) NOT NULL,
	[NgayTao] [datetime] DEFAULT GETDATE(),
	[TrangThai] [nvarchar](50) NULL
);

CREATE TABLE [dbo].[ChuyenTau](
	[MaChuyenTau] [nvarchar](50) NOT NULL PRIMARY KEY,
	[MaTau] [nvarchar](10) NOT NULL FOREIGN KEY REFERENCES [dbo].[Tau](MaTau),
	[MaNV] [nvarchar](50) NULL FOREIGN KEY REFERENCES [dbo].[NhanVien](MaNV), -- MaNV có thể null nếu chưa được phân công
	[MaGaKhoiHanh] [nvarchar](10) NOT NULL FOREIGN KEY REFERENCES [dbo].[Ga](MaGa),
	[MaGaDen] [nvarchar](10) NOT NULL FOREIGN KEY REFERENCES [dbo].[Ga](MaGa),
	[NgayKhoiHanh] [date] NOT NULL,
	[GioKhoiHanh] [time](7) NULL,
	[NgayDenDuKien] [date] NULL,
	[GioDenDuKien] [time](7) NULL,
	[TrangThai] [nvarchar](50) NULL
);

CREATE TABLE [dbo].[KhachHang](
	[MaKhachHang] [nvarchar](12) NOT NULL PRIMARY KEY,
	[HoTen] [nvarchar](100) NOT NULL,
	[CCCD] [char](12) NULL UNIQUE,
	[Tuoi] [int] NULL,
	[SoDienThoai] [varchar](15) NULL,
	[GioiTinh] [nvarchar](50) NULL
);

CREATE TABLE [dbo].[LoaiVe](
	[MaLoaiVe] [nvarchar](10) NOT NULL PRIMARY KEY,
	[TenLoaiVe] [nvarchar](50) NOT NULL,
	[MucGiamGia] [float] NULL
);

CREATE TABLE [dbo].[KhuyenMai](
	[MaKM] [nvarchar](10) NOT NULL PRIMARY KEY,
	[TenKM] [nvarchar](100) NOT NULL,
	[NgayBatDau] [date] NULL,
	[NgayKetThuc] [date] NULL,
	[MoTa] [nvarchar](100) NULL,
	[PhanTramGiam] [float] NULL
);

CREATE TABLE [dbo].[HoaDon](
	[MaHD] [nvarchar](20) NOT NULL PRIMARY KEY,
	[MaKhachHang] [nvarchar](12) NOT NULL FOREIGN KEY REFERENCES [dbo].[KhachHang](MaKhachHang),
	[MaNVLap] [nvarchar](50) NOT NULL FOREIGN KEY REFERENCES [dbo].[NhanVien](MaNV),
	[MaKM] [nvarchar](10) NULL FOREIGN KEY REFERENCES [dbo].[KhuyenMai](MaKM),
	[TongTien] [decimal](18, 0) NULL,
	[NgayLap] [datetime] DEFAULT GETDATE(),
	[PhuongThuc] [nvarchar](100) NULL,
	[LoaiHoaDon] [nvarchar](100) NULL
);

CREATE TABLE [dbo].[Ve](
	[MaVe] [nvarchar](20) NOT NULL PRIMARY KEY,
	[MaChuyenTau] [nvarchar](50) NOT NULL FOREIGN KEY REFERENCES [dbo].[ChuyenTau](MaChuyenTau),
	[MaChoDat] [nvarchar](20) NOT NULL FOREIGN KEY REFERENCES [dbo].[ChoDat](MaCho),
	[MaNV] [nvarchar](50) NULL FOREIGN KEY REFERENCES [dbo].[NhanVien](MaNV),
	[MaKhachHang] [nvarchar](12) NULL FOREIGN KEY REFERENCES [dbo].[KhachHang](MaKhachHang),
	[MaLoaiVe] [nvarchar](10) NOT NULL FOREIGN KEY REFERENCES [dbo].[LoaiVe](MaLoaiVe),
	[GiaVe] [decimal](18, 0) NULL,
	[TrangThai] [nvarchar](50) NULL
);

CREATE TABLE [dbo].[ChiTietHoaDon](
	[MaHD] [nvarchar](20) NOT NULL FOREIGN KEY REFERENCES [dbo].[HoaDon](MaHD),
	[MaVe] [nvarchar](20) NOT NULL FOREIGN KEY REFERENCES [dbo].[Ve](MaVe),
	[SoLuong] [int] NOT NULL,
	CONSTRAINT [PK_ChiTietHoaDon] PRIMARY KEY (MaHD, MaVe)
);

CREATE TABLE [dbo].[CaLamViec](
	[MaCaLamViec] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[MaNV] [nvarchar](50) NOT NULL FOREIGN KEY REFERENCES [dbo].[NhanVien](MaNV),
	[ThoiGianBatDau] [datetime] NULL,
	[ThoiGianKetThuc] [datetime] NULL
);

----------------------------------------------------------------------
-- 2. CHÈN DỮ LIỆU ĐÃ CHUẨN HÓA (INSERT DATA)
----------------------------------------------------------------------

-- 1. GA
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'GADN', N'Ga Đà Nẵng', N'791 Hải Phòng, Đà Nẵng');
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'GAHN', N'Ga Hà Nội', N'120 Lê Duẩn, Hà Nội');
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'GANT', N'Ga Nha Trang', N'17 Thái Nguyên, Nha Trang');
INSERT [dbo].[Ga] ([MaGa], [TenGa], [DiaChi]) VALUES (N'GASA', N'Ga Sài Gòn', N'01 Nguyễn Thông, TP. HCM');
GO

-- 2. TAU
INSERT [dbo].[Tau] ([MaTau], [SoHieu], [TrangThai]) VALUES (N'T001', N'SE1', N'Đang hoạt động');
INSERT [dbo].[Tau] ([MaTau], [SoHieu], [TrangThai]) VALUES (N'T002', N'SE2', N'Đang bảo trì');
INSERT [dbo].[Tau] ([MaTau], [SoHieu], [TrangThai]) VALUES (N'T003', N'SE3', N'Đang hoạt động');
INSERT [dbo].[Tau] ([MaTau], [SoHieu], [TrangThai]) VALUES (N'T004', N'TN1', N'Đang hoạt động');
GO

-- 3. TOA
INSERT [dbo].[Toa] ([MaToa], [MaTau], [LoaiToa]) VALUES (N'T001-A', N'T001', N'Ghế mềm điều hòa');
INSERT [dbo].[Toa] ([MaToa], [MaTau], [LoaiToa]) VALUES (N'T001-B', N'T001', N'Giường nằm điều hòa');
INSERT [dbo].[Toa] ([MaToa], [MaTau], [LoaiToa]) VALUES (N'T002-C', N'T002', N'Ghế cứng');
INSERT [dbo].[Toa] ([MaToa], [MaTau], [LoaiToa]) VALUES (N'T003-D', N'T003', N'Giường nằm cao cấp');
INSERT [dbo].[Toa] ([MaToa], [MaTau], [LoaiToa]) VALUES (N'T004-A', N'T004', N'Giường nằm');
INSERT [dbo].[Toa] ([MaToa], [MaTau], [LoaiToa]) VALUES (N'T004-B', N'T004', N'Ghế mềm');
GO

-- 4. CHODAT (Chèn toàn bộ 48 chỗ)
INSERT [dbo].[ChoDat] ([MaCho], [MaToa], [SoCho], [LoaiCho], [Khoang], [Tang]) VALUES 
(N'C01-A-T001', N'T001-A', N'01A', N'Ghế mềm', 1, NULL), (N'C01-B-T001', N'T001-B', N'K1T1', N'Giường nằm', 1, 1), 
(N'C01---T002', N'T002-C', N'01', N'Ghế cứng', NULL, NULL), (N'C02-A-T001', N'T001-A', N'02B', N'Ghế mềm', 1, NULL), 
(N'C02-B-T001', N'T001-B', N'K1T2', N'Giường nằm', 1, 2), (N'C02-D-T003', N'T003-D', N'K2T2', N'Giường nằm', 2, 2), 
(N'C02---T002', N'T002-C', N'02', N'Ghế cứng', NULL, NULL), (N'C03-A-T001', N'T001-A', N'03C', N'Ghế mềm', 1, NULL), 
(N'C03-B-T001', N'T001-B', N'K2T1', N'Giường nằm', 2, 1), (N'C03---T002', N'T002-C', N'03', N'Ghế cứng', NULL, NULL), 
(N'C04-A-T001', N'T001-A', N'04D', N'Ghế mềm', 1, NULL), (N'C04-B-T001', N'T001-B', N'K2T2', N'Giường nằm', 2, 2), 
(N'C04---T002', N'T002-C', N'04', N'Ghế cứng', NULL, NULL), (N'C05-A-T001', N'T001-A', N'05A', N'Ghế mềm', 2, NULL), 
(N'C05-B-T001', N'T001-B', N'K3T1', N'Giường nằm', 3, 1), (N'C05---T002', N'T002-C', N'05', N'Ghế cứng', NULL, NULL), 
(N'C06-A-T001', N'T001-A', N'06B', N'Ghế mềm', 2, NULL), (N'C06-B-T001', N'T001-B', N'K3T2', N'Giường nằm', 3, 2), 
(N'C06---T002', N'T002-C', N'06', N'Ghế cứng', NULL, NULL), (N'C07-A-T001', N'T001-A', N'07C', N'Ghế mềm', 2, NULL), 
(N'C07---T002', N'T002-C', N'07', N'Ghế cứng', NULL, NULL), (N'C08-A-T001', N'T001-A', N'08D', N'Ghế mềm', 2, NULL), 
(N'C08---T002', N'T002-C', N'08', N'Ghế cứng', NULL, NULL), (N'C09-A-T001', N'T001-A', N'09A', N'Ghế mềm', 3, NULL), 
(N'C09---T002', N'T002-C', N'09', N'Ghế cứng', NULL, NULL), (N'C10-A-T001', N'T001-A', N'10B', N'Ghế mềm', 3, NULL), 
(N'C10---T002', N'T002-C', N'10', N'Ghế cứng', NULL, NULL), (N'C11-A-T001', N'T001-A', N'11C', N'Ghế mềm', 3, NULL), 
(N'C11---T002', N'T002-C', N'11', N'Ghế cứng', NULL, NULL), (N'C12-A-T001', N'T001-A', N'12D', N'Ghế mềm', 3, NULL), 
(N'C12---T002', N'T002-C', N'12', N'Ghế cứng', NULL, NULL), (N'C13-A-T001', N'T001-A', N'13A', N'Ghế mềm', 4, NULL), 
(N'C13---T002', N'T002-C', N'13', N'Ghế cứng', NULL, NULL), (N'C14-A-T001', N'T001-A', N'14B', N'Ghế mềm', 4, NULL), 
(N'C14---T002', N'T002-C', N'14', N'Ghế cứng', NULL, NULL), (N'C15-A-T001', N'T001-A', N'15C', N'Ghế mềm', 4, NULL), 
(N'C15---T002', N'T002-C', N'15', N'Ghế cứng', NULL, NULL), (N'C16-A-T001', N'T001-A', N'16D', N'Ghế mềm', 4, NULL), 
(N'C16---T002', N'T002-C', N'16', N'Ghế cứng', NULL, NULL), (N'C17---T002', N'T002-C', N'17', N'Ghế cứng', NULL, NULL), 
(N'C18---T002', N'T002-C', N'18', N'Ghế cứng', NULL, NULL), (N'C19---T002', N'T002-C', N'19', N'Ghế cứng', NULL, NULL), 
(N'C20---T002', N'T002-C', N'20', N'Ghế cứng', NULL, NULL), (N'C21---T002', N'T002-C', N'21', N'Ghế cứng', NULL, NULL), 
(N'C22---T002', N'T002-C', N'22', N'Ghế cứng', NULL, NULL), (N'C23---T002', N'T002-C', N'23', N'Ghế cứng', NULL, NULL), 
(N'C24---T002', N'T002-C', N'24', N'Ghế cứng', NULL, NULL), (N'C25---T002', N'T002-C', N'25', N'Ghế cứng', NULL, NULL), 
(N'C26---T002', N'T002-C', N'26', N'Ghế cứng', NULL, NULL), (N'C27---T002', N'T002-C', N'27', N'Ghế cứng', NULL, NULL), 
(N'C28---T002', N'T002-C', N'28', N'Ghế cứng', NULL, NULL), (N'C29---T002', N'T002-C', N'29', N'Ghế cứng', NULL, NULL), 
(N'C30---T002', N'T002-C', N'30', N'Ghế cứng', NULL, NULL), (N'C31---T002', N'T002-C', N'31', N'Ghế cứng', NULL, NULL), 
(N'C32---T002', N'T002-C', N'32', N'Ghế cứng', NULL, NULL), (N'C33---T002', N'T002-C', N'33', N'Ghế cứng', NULL, NULL), 
(N'C34---T002', N'T002-C', N'34', N'Ghế cứng', NULL, NULL), (N'C35---T002', N'T002-C', N'35', N'Ghế cứng', NULL, NULL), 
(N'C36---T002', N'T002-C', N'36', N'Ghế cứng', NULL, NULL), (N'C37---T002', N'T002-C', N'37', N'Ghế cứng', NULL, NULL), 
(N'C38---T002', N'T002-C', N'38', N'Ghế cứng', NULL, NULL), (N'C39---T002', N'T002-C', N'39', N'Ghế cứng', NULL, NULL), 
(N'C40---T002', N'T002-C', N'40', N'Ghế cứng', NULL, NULL), (N'C41---T002', N'T002-C', N'41', N'Ghế cứng', NULL, NULL), 
(N'C42---T002', N'T002-C', N'42', N'Ghế cứng', NULL, NULL), (N'C43---T002', N'T002-C', N'43', N'Ghế cứng', NULL, NULL), 
(N'C44---T002', N'T002-C', N'44', N'Ghế cứng', NULL, NULL), (N'C45---T002', N'T002-C', N'45', N'Ghế cứng', NULL, NULL), 
(N'C46---T002', N'T002-C', N'46', N'Ghế cứng', NULL, NULL), (N'C47---T002', N'T002-C', N'47', N'Ghế cứng', NULL, NULL), 
(N'C48---T002', N'T002-C', N'48', N'Ghế cứng', NULL, NULL);
GO

-- 5. LOAIVE
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia]) VALUES (N'VT01', N'Người lớn', 0);
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia]) VALUES (N'VT02', N'Trẻ em (Dưới 12)', 0.25);
INSERT [dbo].[LoaiVe] ([MaLoaiVe], [TenLoaiVe], [MucGiamGia]) VALUES (N'VT03', N'Sinh viên', 0.2);
GO

-- 6. KHUYENMAI
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [NgayBatDau], [NgayKetThuc], [MoTa], [PhanTramGiam]) VALUES (N'GIAM10', N'Giảm 10%', CAST(N'2025-10-15' AS Date), CAST(N'2025-10-31' AS Date), N'Giảm trực tiếp 10% tổng hóa đơn', 0.1);
INSERT [dbo].[KhuyenMai] ([MaKM], [TenKM], [NgayBatDau], [NgayKetThuc], [MoTa], [PhanTramGiam]) VALUES (N'KM50K', N'Giảm giá 50k', CAST(N'2025-10-01' AS Date), CAST(N'2025-11-30' AS Date), N'Giảm 50.000 VNĐ cho đơn hàng trên 500k', NULL);
GO

-- 7. NHANVIEN
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu]) VALUES (N'NVBV001', N'Lê Thị B', N'001198000002', CAST(N'1998-10-22' AS Date), N'thib@vetausg.vn', N'0902345678', N'Nữ', N'200 Trường Chinh, TP. HCM', CAST(N'2020-01-10' AS Date), N'Nhân viên bán vé');
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu]) VALUES (N'NVQL001', N'Trần Văn A', N'001195000001', CAST(N'1995-05-15' AS Date), N'vantrana@vetausg.vn', N'0901234567', N'Nam', N'100 Quang Trung, Hà Nội', CAST(N'2018-08-20' AS Date), N'Trưởng phòng');
INSERT [dbo].[NhanVien] ([MaNV], [HoTen], [SoCCCD], [NgaySinh], [Email], [SDT], [GioiTinh], [DiaChi], [NgayVaoLam], [ChucVu]) VALUES (N'NVQL002', N'Phạm Văn C', N'001190000003', CAST(N'1990-01-01' AS Date), N'vanphamc@vetausg.vn', N'0903456789', N'Nam', N'300 Hai Bà Trưng, Đà Nẵng', CAST(N'2015-03-05' AS Date), N'Quản lý');
GO

-- 8. TAIKHOAN
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'tranvana.ql', N'NVQL001', N'matkhau123', CAST(N'2025-10-17T15:32:05.287' AS DateTime), N'Hoạt động');
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'lethib.bv', N'NVBV001', N'pass456', CAST(N'2025-10-17T15:32:05.287' AS DateTime), N'Hoạt động');
INSERT [dbo].[TaiKhoan] ([TenDangNhap], [MaNV], [MatKhau], [NgayTao], [TrangThai]) VALUES (N'phamvanc.ql', N'NVQL002', N'secure789', CAST(N'2025-10-17T15:32:05.287' AS DateTime), N'Hoạt động');
GO

-- 9. KHACHHANG
INSERT [dbo].[KhachHang] ([MaKhachHang], [HoTen], [CCCD], [Tuoi], [SoDienThoai], [GioiTinh]) VALUES 
(N'KH1710250001', N'Nguyễn Văn Khánh', N'079299000001', 26, N'0912111222', N'Nam'),
(N'KH1710250002', N'Đỗ Thị Mai', N'079299000002', 35, N'0912333444', N'Nữ'),
(N'KH1710250003', N'Hoàng Văn Nam', N'079299000003', 10, N'0912555666', N'Nam'),
(N'KH2510250004', N'Trần Thị D', N'079299000004', 28, N'0912777888', N'Nữ'),
(N'KH2510250005', N'Lê Văn E', N'079299000005', 20, N'0912999000', N'Nam');
GO

-- 10. CHUYENTAU
INSERT [dbo].[ChuyenTau] ([MaChuyenTau], [MaTau], [MaNV], [MaGaKhoiHanh], [MaGaDen], [NgayKhoiHanh], [GioKhoiHanh], [NgayDenDuKien], [GioDenDuKien], [TrangThai]) VALUES 
(N'CTSE1DN_SG06', N'T001', N'NVBV001', N'GADN', N'GASA', CAST(N'2025-11-10' AS Date), CAST(N'11:00:00' AS Time), CAST(N'2025-11-11' AS Date), CAST(N'03:00:00' AS Time), N'Chờ Khởi Hành'),
(N'CTSE1HN_DN01', N'T001', N'NVQL002', N'GAHN', N'GADN', CAST(N'2025-11-01' AS Date), CAST(N'20:00:00' AS Time), CAST(N'2025-11-02' AS Date), CAST(N'11:30:00' AS Time), N'Chờ Khởi Hành'),
(N'CTSE2DN_SG05', N'T002', N'NVQL001', N'GADN', N'GASA', CAST(N'2025-11-10' AS Date), CAST(N'08:30:00' AS Time), CAST(N'2025-11-11' AS Date), CAST(N'06:00:00' AS Time), N'Chờ Khởi Hành'),
(N'CTSE3SG_NT02', N'T003', N'NVQL001', N'GASA', N'GANT', CAST(N'2025-11-05' AS Date), CAST(N'09:30:00' AS Time), CAST(N'2025-11-05' AS Date), CAST(N'18:00:00' AS Time), N'Chờ Khởi Hành'),
(N'CTTN1DN_SG03', N'T004', NULL, N'GADN', N'GASA', CAST(N'2025-11-10' AS Date), CAST(N'15:00:00' AS Time), CAST(N'2025-11-11' AS Date), CAST(N'12:00:00' AS Time), N'Đã Khởi Hành');
GO

-- 12. HOADON
INSERT [dbo].[HoaDon] ([MaHD], [MaKhachHang], [MaNVLap], [MaKM], [TongTien], [NgayLap], [PhuongThuc], [LoaiHoaDon]) VALUES 
(N'HD0117102500020001', N'KH1710250001', N'NVBV001', N'KM50K', CAST(1650000 AS Decimal(18, 0)), CAST(N'2025-10-17T10:30:00.000' AS DateTime), N'Chuyển khoản', N'Bán vé trực tiếp'),
(N'HD0117102500020002', N'KH1710250002', N'NVBV001', N'GIAM10', CAST(850000 AS Decimal(18, 0)), CAST(N'2025-10-17T11:00:00.000' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp'),
(N'HD0125102500020003', N'KH2510250004', N'NVBV001', NULL, CAST(600000 AS Decimal(18, 0)), CAST(N'2025-10-25T14:00:00.000' AS DateTime), N'Tiền mặt', N'Bán vé trực tiếp'),
(N'HD0217102500010001', N'KH1710250003', N'NVQL001', NULL, CAST(450000 AS Decimal(18, 0)), CAST(N'2025-10-17T12:00:00.000' AS DateTime), N'Chuyển khoản', N'Đặt vé online'),
(N'HD0225102500010002', N'KH2510250005', N'NVQL001', N'GIAM10', CAST(1440000 AS Decimal(18, 0)), CAST(N'2025-10-25T15:30:00.000' AS DateTime), N'Chuyển khoản', N'Đặt vé online');
GO

-- 13. VE
INSERT [dbo].[Ve] ([MaVe], [MaChuyenTau], [MaChoDat], [MaNV], [MaKhachHang], [MaLoaiVe], [GiaVe], [TrangThai]) VALUES 
(N'VE0117102500001', N'CTSE1HN_DN01', N'C01-A-T001', N'NVQL002', N'KH1710250001', N'VT01', CAST(850000 AS Decimal(18, 0)), N'DA-BAN'),
(N'VE0117102500002', N'CTSE1HN_DN01', N'C02-A-T001', N'NVQL002', N'KH1710250002', N'VT01', CAST(850000 AS Decimal(18, 0)), N'DA-BAN'),
(N'VE0125102500003', N'CTSE1DN_SG06', N'C03-A-T001', N'NVBV001', N'KH2510250004', N'VT01', CAST(600000 AS Decimal(18, 0)), N'DA-BAN'),
(N'VE0217102500001', N'CTSE3SG_NT02', N'C01-B-T001', N'NVQL001', N'KH1710250003', N'VT02', CAST(450000 AS Decimal(18, 0)), N'DA-HUY'),
(N'VE0225102500002', N'CTSE2DN_SG05', N'C01---T002', N'NVQL001', N'KH2510250005', N'VT03', CAST(800000 AS Decimal(18, 0)), N'DA-BAN'),
(N'VE0225102500003', N'CTSE2DN_SG05', N'C02---T002', N'NVQL001', N'KH2510250005', N'VT03', CAST(800000 AS Decimal(18, 0)), N'DA-BAN');
GO

-- 14. CHITIETHOADON
INSERT [dbo].[ChiTietHoaDon] ([MaHD], [MaVe], [SoLuong]) VALUES 
(N'HD0117102500020001', N'VE0117102500001', 1),
(N'HD0117102500020002', N'VE0117102500002', 1),
(N'HD0125102500020003', N'VE0125102500003', 1),
(N'HD0217102500010001', N'VE0217102500001', 1),
(N'HD0225102500010002', N'VE0225102500002', 1),
(N'HD0225102500010002', N'VE0225102500003', 1);
GO

USE [master]
GO
ALTER DATABASE [QuanLyBanVeTau] SET READ_WRITE 
GO