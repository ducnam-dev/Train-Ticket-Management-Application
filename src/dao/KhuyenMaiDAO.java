package dao;

import database.ConnectDB;
import entity.KhuyenMai;
import entity.DieuKienKhuyenMai;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO cho KhuyenMai - tải kèm danh sách dieu kien
 */
public class KhuyenMaiDAO {
    private DieuKienKhuyenMaiDAO dieuKienDao = new DieuKienKhuyenMaiDAO();

    /**
     * Lấy tất cả khuyến mãi "active".
     * Lưu ý: cột TrangThai là NVARCHAR(50) theo schema mới;
     * query so sánh với '1' hoặc 'ACTIVE' (case-insensitive).
     * Nếu bạn dùng giá trị khác cho TrangThai, chỉnh WHERE tương ứng.
     */
    public List<KhuyenMai> getAllActivePromosWithConditions() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT MaKM, TenKM, NgayBatDau, NgayKetThuc, MoTa, PhanTramGiam, GiaTienGiamTru, LoaiApDung, TrangThai " +
                "FROM KhuyenMai " +
                "WHERE (TrangThai = '1' OR UPPER(TrangThai) = 'HoatDong')";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setMaKM(rs.getString("MaKM"));
                km.setTenKM(rs.getString("TenKM"));
                java.sql.Date d1 = rs.getDate("NgayBatDau");
                if (d1 != null) km.setNgayBatDau(new Date(d1.getTime()));
                java.sql.Date d2 = rs.getDate("NgayKetThuc");
                if (d2 != null) km.setNgayKetThuc(new Date(d2.getTime()));
                km.setMoTa(rs.getString("MoTa"));
                // PhanTramGiam DECIMAL -> double
                km.setPhanTramGiam(rs.getDouble("PhanTramGiam"));
                // GiaTienGiamTru DECIMAL(18,0) -> long
                try {
                    km.setGiaTienGiamTru(rs.getLong("GiaTienGiamTru"));
                } catch (SQLException ex) {
                    // nếu trường null hoặc khác, set 0
                    km.setGiaTienGiamTru(0L);
                }
                km.setLoaiApDung(rs.getString("LoaiApDung"));
                km.setTrangThai(rs.getString("TrangThai"));

                // load điều kiện liên quan
                List<DieuKienKhuyenMai> dkList = dieuKienDao.loadByMaKM(con, km.getMaKM());
                km.setDieuKienList(dkList);

                list.add(km);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy 1 khuyến mãi theo MaKM (kèm điều kiện)
     */
    public KhuyenMai findByMaKM(String maKM) {
        String sql = "SELECT MaKM, TenKM, NgayBatDau, NgayKetThuc, MoTa, PhanTramGiam, GiaTienGiamTru, LoaiApDung, TrangThai " +
                "FROM KhuyenMai WHERE MaKM = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    java.sql.Date d1 = rs.getDate("NgayBatDau");
                    if (d1 != null) km.setNgayBatDau(new Date(d1.getTime()));
                    java.sql.Date d2 = rs.getDate("NgayKetThuc");
                    if (d2 != null) km.setNgayKetThuc(new Date(d2.getTime()));
                    km.setMoTa(rs.getString("MoTa"));
                    km.setPhanTramGiam(rs.getDouble("PhanTramGiam"));
                    try {
                        km.setGiaTienGiamTru(rs.getLong("GiaTienGiamTru"));
                    } catch (SQLException ex) {
                        km.setGiaTienGiamTru(0L);
                    }
                    km.setLoaiApDung(rs.getString("LoaiApDung"));
                    km.setTrangThai(rs.getString("TrangThai"));

                    List<DieuKienKhuyenMai> dkList = dieuKienDao.loadByMaKM(con, km.getMaKM());
                    km.setDieuKienList(dkList);
                    return km;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}