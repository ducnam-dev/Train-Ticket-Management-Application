package entity;

import entity.lopEnum.TrangThaiChoDat;

public class ChoDat {
        //maCho, maToa, soCho, khoang, tang, trangThai
        private String maCho;
        private String maToa;
        private String soCho;
        private String khoang;
        private int tang;
        private TrangThaiChoDat trangThai;
    private boolean daDat;

        public ChoDat() {}

    public ChoDat(String maCho, String maToa, String soCho, String khoang, int tang, String trangThaiStr) {
        this.maCho = maCho;
        this.maToa = maToa;
        this.soCho = soCho;
        this.khoang = khoang;
        this.tang = tang;
        this.trangThai = trangThai = TrangThaiChoDat.fromString(trangThaiStr);
    }

    public String getMaCho() {
        return maCho;
    }

    public void setMaCho(String maCho) {
        this.maCho = maCho;
    }

    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public String getSoCho() {
        return soCho;
    }

    public void setSoCho(String soCho) {
        this.soCho = soCho;
    }

    public String getKhoang() {
        return khoang;
    }

    public void setKhoang(String khoang) {
        this.khoang = khoang;
    }

    public int getTang() {
        return tang;
    }

    public void setTang(int tang) {
        this.tang = tang;
    }

    public TrangThaiChoDat getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiChoDat trangThai) {
        this.trangThai = trangThai;
    }
    public void setDaDat(boolean daDat) {
        this.daDat = daDat;
    }
}
