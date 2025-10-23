package entity;

import java.time.LocalDate;

public class TaiKhoan {
    private String tenDangNhap;
    private String maNV;
    private String matKhau;
    private LocalDate ngayTao;
    private String trangThai;

    public TaiKhoan(String tenDangNhap, String maNV, String matKhau, LocalDate ngayTao, String trangThai) {
        this.tenDangNhap = tenDangNhap;
        this.maNV = maNV;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
    }
    public TaiKhoan(){}

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }
}