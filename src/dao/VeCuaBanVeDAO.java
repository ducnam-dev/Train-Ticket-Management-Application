package dao;

import database.ConnectDB;
import entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.sql.DriverManager;

public class VeCuaBanVeDAO {
    // Khởi tạo các DAO phụ thuộc (Cần đảm bảo chúng có constructor mặc định)
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO cthdDAO = new ChiTietHoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

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
        String ngayStr = ngayTaoVe.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
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
     * Tạo mã vé mới theo quy tắc: VE[CC][DDmmYY][NNNN].
     */
    public String taoMaVeMoi(int nextNumber, String soHieuCa) throws SQLException {
        LocalDate homNay = LocalDate.now();
        String ngayStr = homNay.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
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
//    public boolean banVeTau(HoaDon hoaDon, List<VeCuaBanVe> danhSachVe, KhachHang khachHang) throws SQLException {
//        Connection conn = null;
//        boolean success = false;
//
//        try {
//            conn = ConnectDB.getConnection();
//            conn.setAutoCommit(false); // Bắt đầu giao dịch
//
//            // --- B1: Xử lý Khách hàng ---
//            // khachHangDao.addOrUpdateKhachHang(conn, khachHang);
//
//            // --- B2: Thêm Hóa đơn ---
//            // hoaDonDao.addHoaDon(conn, hoaDon);
//
//            // --- B3 & B4: Thêm từng Vé và Chi tiết Hóa đơn ---
//            for (VeCuaBanVe ve : danhSachVe) {
//                // Đặt MaVe tự động
//                ve.setMaVe(taoMaVeMoi());
//
//                // Thêm Vé (Giả định: Có hàm addVe)
//                // addVe(conn, ve);
//
//                // Thêm Chi tiết Hóa đơn (Giả định: Có hàm addChiTietHoaDon)
//                ChiTietHoaDon cthd = new ChiTietHoaDon(hoaDon.getMaHD(), ve.getMaVe(), 1);
//                // addChiTietHoaDon(conn, cthd);
//            }
//
//            conn.commit(); // Hoàn tất giao dịch
//            success = true;
//
//        } catch (SQLException e) {
//            if (conn != null) {
//                try {
//                    conn.rollback();
//                    System.err.println("Giao dịch bán vé thất bại, đã rollback.");
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            throw e; // Ném lỗi lên để UI hiển thị thông báo thất bại
//        } finally {
//            if (conn != null) {
//                conn.setAutoCommit(true); // Trả lại chế độ mặc định
//                ConnectDB.disconnect();
//            }
//        }
//        return success;
//    }

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


    // Giả định các hàm tạo mã vé và các hàm tiện ích khác đã được định nghĩa tại đây

    /**
     * Hàm chính thực hiện Transaction Bán Vé (ACID).
     *
     * @param hoaDon Đối tượng hóa đơn đã chuẩn bị.
     * @param danhSachVe Danh sách vé đã được tạo (MaVe=null).
     * @param khachHang Khách hàng liên quan (đã có MaKH/CCCD).
     * @return true nếu toàn bộ giao dịch thành công.
     * @throws SQLException Nếu có lỗi CSDL (Transaction đã bị ROLLBACK).
     */
    public boolean banVeTrongTransaction(HoaDon hoaDon, List<VeCuaBanVe> danhSachVe, KhachHang khachHang) throws SQLException {
        Connection conn = null;
        boolean success = false;

        // --- BƯỚC 1: TÍNH TOÁN MÃ VÉ CHO TẤT CẢ VÉ TRƯỚC TRANSACTION ---
        LocalDate homNay = LocalDate.now();
        String soHieuCa = getSoHieuCaHienTai();
        String lastSTTStr = getLastSoThuTuTrongCa(soHieuCa, homNay);

        int currentMaxNumber = 0;
        if (lastSTTStr != null) {
            try {
                currentMaxNumber = Integer.parseInt(lastSTTStr);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi phân tích số thứ tự cuối cùng: " + e.getMessage());
            }
        }

        // --- BƯỚC 2: KHỞI TẠO TRANSACTION VÀ THỰC HIỆN INSERT ---
        try {
            conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=QuanLyVeTauTest2;trustServerCertificate=true", "sa", "sapassword");
            conn.setAutoCommit(false); // 1. Bắt đầu giao dịch

            // --- B1: Xử lý Khách hàng (Thêm mới hoặc Cập nhật) ---
            // Gọi KhachHangDAO để đảm bảo khách hàng tồn tại trong CSDL
            if (!khachHangDAO.addOrUpdateKhachHang(conn, khachHang)) {
                throw new SQLException("Lỗi khi thêm/cập nhật thông tin Khách hàng.");
            }

            // --- B2: Thêm Hóa đơn ---
            if (!hoaDonDAO.themHoaDon(conn, hoaDon)) {
                throw new SQLException("Thêm Hóa đơn thất bại.");
            }

            // B3 & B4: Thêm từng Vé và Chi tiết Hóa đơn
            for (int i = 0; i < danhSachVe.size(); i++) {
                VeCuaBanVe ve = danhSachVe.get(i);

                // a. TẠO MÃ VÉ MỚI DUY NHẤT TRONG GIAO DỊCH
                int nextNumber = currentMaxNumber + i + 1;
                ve.setMaVe(taoMaVeMoi(nextNumber, soHieuCa));

                // b. Thêm Vé
                if (!themVe(conn, ve)) { // Thêm Vé vào CSDL
                    throw new SQLException("Thêm Vé thất bại: " + ve.getMaVe());
                }

                // c. Thêm Chi tiết Hóa đơn
                ChiTietHoaDon cthd = new ChiTietHoaDon(hoaDon.getMaHD(), ve.getMaVe(), 1);
                if (!cthdDAO.themChiTietHoaDon(conn, cthd)) {
                    throw new SQLException("Thêm Chi tiết Hóa đơn thất bại.");
                }
            }

            conn.commit(); // 5. Hoàn tất giao dịch
            success = true;

        } catch (SQLException e) {
            // Nếu có lỗi, hủy bỏ tất cả thay đổi
            if (conn != null) {
                try {
                    conn.rollback(); // 4. Hủy bỏ (Rollback)
                    System.err.println("Giao dịch bán vé thất bại, đã rollback.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném lỗi lên để UI xử lý thông báo thất bại
        } finally {
            if (conn != null) {
                // Đóng kết nối giao dịch cục bộ một cách an toàn
                try {
                    conn.setAutoCommit(true); // Quan trọng: Đặt lại AUTOCOMMIT trước khi đóng
                    conn.close(); // Đóng kết nối cục bộ
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    // NOTE: Cần bổ sung các phương thức: themVe, taoMaVeMoi, v.v. tại đây.

    /**
     * Thêm một thực thể Vé mới vào CSDL (trong bối cảnh giao dịch).
     * <p>
     * Phương thức này sử dụng kết nối đã có sẵn (tham số {@code conn}),
     * không tự mở hay đóng kết nối, đảm bảo tính toàn vẹn của transaction.
     * * @param conn Kết nối CSDL đang mở (có {@code autoCommit=false}).
     * @param ve Đối tượng Vé cần thêm (đã có MaVe).
     * @return true nếu thêm thành công (ít nhất 1 dòng được thêm).
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean themVe(Connection conn, VeCuaBanVe ve) throws SQLException {
        // Cần khớp với 8 cột trong bảng Ve: MaVe, MaChuyenTau, MaChoDat, MaNV, MaKhachHang, MaLoaiVe, GiaVe, TrangThai
        String sql = "INSERT INTO Ve (MaVe, MaChuyenTau, MaChoDat, MaNV, MaKhachHang, MaLoaiVe, GiaVe, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Sử dụng PreparedStatement của kết nối giao dịch
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            // NOTE: MaVe đã được tạo và gán trong hàm banVeTrongTransaction

            pstmt.setString(1, ve.getMaVe());
            pstmt.setString(2, ve.getMaChuyenTau());
            pstmt.setString(3, ve.getMaChoDat());

            // Xử lý MaNV (có thể NULL)
            if (ve.getMaNV() != null && !ve.getMaNV().isEmpty()) {
                pstmt.setString(4, ve.getMaNV());
            } else {
                pstmt.setNull(4, java.sql.Types.NVARCHAR);
            }

            // Xử lý MaKhachHang (có thể NULL)
            if (ve.getMaKhachHang() != null && !ve.getMaKhachHang().isEmpty()) {
                pstmt.setString(5, ve.getMaKhachHang());
            } else {
                pstmt.setNull(5, java.sql.Types.NVARCHAR);
            }

            pstmt.setString(6, ve.getMaLoaiVe());
            pstmt.setDouble(7, ve.getGiaVe());
            pstmt.setString(8, ve.getTrangThai());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm Vé (MaVe: " + ve.getMaVe() + "): " + e.getMessage());
            throw e;
        } finally {
            // Đóng PreparedStatement
            if (pstmt != null) pstmt.close();
        }
    }
}