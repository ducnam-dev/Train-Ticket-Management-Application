package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class ChuyenTau {
    public String maChuyenTau;
    public String tenChuyenTau;
    public LocalDate ngayKhoiHanh;
    public LocalTime gioiKhoiHanh;
    public Ga gaDi;
    public Ga gaDen;
    public Tau tau;
    public LocalDate ngayDenDuKien;
    public LocalTime gioDenDuKien;
    public NhanVien nhanVien;

    public ChuyenTau() {

    }

    public ChuyenTau(String maChuyenTau, String tenChuyenTau, LocalDate ngayKhoiHanh, LocalTime gioiKhoiHanh, Ga gaDi, Ga gaDen, Tau tau, LocalDate ngayDenDuKien, LocalTime gioDenDuKien, NhanVien nhanVien) {
        this.maChuyenTau = maChuyenTau;
        this.tenChuyenTau = tenChuyenTau;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.gioiKhoiHanh = gioiKhoiHanh;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.tau = tau;
        this.ngayDenDuKien = ngayDenDuKien;
        this.gioDenDuKien = gioDenDuKien;
        this.nhanVien = nhanVien;
    }

    public String getMaChuyenTau() {
        return maChuyenTau;
    }

    public void setMaChuyenTau(String maChuyenTau) {
        this.maChuyenTau = maChuyenTau;
    }

    public String getTenChuyenTau() {
        return tenChuyenTau;
    }

    public void setTenChuyenTau(String tenChuyenTau) {
        this.tenChuyenTau = tenChuyenTau;
    }

    public LocalDate getNgayKhoiHanh() {
        return ngayKhoiHanh;
    }

    public void setNgayKhoiHanh(LocalDate ngayKhoiHanh) {
        this.ngayKhoiHanh = ngayKhoiHanh;
    }

    public LocalTime getGioiKhoiHanh() {
        return gioiKhoiHanh;
    }

    public void setGioiKhoiHanh(LocalTime gioiKhoiHanh) {
        this.gioiKhoiHanh = gioiKhoiHanh;
    }

    public Ga getGaDi() {
        return gaDi;
    }

    public void setGaDi(Ga gaDi) {
        this.gaDi = gaDi;
    }

    public Ga getGaDen() {
        return gaDen;
    }

    public void setGaDen(Ga gaDen) {
        this.gaDen = gaDen;
    }

    public Tau getTau() {
        return tau;
    }

    public void setTau(Tau tau) {
        this.tau = tau;
    }

    public LocalDate getNgayDenDuKien() {
        return ngayDenDuKien;
    }

    public void setNgayDenDuKien(LocalDate ngayDenDuKien) {
        this.ngayDenDuKien = ngayDenDuKien;
    }

    public LocalTime getGioDenDuKien() {
        return gioDenDuKien;
    }

    public void setGioDenDuKien(LocalTime gioDenDuKien) {
        this.gioDenDuKien = gioDenDuKien;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
}
