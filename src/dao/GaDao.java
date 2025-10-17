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

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanVeTau;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "sapassword";

    /**
     * Lấy danh sách tên ga từ CSDL.
     * @return Vector chứa tên tất cả các ga, hoặc Vector rỗng nếu có lỗi.
     */
    // Trong GaDao.java
    public Vector<Ga> layDanhSachGa() {
        Vector<Ga> danhSachGa = new Vector<>();
        String sql = "SELECT MaGa, TenGa, DiaChi FROM Ga"; // Bảng Ga của bạn

        // ... Khai báo kết nối ...
        try {
             Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ga ga = new Ga(rs.getString("MaGa"), rs.getString("TenGa")
                , rs.getString("DiaChi"));
//                String ma = rs.getString("MaGa");
//                String ten = rs.getString("TenGa");
//                String diaChi = rs.getString("DiaChi");
                // Tạo đối tượng Ga mới và thêm vào danh sách
                danhSachGa.add(ga);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachGa;
    }

}
