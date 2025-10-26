package dao;

import database.ConnectDB;
import entity.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KhachHangDAO {

    /**
     * Tra cứu chi tiết Khách hàng bằng Mã Khách hàng (MaKhachHang).
     * @param maKhachHang Mã khách hàng.
     * @return Đối tượng KhachHang hoặc null nếu không tìm thấy.
     */
    public static KhachHang getKhachHangById(String maKhachHang) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh FROM KhachHang WHERE MaKhachHang = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maKhachHang);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    kh = new KhachHang(
                            rs.getString("MaKhachHang"),
                            rs.getString("HoTen"),
                            rs.getString("CCCD"),
                            rs.getInt("Tuoi"),
                            rs.getString("SoDienThoai"),
                            rs.getString("GioiTinh")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Khách hàng theo ID: " + e.getMessage());
            // Gợi ý: Không in stack trace đầy đủ trong môi trường sản phẩm
        }
        return kh;
    }
}