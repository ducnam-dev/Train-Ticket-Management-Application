package entity.lopEnum;

public enum TrangThaiChuyenTau {

    DANG_CHO("Chờ khởi hành"), // <--- Đã sửa tên
    DA_HUY("Đã hủy"),
    DA_KHOI_HANH("Đã khởi hành"),
    DA_DEN("Đã đến");

    private final String tenHienThi;

    TrangThaiChuyenTau(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    // Phương thức chuyển đổi tùy chỉnh
    public static TrangThaiChuyenTau fromString(String text) {
        for (TrangThaiChuyenTau b : TrangThaiChuyenTau.values()) {
            if (b.tenHienThi.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái: " + text);
    }
}