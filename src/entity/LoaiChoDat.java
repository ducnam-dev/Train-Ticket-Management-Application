package entity;

public class LoaiChoDat {
    private String maLoai;
    private String tenLoai;
    private double mucGiaGiam; // hiểu là hệ số nhân

    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }
    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }
    public double getMucGiaGiam() { return mucGiaGiam; }
    public void setMucGiaGiam(double mucGiaGiam) { this.mucGiaGiam = mucGiaGiam; }
}