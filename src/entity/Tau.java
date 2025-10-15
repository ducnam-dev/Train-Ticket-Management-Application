package entity;

public class Tau {
private int id;
private String maTau;
private String gaDi;
private String gaDen;
private int soGhe;


public Tau() {}


public Tau(int id, String maTau, String gaDi, String gaDen, int soGhe) {
this.id = id;
this.maTau = maTau;
this.gaDi = gaDi;
this.gaDen = gaDen;
this.soGhe = soGhe;
}


public int getId() { return id; }
public void setId(int id) { this.id = id; }
public String getMaTau() { return maTau; }
public void setMaTau(String maTau) { this.maTau = maTau; }
public String getGaDi() { return gaDi; }
public void setGaDi(String gaDi) { this.gaDi = gaDi; }
public String getGaDen() { return gaDen; }
public void setGaDen(String gaDen) { this.gaDen = gaDen; }
public int getSoGhe() { return soGhe; }
public void setSoGhe(int soGhe) { this.soGhe = soGhe; }


@Override
public String toString() {
return maTau + " - " + gaDi + " → " + gaDen + " (" + soGhe + " ghe)";
}
}
