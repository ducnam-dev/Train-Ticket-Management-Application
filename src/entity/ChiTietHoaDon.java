package entity;

import java.util.Objects;

public class ChiTietHoaDon {
    private String maHD; // Khóa chính tổ hợp
    private String maVe; // Khóa chính tổ hợp
    private int soLuong;

    // Constructor đầy đủ
    public ChiTietHoaDon(String maHD, String maVe, int soLuong) {
        this.maHD = maHD;
        this.maVe = maVe;
        this.soLuong = soLuong;
    }

    // Constructor mặc định
    public ChiTietHoaDon() {
    }

    // Getters và Setters
    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    // Rất quan trọng khi sử dụng khóa tổng hợp: phải dựa trên cả hai trường
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(maHD, that.maHD) && Objects.equals(maVe, that.maVe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD, maVe);
    }
}