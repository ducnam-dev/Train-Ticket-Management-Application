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
        String sql = "SELECT cd.MaCho, cd.MaToa, cd.SoCho, cd.Khoang, cd.Tang, "
                // Trả về 1 (Đã đặt) nếu tìm thấy một vé cho MaCho này trên MaChuyenTau này
                // VÀ trạng thái của vé KHÔNG PHẢI là 'DA-HUY'
                + "CASE WHEN v.MaVe IS NOT NULL AND v.TrangThai <> N'DA-HUY' THEN 1 ELSE 0 END AS DaDatTrenChuyenTau "
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
                        String khoang = rs.getString("Khoang");

                        int tang = rs.getInt("Tang");

                        int daDatInt = rs.getInt("DaDatTrenChuyenTau"); // 1: Đã đặt, 0: Trống

                        // **SỬ DỤNG CONSTRUCTOR ĐÃ CẬP NHẬT**
                        // Constructor ChoDat sẽ tự động chuyển đổi trangThaiChoStr sang Enum
                        ChoDat choDat = new ChoDat(maCho, maToa, soCho, khoang, tang);

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
    public static ChoDat getChoDatById(String maCho) {
        ChoDat cd = null;
        String sql = "SELECT MaCho, MaToa, SoCho, Khoang, Tang, TrangThai FROM ChoDat WHERE MaCho = ?";

        // Sử dụng try-with-resources để đảm bảo tài nguyên được đóng
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maCho);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cd = new ChoDat(
                            rs.getString("MaCho"),
                            rs.getString("MaToa"),
                            rs.getString("SoCho"),
                            rs.getString("Khoang"),
                            rs.getInt("Tang")
                            // TrangThai không được dùng trong constructor hiện tại
                    );
                    // Giả sử trạng thái "Đã bán" trong CSDL là true
                    String trangThaiDb = rs.getString("TrangThai");
                    cd.setDaDat(trangThaiDb != null && trangThaiDb.equals("Đã bán"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Chỗ đặt theo ID: " + e.getMessage());
        }
        return cd;
    }
}
