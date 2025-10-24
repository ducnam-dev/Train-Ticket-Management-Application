package entity;

/**
 * Entity DieuKienKhuyenMai phù hợp với bảng:
 * MaDieuKien INT IDENTITY, MaKM NVARCHAR(20), LoaiDieuKien NVARCHAR(50), GiaTriDoiChieu NVARCHAR(255), CreatedAt DATETIME2
 *
 * Lưu GiaTriDoiChieu dưới dạng String để linh hoạt (có thể là "4" hoặc "500000").
 * Có helper để chuyển về double khi cần.
 */
public class DieuKienKhuyenMai {
    private int maDieuKien;       // INT identity
    private String maKM;
    private String loaiDieuKien;  // MIN_TICKETS | MIN_AMOUNT | ...
    private String giaTriDoiChieu; // lưu kiểu string, parse khi cần

    public DieuKienKhuyenMai() {}

    public int getMaDieuKien() {
        return maDieuKien;
    }

    public void setMaDieuKien(int maDieuKien) {
        this.maDieuKien = maDieuKien;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getLoaiDieuKien() {
        return loaiDieuKien;
    }

    public void setLoaiDieuKien(String loaiDieuKien) {
        this.loaiDieuKien = loaiDieuKien;
    }

    public String getGiaTriDoiChieu() {
        return giaTriDoiChieu;
    }

    public void setGiaTriDoiChieu(String giaTriDoiChieu) {
        this.giaTriDoiChieu = giaTriDoiChieu;
    }


    /**
     * Helper: parse GiaTriDoiChieu thành double (trả về 0 nếu parse lỗi).
     */
    public double getGiaTriAsDouble() {
        if (giaTriDoiChieu == null) return 0;
        try {
            // bỏ dấu phẩy nếu có, thay dấu '.' cho decimal
            String s = giaTriDoiChieu.replaceAll(",", "").trim();
            return Double.parseDouble(s);
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "DieuKien{" + maDieuKien + "," + maKM + "," + loaiDieuKien + "," + giaTriDoiChieu + "}";
    }
}