package dao;

import database.ConnectDB;
import entity.VeCuaBanVe; // FIX: Sử dụng lớp entity đã đổi tên
import entity.HoaDon;
import entity.ChiTietHoaDon;
import entity.KhachHang;
import entity.LoaiVe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class VeCuaBanVeDAO {
    private String getSoHieuCaHienTai() {
        // TODO: Thay thế bằng logic truy vấn thực tế.
        return "01";
    }

    /**
     * Truy vấn MaVe lớn nhất trong ngày/ca hiện tại.
     * Vẫn truy vấn bảng "Ve" trong CSDL.
     */
    public String getLastSoThuTuTrongCa(String soHieuCa, LocalDate ngayTaoVe) throws SQLException {
        String lastSTT = null;
        String ngayStr = ngayTaoVe.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        String maVePattern = "VE" + soHieuCa + ngayStr + "%";

        String sql = "SELECT TOP 1 MaVe FROM Ve WHERE MaVe LIKE ? ORDER BY MaVe DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maVePattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastMaVe = rs.getString("MaVe");
                    // Cắt lấy 4 ký tự cuối (số thứ tự)
                    lastSTT = lastMaVe.substring(lastMaVe.length() - 4);
                }
            }
        }
        return lastSTT;
    }

    /**
     * Tạo mã vé mới theo quy tắc: VE[CC][YYMMDD][NNNN].
     */
    public String taoMaVeMoi() throws SQLException {
        LocalDate homNay = LocalDate.now();
        String soHieuCa = getSoHieuCaHienTai();
        String ngayStr = homNay.format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
        String lastSTTStr = getLastSoThuTuTrongCa(soHieuCa, homNay);

        int nextNumber = 1;
        if (lastSTTStr != null) {
            try {
                nextNumber = Integer.parseInt(lastSTTStr) + 1;
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu chuỗi không phải số
                System.err.println("Lỗi phân tích số thứ tự cuối cùng: " + e.getMessage());
            }
        }

        String soThuTuStr = String.format("%04d", nextNumber);
        return "VE" + soHieuCa + ngayStr + soThuTuStr;
    }

    // =================================================================================
    // CHỨC NĂNG BÁN VÉ (TRANSACTION)
    // =================================================================================

    /**
     * Thực hiện toàn bộ quy trình bán vé.
     * @param hoaDon Đối tượng hóa đơn cần tạo.
     * @param danhSachVe FIX: Sử dụng List<VeCuaBanVe>
     * @param khachHang Khách hàng liên quan.
     * @return true nếu giao dịch thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi CSDL không thể phục hồi.
     */
    public boolean banVeTau(HoaDon hoaDon, List<VeCuaBanVe> danhSachVe, KhachHang khachHang) throws SQLException {
        Connection conn = null;
        boolean success = false;

        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // --- B1: Xử lý Khách hàng ---
            // khachHangDao.addOrUpdateKhachHang(conn, khachHang);

            // --- B2: Thêm Hóa đơn ---
            // hoaDonDao.addHoaDon(conn, hoaDon);

            // --- B3 & B4: Thêm từng Vé và Chi tiết Hóa đơn ---
            for (VeCuaBanVe ve : danhSachVe) {
                // Đặt MaVe tự động
                ve.setMaVe(taoMaVeMoi());

                // Thêm Vé (Giả định: Có hàm addVe)
                // addVe(conn, ve);

                // Thêm Chi tiết Hóa đơn (Giả định: Có hàm addChiTietHoaDon)
                ChiTietHoaDon cthd = new ChiTietHoaDon(hoaDon.getMaHD(), ve.getMaVe(), 1);
                // addChiTietHoaDon(conn, cthd);
            }

            conn.commit(); // Hoàn tất giao dịch
            success = true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Giao dịch bán vé thất bại, đã rollback.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném lỗi lên để UI hiển thị thông báo thất bại
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Trả lại chế độ mặc định
                ConnectDB.disconnect();
            }
        }
        return success;
    }

    // =================================================================================
    // CÁC HÀM CRUD CƠ BẢN (Cần code chi tiết các hàm này)
    // =================================================================================

    /* * NOTE: Để hoàn thiện, bạn cần code chi tiết các hàm CRUD sau:
     * public boolean addVe(Connection conn, VeCuaBanVe ve)
     * public boolean addHoaDon(Connection conn, HoaDon hoaDon)
     * public boolean addChiTietHoaDon(Connection conn, ChiTietHoaDon cthd)
     * public KhachHang findKhachHangByCCCD(String cccd)
     * public boolean addOrUpdateKhachHang(Connection conn, KhachHang khachHang)
     */
}