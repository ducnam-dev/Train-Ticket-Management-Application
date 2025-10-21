package entity.lopEnum;

public enum TrangThaiChoDat {
    DA_SU_DUNG("Đã bán"),
    CON_TRONG("Còn trống"),
    DA_HUY("Đã hủy"); // Nếu trạng thái 'Đã hủy' là cần thiết

    private final String giaTriCSDL;

    TrangThaiChoDat(String giaTriCSDL) {
        this.giaTriCSDL = giaTriCSDL;
    }

    public String getGiaTriCSDL() {
        return giaTriCSDL;
    }

    /**
     * Phương thức tĩnh để chuyển đổi chuỗi từ CSDL sang Enum
     * @param trangThaiStr Chuỗi trạng thái từ cột "TrangThai" trong CSDL
     * @return Enum TrangThaiCho tương ứng
     */
    public static TrangThaiChoDat fromString(String trangThaiStr) {
        if (trangThaiStr == null) return CON_TRONG; // Giả định mặc định

        for (TrangThaiChoDat trangThai : TrangThaiChoDat.values()) {
            if (trangThai.giaTriCSDL.equalsIgnoreCase(trangThaiStr.trim())) {
                return trangThai;
            }
        }
        // Có thể throw Exception hoặc trả về giá trị mặc định nếu không khớp
        return CON_TRONG;
    }
}