package dao;

import database.ConnectDB;
import entity.GaTrongTuyen;
import entity.Tuyen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Entity GaTrongTuyen.
 * Đã sửa đổi để sử dụng kiểu INT (số phút) cho các trường thời gian.
 */
public class GaTrongTuyenDao {
    private Connection con;
    private TuyenDao tuyenDao;

    public GaTrongTuyenDao() {
        try {
            con = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Không thể kết nối CSDL trong GaTrongTuyenDao." + e.getMessage());
        }
        tuyenDao = new TuyenDao();
    }

    /**
     * Lấy danh sách GaTrongTuyen theo Mã Tuyến. (SỬA: Đọc INT)
     * @param maTuyen Mã tuyến cần truy vấn.
     * @return Danh sách GaTrongTuyen.
     */
    public List<GaTrongTuyen> layGaTrongTuyenTheoMa(String maTuyen) throws SQLException {
        List<GaTrongTuyen> danhSach = new ArrayList<>();

        // Cần đảm bảo tên cột trong CSDL là: ThoiGianDiChuyenToiGaTiepTheo và ThoiGianDung
        String sql = "SELECT MaTuyen, MaGa, ThuTuGa, KhoangCachTichLuy, ThoiGianDiChuyenToiGaTiepTheo, ThoiGianDung " +
                "FROM GA_TRONG_TUYEN WHERE MaTuyen = ? ORDER BY ThuTuGa ASC";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTuyen);
            try (ResultSet rs = pst.executeQuery()) {

                // Tải đối tượng Tuyen một lần
                Tuyen tuyen = tuyenDao.layTuyenTheoMa(maTuyen); // Giả định TuyenDao.layTuyenTheoMa hoạt động
                if (tuyen == null) {
                    // Nếu không tìm thấy Tuyến cha, không thể load con -> Return list rỗng hoặc throw
                    System.err.println("Lỗi logic: Không tìm thấy Tuyến có mã " + maTuyen);
                    return new ArrayList<>();
                }

                while (rs.next()) {
                    // LỖI LOGIC GỐC: Tên cột CSDL trong script tổng là GA_TRONG_TUYEN.[ThoiGianDiChuyenToiGaTiepTheo]
                    // TÊN CỘT ĐƯỢC DÙNG TRONG SELECT PHẢI KHỚP VỚI CSDL

                    // SỬA: Đọc giá trị thời gian dưới dạng INT
                    int tgDi = rs.getInt("ThoiGianDiChuyenToiGaTiepTheo");
                    int tgDung = rs.getInt("ThoiGianDung");

                    // SỬA: Tạo Entity GaTrongTuyen với kiểu INT cho thời gian
                    GaTrongTuyen gtt = new GaTrongTuyen(
                            tuyen,
                            rs.getString("MaGa"),
                            rs.getInt("ThuTuGa"),
                            rs.getInt("KhoangCachTichLuy"),
                            tgDi, // Dùng INT
                            tgDung // Dùng INT
                    );
                    danhSach.add(gtt);
                }
            }
        }
        return danhSach;
    }

    /**
     * Thêm một GaTrongTuyen mới. (SỬA: Gán INT)
     */
    public boolean themGaTrongTuyen(GaTrongTuyen gtt) throws SQLException {
        String sql = "INSERT INTO GA_TRONG_TUYEN (MaTuyen, MaGa, ThuTuGa, KhoangCachTichLuy, ThoiGianDiChuyenToiGaTiepTheo, ThoiGianDung) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, gtt.getTuyen().getMaTuyen());
            pst.setString(2, gtt.getMaGa());
            pst.setInt(3, gtt.getThuTuGa());
            pst.setInt(4, gtt.getKhoangCachTichLuy());
            // SỬA: Gán giá trị INT cho PreparedStatement
            pst.setInt(5, gtt.getThoiGianDiDenGaTiepTheo());
            pst.setInt(6, gtt.getThoiGianDung());
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật thông tin GaTrongTuyen. (SỬA: Gán INT)
     */
    public boolean capNhatGaTrongTuyen(GaTrongTuyen gtt) throws SQLException {
        String sql = "UPDATE GA_TRONG_TUYEN SET ThuTuGa = ?, KhoangCachTichLuy = ?, ThoiGianDiChuyenToiGaTiepTheo = ?, ThoiGianDung = ? " +
                "WHERE MaTuyen = ? AND MaGa = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, gtt.getThuTuGa());
            pst.setInt(2, gtt.getKhoangCachTichLuy());
            // SỬA: Gán giá trị INT cho PreparedStatement
            pst.setInt(3, gtt.getThoiGianDiDenGaTiepTheo());
            pst.setInt(4, gtt.getThoiGianDung());
            pst.setString(5, gtt.getTuyen().getMaTuyen());
            pst.setString(6, gtt.getMaGa());
            return pst.executeUpdate() > 0;
        }
    }

    /**
     * Xóa một Ga khỏi Tuyến. (Không cần sửa đổi)
     */
    public boolean xoaGaTrongTuyen(String maTuyen, String maGa) throws SQLException {
        String sql = "DELETE FROM GA_TRONG_TUYEN WHERE MaTuyen = ? AND MaGa = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTuyen);
            pst.setString(2, maGa);
            return pst.executeUpdate() > 0;
        }
    }
}