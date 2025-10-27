package entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class HoaDon {
    private String maHD;
    private String maKhachHang;
    private String maNVLap;
    private String maKM; // Có thể NULL
    private double tongTien; // decimal(18, 0) trong SQL
    private LocalDateTime ngayLap; // datetime trong SQL
    private String phuongThuc;
    private String loaiHoaDon;

    // Constructor đầy đủ
    public HoaDon(String maHD, String maKhachHang, String maNVLap, String maKM,
                  double tongTien, LocalDateTime ngayLap, String phuongThuc,
                  String loaiHoaDon) {
        this.maHD = maHD;
        this.maKhachHang = maKhachHang;
        this.maNVLap = maNVLap;
        this.maKM = maKM;
        this.tongTien = tongTien;
        this.ngayLap = ngayLap;
        this.phuongThuc = phuongThuc;
        this.loaiHoaDon = loaiHoaDon;
    }

    // Constructor mặc định
    public HoaDon() {
    }

    // Getters và Setters
    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getMaNVLap() { return maNVLap; }
    public void setMaNVLap(String maNVLap) { this.maNVLap = maNVLap; }
    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }
    public String getLoaiHoaDon() { return loaiHoaDon; }
    public void setLoaiHoaDon(String loaiHoaDon) { this.loaiHoaDon = loaiHoaDon; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHD, hoaDon.maHD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHD='" + maHD + '\'' +
                ", maKhachHang='" + maKhachHang + '\'' +
                ", maNVLap='" + maNVLap + '\'' +
                ", maKM='" + maKM + '\'' +
                ", tongTien=" + tongTien +
                ", ngayLap=" + ngayLap +
                ", phuongThuc='" + phuongThuc + '\'' +
                ", loaiHoaDon='" + loaiHoaDon + '\'' +
                '}';
    }
}