package dao;

import database.ConnectDB;
import entity.KhachHang;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date; // Thêm import java.util.Date nếu entity dùng nó

/**
 * Lớp DAO cho KhachHang, đã bổ sung findOrCreateKhachHang.
 */
public class KhachHangDAO {

    // ==========================================================
    // CÁC PHƯƠNG THỨC STATIC HIỆN CÓ (Giữ nguyên theo yêu cầu)
    // ==========================================================

    /**
     * Tra cứu chi tiết Khách hàng bằng Mã Khách hàng (MaKhachHang).
     * LƯU Ý: Phương thức này là static và chưa đóng tài nguyên đúng cách trong mọi trường hợp.
     */
    public static KhachHang getKhachHangById(String maKhachHang) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh FROM KhachHang WHERE MaKhachHang = ?";
        Connection con = null;
        PreparedStatement pstmt = null; // Khai báo ngoài try-with-resources
        ResultSet rs = null;    // Khai báo ngoài try-with-resources
        try {
            con = ConnectDB.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maKhachHang);
            rs = pstmt.executeQuery();
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cố gắng đóng tài nguyên
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            // Không đóng connection ở đây nếu là static method dùng chung
        }
        return kh;
    }

    /**
     * Tìm Khách hàng theo CCCD (Stub - Chưa hoàn chỉnh).
     * LƯU Ý: Phương thức này là static.
     */
    public static KhachHang findKhachHangByCCCD(String cccd) {
        System.out.println("WARN: Hàm findKhachHangByCCCD (static) chưa được implement.");
        // Cần thêm logic truy vấn SELECT * FROM KhachHang WHERE CCCD = ?
        // và trả về đối tượng KhachHang nếu tìm thấy.
        return null;
    }

    /**
     * Tạo mã Khách hàng mới (Stub - Chưa hoàn chỉnh).
     * LƯU Ý: Phương thức này là non-static nhưng logic chưa hoàn chỉnh.
     */
    public String taoMaKhachHangMoi() throws SQLException {
        System.out.println("WARN: Hàm taoMaKhachHangMoi chưa hoàn chỉnh logic truy vấn.");
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maKhachHangPattern = "KH" + ngayStr + "%";

        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String lastMaKH = null;
        int nextNumber = 1;

        try {
            conn = ConnectDB.getConnection(); // Cần kết nối ở đây
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maKhachHangPattern);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                lastMaKH = rs.getString(1);
                // Logic tính nextNumber (cần hoàn thiện)
                try {
                    String numPart = lastMaKH.substring(8); // Bỏ qua "KHddmmyy"
                    nextNumber = Integer.parseInt(numPart) + 1;
                } catch(Exception e){
                    System.err.println("Lỗi parse số thứ tự KH: " + lastMaKH);
                    nextNumber = 1; // Reset nếu lỗi
                }
            }
        } finally {
            closeResource(rs);
            closeResource(pstmt);
            // Không đóng conn nếu dùng chung
        }

        return "KH" + ngayStr + String.format("%04d", nextNumber);
    }

    /**
     * Thêm hoặc Cập nhật Khách hàng (Stub - Chưa hoàn chỉnh).
     * LƯU Ý: Logic chưa hoàn chỉnh.
     */
    public boolean addOrUpdateKhachHang(Connection conn, KhachHang kh) throws SQLException {
        System.out.println("WARN: Hàm addOrUpdateKhachHang chưa hoàn chỉnh logic INSERT/UPDATE.");
        // Cần kiểm tra kh.getMaKH() xem đã tồn tại chưa (SELECT)
        // Nếu có -> UPDATE
        // Nếu không -> INSERT (cần MaKH mới nếu chưa có)
        return true; // Tạm trả về true
    }

    // ==========================================================
    // PHƯƠNG THỨC NON-STATIC MỚI (findOrCreateKhachHang)
    // ==========================================================

    /**
     * Tìm khách hàng theo CCCD. Nếu không tìm thấy, tạo khách hàng mới.
     * Phương thức này là non-static và sử dụng Connection từ transaction.
     * @param hoTen Họ tên khách hàng.
     * @param cccd Số CCCD (dùng để tìm kiếm).
     * @param sdt Số điện thoại.
     * @param tuoi Tuổi.
     * @param gioiTinh Giới tính.
     * @param conn Kết nối CSDL (trong transaction).
     * @return Đối tượng KhachHang đã tồn tại hoặc vừa được tạo.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public KhachHang findOrCreateKhachHang(String hoTen, String cccd, String sdt, int tuoi, String gioiTinh, Connection conn) throws SQLException {
        // Bước 1: Tìm kiếm theo CCCD (nếu có)
        KhachHang existingKh = null;
        if (cccd != null && !cccd.trim().isEmpty()) {
            String sqlFind = "SELECT * FROM KhachHang WHERE CCCD = ?";
            PreparedStatement pstmtFind = null;
            ResultSet rsFind = null;
            try {
                pstmtFind = conn.prepareStatement(sqlFind);
                pstmtFind.setString(1, cccd.trim());
                rsFind = pstmtFind.executeQuery();
                if (rsFind.next()) {
                    existingKh = mapResultSetToKhachHang(rsFind); // Tạo đối tượng từ KQ tìm thấy
                }
            } finally {
                closeResource(rsFind);
                closeResource(pstmtFind);
            }
        }

        // Bước 2: Nếu tìm thấy, trả về khách hàng đó
        if (existingKh != null) {
            // Optional: Có thể cập nhật thông tin (HoTen, SDT, Tuoi) nếu cần
            // Ví dụ: updateKhachHangIfNeeded(conn, existingKh, hoTen, sdt, tuoi, gioiTinh);
            return existingKh;
        }

        // Bước 3: Nếu không tìm thấy, tạo khách hàng mới
        String newMaKH = generateNewMaKH(conn); // Gọi hàm helper tạo mã mới
        KhachHang newKh = new KhachHang(newMaKH, hoTen, cccd, tuoi, sdt, gioiTinh);

        String sqlInsert = "INSERT INTO KhachHang (MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmtInsert = null;
        try {
            pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, newKh.getMaKH());
            pstmtInsert.setString(2, newKh.getHoTen());
            pstmtInsert.setString(3, newKh.getSoCCCD());
            pstmtInsert.setInt(4, newKh.getTuoi() > 0 ? newKh.getTuoi() : Types.INTEGER); // Xử lý tuổi = 0
            pstmtInsert.setString(5, newKh.getSdt());
            pstmtInsert.setString(6, newKh.getGioiTinh());

            int rowsAffected = pstmtInsert.executeUpdate();
            if (rowsAffected > 0) {
                return newKh; // Trả về khách hàng vừa tạo
            } else {
                throw new SQLException("Thêm khách hàng mới thất bại.");
            }
        } finally {
            closeResource(pstmtInsert);
        }
    }

    // ==========================================================
    // CÁC HÀM HỖ TRỢ (PRIVATE, NON-STATIC)
    // ==========================================================

    /**
     * Hàm helper để ánh xạ ResultSet sang đối tượng KhachHang.
     */
    private KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("MaKhachHang"),
                rs.getString("HoTen"),
                rs.getString("CCCD"),
                rs.getInt("Tuoi"),
                rs.getString("SoDienThoai"),
                rs.getString("GioiTinh")
        );
    }

    /**
     * Hàm helper để sinh Mã Khách Hàng mới (non-static, dùng trong transaction).
     */
    private String generateNewMaKH(Connection conn) throws SQLException {
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maKhachHangPattern = "KH" + ngayStr + "%";
        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int nextNumber = 1;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maKhachHangPattern);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String lastMaKH = rs.getString(1);
                try {
                    String numPart = lastMaKH.substring(8); // Bỏ "KHddmmyy"
                    nextNumber = Integer.parseInt(numPart) + 1;
                } catch (Exception e) {
                    System.err.println("Lỗi parse số thứ tự KH: " + lastMaKH);
                    nextNumber = 1;
                }
            }
        } finally {
            closeResource(rs);
            closeResource(pstmt);
        }
        return "KH" + ngayStr + String.format("%04d", nextNumber);
    }

    /**
     * Hàm helper để đóng tài nguyên JDBC.
     */
    private void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}