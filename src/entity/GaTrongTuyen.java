package entity;

import java.sql.Time;
import java.util.Objects;

/**
 * Entity GaTrongTuyen: Thông tin chi tiết của một Ga trong một Tuyến.
 * (Tương ứng với khóa chính kép: MaTuyen + MaGa)
 */
public class GaTrongTuyen {
    // Khóa chính
    private Tuyen tuyen; // Tham chiếu đến đối tượng Tuyen
    private String maGa; // Chỉ cần Mã Ga (String), vì Ga đã là một Entity độc lập

    // Thuộc tính
    private int thuTuGa;
    private int khoangCachTichLuy;
    private int thoiGianDiDenGaTiepTheo; // Thời gian chạy từ Ga này đến Ga kế tiếp
    private int thoiGianDung; // Thời gian dừng tại Ga này

    // Constructors
    public GaTrongTuyen() {}

    public GaTrongTuyen(Tuyen tuyen, String maGa, int thuTuGa, int khoangCachTichLuy, int thoiGianDiDenGaTiepTheo, int thoiGianDung) {
        this.tuyen = tuyen;
        this.maGa = maGa;
        this.thuTuGa = thuTuGa;
        this.khoangCachTichLuy = khoangCachTichLuy;
        this.thoiGianDiDenGaTiepTheo = thoiGianDiDenGaTiepTheo;
        this.thoiGianDung = thoiGianDung;
    }

    // Getters and Setters
    public Tuyen getTuyen() {
        return tuyen;
    }

    public void setTuyen(Tuyen tuyen) {
        this.tuyen = tuyen;
    }

    public String getMaGa() {
        return maGa;
    }

    public void setMaGa(String maGa) {
        this.maGa = maGa;
    }

    public int getThuTuGa() {
        return thuTuGa;
    }

    public void setThuTuGa(int thuTuGa) {
        this.thuTuGa = thuTuGa;
    }

    public int getKhoangCachTichLuy() {
        return khoangCachTichLuy;
    }

    public void setKhoangCachTichLuy(int khoangCachTichLuy) {
        this.khoangCachTichLuy = khoangCachTichLuy;
    }

    public int getThoiGianDiDenGaTiepTheo() {
        return thoiGianDiDenGaTiepTheo;
    }

    public void setThoiGianDiDenGaTiepTheo(int thoiGianDiDenGaTiepTheo) {
        this.thoiGianDiDenGaTiepTheo = thoiGianDiDenGaTiepTheo;
    }

    public int getThoiGianDung() {
        return thoiGianDung;
    }

    public void setThoiGianDung(int thoiGianDung) {
        this.thoiGianDung = thoiGianDung;
    }

    // Override equals() và hashCode() dựa trên khóa chính kép
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GaTrongTuyen that = (GaTrongTuyen) o;
        return Objects.equals(tuyen, that.tuyen) && Objects.equals(maGa, that.maGa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tuyen, maGa);
    }
}