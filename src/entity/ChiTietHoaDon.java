package entity;

import java.util.Objects;

// Lớp này ánh xạ bảng ChiTietHoaDon (MaHD, MaVe là khóa chính tổ hợp)
public class ChiTietHoaDon {
    private String maHD; // Mã hóa đơn (Khóa chính tổ hợp)
    private String maVe; // Mã vé (Khóa chính tổ hợp)
    private int soLuong;

    public ChiTietHoaDon(String maHD, String maVe, int soLuong) {
        this.maHD = maHD;
        this.maVe = maVe;
        this.soLuong = soLuong;
    }

    public ChiTietHoaDon() {
    }

    // Getters và Setters
    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    // Quan trọng khi sử dụng khóa tổ hợp
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return soLuong == that.soLuong && Objects.equals(maHD, that.maHD) && Objects.equals(maVe, that.maVe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD, maVe, soLuong);
    }
}