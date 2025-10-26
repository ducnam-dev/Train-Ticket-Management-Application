package dao;

import database.ConnectDB;
import entity.ChoDat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChoDatDAO {

    /**
     * Lấy danh sách chỗ đặt theo Mã toa và Mã chuyến tàu.
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public List<ChoDat> getDanhSachChoDatByMaToaVaTrangThai(String maToa, String maChuyenTau) {
        List<ChoDat> danhSachChoDat = new ArrayList<>();

        String sql = "SELECT cd.MaCho, cd.MaToa, cd.SoCho, cd.Khoang, cd.Tang, "
                + "CASE WHEN v.MaVe IS NOT NULL AND v.TrangThai <> N'DA-HUY' THEN 1 ELSE 0 END AS DaDatTrenChuyenTau "
                + "FROM ChoDat cd "
                + "LEFT JOIN Ve v ON cd.MaCho = v.MaChoDat AND v.MaChuyenTau = ? "
                + "WHERE cd.MaToa = ? "
                + "ORDER BY cd.SoCho";

        Connection con = null; // KHAI BÁO BÊN NGOÀI
        try {
            con = ConnectDB.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(sql)) { // Dùng try-with-resources cho pstmt

                pstmt.setString(1, maChuyenTau);
                pstmt.setString(2, maToa);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String maCho = rs.getString("MaCho");
                        String soCho = rs.getString("SoCho");
                        String khoang = rs.getString("Khoang");

                        int tang = rs.getInt("Tang");
                        int daDatInt = rs.getInt("DaDatTrenChuyenTau");

                        ChoDat choDat = new ChoDat(maCho, maToa, soCho, khoang, tang);
                        choDat.setDaDat(daDatInt == 1);

                        danhSachChoDat.add(choDat);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách chỗ đặt: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachChoDat;
    }

    /**
     * Tra cứu chi tiết Chỗ đặt bằng Mã Chỗ (MaCho).
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public static ChoDat getChoDatById(String maCho) {
        ChoDat cd = null;
        String sql = "SELECT MaCho, MaToa, SoCho, Khoang, Tang FROM ChoDat WHERE MaCho = ?";

        Connection con = null; // KHAI BÁO BÊN NGOÀI
        try {
            con = ConnectDB.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(sql)) { // Dùng try-with-resources cho pstmt

                pstmt.setString(1, maCho);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        cd = new ChoDat(
                                rs.getString("MaCho"),
                                rs.getString("MaToa"),
                                rs.getString("SoCho"),
                                rs.getString("Khoang"),
                                rs.getInt("Tang")
                        );
                        cd.setDaDat(false);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Chỗ đặt theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return cd;
    }
}