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

    static Tau getTauById(String maTau) {
		// TODO Auto-generated method stub
		return null;
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