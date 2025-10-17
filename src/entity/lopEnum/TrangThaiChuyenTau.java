package entity.lopEnum;

public enum TrangThaiChuyenTau {
    // Giá trị enum (thường là viết hoa, không dấu, dùng gạch dưới)
    CHUA_MO_BAN_VE("Chưa mở bán vé"), 
    DANG_MO_BAN_VE("Đang mở bán vé"),
    DA_KHOI_HANH("Đã khởi hành"),
    DA_HUY("Đã hủy");

    // Trường để lưu trữ mô tả tiếng Việt
    private final String moTa;

    // Constructor 
    TrangThaiChuyenTau(String moTa) {
        this.moTa = moTa;
    }

    // Phương thức getter
    public String getMoTa() {
        return moTa;
    }
}