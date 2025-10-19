package entity;

public class Toa {
    //maToa, maTau, loaiToa
    public String maToa;
    public Tau tau;
    public String loaiToa;

    public Toa() {
    }
    public Toa(String maToa, Tau tau, String loaiToa) {
        this.maToa = maToa;
        this.tau = tau;
        this.loaiToa = loaiToa;
    }
    public String getMaToa() {
        return maToa;
    }

    public void setMaToa(String maToa) {
        this.maToa = maToa;
    }

    public Tau getTau() {
        return tau;
    }

    public void setTau(Tau tau) {
        this.tau = tau;
    }

    public String getLoaiToa() {
        return loaiToa;
    }

    public void setLoaiToa(String loaiToa) {
        this.loaiToa = loaiToa;
    }
}
