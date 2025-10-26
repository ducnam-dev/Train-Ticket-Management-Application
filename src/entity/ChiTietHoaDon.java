package entity;

public class ChiTietHoaDon {
    private String hoTen;
    private String soCCCD;
    private String loaiVe;
    private int giaVe;
    private double khuyenMai;
    private double thanhTien;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String hoTen, String soCCCD, String loaiVe, int giaVe, double khuyenMai, double thanhTien) {
        setGiaVe(giaVe);
        setHoTen(hoTen);
        setSoCCCD(soCCCD);
        setLoaiVe(loaiVe);
        setKhuyenMai(khuyenMai);
        setThanhTien(thanhTien);
    }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoCCCD() { return soCCCD; }
    public void setSoCCCD(String soCCCD) { this.soCCCD = soCCCD; }

    public String getLoaiVe() { return loaiVe; }
    public void setLoaiVe(String loaiVe) { this.loaiVe = loaiVe; }

    public int getGiaVe() { return giaVe; }
    public void setGiaVe(int giaVe) { this.giaVe = giaVe; }

    public double getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(double khuyenMai) { this.khuyenMai = khuyenMai; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

    @Override
    public String toString() {
        return "HoTen: " + hoTen +
                ", SoCCCD: " + soCCCD +
                ", LoaiVe: " + loaiVe +
                ", GiaVe: " + giaVe +
                ", KhuyenMai: " + khuyenMai +
                ", ThanhTien: " + thanhTien;
    }
}
