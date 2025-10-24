package entity;

public class LoaiVe {
    private String maLoaiVe;
    private String tenLoai;
    private double mucGiaGiam; // là hệ số nhân (vd 0.75)

    public String getMaLoaiVe() { return maLoaiVe; }
    public void setMaLoaiVe(String maLoaiVe) { this.maLoaiVe = maLoaiVe; }
    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }
    public double getMucGiaGiam() { return mucGiaGiam; }
    public void setMucGiaGiam(double mucGiaGiam) { this.mucGiaGiam = mucGiaGiam; }
}