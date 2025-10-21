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
    // Trong lớp ChoDatDao (hoặc GheDao)
    public List<ChoDat> getDanhSachChoDatByMaToaVaTrangThai(String maToa, String maChuyenTau) {
        List<ChoDat> danhSachChoDat = new ArrayList<>();

        // Truy vấn được giữ nguyên
        String sql = "SELECT cd.MaCho, cd.SoCho, cd.LoaiCho, cd.Khoang, cd.TrangThai AS TrangThaiCho, "
                + "CASE WHEN v.MaVe IS NOT NULL AND v.TrangThai <> N'Đã hủy' THEN 1 ELSE 0 END AS DaDatTrenChuyenTau "
                + "FROM ChoDat cd "
                + "LEFT JOIN Ve v ON cd.MaCho = v.MaChoDat AND v.MaChuyenTau = ? "
                + "WHERE cd.MaToa = ? "
                + "ORDER BY cd.SoCho";

        try {
            // Giả sử ConnectDB.getConnection() trả về Connection
            Connection con = ConnectDB.getConnection();

            try(PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, maChuyenTau);
                pstmt.setString(2, maToa);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String maCho = rs.getString("MaCho");
                        String soCho = rs.getString("SoCho");
                        String loaiCho = rs.getString("LoaiCho");
                        int khoang = rs.getInt("Khoang");

                        // Lấy chuỗi trạng thái từ CSDL
                        String trangThaiChoStr = rs.getString("TrangThaiCho");

                        int daDatInt = rs.getInt("DaDatTrenChuyenTau"); // 1: Đã đặt, 0: Trống

                        // **SỬ DỤNG CONSTRUCTOR ĐÃ CẬP NHẬT**
                        // Constructor ChoDat sẽ tự động chuyển đổi trangThaiChoStr sang Enum
                        ChoDat choDat = new ChoDat(maCho, maToa, soCho, loaiCho, khoang, trangThaiChoStr);

                        // Cập nhật trạng thái đặt trên chuyến tàu cụ thể
                        choDat.setDaDat(daDatInt == 1);

                        System.out.println("Danh sách cách chỗ tìm thấy" + choDat);

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

}
