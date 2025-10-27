package entity;

public class Ve {
    private String id;
    private String idTau;
    private String khachHang;
    private int soGhe;
    private double gia;

    private String maLoaiVe; // Thêm thuộc tính mã loại vé

    private KhachHang khachHangChiTiet;
    private ChuyenTau chuyenTauChiTiet;
    private ChoDat choDatChiTiet;
    private String trangThai; // Thêm thuộc tính trạng thái vé

    public Ve() {
    }

    public Ve(String id, String idTau, String khachHang, int soGhe, double gia) {
        this.id = id;
        this.idTau = idTau;
        this.khachHang = khachHang;
        this.soGhe = soGhe;
        this.gia = gia;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdTau() {
        return idTau;
    }

    public void setIdTau(String idTau) {
        this.idTau = idTau;
    }

    public String getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(String khachHang) {
        this.khachHang = khachHang;
    }

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public KhachHang getKhachHangChiTiet() {
        return khachHangChiTiet;
    }

    public void setKhachHangChiTiet(KhachHang khachHangChiTiet) {
        this.khachHangChiTiet = khachHangChiTiet;
    }

    public ChuyenTau getChuyenTauChiTiet() {
        return chuyenTauChiTiet;
    }

    public void setChuyenTauChiTiet(ChuyenTau chuyenTauChiTiet) {
        this.chuyenTauChiTiet = chuyenTauChiTiet;
    }

    public ChoDat getChoDatChiTiet() {
        return choDatChiTiet;
    }

    public void setChoDatChiTiet(ChoDat choDatChiTiet) {
        this.choDatChiTiet = choDatChiTiet;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;


    }

    @Override
    public String toString() {
        return "Ve{" +
                "id='" + id + '\'' +
                ", idTau='" + idTau + '\'' +
                ", khachHang='" + khachHang + '\'' +
                ", soGhe=" + soGhe +
                ", gia=" + gia +
                ", khachHangChiTiet=" + khachHangChiTiet +
                ", chuyenTauChiTiet=" + chuyenTauChiTiet +
                ", choDatChiTiet=" + choDatChiTiet +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}