package dao;

import database.ConnectDB;
import entity.ChiTietHoaDon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChiTietHoaDonDAO {

    /**
     * Thêm Chi tiết Hóa đơn vào CSDL.
     * Phương thức này được thiết kế để chạy trong một giao dịch (transaction),
     * do đó nó nhận Connection đã có sẵn.
     * * @param conn Kết nối CSDL (đang mở transaction).
     * @param cthd Đối tượng ChiTietHoaDon cần thêm.
     * @return true nếu thêm thành công.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean themChiTietHoaDon(Connection conn, ChiTietHoaDon cthd) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (MaHD, MaVe, SoLuong) VALUES (?, ?, ?)";
        PreparedStatement pstmt = null; // Khai báo PreparedStatement bên ngoài try

        try { // Chỉ sử dụng khối try/catch truyền thống
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cthd.getMaHD());
            pstmt.setString(2, cthd.getMaVe());
            pstmt.setInt(3, cthd.getSoLuong());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Chi tiết Hóa đơn (MaHD: " + cthd.getMaHD() + ", MaVe: " + cthd.getMaVe() + "):");
            e.printStackTrace();
            throw e;
        } finally {
            // Đóng PreparedStatement, nhưng KHÔNG đóng Connection
            if (pstmt != null) pstmt.close();
        }
    }

    // (Có thể thêm các phương thức khác như: getChiTietByMaHD, deleteChiTiet, v.v.)
}