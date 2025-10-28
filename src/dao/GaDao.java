package dao;

import database.ConnectDB;
import entity.Ga;

import java.sql.*;
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
    public Vector<Ga> layDanhSachGa() {
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

}
