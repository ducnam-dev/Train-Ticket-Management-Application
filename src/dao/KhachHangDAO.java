package dao;

import database.ConnectDB;
import entity.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KhachHangDAO {

    /**
     * Tra cứu chi tiết Khách hàng bằng Mã Khách hàng (MaKhachHang).
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public static KhachHang getKhachHangById(String maKhachHang) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh FROM KhachHang WHERE MaKhachHang = ?";

        Connection con = null; // KHAI BÁO BÊN NGOÀI KHỐI TRY-WITH-RESOURCES
        try {
            con = ConnectDB.getConnection(); // Lấy kết nối

            // CHỈ DÙNG TRY-WITH-RESOURCES CHO PreparedStatement và ResultSet
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

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
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }

    public static KhachHang findKhachHangByCCCD(String cccd) {
    return null; // Thực hiện truy vấn tìm Khách hàng theo CCCD
    }

    // Trong KhachHangDAO.java
    public String taoMaKhachHangMoi() throws SQLException {
        LocalDate homNay = LocalDate.now();
        // Định dạng ngày: DDMMYY (Ví dụ: 281025)
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));

        String maKhachHangPattern = "KH" + ngayStr + "%"; // Ví dụ: KH271025%

        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";

        String lastMaKH = null;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKhachHangPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaKH = rs.getString("MaKhachHang");
                }
            }
        } catch (SQLException e) {
            // In lỗi và ném lại để tầng trên (UI) xử lý
            System.err.println("Lỗi CSDL khi tạo Mã Khách hàng mới: " + e.getMessage());
            throw e;
        }

        int nextNumber = 1;
        if (lastMaKH != null) {
            // Trích xuất 4 ký tự cuối (số thứ tự) từ mã lớn nhất
            try {
                String soThuTuStr = lastMaKH.substring(lastMaKH.length() - 4);
                nextNumber = Integer.parseInt(soThuTuStr) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.err.println("Không thể phân tích số thứ tự từ mã cuối: " + lastMaKH + ". Bắt đầu lại từ 1.");
                nextNumber = 1;
            }
        }
        String soThuTuStr = String.format("%04d", nextNumber);

        return "KH" + ngayStr + String.format("%04d", nextNumber);
    }

    public boolean addOrUpdateKhachHang(Connection conn, KhachHang kh) throws SQLException {
        // Logic kiểm tra CCCD, INSERT/UPDATE KhachHang
        return true;
    }


}