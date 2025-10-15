package entity;

public class Ve {
private int id;
private int idTau;
private String khachHang;
private int soGhe;
private double gia;


public Ve() {}


public Ve(int id, int idTau, String khachHang, int soGhe, double gia) {
this.id = id;
this.idTau = idTau;
this.khachHang = khachHang;
this.soGhe = soGhe;
this.gia = gia;
}


public int getId() { return id; }
public void setId(int id) { this.id = id; }
public int getIdTau() { return idTau; }
public void setIdTau(int idTau) { this.idTau = idTau; }
public String getKhachHang() { return khachHang; }
public void setKhachHang(String khachHang) { this.khachHang = khachHang; }
public int getSoGhe() { return soGhe; }
public void setSoGhe(int soGhe) { this.soGhe = soGhe; }
public double getGia() { return gia; }
public void setGia(double gia) { this.gia = gia; }


@Override
public String toString() {
return "Ve[id=" + id + ", idTau=" + idTau + ", khachHang=" + khachHang + ", soGhe=" + soGhe + ", gia=" + gia + "]";
}
}