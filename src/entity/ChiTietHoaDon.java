package entity;

// Class này đóng gói thông tin chi tiết cần thiết cho UI (Tra cứu/Trả vé)
public class ChiTietHoaDon {

    // --- CÁC TRƯỜNG CƠ BẢN CỦA VÉ/HÓA ĐƠN ---
    private String maHD;
    private String maVe;
    private double giaVe;

    // --- THÔNG TIN KHÁCH HÀNG (Từ KhachHang) ---
    private String hoTenKhachHang;
    private String soDienThoai;
    private String soCCCD;

    // --- THÔNG TIN CHUYẾN TÀU (Từ ChuyenTau, Ga) ---
    private String maChuyenTau;
    private String tuyenDuong; // Ví dụ: Ga A - Ga B
    private String thoiGianKhoiHanh; // Ngày và Giờ khởi hành

    // --- THÔNG TIN CHỖ (Từ ChoDat, Toa) ---
    private String maToa;
    private String soGhe;
    private String trangThaiVe; // Trạng thái hiện tại của vé


    public ChiTietHoaDon() {
    }

    // =======================================================
    // GETTERS VÀ SETTERS
    // =======================================================

    // CÁC TRƯỜNG CƠ BẢN
    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }

    public double getGiaVe() { return giaVe; }
    public void setGiaVe(double giaVe) { this.giaVe = giaVe; }

    // THÔNG TIN KHÁCH HÀNG
    public String getHoTenKhachHang() { return hoTenKhachHang; }
    public void setHoTenKhachHang(String hoTenKhachHang) { this.hoTenKhachHang = hoTenKhachHang; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getSoCCCD() { return soCCCD; }
    public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }

    // THÔNG TIN CHUYẾN TÀU
    public String getMaChuyenTau() { return maChuyenTau; }
    public void setMaChuyenTau(String maChuyenTau) { this.maChuyenTau = maChuyenTau; }

    public String getTuyenDuong() { return tuyenDuong; }
    public void setTuyenDuong(String tuyenDuong) { this.tuyenDuong = tuyenDuong; }

    public String getThoiGianKhoiHanh() { return thoiGianKhoiHanh; }
    public void setThoiGianKhoiHanh(String thoiGianKhoiHanh) { this.thoiGianKhoiHanh = thoiGianKhoiHanh; }

    // THÔNG TIN CHỖ
    public String getMaToa() { return maToa; }
    public void setMaToa(String maToa) { this.maToa = maToa; }

    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { this.soGhe = soGhe; }

    public String getTrangThaiVe() { return trangThaiVe; }
    public void setTrangThaiVe(String trangThaiVe) { this.trangThaiVe = trangThaiVe; }

    // *LƯU Ý: Bạn cần cập nhật VeDAOImpl để trả về đối tượng này thay vì Ve.
}