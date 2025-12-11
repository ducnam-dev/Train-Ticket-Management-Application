package entity;

import java.time.LocalDate; // <-- Thêm import cho LocalDate
import java.sql.Date; // Giả định bạn cần dùng sql.Date để tương thích với CSDL

public class NhanVien {
    // 1. CHUYỂN ĐỔI KIỂU DỮ LIỆU
    public String maNV;
    public String hoTen;
    public String soCCCD;
    public LocalDate ngaySinh; // <--- Đã sửa từ String sang LocalDate
    public String email;
    public String sdt;
    public String gioiTinh;
    public String diaChi;
    public LocalDate ngayVaoLam; // <--- Đã sửa từ String sang LocalDate
    public String chucVu;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String sdt) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.sdt = sdt;
    }

    // 2. CONSTRUCTOR 10 THAM SỐ (Đã cập nhật kiểu tham số)
    public NhanVien(String maNV, String hoTen, String soCCCD, LocalDate ngaySinh, String email, String sdt,
                    String gioiTinh, String diaChi, LocalDate ngayVaoLam, String chucVu) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.ngaySinh = ngaySinh;
        this.email = email;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.ngayVaoLam = ngayVaoLam;
        this.chucVu = chucVu;
    }

    // 3. CONSTRUCTOR 5 THAM SỐ (Giữ lại kiểu String cho các tham số đã sửa để tránh lỗi)
    public NhanVien(String maNV, String hoTen, String cccd, String diaChi, String sdt) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = cccd;
        this.diaChi = diaChi;
        this.sdt = sdt;
        // Các thuộc tính LocalDate còn lại sẽ là null
    }

    // CONSTRUCTOR MỚI: Giả định constructor 10 tham số của bạn trong ManHinh... sử dụng java.sql.Date
    public NhanVien(String maNV, String hoTen, String soCCCD, String rolePlaceholder, String email, String sdt,
                    String gioiTinh, String diaChi, Date ngayVaoLamSql, String chucVu) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.email = email;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        // Chuyển đổi java.sql.Date sang LocalDate (nếu cần)
        this.ngayVaoLam = ngayVaoLamSql != null ? ngayVaoLamSql.toLocalDate() : null;
        this.chucVu = chucVu;
        // Không có ngaySinh trong constructor này, giữ nguyên null
    }

    // =================================================================================
    // GETTERS & SETTERS ĐÃ SỬA
    // =================================================================================

    // ... (Getters/Setters cho các thuộc tính String giữ nguyên) ...

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    // ... (Các getters/setters còn lại giữ nguyên) ...
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    //Method XuLy
}