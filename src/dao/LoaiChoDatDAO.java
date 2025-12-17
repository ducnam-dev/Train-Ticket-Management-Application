package dao;

import database.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    }
    /**
     * Lấy hệ số giá dựa trên tên loại toa từ cơ sở dữ liệu.
     * @param loaiToa Tên loại toa (ví dụ: "Giường nằm", "Ghế ngồi")
     * @return Hệ số nhân (mặc định 1.0 nếu không tìm thấy hoặc có lỗi)
     */
    public double getHeSoByLoaiToa(String loaiToa) {
        if (loaiToa == null || loaiToa.trim().isEmpty()) {
            return 1.0;
        }

        double heSo = 1.0;
        // Giả sử bảng tên là Toa, cột là loaiToa và heSoToa
        // Nếu bạn có bảng riêng cho Loại Toa, hãy đổi tên bảng tương ứng
        String sql = "SELECT heSoToa FROM Toa WHERE loaiToa = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, loaiToa);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    heSo = rs.getDouble("heSoToa");
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn hệ số loại chỗ đặt: " + e.getMessage());
            // Log lỗi nếu cần thiết
        }

        return heSo;
    }

//    public double getHeSoByLoaiToa(String loaiToa) {
//        if (loaiToa == null) return 1.0;
//        return mock.getOrDefault(loaiToa, 1.0);
//    }



}