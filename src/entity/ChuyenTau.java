package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import entity.lopEnum.TrangThaiChuyenTau;

public class ChuyenTau {
    public String maChuyenTau;
    public String maTau;
    public LocalDate ngayKhoiHanh;
    public LocalTime gioKhoiHanh;
    public Ga gaDi;
    public Ga gaDen;
    public Tau tau;
    public LocalDate ngayDenDuKien;
    public LocalTime gioDenDuKien;
    public NhanVien nhanVien;
    public TrangThaiChuyenTau thct;

    public ChuyenTau() {

    }

    public ChuyenTau(String maChuyenTau, String maTau, LocalDate ngayKhoiHanh, LocalTime gioKhoiHanh, Ga gaDi, Ga gaDen, Tau tau, LocalDate ngayDenDuKien, LocalTime gioDenDuKien, NhanVien nhanVien, TrangThaiChuyenTau thct) {
        this.maChuyenTau = maChuyenTau;
        this.maTau = maTau;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.gioKhoiHanh = gioKhoiHanh;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.tau = tau;
        this.ngayDenDuKien = ngayDenDuKien;
        this.gioDenDuKien = gioDenDuKien;
        this.nhanVien = nhanVien;
        this.thct = thct;
    }

    public String getMaChuyenTau() {
        return maChuyenTau;
    }

    public void setMaChuyenTau(String maChuyenTau) {
        this.maChuyenTau = maChuyenTau;
    }

    public String getMaTau() {
        return maTau;
    }

    public void setMaTau(String tenChuyenTau) {
        this.maTau = tenChuyenTau;
    }

    public LocalDate getNgayKhoiHanh() {
        return ngayKhoiHanh;
    }

    public void setNgayKhoiHanh(LocalDate ngayKhoiHanh) {
        this.ngayKhoiHanh = ngayKhoiHanh;
    }

    public LocalTime getGioKhoiHanh() {
        return gioKhoiHanh;
    }

    public void setGioKhoiHanh(LocalTime gioKhoiHanh) {
        this.gioKhoiHanh = gioKhoiHanh;
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

    public TrangThaiChuyenTau getThct() {
        return thct;
    }

    public void setThct(TrangThaiChuyenTau thct) {
        this.thct = thct;
    }


    @Override
    public String toString() {
        return "ChuyenTau{" +
                "maChuyenTau='" + maChuyenTau + '\'' +
                ", maTau='" + maTau + '\'' +
                ", ngayKhoiHanh=" + ngayKhoiHanh +
                ", gioKhoiHanh=" + gioKhoiHanh +
                ", gaDi=" + gaDi +
                ", gaDen=" + gaDen +
                ", tau=" + tau +
                ", ngayDenDuKien=" + ngayDenDuKien +
                ", gioDenDuKien=" + gioDenDuKien +
                ", nhanVien=" + nhanVien +
                ", thct=" + thct +
                '}';
    }
}
