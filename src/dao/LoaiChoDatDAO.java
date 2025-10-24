package dao;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO tạm thời cho hệ số theo loại chỗ đặt.
 * mucGiaGiam trong DB được hiểu là hệ số nhân (ví dụ giường nằm = 1.5)
 * TODO: thay bằng truy vấn DB thực tế.
 */
public class LoaiChoDatDAO {
    private final Map<String, Double> mock = new HashMap<>();

    public LoaiChoDatDAO() {
        // Ví dụ mapping theo tên loại toa
        mock.put("Giường nằm", 1.5);
        mock.put("Ghế ngồi", 1.0);
        mock.put("Toa VIP", 1.7);
    }

    public double getHeSoByLoaiToa(String loaiToa) {
        if (loaiToa == null) return 1.0;
        return mock.getOrDefault(loaiToa, 1.0);
    }
}