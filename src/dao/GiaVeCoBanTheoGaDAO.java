package dao;

import database.ConnectDB;
import entity.GiaVeCoBanTheoGa;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO tạm thời cho Giá vé cơ bản theo ga.
 * TODO: Thay implementation này bằng truy vấn DB thực tế.
 */
public class GiaVeCoBanTheoGaDAO {
    private final Map<String, Long> mock = new HashMap<>();
    //se kết nối với csdl trong tương lai để lấy giá vé cơ bản theo ga

    public GiaVeCoBanTheoGaDAO() {
        // Mock data: key = maGaDi + "-" + maGaDen
        mock.put("GASA-GAHN", 100000L);
        mock.put("GAHN-GASA", 100000L);
        mock.put("GASA-GADN", 100000L);
        mock.put("GADN-GASA", 100000L);
        // Thêm vài dữ liệu mẫu nếu cần
        ConnectDB connectDB = new ConnectDB();

    }

    /**
     * Trả về giá cơ bản (VNĐ) cho cặp ga. Nếu không tìm thấy trả về -1.
     */
    public long getGiaCoBan(String maGaDi, String maGaDen) {
        if (maGaDi == null || maGaDen == null) return -1;
        String key = maGaDi + "-" + maGaDen;
        return mock.getOrDefault(key, -1L);
    }
}