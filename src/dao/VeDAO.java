package dao;

import database.ConnectDB;
import entity.*;

import java.sql.*;
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

    public List<Ve> timVeTheoKhachHang(String hoTen, String sdt, String cccd, String maVe) {
        List<Ve> danhSachVe = new ArrayList<>();

        // Xây dựng câu SQL động dựa trên tham số
        StringBuilder sql = new StringBuilder(
                "SELECT V.MaVe, V.GiaVe, V.TrangThai, V.MaKhachHang, V.MaChuyenTau, V.MaChoDat " +
                        "FROM Ve V " +
                        "JOIN KhachHang KH ON V.MaKhachHang = KH.MaKhachHang " +
                        "WHERE 1=1"
        );

        if (maVe != null && !maVe.isEmpty()) {
            sql.append(" AND V.MaVe = ?");
        }
        if (hoTen != null && !hoTen.isEmpty()) {
            sql.append(" AND KH.HoTen LIKE ?");
        }
        if (sdt != null && !sdt.isEmpty()) {
            sql.append(" AND KH.SoDienThoai LIKE ?");
        }
        if (cccd != null && !cccd.isEmpty()) {
            sql.append(" AND KH.CCCD LIKE ?");
        }

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql.toString())) {

                int paramIndex = 1;
                if (maVe != null && !maVe.isEmpty()) {
                    pstmt.setString(paramIndex++, maVe);
                }
                if (hoTen != null && !hoTen.isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + hoTen + "%");
                }
                if (sdt != null && !sdt.isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + sdt + "%");
                }
                if (cccd != null && !cccd.isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + cccd + "%");
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Ve ve = new Ve();

                        String maKHDb = rs.getString("MaKhachHang");
                        String maCTDb = rs.getString("MaChuyenTau");
                        String maChoDatDb = rs.getString("MaChoDat");

                        ve.setId(rs.getString("MaVe"));
                        ve.setGia(rs.getDouble("GiaVe"));
                        ve.setTrangThai(rs.getString("TrangThai")); // Lấy trạng thái thực tế

                        // GỌI DAO PHỤ TRỢ (Đã sửa lỗi đóng kết nối)
                        KhachHang kh = (maKHDb != null) ? KhachHangDAO.getKhachHangById(maKHDb) : null;
                        ChuyenTau ct = (maCTDb != null) ? ChuyenTauDao.layChuyenTauBangMa(maCTDb) : null;
                        ChoDat cd = (maChoDatDb != null) ? ChoDatDAO.getChoDatById(maChoDatDb) : null;

                        // Gán Entity chi tiết vào Ve
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

    //  Tren là của Nam

    //  PHẦN CỦA DUY BÊN DƯỚI

    private static final String DEFAULT_SO_HIEU_CA = "01";

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


    public Ve getVeById(String maVe) {
        Ve ve = null;

        // Truy vấn SQL cơ bản chỉ lấy các khóa ngoại và thông tin trực tiếp của bảng Ve
        String sql = "SELECT MaVe, GiaVe, TrangThai, MaKhachHang, MaChuyenTau, MaChoDat, MaLoaiVe " +
                "FROM Ve " +
                "WHERE MaVe = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                // Đảm bảo không truyền giá trị rỗng hoặc null, chỉ ID hợp lệ
                if (maVe == null || maVe.isEmpty()) {
                    return null;
                }

                pstmt.setString(1, maVe);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ve = new Ve();
                        ve.setId(rs.getString("MaVe"));
                        ve.setGia(rs.getDouble("GiaVe"));
                        ve.setTrangThai(rs.getString("TrangThai"));

//                        ve.setMaLoaiVe(rs.getString("MaLoaiVe"));

                        String maKHDb = rs.getString("MaKhachHang");
                        String maCTDb = rs.getString("MaChuyenTau");
                        String maChoDatDb = rs.getString("MaChoDat");

                        // GỌI DAO KHÁC ĐỂ NẠP ĐẦY ĐỦ CÁC THỰC THỂ PHỤ THUỘC
                        // (Giả định các DAO này đã được viết và trả về Entity đầy đủ)
                        KhachHang kh = (maKHDb != null) ? KhachHangDAO.getKhachHangById(maKHDb) : null;
                        ChuyenTau ct = (maCTDb != null) ? ChuyenTauDao.layChuyenTauBangMa(maCTDb) : null;
                        ChoDat cd = (maChoDatDb != null) ? ChoDatDAO.getChoDatById(maChoDatDb) : null;

                        // Gán Entity chi tiết
                        ve.setKhachHangChiTiet(kh);
                        ve.setChuyenTauChiTiet(ct);
                        ve.setChoDatChiTiet(cd);

                        // Gán các trường tiện ích (Tên khách, số ghế)
                        if (kh != null) {
                            ve.setKhachHang(kh.getHoTen()); // Dùng trường HoTen đã nạp từ KhachHangDAO
                        }
                        if (cd != null && cd.getSoCho() != null) {
                            try {
                                // Logic chuyển đổi số ghế (tương tự như code gốc)
                                ve.setSoGhe(Integer.parseInt(cd.getSoCho().replaceAll("[^\\d]", "")));
                            } catch (NumberFormatException e) {
                                ve.setSoGhe(0);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm chi tiết vé theo ID từ CSDL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                ConnectDB.disconnect(); // Đảm bảo đóng kết nối
            }
        }
        return ve;
    }

    // Phương thức helper để tạo Ve giả lập, giúp test giao diện ngay lập tức
    public Ve createMockVe() {
        // Tạo các đối tượng Entity lồng nhau
        Ga gaDi = new Ga("SG", "Sài Gòn", "Địa chỉ SG");
        Ga gaDen = new Ga("BH", "Biên Hòa", "Địa chỉ BH");

        ChuyenTau ct = new ChuyenTau("CT001", "SE8", "12/10/2026", "06:45", "SG", "BH", null, null, null);
        ct.setGaDi(gaDi); // Thiết lập các đối tượng Ga đã nạp
        ct.setGaDen(gaDen);

        KhachHang kh = new KhachHang("KH001", "Nguyễn Văn A", "012345678901",12 , "0901234567");

        // Giả sử mã toa là T01-1, ghế là 11
        ChoDat cd = new ChoDat("CD001", "T01-1", "11", "NM", 1); // Mã loại chỗ đặt NM = Ngồi Mềm
        cd.setMaCho("Ghế mềm điều hòa"); // Thiết lập thêm mô tả nếu cần

        // Tạo đối tượng Ve chính
        Ve ve = new Ve("1213", "KH001", "CD001", 1, 300000.0);
        ve.setTrangThai("DA-BAN");
        ve.setKhachHangChiTiet(kh);
        ve.setChuyenTauChiTiet(ct);
        ve.setChoDatChiTiet(cd);

        return ve;
    }



}