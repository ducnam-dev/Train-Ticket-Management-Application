package entity;

import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private LocalDateTime ngayLap;
    private String maKhachHang;
    private String nhanVienLap;
    private String maKM;
    private double tongCong; // Tổng tiền gốc (chưa KM)
    private double tongTien; // Tổng tiền cuối cùng (sau KM)
    private String phuongThucThanhToan;
    private String maHoaDon_Goc; // Mã hóa đơn gốc cho hóa đơn trả vé và đổi vé
    private String loaiHoaDon; // Loại hóa đơn (Mua vé, Trả vé, Đổi vé)

    // Constructor đầy đủ sử dụng setters
    public HoaDon(String maHoaDon, LocalDateTime ngayLap, String maKhachHang, String nhanVienLap, String maKM, double tongCong, double tongTien, String phuongThucThanhToan, String maHoaDon_Goc, String loaiHoaDon) {
        this.setMaHoaDon(maHoaDon);
        this.setNgayLap(ngayLap);
        this.setMaKhachHang(maKhachHang);
        this.setNhanVienLap(nhanVienLap);
        this.setMaKM(maKM);
        this.setTongCong(tongCong);
        this.setTongTien(tongTien);
        this.setPhuongThucThanhToan(phuongThucThanhToan);
        this.setMaHoaDon_Goc(maHoaDon_Goc); // Đã thêm
        this.setLoaiHoaDon(loaiHoaDon);     // Đã thêm
    }

    // Constructor mặc định (không tham số)
    public HoaDon() {
    }

    // Getter & Setter gọn
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getNhanVienLap() { return nhanVienLap; }
    public void setNhanVienLap(String nhanVienLap) { this.nhanVienLap = nhanVienLap; }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public double getTongCong() { return tongCong; }
    public void setTongCong(double tongCong) { this.tongCong = tongCong; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public String getMaHoaDon_Goc() { return maHoaDon_Goc; }
    public void setMaHoaDon_Goc(String maHoaDon_Goc) { this.maHoaDon_Goc = maHoaDon_Goc; }

    public String getLoaiHoaDon() { return loaiHoaDon; }
    public void setLoaiHoaDon(String loaiHoaDon) { this.loaiHoaDon = loaiHoaDon; }

    // toString (Đã cập nhật)
    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", ngayLap=" + ngayLap +
                ", maKhachHang='" + maKhachHang + '\'' +
                ", nhanVienLap='" + nhanVienLap + '\'' +
                ", maKM='" + maKM + '\'' +
                ", tongCong=" + tongCong +
                ", tongTien=" + tongTien +
                ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
                ", maHoaDon_Goc='" + maHoaDon_Goc + '\'' + // Đã thêm
                ", loaiHoaDon='" + loaiHoaDon + '\'' +     // Đã thêm
                '}';
    }
}