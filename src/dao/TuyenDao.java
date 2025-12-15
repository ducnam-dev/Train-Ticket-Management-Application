package dao;

import database.ConnectDB;
import entity.Tuyen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Entity Tuyen.
 */
public class TuyenDao {
    private Connection con;

    public TuyenDao() {
        // Khởi tạo kết nối trong constructor (Giả sử ConnectDB đã được thiết lập)
        try {
            con = ConnectDB.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy tất cả các tuyến tàu từ CSDL.
     * @return Danh sách các đối tượng Tuyen.
     */
    // Trong TuyenDao.java

    public List<Tuyen> layTatCaTuyen() throws SQLException {
        List<Tuyen> danhSach = new ArrayList<>();
        // Truy vấn SQL phải khớp với cấu trúc bảng Tuyen của bạn
        String sql = "SELECT MaTuyen, TenTuyen, GaDau, GaCuoi FROM Tuyen";

        // Sử dụng try-with-resources để đảm bảo PreparedStatement và ResultSet được đóng (Đã có)
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                // Khởi tạo đối tượng Tuyen với dữ liệu từ ResultSet
                Tuyen t = new Tuyen(
                        rs.getString("MaTuyen"),
                        rs.getString("TenTuyen"),
                        rs.getString("GaDau"), // Giả định là Mã Ga
                        rs.getString("GaCuoi")  // Giả định là Mã Ga
                );
                danhSach.add(t);
            }
        }
        // Không cần khối catch ở đây vì phương thức đã khai báo throws SQLException
        // và các tài nguyên (pst, rs) sẽ tự động đóng.

        return danhSach;
    }

    /**
     * Tìm Tuyen theo mã.
     * @param maTuyen Mã tuyến cần tìm.
     * @return Đối tượng Tuyen hoặc null nếu không tìm thấy.
     */
    public Tuyen layTuyenTheoMa(String maTuyen) throws SQLException {
        Tuyen tuyen = null;
        String sql = "SELECT MaTuyen, TenTuyen, GaDau, GaCuoi FROM Tuyen WHERE MaTuyen = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maTuyen);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    tuyen = new Tuyen(
                            rs.getString("MaTuyen"),
                            rs.getString("TenTuyen"),
                            rs.getString("GaDau"),
                            rs.getString("GaCuoi")
                    );
                }
            }
        }
        return tuyen;
    }

    /**
     * Thêm một Tuyến mới vào CSDL.
     */
    public boolean themTuyen(Tuyen tuyen) throws SQLException {
        String sql = "INSERT INTO Tuyen (MaTuyen, TenTuyen, GaDau, GaCuoi) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, tuyen.getMaTuyen());
            pst.setString(2, tuyen.getTenTuyen());
            pst.setString(3, tuyen.getGaDau());
            pst.setString(4, tuyen.getGaCuoi());
            return pst.executeUpdate() > 0;
        }
    }

    // TODO: Bổ sung các phương thức Sửa và Xóa Tuyến nếu cần
}