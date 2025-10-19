package entity.lopEnum;

public enum TrangThaiChoDat {
    CON_TRONG,    // Ví dụ 1: Chỗ còn vật lý trống
    DANG_SU_DUNG, // Ví dụ 2: Chỗ đang được sử dụng (có thể đã đặt)
    BAO_TRI;      // Ví dụ 3: Chỗ không được phép sử dụng

    /**
     * Chuyển đổi String từ CSDL thành enum TrangThaiChoDat.
     * Xử lý trường hợp không khớp (casing) và giá trị NULL/Invalid.
     * @param trangThaiStr Chuỗi trạng thái từ CSDL.
     * @return TrangThaiChoDat tương ứng hoặc giá trị mặc định (ví dụ: CON_TRONG).
     */
    public static TrangThaiChoDat fromString(String trangThaiStr) {
        if (trangThaiStr == null || trangThaiStr.trim().isEmpty()) {
            return CON_TRONG; // Giá trị mặc định nếu NULL
        }
        try {
            // Chuyển đổi chuỗi thành chữ hoa để khớp với tên enum
            return TrangThaiChoDat.valueOf(trangThaiStr.trim().toUpperCase()
                    .replace(" ", "_")); // Thay thế khoảng trắng bằng gạch dưới nếu cần
        } catch (IllegalArgumentException e) {
            // Nếu chuỗi không khớp với bất kỳ tên enum nào
            System.err.println("Giá trị TrangThaiChoDat không hợp lệ: " + trangThaiStr);
            return CON_TRONG; // Trả về mặc định
        }
    }
}