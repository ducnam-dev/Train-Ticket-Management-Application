package dao;

import database.ConnectDB;
import entity.DieuKienKhuyenMai;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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
}