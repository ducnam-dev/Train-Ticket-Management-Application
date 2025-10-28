package dao;

import database.ConnectDB;
import entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// LƯU Ý: Đây là lớp triển khai logic CSDL
public class VeDAO {

    /**
     * Tra cứu chi tiết vé theo Mã vé hoặc SĐT khách hàng (cho màn hình Trả vé).
     */
    public Ve getChiTietVeChoTraVe(String maVe, String sdt) {
        Ve ve = null;

        String sql = "SELECT V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat, " +
                "KH.HoTen AS TenKhachHang, KH.SoDienThoai, " +
                "CT.NgayKhoiHanh, CT.GioKhoiHanh, CT.GaDi, CT.GaDen, " + // <- added comma and space
                "CD.SoCho, T.MaToa " +
                "FROM Ve V " +
                "LEFT JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " +
                "LEFT JOIN ChuyenTau CT ON V.MaChuyenTau = CT.MaChuyenTau " +
                "LEFT JOIN ChoDat CD ON V.MaChoDat = CD.MaCho " +
                "LEFT JOIN Toa T ON CD.MaToa = T.MaToa " +
                "WHERE (V.MaVe = ? OR KH.SoDienThoai = ?) AND V.TrangThai <> N'DA-HUY'";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
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

                        KhachHang kh = KhachHangDAO.getKhachHangById(maKHDb);
                        ChuyenTau ct = ChuyenTauDao.layChuyenTauBangMa(maCTDb);
                        //tạo ra thực thể chuyến tàu từ mã chuyến tàu có ga đi ga đến loại Ga
                        ChoDat cd = ChoDatDAO.getChoDatById(maChoDatDb);

                        ve.setKhachHangChiTiet(kh);
                        ve.setChuyenTauChiTiet(ct);
                        ve.setChoDatChiTiet(cd);

                        if (kh != null) {
                            ve.setKhachHang(kh.getHoTen());
                        }
                        if (cd != null && cd.getSoCho() != null) {
                            try {
                                ve.setSoGhe(Integer.parseInt(cd.getSoCho().replaceAll("[^\\d]", "")));
                            } catch (NumberFormatException e) {
                                ve.setSoGhe(0);
                            }
                        }
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
    /**
     * Tra cứu danh sách vé dựa trên Họ tên, Số điện thoại hoặc CCCD (cho màn hình Tra cứu).
     * KHÔNG LỌC TRẠNG THÁI DA-HUY.
     */
    public List<Ve> timVeTheoKhachHang(String hoTen, String sdt, String cccd) {
        List<Ve> danhSachVe = new ArrayList<>();

        // SQL: Truy vấn linh hoạt bằng HoTen (LIKE), SĐT (LIKE), và CCCD (LIKE).
        // Loại bỏ điều kiện lọc trạng thái để hiển thị cả vé Đã bán và Đã hủy.
        String sql = "SELECT V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat " +
                "FROM Ve V " +
                "JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " +
                "WHERE KH.HoTen LIKE ? AND (KH.SoDienThoai LIKE ? OR KH.CCCD LIKE ?)";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                // 1. Thiết lập tham số (Sử dụng % để tìm kiếm linh hoạt)
                String hoTenParam = (hoTen != null && !hoTen.isEmpty()) ? "%" + hoTen + "%" : "%";
                // LƯU Ý: Nếu tham số là null, ta dùng % để tìm tất cả.
                String sdtParam = (sdt != null && !sdt.isEmpty()) ? "%" + sdt + "%" : "%";
                String cccdParam = (cccd != null && !cccd.isEmpty()) ? "%" + cccd + "%" : "%";

                pstmt.setString(1, hoTenParam);
                pstmt.setString(2, sdtParam);
                pstmt.setString(3, cccdParam);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Ve ve = new Ve();

                        // Lấy dữ liệu thô
                        String maKHDb = rs.getString("MaKhachHang");
                        String maCTDb = rs.getString("MaChuyenTau");
                        String maChoDatDb = rs.getString("MaChoDat");

                        // Gán thuộc tính cơ bản
                        ve.setId(rs.getString("MaVe"));
                        ve.setGia(rs.getDouble("GiaVe"));
                        // NOTE: Cần Entity Ve có setter/getter cho TrangThai để UI hiển thị đúng
                        // ve.setTrangThai(rs.getString("TrangThai"));

                        // 2. GỌI DAO PHỤ TRỢ (Tra cứu Entities chi tiết)
                        KhachHang kh = (maKHDb != null) ? KhachHangDAO.getKhachHangById(maKHDb) : null;
                        ChuyenTau ct = (maCTDb != null) ? ChuyenTauDao.getChuyenTauById(maCTDb) : null;
                        ChoDat cd = (maChoDatDb != null) ? ChoDatDAO.getChoDatById(maChoDatDb) : null;

                        // 3. Gán Entity chi tiết vào Ve
                        ve.setKhachHangChiTiet(kh);
                        ve.setChuyenTauChiTiet(ct);
                        ve.setChoDatChiTiet(cd);

                        if (kh != null) {
                            ve.setKhachHang(kh.getHoTen());
                        }

                        danhSachVe.add(ve);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm vé theo Khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachVe;
    }
    //Tren là của Nam

    //  PHẦN CỦA DUY BÊN DƯỚI

    private static final String MA_NV_LAP_MOCK = "NVBV001";
    private static final String DEFAULT_SO_HIEU_CA = "01";

    // =================================================================================
    // CÁC HÀM TẠO MÃ TỰ ĐỘNG
    // =================================================================================


    /**
     * Lấy số hiệu ca làm việc hiện tại (Mặc định là "01").
     */
    private String getSoHieuCaHienTai() {
        // Giá trị tĩnh (mock) cho đến khi có logic quản lý ca thực tế.
        return DEFAULT_SO_HIEU_CA;
    }

    /**
     * Truy vấn MaVe lớn nhất trong ngày/ca hiện tại.
     * Quy tắc pattern: VE[CC][DDMMYY]%
     */
    public String getLastSoThuTuTrongCa(String soHieuCa, LocalDate ngayTaoVe) throws SQLException {
        String lastSTT = null;

        // FIX: Sử dụng định dạng ddMMyy (Ngày-Tháng-Năm)
        String ngayStr = ngayTaoVe.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maVePattern = "VE" + soHieuCa + ngayStr + "%"; // Ví dụ: VE01271025%

        // NOTE: Cột MaVe trong CSDL phải khớp với 'id' trong entity.Ve
        String sql = "SELECT TOP 1 id FROM Ve WHERE id LIKE ? ORDER BY id DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maVePattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastMaVe = rs.getString("id");
                    // Cắt lấy 4 ký tự cuối (số thứ tự)
                    lastSTT = lastMaVe.substring(lastMaVe.length() - 4);
                }
            }
        }
        return lastSTT;
    }

    /**
     * Tạo mã vé mới theo quy tắc: VE[CC][DDMMYY][NNNN].
     */
    public String taoMaVeMoi() throws SQLException {
        LocalDate homNay = LocalDate.now();
        String soHieuCa = getSoHieuCaHienTai();

        // FIX: Sử dụng định dạng ddMMyy
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));

        String lastSTTStr = getLastSoThuTuTrongCa(soHieuCa, homNay);

        int nextNumber = 1;
        if (lastSTTStr != null) {
            try {
                // Chuyển "0001" -> 1, rồi cộng 1
                nextNumber = Integer.parseInt(lastSTTStr) + 1;
            } catch (NumberFormatException e) {
                System.err.println("Lỗi phân tích số thứ tự cuối cùng: " + e.getMessage());
            }
        }

        // Định dạng số thứ tự thành chuỗi 4 chữ số (0001)
        String soThuTuStr = String.format("%04d", nextNumber);

        // Gộp và trả về mã mới (Ví dụ: VE012710250001)
        return "VE" + soHieuCa + ngayStr + soThuTuStr;
    }



    // =================================================================================
    // NGHIỆP VỤ BÁN VÉ (TRANSACTION)
    // =================================================================================




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
    // HÀM CRUD CƠ BẢN (Cần code chi tiết các hàm này)
    // =================================================================================

    /**
     * Thêm một thực thể Vé mới vào CSDL (trong bối cảnh giao dịch).
     * @param conn Kết nối CSDL đang mở.
     * @param ve Đối tượng Vé cần thêm.
     * @return true nếu thêm thành công.
     */
    public boolean themVe(Connection conn, Ve ve) throws SQLException {
        // NOTE: Câu lệnh SQL cần khớp với 8 cột trong bảng Ve của bạn
        String sql = "INSERT INTO Ve (id, idTau, khachHang, soGhe, gia, trangThai) VALUES (?, ?, ?, ?, ?, ?)";

        // Sử dụng PreparedStatement của kết nối giao dịch
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ve.getId());
            pstmt.setString(2, ve.getIdTau());
            pstmt.setString(3, ve.getKhachHang()); // Giả định là Mã KH
            pstmt.setInt(4, ve.getSoGhe());
            pstmt.setDouble(5, ve.getGia());
            pstmt.setString(6, ve.getTrangThai());

            return pstmt.executeUpdate() > 0;
        }
    }

    // ... (Giữ nguyên các hàm khác: getChiTietVeChoTraVe, huyVe, taoVe, layTheoTau)
    // Cần bổ sung các lớp DAO: HoaDonDAO và ChiTietHoaDonDAO với các phương thức them...

}