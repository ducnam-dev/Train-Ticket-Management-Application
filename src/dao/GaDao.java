package dao;

import database.ConnectDB;
import entity.Ga;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GaDao {

	public static Ga getGaById(String maGaDi) {
		return null;
	}
	private static GaDao instance;
	public static GaDao getInstance() {
        if (instance == null) {
            // Đồng bộ hóa (synchronized) để đảm bảo an toàn đa luồng khi khởi tạo
            synchronized (GaDao.class) {
                if (instance == null) {
                    // Tạo instance chỉ một lần
                    instance = new GaDao();
                }
            }
        }
        return instance;
    }


    // Trong GaDao.java
    public static Vector<Ga> layDanhSachGa() {
        Vector<Ga> danhSachGa = new Vector<>();
        String sql = "SELECT MaGa, TenGa, DiaChi FROM Ga";

        try (Connection con = ConnectDB.getInstance().getConnection(); // Khai báo trong try-with-resources
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) { // Khai báo ResultSet trong try-with-resources

            while (rs.next()) {
                Ga ga = new Ga(rs.getString("MaGa"), rs.getString("TenGa"), rs.getString("DiaChi"));
                danhSachGa.add(ga);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách Ga: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachGa;
    }

    //---

    /**
     * Lấy danh sách Tên Ga từ CSDL.
     * @return Danh sách các chuỗi TenGa.
     * @throws SQLException Nếu có lỗi xảy ra khi truy vấn CSDL.
     */
    public static List<String> layDanhSachTenGa() throws SQLException {
        List<String> danhSachTenGa = new ArrayList<>();
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT TenGa FROM Ga");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSachTenGa.add(rs.getString("TenGa"));
            }
        }
        return danhSachTenGa;
    }

}
