package entity;

public class Ve {
    private String maVe;
    private String maChoDat;
    private String maLoaiVe;
    private long giaVe; // VNĐ

    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }
    public String getMaCho() { return maChoDat; }
    public void setMaCho(String maCho) { this.maChoDat = maCho; }
    public String getMaLoaiVe() { return maLoaiVe; }
    public void setMaLoaiVe(String maLoaiVe) { this.maLoaiVe = maLoaiVe; }
    public long getGiaVe() { return giaVe; }
    public void setGiaVe(long giaVe) { this.giaVe = giaVe; }

    @Override
    public String toString() {
        return "Ve{" +
                "maVe='" + maVe + '\'' +
                ", maCho='" + maChoDat + '\'' +
                ", maLoaiVe='" + maLoaiVe + '\'' +
                ", giaVe=" + giaVe +
                '}';
    }
}