package entity.lopEnum;

public enum TrangThaiChuyenTau {
    // Nếu giá trị CSDL là "Đã Khởi Hành"
    // CÁCH NÀY KHÔNG CHUẨN
    // Đã Khởi Hành, // Không hợp lệ

    // CÁCH TỐT NHẤT: Thêm một field để lưu giá trị tiếng Việt
    ĐÃ_KHỞI_HÀNH("Đã Khởi Hành"),
    ĐANG_CHỜ("Chờ khởi hành");

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