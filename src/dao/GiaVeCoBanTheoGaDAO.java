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
        mock.put("GASG-GAHN", 100000L);
        mock.put("GAHN-GASG", 100000L);
        mock.put("GASG-GADN", 100000L);
        mock.put("GADN-GASG", 100000L);
        mock.put("GASG-GAPT", 500000L);
        mock.put("GAPT-GASG", 500000L);

        /*
        sài gòn - Nha trang
        sài gòn - đà nẵng
        sài gòn - huế
        sài gòn - quảng trị
        sài gòn - lâm đồng
        sài gòn - phan thiết
         */

        // Thêm vài dữ liệu mẫu nếu cần
        // Kết nối CSDL (chưa sử dụng trong mock)
        /*hiện tại chưa nghĩ ra cách lấy dữ liệu từ csdl để tính toán giá vé cơ bản theo ga nên
        tạm thời để connect db ở đây và dùng mock data thôi
        */
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