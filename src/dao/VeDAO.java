package dao;

import database.ConnectDB;
import entity.Ve;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface VeDAO: Định nghĩa các hành vi truy cập dữ liệu cho đối tượng Vé (Ticket).
 * Dùng để đảm bảo tính nhất quán và triển khai theo mô hình DAO.
 */
public class VeDAO {



    /**
     * Tìm kiếm chi tiết vé theo Mã vé hoặc SĐT khách hàng (cho màn hình Trả vé).
     *
     * @param maVe Mã vé.
     * @param sdt  Số điện thoại.
     * @return Đối tượng Ve chứa đầy đủ thông tin hoặc null nếu không tìm thấy.
     */

    /**
     * Triển khai: Lấy chi tiết vé cho màn hình Trả vé.
     * JOIN các bảng: Ve, KhachHang, ChuyenTau, ChoDat, Ga, Toa.
     */

    public Ve getChiTietVeChoTraVe(String maVe, String sdt) {
        Ve ve = null;

        // Trong VeDAOImpl.java, phương thức getChiTietVeChoTraVe

// SỬ DỤNG LEFT JOIN VÀ NỚI LỎNG ĐIỀU KIỆN TRẠNG THÁI
        String sql = "SELECT V.MaVe, V.GiaVe, V.TrangThai, " +
                "KH.HoTen AS TenKhachHang, KH.SoDienThoai, " +
                "CT.NgayKhoiHanh, CT.GioKhoiHanh, " +
                "GA_DI.TenGa AS GaDi, GA_DEN.TenGa AS GaDen, " +
                "CD.SoCho, T.MaToa, CT.MaChuyenTau " +
                "FROM Ve V " +
                "LEFT JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " + // Đổi thành LEFT JOIN
                "LEFT JOIN ChuyenTau CT ON V.MaChuyenTau = CT.MaChuyenTau " +  // Đổi thành LEFT JOIN
                "LEFT JOIN Ga GA_DI ON CT.MaGaKhoiHanh = GA_DI.MaGa " +
                "LEFT JOIN Ga GA_DEN ON CT.MaGaDen = GA_DEN.MaGa " +
                "LEFT JOIN ChoDat CD ON V.MaChoDat = CD.MaCho " +
                "LEFT JOIN Toa T ON CD.MaToa = T.MaToa " +
                // TÌM KIẾM THEO TẤT CẢ TRẠNG THÁI (dùng WHERE TrangThai <> N'DA-HUY')
                "WHERE (V.MaVe = ? OR KH.SoDienThoai = ?) AND V.TrangThai <> N'DA-HUY'";

        // SỬ DỤNG TRY-WITH-RESOURCES cho Connection và PreparedStatement
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // Xử lý tham số
            pstmt.setString(1, maVe != null && !maVe.isEmpty() ? maVe : "NULL_MAVE");
            pstmt.setString(2, sdt != null && !sdt.isEmpty() ? sdt : "NULL_SDT");

            // SỬ DỤNG TRY-WITH-RESOURCES cho ResultSet
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ve = new Ve();

                    // SỬA LỖI 1: Sử dụng setId(String)
                    ve.setId(rs.getString("MaVe"));
                    ve.setGia(rs.getDouble("GiaVe"));

                    // SỬA LỖI 2: Sử dụng setIdTau(String)
                    ve.setKhachHang(rs.getString("TenKhachHang"));
                    ve.setIdTau(rs.getString("MaChuyenTau"));

                    // Lấy SoCho (String) và chuyển đổi thành int (cho setSoGhe(int))
                    try {
                        ve.setSoGhe(Integer.parseInt(rs.getString("SoCho")));
                    } catch (NumberFormatException e) {
                        ve.setSoGhe(0);
                    }
                }
            } // ResultSet đóng tại đây

        } catch (SQLException e) { // Bắt lỗi SQL từ Connection/PreparedStatement/ResultSet
            System.err.println("Lỗi khi tìm chi tiết vé từ CSDL: " + e.getMessage());
            e.printStackTrace();
        }

        // Cú pháp return phải nằm ở cuối phương thức, ngoài khối try/catch chính.
        return ve;
    }

    /**
     * Triển khai: Cập nhật trạng thái vé thành "Đã hủy" (Trả vé).
     */
    public boolean huyVe(String maVe) {
        // Cập nhật trạng thái vé thành 'DA-HUY'
        String sql = "UPDATE Ve SET TrangThai = N'DA-HUY' WHERE MaVe = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maVe);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy vé: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Các phương thức khác (Giữ nguyên logic CSDL)
    public Ve taoVe(Ve ve) {
        // Triển khai logic INSERT cho chức năng Bán vé
        return null;
    }

    public List<Ve> layTheoTau(int idTau) {
        // Triển khai logic tìm kiếm theo tàu
        return new ArrayList<>();
    }
}