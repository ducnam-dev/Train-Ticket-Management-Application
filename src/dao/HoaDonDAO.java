package dao;

import database.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HoaDonDAO {

    // Trong VeCuaBanVeDAO.java
    /**
     * Truy vấn MaHD lớn nhất bắt đầu bằng một tiền tố dài (HD[CC][YYMMDD][MaNV]...).
     * @param maHdPatternPrefix Tiền tố đầy đủ của MaHD
     * @return MaHD lớn nhất tìm được, hoặc null nếu không tìm thấy.
     * @throws SQLException
     */
    public static String getLastMaHoaDonByPrefix(String maHdPatternPrefix) throws SQLException {
        String lastMaHD = null;
        // NOTE: Câu truy vấn SQL phải khớp với cấu trúc MaHD trong CSDL
        String sql = "SELECT TOP 1 MaHD FROM HoaDon WHERE MaHD LIKE ? ORDER BY MaHD DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHdPatternPrefix);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaHD = rs.getString("MaHD");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return lastMaHD;
    }
}
