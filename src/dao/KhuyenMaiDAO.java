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

    /**
     * Thêm một Khuyến Mãi mới vào CSDL (kèm các điều kiện liên quan).
     * @param km Khuyến Mãi cần thêm.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean addKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai (MaKM, TenKM, NgayBatDau, NgayKetThuc, MoTa, PhanTramGiam, GiaTienGiamTru, LoaiApDung, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'HoatDong')"; // Mặc định trạng thái là 'HoatDong'

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Thêm Khuyến Mãi chính
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, km.getMaKM());
                ps.setString(2, km.getTenKM());
                ps.setDate(3, new java.sql.Date(km.getNgayBatDau().getTime()));
                ps.setDate(4, new java.sql.Date(km.getNgayKetThuc().getTime()));
                ps.setString(5, km.getMoTa());
                ps.setDouble(6, km.getPhanTramGiam());
                ps.setLong(7, km.getGiaTienGiamTru());
                ps.setString(8, km.getLoaiApDung());

                if (ps.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // 2. Thêm các Điều kiện Khuyến Mãi
            if (km.getDieuKienList() != null) {
                for (DieuKienKhuyenMai dk : km.getDieuKienList()) {
                    dk.setMaKM(km.getMaKM()); // Gán lại MaKM
                    if (!dieuKienDao.addDieuKien(con, dk)) {
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit(); // Hoàn thành giao dịch
            return true;

        } catch (SQLException ex) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC CẬP NHẬT (UPDATE)
    // =========================================================================

    /**
     * Cập nhật thông tin Khuyến Mãi (và các điều kiện liên quan).
     * @param km Khuyến Mãi đã sửa đổi.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean updateKhuyenMai(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET TenKM=?, NgayBatDau=?, NgayKetThuc=?, MoTa=?, PhanTramGiam=?, GiaTienGiamTru=?, LoaiApDung=?, TrangThai=? " +
                "WHERE MaKM=?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Cập nhật Khuyến Mãi chính
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, km.getTenKM());
                ps.setDate(2, new java.sql.Date(km.getNgayBatDau().getTime()));
                ps.setDate(3, new java.sql.Date(km.getNgayKetThuc().getTime()));
                ps.setString(4, km.getMoTa());
                ps.setDouble(5, km.getPhanTramGiam());
                ps.setLong(6, km.getGiaTienGiamTru());
                ps.setString(7, km.getLoaiApDung());
                ps.setString(8, km.getTrangThai());
                ps.setString(9, km.getMaKM());

                if (ps.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // 2. Xóa các Điều kiện Khuyến Mãi cũ
            dieuKienDao.deleteByMaKM(con, km.getMaKM());

            // 3. Thêm các Điều kiện Khuyến Mãi mới
            if (km.getDieuKienList() != null) {
                for (DieuKienKhuyenMai dk : km.getDieuKienList()) {
                    dk.setMaKM(km.getMaKM());
                    if (!dieuKienDao.addDieuKien(con, dk)) {
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit(); // Hoàn thành giao dịch
            return true;

        } catch (SQLException ex) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
                if (con != null) con.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC KẾT THÚC (END)
    // =========================================================================

    /**
     * Cập nhật trạng thái Khuyến Mãi thành 'DaKetThuc' và đặt Ngày Kết Thúc là ngày hiện tại.
     * @param maKM Mã Khuyến Mãi cần kết thúc.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean ketThucKhuyenMai(String maKM) {
        // Cập nhật NgayKetThuc thành ngày hôm nay
        String sql = "UPDATE KhuyenMai SET NgayKetThuc = ?, TrangThai = 'DaKetThuc' WHERE MaKM = ? AND TrangThai = 'HoatDong'";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Lấy ngày hiện tại
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            ps.setDate(1, today);
            ps.setString(2, maKM);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC GIA HẠN (EXTEND)
    // =========================================================================

    /**
     * Gia hạn Khuyến Mãi bằng cách cập nhật Ngày Kết Thúc và đảm bảo trạng thái là 'HoatDong'.
     * @param maKM Mã Khuyến Mãi cần gia hạn.
     * @param newNgayKetThuc Ngày Kết Thúc mới.
     * @return true nếu thành công, false nếu thất bại.
     */
    public boolean giaHanKhuyenMai(String maKM, Date newNgayKetThuc) {
        String sql = "UPDATE KhuyenMai SET NgayKetThuc = ?, TrangThai = 'HoatDong' WHERE MaKM = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(newNgayKetThuc.getTime()));
            ps.setString(2, maKM);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}