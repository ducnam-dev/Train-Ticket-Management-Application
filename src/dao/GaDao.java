package dao;

import database.ConnectDB;
import entity.Ga;

import java.sql.*;
import java.util.Vector;

public class GaDao {

    public static Ga getGaById(String maGa) {
        Ga ga = null;
        String sql = "SELECT MaGa, TenGa, DiaChi FROM Ga WHERE MaGa = ?";

        // ĐÃ SỬA: KHÔNG DÙNG TRY-WITH-RESOURCES CHO CON
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, maGa);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ga = new Ga(rs.getString("MaGa"), rs.getString("TenGa"), rs.getString("DiaChi"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Ga theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return ga;
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
