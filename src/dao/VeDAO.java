package dao;

import database.ConnectDB;
import entity.ChoDat;
import entity.ChuyenTau;
import entity.KhachHang;
import entity.Ve;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// LƯU Ý: Đây là lớp triển khai logic CSDL
public class VeDAO {

    /**
     * Tra cứu chi tiết vé theo Mã vé hoặc SĐT khách hàng (cho màn hình Trả vé).
     */
    public Ve getChiTietVeChoTraVe(String maVe, String sdt) {
        Ve ve = null;

        // Câu truy vấn JOIN phức tạp để lấy tất cả các mã khóa ngoại và dữ liệu thô
        String sql = "SELECT V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat, " +
                "KH.HoTen AS TenKhachHang, KH.SoDienThoai, " +
                "CT.NgayKhoiHanh, CT.GioKhoiHanh, " +
                "GA_DI.TenGa AS GaDi, GA_DEN.TenGa AS GaDen, " +
                "CD.SoCho, T.MaToa " +
                "FROM Ve V " +
                "LEFT JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " +
                "LEFT JOIN ChuyenTau CT ON V.MaChuyenTau = CT.MaChuyenTau " +
                "LEFT JOIN Ga GA_DI ON CT.MaGaKhoiHanh = GA_DI.MaGa " +
                "LEFT JOIN Ga GA_DEN ON CT.MaGaDen = GA_DEN.MaGa " +
                "LEFT JOIN ChoDat CD ON V.MaChoDat = CD.MaCho " +
                "LEFT JOIN Toa T ON CD.MaToa = T.MaToa " +
                "WHERE (V.MaVe = ? OR KH.SoDienThoai = ?) AND V.TrangThai <> N'DA-HUY'";

        Connection con = null; // Khai báo Connection bên ngoài khối try-with-resources
        try {
            con = ConnectDB.getConnection(); // Lấy kết nối

            // BẮT ĐẦU KHỐI TRY-WITH-RESOURCES CHÍNH
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setString(1, maVe != null && !maVe.isEmpty() ? maVe : "NULL_MAVE");
                pstmt.setString(2, sdt != null && !sdt.isEmpty() ? sdt : "NULL_SDT");

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ve = new Ve();

                        // Gán các thuộc tính cơ bản
                        ve.setId(rs.getString("MaVe"));
                        ve.setGia(rs.getDouble("GiaVe"));

                        // Lấy Mã Khóa ngoại
                        String maKHDb = rs.getString("MaKhachHang");
                        String maCTDb = rs.getString("MaChuyenTau");
                        String maChoDatDb = rs.getString("MaChoDat");

                        // GỌI DAO PHỤ TRỢ (Tra cứu Entities chi tiết):
                        KhachHang kh = KhachHangDAO.getKhachHangById(maKHDb);
                        ChuyenTau ct = ChuyenTauDao.getChuyenTauById(maCTDb);
                        ChoDat cd = ChoDatDAO.getChoDatById(maChoDatDb);

                        // Gán các Entity chi tiết vào Ve (Yêu cầu Entity Ve đã được sửa)
                        ve.setKhachHangChiTiet(kh);
                        ve.setChuyenTauChiTiet(ct);
                        ve.setChoDatChiTiet(cd);

                        // Gán thuộc tính UI cần thiết (dùng thuộc tính từ Entity chi tiết)
                        if (kh != null) {
                            ve.setKhachHang(kh.getHoTen()); // Họ tên
                            // Nếu Ve có trường SoDienThoai, bạn sẽ gán: ve.setSoDienThoai(kh.getSdt());
                        }
                        if (cd != null && cd.getSoCho() != null) {
                            try {
                                ve.setSoGhe(Integer.parseInt(cd.getSoCho().replaceAll("[^\\d]", "")));
                            } catch (NumberFormatException e) {
                                ve.setSoGhe(0);
                            }
                        }
                    }
                } // ResultSet đóng
            } // PreparedStatement đóng

        } catch (SQLException e) { // BẮT LỖI TỪ KẾT NỐI VÀ DAO PHỤ TRỢ
            System.err.println("Lỗi khi tìm chi tiết vé từ CSDL: " + e.getMessage());
            e.printStackTrace();
        }

        return ve;
    }

    /**
     * Triển khai: Cập nhật trạng thái vé thành "Đã hủy" (Trả vé).
     */
    public boolean huyVe(String maVe) {
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