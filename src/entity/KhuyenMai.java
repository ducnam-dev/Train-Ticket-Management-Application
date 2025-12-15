package entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thực thể KhuyenMai Optimized - Tương ứng với cấu trúc CSDL đã được tối ưu hóa.
 * Sử dụng GiaTriDK (BigDecimal) cho cả điều kiện giá và số lượng.
 */
public class KhuyenMai{

    // 1. Thuộc tính (Fields)
    private String maKM;
    private String tenKM;
    private String loaiKM; // PHAN_TRAM_GIA, CO_DINH
    private BigDecimal giaTriGiam;

    private String dkApDung; // MIN_GIA, MIN_SL, NONE
    private BigDecimal giaTriDK; // Giá trị điều kiện (e.g., 1000000.00 hoặc 5.00)

    private LocalDateTime ngayBD; // Ngay Bat Dau
    private LocalDateTime ngayKT; // Ngay Ket Thuc
    private String trangThai; // HOAT_DONG, HET_HAN, KHONG_HOAT_DONG

    // 2. Constructor Rỗng
    public KhuyenMai() {
    }

    // 3. Constructor Đầy Đủ
    public KhuyenMai(String maKM, String tenKM, String loaiKM, BigDecimal giaTriGiam, String dkApDung,
                              BigDecimal giaTriDK, LocalDateTime ngayBD, LocalDateTime ngayKT, String trangThai) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.loaiKM = loaiKM;
        this.giaTriGiam = giaTriGiam;
        this.dkApDung = dkApDung;
        this.giaTriDK = giaTriDK;
        this.ngayBD = ngayBD;
        this.ngayKT = ngayKT;
        this.trangThai = trangThai;
    }

    // 4. Getters và Setters

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    public BigDecimal getGiaTriGiam() {
        return giaTriGiam;
    }

    public void setGiaTriGiam(BigDecimal giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
    }

    public String getDkApDung() {
        return dkApDung;
    }

    public void setDkApDung(String dkApDung) {
        this.dkApDung = dkApDung;
    }

    public BigDecimal getGiaTriDK() {
        return giaTriDK;
    }

    public void setGiaTriDK(BigDecimal giaTriDK) {
        this.giaTriDK = giaTriDK;
    }

    public LocalDateTime getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(LocalDateTime ngayBD) {
        this.ngayBD = ngayBD;
    }

    public LocalDateTime getNgayKT() {
        return ngayKT;
    }

    public void setNgayKT(LocalDateTime ngayKT) {
        this.ngayKT = ngayKT;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        // Tùy chỉnh chuỗi hiển thị theo ý bạn, ví dụ: Mã KM - Tên KM
        return this.tenKM;

        // Hoặc chỉ tên:
        // return this.tenKM;
    }
}