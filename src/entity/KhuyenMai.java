package entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity KhuyenMai phù hợp với schema:
 * MaKM NVARCHAR(20), TenKM NVARCHAR(100), NgayBatDau, NgayKetThuc, MoTa, PhanTramGiam DECIMAL(7,4),
 * GiaTienGiamTru DECIMAL(18,0), LoaiApDung NVARCHAR(50), TrangThai NVARCHAR(50), CreatedAt DATETIME2
 */
public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String moTa;
    private double phanTramGiam;     // lưu 0.10 = 10%
    private long giaTienGiamTru;     // VND
    private String loaiApDung;
    private String trangThai;
    private List<DieuKienKhuyenMai> dieuKienList = new ArrayList<>();

    public KhuyenMai() {}

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getPhanTramGiam() {
        return phanTramGiam;
    }

    public void setPhanTramGiam(double phanTramGiam) {
        this.phanTramGiam = phanTramGiam;
    }

    public long getGiaTienGiamTru() {
        return giaTienGiamTru;
    }

    public void setGiaTienGiamTru(long giaTienGiamTru) {
        this.giaTienGiamTru = giaTienGiamTru;
    }

    public String getLoaiApDung() {
        return loaiApDung;
    }

    public void setLoaiApDung(String loaiApDung) {
        this.loaiApDung = loaiApDung;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }


    public List<DieuKienKhuyenMai> getDieuKienList() {
        return dieuKienList;
    }

    public void setDieuKienList(List<DieuKienKhuyenMai> dieuKienList) {
        this.dieuKienList = dieuKienList;
    }

    @Override
    public String toString() {
        // Hiển thị thân thiện trong JComboBox: "MA - Tên (10%)" hoặc "MA - Tên (50.000 VND)"
        if (phanTramGiam > 0) {
            return String.format("%s - %s (%.0f%%)", maKM, tenKM == null ? "" : tenKM, phanTramGiam * 100);
        } else if (giaTienGiamTru > 0) {
            return String.format("%s - %s (%s VND)", maKM, tenKM == null ? "" : tenKM, String.format("%,d", giaTienGiamTru));
        } else {
            return String.format("%s - %s", maKM, tenKM == null ? "" : tenKM);
        }
    }
}