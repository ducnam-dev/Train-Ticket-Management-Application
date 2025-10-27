package entity;

import dao.ChuyenTauDao;

import java.util.Objects;
import java.time.LocalDate; // Giữ lại LocalDate để nhất quán

public class VeCuaBanVe {
    private String maVe;
    private String maChuyenTau;
    private String maChoDat;
    private String maNV; // Có thể NULL
    private String maKhachHang; // Có thể NULL
    private String maLoaiVe;
    private double giaVe; // decimal(18, 0) trong SQL
    private String trangThai;

    // Constructor đầy đủ
    public VeCuaBanVe(String maVe, String maChuyenTau, String maChoDat, String maNV, String maKhachHang,
              String maLoaiVe, double giaVe, String trangThai) {
        this.maVe = maVe;
        this.maChuyenTau = maChuyenTau;
        this.maChoDat = maChoDat;
        this.maNV = maNV;
        this.maKhachHang = maKhachHang;
        this.maLoaiVe = maLoaiVe;
        this.giaVe = giaVe;
        this.trangThai = trangThai;
    }

    // Constructor mặc định
    public VeCuaBanVe() {
    }

    // Getters và Setters
    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
    public String getMaChuyenTau() { return maChuyenTau; }
    public void setMaChuyenTau(String maChuyenTau) { this.maChuyenTau = maChuyenTau; }
    public String getMaChoDat() { return maChoDat; }
    public void setMaChoDat(String maChoDat) { this.maChoDat = maChoDat; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getMaLoaiVe() { return maLoaiVe; }
    public void setMaLoaiVe(String maLoaiVe) { this.maLoaiVe = maLoaiVe; }
    public double getGiaVe() { return giaVe; }
    public void setGiaVe(double giaVe) { this.giaVe = giaVe; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VeCuaBanVe ve = (VeCuaBanVe) o;
        return Objects.equals(maVe, ve.maVe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maVe);
    }

    @Override
    public String toString() {
        return "VeCuaBanVe{" +
                "maVe='" + maVe + '\'' +
                ", maChuyenTau='" + maChuyenTau + '\'' +
                ", maChoDat='" + maChoDat + '\'' +
                ", maNV='" + maNV + '\'' +
                ", maKhachHang='" + maKhachHang + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", giaVe=" + giaVe +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}