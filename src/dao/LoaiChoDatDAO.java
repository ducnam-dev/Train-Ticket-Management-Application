package dao;

import database.ConnectDB;
import entity.LoaiChoDat;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.ConnectDB;

/**
 * DAO tạm thời cho hệ số theo loại chỗ đặt.
 * mucGiaGiam trong DB được hiểu là hệ số nhân (ví dụ giường nằm = 1.5)
 */
public class LoaiChoDatDAO {
    private final Map<String, Double> mock = new HashMap<>();

    public List<LoaiChoDat> getAllLoaiChoDat() {
        List<LoaiChoDat> ds = new ArrayList<>();
        String sql = "SELECT * FROM LoaiChoDat";

        try { Connection con = ConnectDB.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                String ma = rs.getString("MaLoaiCho");
                String ten = rs.getString("TenLoaiCho");
                double heSo = rs.getDouble("HeSo");

                LoaiChoDat loai = new LoaiChoDat(ma, ten, heSo);
                ds.add(loai);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Tìm một loại chỗ theo Mã
     */
    public LoaiChoDat getLoaiChoDatByMa(String maLoai) {
        LoaiChoDat loai = null;

        String sql = "SELECT * FROM LoaiChoDat WHERE MaLoaiCho = ?";

        try {Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, maLoai);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                loai = new LoaiChoDat(
                        rs.getString("MaLoaiCho"),
                        rs.getString("TenLoaiCho"),
                        rs.getDouble("HeSo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loai;
    }

    /**
     * Thêm mới một loại chỗ
     */
    public boolean addLoaiChoDat(LoaiChoDat loai) {

        PreparedStatement ps = null;
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            ps = con.prepareStatement("INSERT INTO LoaiChoDat VALUES(?, ?, ?)");
            ps.setString(1, loai.getMaLoaiCho());
            ps.setString(2, loai.getTenLoaiCho());
            ps.setDouble(3, loai.getHeSo());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    /**
     * Cập nhật thông tin loại chỗ
     */
    public boolean updateLoaiChoDat(LoaiChoDat loai) {
        int n = 0;
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE LoaiChoDat SET TenLoaiCho = ?, HeSo = ? WHERE MaLoaiCho = ?");
            ps.setString(1, loai.getTenLoaiCho());
            ps.setDouble(2, loai.getHeSo());
            ps.setString(3, loai.getMaLoaiCho());
            n = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

}