package entity;

public class KhachHang {
    public String maKH;
    public String hoTen;
    public String soCCCD;
    public int tuoi;
    public String sdt;
    public String gioiTinh;

    public KhachHang() {
    }

    public KhachHang(String maKH, String hoTen, String soCCCD, int tuoi, String sdt, String gioiTinh) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.soCCCD = soCCCD;
        this.tuoi = tuoi;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
    }

//Viết ràng buộc vào đây
    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public int getTuoi() {
        return tuoi;
    }

    public void setTuoi(int tuoi) {
        this.tuoi = tuoi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
}
