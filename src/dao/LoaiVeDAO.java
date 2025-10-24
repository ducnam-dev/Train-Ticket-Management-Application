package dao;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO tạm thời cho hệ số theo loại vé (mucGiaGiam được hiểu là hệ số nhân)
 * TODO: thay bằng truy vấn DB thực tế.
 */
public class LoaiVeDAO {
    private final Map<String, Double> mock = new HashMap<>();

    public LoaiVeDAO() {
        mock.put("VT01", 1.0);   // Người lớn
        mock.put("VT02", 0.75);  // Trẻ em
        mock.put("VT03", 0.85);  // Người cao tuổi
        mock.put("VT04", 0.9);   // Sinh viên
    }

    public double getHeSoByMaLoaiVe(String maLoaiVe) {
        if (maLoaiVe == null) return 1.0;
        return mock.getOrDefault(maLoaiVe, 1.0);
    }
}