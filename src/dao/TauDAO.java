package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;
import entity.Tau;


public class TauDAO {
    List<Tau> layTatCa() {
        return null;
    }

    Tau timTheoId(int id) {
        return null;
    }

    Tau timTheoMa(String maTau) {
        return null;
    }

    void capNhat(Tau tau) {

    }

    public TauDAO() {
    }

    /**
     * Lấy thông tin chi tiết về một đối tượng Tau dựa trên Mã Tàu.
     * @param maTau Mã số hiệu tàu (varchar(10) trong CSDL).
     * @return Đối tượng Tau nếu tìm thấy, ngược lại là null.
     */
    public static Tau getTauById(String maTau) {
        Tau tau = null;

        // Câu truy vấn: Lấy tất cả thông tin của tàu dựa trên số hiệu (MaTau)
        String sql = "SELECT SoHieu, TrangThai FROM Tau WHERE SoHieu = ?";

        // Sử dụng try-with-resources để đảm bảo Connection và PreparedStatement được đóng
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // Đặt tham số cho truy vấn
            pstmt.setString(1, maTau);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String soHieu = rs.getString("SoHieu");
                    String trangThai = rs.getString("TrangThai");
                    tau = new Tau(soHieu, trangThai);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm thông tin Tàu theo Mã " + maTau + ": " + e.getMessage());
            e.printStackTrace();
            // Trả về null nếu có lỗi CSDL
            return null;
        }
        return tau;
    }
    /**
     * Lấy danh sách Số Hiệu Tàu từ CSDL.
     * @return Danh sách các chuỗi SoHieu.
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn CSDL.
     */
    public static List<String> layDanhSachMaTau() throws SQLException {
        List<String> danhSachMaTau = new ArrayList<>();
        // Giả sử ConnectDB là lớp quản lý kết nối của bạn
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT SoHieu FROM Tau");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSachMaTau.add(rs.getString("SoHieu"));
            }
        }
        // Lưu ý: Đảm bảo ConnectDB.getConnection() trả về một Connection mới hoặc
        // quản lý việc đóng/mở Connection hiệu quả. Dùng try-with-resources sẽ tự đóng PreparedStatement và ResultSet.
        return danhSachMaTau;
    }



}