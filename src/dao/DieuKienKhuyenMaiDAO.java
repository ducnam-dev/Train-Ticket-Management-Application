package dao;

import database.ConnectDB;
import entity.DieuKienKhuyenMai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho DieuKienKhuyenMai
 */
public class DieuKienKhuyenMaiDAO {

    /**
     * Load điều kiện bằng MaKM, sử dụng Connection đã có (để reuse connection khi gọi từ KhuyenMaiDAO)
     */
    public List<DieuKienKhuyenMai> loadByMaKM(Connection con, String maKM) throws SQLException {
        List<DieuKienKhuyenMai> list = new ArrayList<>();
        String sql = "SELECT MaDieuKien, MaKM, LoaiDieuKien, GiaTriDoiChieu FROM DieuKienKhuyenMai WHERE MaKM = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DieuKienKhuyenMai dk = new DieuKienKhuyenMai();
                    dk.setMaDieuKien(rs.getInt("MaDieuKien"));
                    dk.setMaKM(rs.getString("MaKM"));
                    dk.setLoaiDieuKien(rs.getString("LoaiDieuKien"));
                    dk.setGiaTriDoiChieu(rs.getString("GiaTriDoiChieu"));
                    list.add(dk);
                }
            }
        }
        return list;
    }

    /**
     * Tiện lợi: mở connection rồi gọi loadByMaKM
     */
    public List<DieuKienKhuyenMai> findByMaKM(String maKM) {
        try (Connection con = ConnectDB.getConnection()) {
            return loadByMaKM(con, maKM);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC THÊM (CREATE)
    // =========================================================================

    /**
     * Thêm một điều kiện mới vào CSDL, sử dụng Connection đã có.
     * MaDieuKien sẽ được CSDL tự động tạo (IDENTITY).
     * @param con Connection dùng cho giao dịch.
     * @param dk Điều kiện cần thêm.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean addDieuKien(Connection con, DieuKienKhuyenMai dk) throws SQLException {
        String sql = "INSERT INTO DieuKienKhuyenMai (MaKM, LoaiDieuKien, GiaTriDoiChieu) VALUES (?, ?, ?)";
        // Sử dụng RETURN_GENERATED_KEYS để lấy MaDieuKien tự động sinh ra
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dk.getMaKM());
            ps.setString(2, dk.getLoaiDieuKien());
            ps.setString(3, dk.getGiaTriDoiChieu());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Lấy MaDieuKien vừa được tạo
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        dk.setMaDieuKien(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC CẬP NHẬT (UPDATE)
    // =========================================================================

    /**
     * Cập nhật một điều kiện hiện có.
     * @param con Connection dùng cho giao dịch.
     * @param dk Điều kiện cần cập nhật.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean updateDieuKien(Connection con, DieuKienKhuyenMai dk) throws SQLException {
        String sql = "UPDATE DieuKienKhuyenMai SET LoaiDieuKien = ?, GiaTriDoiChieu = ? WHERE MaDieuKien = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dk.getLoaiDieuKien());
            ps.setString(2, dk.getGiaTriDoiChieu());
            ps.setInt(3, dk.getMaDieuKien());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC XÓA (DELETE)
    // =========================================================================

    /**
     * Xóa tất cả các điều kiện liên quan đến một Khuyến Mãi nhất định.
     * Phương thức này thường được gọi trước khi thêm các điều kiện mới trong quá trình cập nhật Khuyến Mãi.
     * @param con Connection dùng cho giao dịch.
     * @param maKM Mã Khuyến Mãi cần xóa điều kiện.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean deleteByMaKM(Connection con, String maKM) throws SQLException {
        String sql = "DELETE FROM DieuKienKhuyenMai WHERE MaKM = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            // executeUpdate trả về số lượng hàng bị ảnh hưởng.
            // Ta trả về true nếu không có lỗi, vì 0 rows affected vẫn là thành công (nếu không có điều kiện nào để xóa).
            ps.executeUpdate();
            return true;
        }
    }
}