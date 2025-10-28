package entity;

public class ChiTietHoaDon {
    private String maHD;
    private String maVe;
    private int soLuong;
    private double donGia;

    // Constructor mặc định
    public ChiTietHoaDon() {
    }

    // Constructor đầy đủ tham số
    public ChiTietHoaDon(String maHD, String maVe, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maVe = maVe;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // Getter và Setter
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

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    // toString (tùy chọn, để debug hoặc hiển thị)
    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "maHD='" + maHD + '\'' +
                ", maVe='" + maVe + '\'' +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                '}';
    }
}