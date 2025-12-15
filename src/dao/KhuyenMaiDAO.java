package dao;

import database.ConnectDB;
import entity.KhuyenMai; // Đã đổi tên từ KhuyenMaiOptimized thành KhuyenMai

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter; // Để format LocalDateTime sang String cho SQL

/**
 * DAO cho KhuyenMai - Tương ứng với cấu trúc CSDL đã được tối ưu hóa.
 */
public class KhuyenMaiDAO {

    // Sử dụng định dạng DATETIME của SQL Server
    private static final DateTimeFormatter SQL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Lấy tất cả khuyến mãi "active" theo logic thời gian (NgayBD, NgayKT).
     */
    public List<KhuyenMai> layTatCaKMHoatDong() {
        List<KhuyenMai> activeList = new ArrayList<>();
        // LƯU Ý: Điều kiện TrangThai = 'HOAT_DONG' loại trừ các KM bị quản lý viên tạm dừng thủ công
        String sql = "SELECT * FROM KhuyenMai " +
                "WHERE TrangThai = 'HOAT_DONG' " +
                "  AND NgayBD <= GETDATE() " +
                "  AND NgayKT >= GETDATE()";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhuyenMai km = chuyenKetQuaSangKhuyenMai(rs);
                activeList.add(km);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn lấy Khuyến mãi hoạt động: " + e.getMessage());
        }
        return activeList;
    }

    /**
     * Phương thức thêm mới một Khuyến mãi vào CSDL.
     * @param km Đối tượng KhuyenMai cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean themKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai (MaKM, TenKM, LoaiKM, GiaTriGiam, DKApDung, GiaTriDK, NgayBD, NgayKT, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setNString(2, km.getTenKM());
            ps.setString(3, km.getLoaiKM());
            ps.setBigDecimal(4, km.getGiaTriGiam());
            ps.setString(5, km.getDkApDung());

            // Xử lý giá trị có thể là NULL
            if (km.getGiaTriDK() != null) {
                ps.setBigDecimal(6, km.getGiaTriDK());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            ps.setTimestamp(7, Timestamp.valueOf(km.getNgayBD()));
            ps.setTimestamp(8, Timestamp.valueOf(km.getNgayKT()));
            ps.setString(9, km.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm Khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Phương thức cập nhật thông tin Khuyến mãi.
     * @param km Đối tượng KhuyenMai chứa thông tin mới.
     * @return true nếu sửa thành công, false nếu thất bại.
     */
    public boolean suaKhuyenMai(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET TenKM=?, LoaiKM=?, GiaTriGiam=?, DKApDung=?, GiaTriDK=?, NgayBD=?, NgayKT=?, TrangThai=? " +
                "WHERE MaKM=?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setNString(1, km.getTenKM());
            ps.setString(2, km.getLoaiKM());
            ps.setBigDecimal(3, km.getGiaTriGiam());
            ps.setString(4, km.getDkApDung());

            if (km.getGiaTriDK() != null) {
                ps.setBigDecimal(5, km.getGiaTriDK());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }

            ps.setTimestamp(6, Timestamp.valueOf(km.getNgayBD()));
            ps.setTimestamp(7, Timestamp.valueOf(km.getNgayKT()));
            ps.setString(8, km.getTrangThai());
            ps.setString(9, km.getMaKM()); // Điều kiện WHERE

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi sửa Khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm kiếm Khuyến mãi theo Mã KM.
     * @param maKM Mã Khuyến mãi cần tìm.
     * @return Đối tượng KhuyenMai nếu tìm thấy, null nếu không tìm thấy.
     */
    public KhuyenMai timKiemTheoMa(String maKM) {
        KhuyenMai km = null;
        String sql = "SELECT * FROM KhuyenMai WHERE MaKM = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    km = chuyenKetQuaSangKhuyenMai(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm Khuyến mãi: " + e.getMessage());
        }
        return km;
    }

    // Phương thức trợ giúp ánh xạ ResultSet sang đối tượng KhuyenMai
    //mapResultSetToKhuyenMai có tiếng Việt là chuyển kết quả tập sang khuyến mãi
    //chuyenKetQuaSangKhuyenMai
    private KhuyenMai chuyenKetQuaSangKhuyenMai(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(rs.getString("MaKM"));
        km.setTenKM(rs.getString("TenKM"));
        km.setLoaiKM(rs.getString("LoaiKM"));
        km.setGiaTriGiam(rs.getBigDecimal("GiaTriGiam"));
        km.setDkApDung(rs.getString("DKApDung"));
        km.setGiaTriDK(rs.getBigDecimal("GiaTriDK"));

        // Chuyển đổi từ SQL DATETIME sang Java LocalDateTime
        km.setNgayBD(rs.getTimestamp("NgayBD").toLocalDateTime());
        km.setNgayKT(rs.getTimestamp("NgayKT").toLocalDateTime());

        km.setTrangThai(rs.getString("TrangThai"));
        return km;
    }
}