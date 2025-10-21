package entity.lopEnum;

import java.util.HashMap;
import java.util.Map;

public enum TrangThaiChoDat {
    CON_TRONG,
    DA_BAN,       // BỔ SUNG
    DANG_SU_DUNG,
    BAO_TRI;

    // Ánh xạ tĩnh: Map các chuỗi tiếng Việt từ CSDL sang các hằng số Enum
    private static final Map<String, TrangThaiChoDat> VIETNAMESE_MAP = new HashMap<>();

    // Khối static để khởi tạo Map
    static {
        VIETNAMESE_MAP.put("CÒN TRỐNG", CON_TRONG);
        VIETNAMESE_MAP.put("ĐÃ BÁN", DA_BAN); // Thêm trạng thái này
        VIETNAMESE_MAP.put("ĐANG SỬ DỤNG", DANG_SU_DUNG);
        VIETNAMESE_MAP.put("BAO TRÌ", BAO_TRI);

        // Thêm các biến thể khác nếu cần, ví dụ: "Da ban" -> DA_BAN
    }


    public static TrangThaiChoDat fromString(String trangThaiStr) {
        if (trangThaiStr == null || trangThaiStr.trim().isEmpty()) {
            return CON_TRONG;
        }

        // BƯỚC KHẮC PHỤC QUAN TRỌNG: Chuẩn hóa chuỗi CSDL
        // 1. Cắt khoảng trắng thừa
        // 2. Chuyển thành chữ hoa
        String cleanedKey = trangThaiStr.trim().toUpperCase();

        // 3. Tra cứu trong Map tiếng Việt (đã được chuẩn hóa chữ hoa)
        if (VIETNAMESE_MAP.containsKey(cleanedKey)) {
            return VIETNAMESE_MAP.get(cleanedKey);
        }

        // Nếu không tìm thấy, thử dùng logic cũ (nếu có ai dùng tên Enum trực tiếp)
        try {
            return TrangThaiChoDat.valueOf(cleanedKey.replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            // Nếu chuỗi hoàn toàn không hợp lệ (không tìm thấy trong Map và không phải tên Enum)
            System.err.println("Giá trị TrangThaiChoDat không hợp lệ: " + trangThaiStr);
            return CON_TRONG; // Trả về mặc định
        }
    }
}