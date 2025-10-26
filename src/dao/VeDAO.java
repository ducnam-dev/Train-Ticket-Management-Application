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

// LƯU Ý: Đây phải là một lớp triển khai (Ví dụ: VeDAOImpl)
public class VeDAO {

    // Giả định các DAO phụ trợ này là static và có sẵn (đã được tạo ở nơi khác)
    // Nếu bạn chưa tạo các lớp này, bạn sẽ gặp lỗi 'cannot resolve symbol'
    // CẦN CÓ: KhachHangDao.java, ChuyenTauDao.java, ChoDatDAO.java

    public Ve getChiTietVeChoTraVe(String maVe, String sdt) {
        Ve ve = null;

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

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maVe != null && !maVe.isEmpty() ? maVe : "NULL_MAVE");
            pstmt.setString(2, sdt != null && !sdt.isEmpty() ? sdt : "NULL_SDT");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ve = new Ve();

                    ve.setId(rs.getString("MaVe"));
                    ve.setGia(rs.getDouble("GiaVe"));

                    String maKHDb = rs.getString("MaKhachHang");
                    String maCTDb = rs.getString("MaChuyenTau");
                    String maChoDatDb = rs.getString("MaChoDat");

                    // SỬA LỖI CÚ PHÁP VÀ LOGIC GỌI DAO PHỤ TRỢ:
                    ve.setKhachHang(rs.getString("TenKhachHang"));
                    ve.setIdTau(rs.getString("MaChuyenTau"));

                    // GỌI DAO PHỤ TRỢ (CẦN TỒN TẠI):
                    KhachHang kh = KhachHangDAO.getKhachHangById(maKHDb);
                    ChuyenTau ct = ChuyenTauDao.getChuyenTauById(maCTDb); // CẦN ĐẢM BẢO PHƯƠNG THỨC NÀY TỒN TẠI
                    ChoDat cd = ChoDatDAO.getChoDatById(maChoDatDb); // CẦN ĐẢM BẢO PHƯƠNG THỨC NÀY TỒN TẠI

                    // LƯU DỮ LIỆU ĐÃ TRA CỨU VÀO ENTITY VE (Dùng các setter đã sửa/thêm)
                    // ve.setKhachHangChiTiet(kh);  <-- Cần thêm setter này vào Ve.java
                    // ve.setChuyenTauChiTiet(ct);  <-- Cần thêm setter này vào Ve.java
                    // ve.setChoDatChiTiet(cd);    <-- Cần thêm setter này vào Ve.java

                    try {
                        ve.setSoGhe(Integer.parseInt(rs.getString("SoCho")));
                    } catch (NumberFormatException e) {
                        ve.setSoGhe(0);
                    }
                }
            }
        } catch (SQLException e) {
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