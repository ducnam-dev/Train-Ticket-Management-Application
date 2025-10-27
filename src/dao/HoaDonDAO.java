package dao;

import database.ConnectDB;
import entity.HoaDon;

import java.sql.*;

public class HoaDonDAO {

    /**
     * Thêm thực thể HoaDon mới vào CSDL.
     * Phương thức này được thiết kế để chạy trong một giao dịch (transaction)
     * và phải nhận Connection đã có sẵn.
     * * @param conn Kết nối CSDL (đang mở transaction).
     * @param hoaDon Đối tượng HoaDon cần thêm.
     * @return true nếu thêm thành công.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean themHoaDon(Connection conn, HoaDon hoaDon) throws SQLException {
        // Cần khớp với 8 cột trong bảng HoaDon: MaHD, MaKhachHang, MaNVLap, MaKM, TongTien, NgayLap, PhuongThuc, LoaiHoaDon
        String sql = "INSERT INTO HoaDon (MaHD, MaKhachHang, MaNVLap, MaKM, TongTien, NgayLap, PhuongThuc, LoaiHoaDon) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Sử dụng PreparedStatement của kết nối giao dịch (conn)
        PreparedStatement pstmt = null;
        try {
                pstmt = conn.prepareStatement(sql);
            // Chuyển đổi java.time.LocalDateTime sang java.sql.Timestamp
            Timestamp ngayLapTimestamp = (hoaDon.getNgayLap() != null)
                    ? Timestamp.valueOf(hoaDon.getNgayLap())
                    : null;

            pstmt.setString(1, hoaDon.getMaHD());
            pstmt.setString(2, hoaDon.getMaKhachHang());
            pstmt.setString(3, hoaDon.getMaNVLap());

            // Xử lý MaKM có thể là NULL
            if (hoaDon.getMaKM() != null && !hoaDon.getMaKM().isEmpty()) {
                pstmt.setString(4, hoaDon.getMaKM());
            } else {
                pstmt.setNull(4, Types.NVARCHAR); // Đặt giá trị NULL SQL
            }

            pstmt.setDouble(5, hoaDon.getTongTien());
            pstmt.setTimestamp(6, ngayLapTimestamp);
            pstmt.setString(7, hoaDon.getPhuongThuc());
            pstmt.setString(8, hoaDon.getLoaiHoaDon());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Hóa đơn (MaHD: " + hoaDon.getMaHD() + "): " + e.getMessage());
            throw e;
        } finally {
            // Đóng PreparedStatement
            if (pstmt != null) pstmt.close();
        }
    }


    /**
     * Truy vấn MaHD lớn nhất bắt đầu bằng một tiền tố dài (HD[CC][YYMMDD][MaNV]...).
     * @param maHdPatternPrefixDayDu Tiền tố đầy đủ của MaHD
     * @return MaHD lớn nhất tìm được, hoặc null nếu không tìm thấy.
     * @throws SQLException
     */
    // Trong HoaDonDAO.java
    public static String getLastMaHoaDonByPrefix(String maHdPatternPrefixDayDu) throws SQLException {
        String lastMaHD = null;

        // Sử dụng tiền tố đầy đủ (VD: HD012810250001) và thêm ký tự đại diện (%)
        String fixedPrefix = maHdPatternPrefixDayDu + "%";

        String sql = "SELECT TOP 1 MaHD FROM HoaDon WHERE MaHD LIKE ? ORDER BY MaHD DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fixedPrefix);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaHD = rs.getString("MaHD"); // Lấy mã đầy đủ (VD: HD0128102500010004)
                }
            }
        }
        return lastMaHD;
    }


}
