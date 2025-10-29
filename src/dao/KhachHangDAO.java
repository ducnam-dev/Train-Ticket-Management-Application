package dao;

import database.ConnectDB;
import entity.KhachHang;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KhachHangDAO {

    /**
     * Tra cứu chi tiết Khách hàng bằng Mã Khách hàng (MaKhachHang).
     * ĐÃ SỬA LỖI: KHÔNG dùng try-with-resources cho Connection.
     */
    public static KhachHang getKhachHangById(String maKhachHang) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh FROM KhachHang WHERE MaKhachHang = ?";

        Connection con = null; // KHAI BÁO BÊN NGOÀI KHỐI TRY
        try {
            con = ConnectDB.getConnection(); // Lấy kết nối

            // CHỈ DÙNG TRY-WITH-RESOURCES CHO PreparedStatement và ResultSet
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setString(1, maKhachHang);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        kh = new KhachHang(
                                rs.getString("MaKhachHang"),
                                rs.getString("HoTen"),
                                rs.getString("CCCD"),
                                rs.getInt("Tuoi"),
                                rs.getString("SoDienThoai"),
                                rs.getString("GioiTinh")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return kh;
    }

    // LƯU Ý: TẤT CẢ CÁC PHƯƠNG THỨC TRUY VẤN KHÁC CŨNG PHẢI SỬA TƯƠNG TỰ

    public static KhachHang findKhachHangByCCCD(String cccd) {
        KhachHang kh = null;
        String sql = "SELECT MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh FROM KhachHang WHERE CCCD = ?";

        Connection conn = null; // KHAI BÁO BÊN NGOÀI
        try {
            conn = ConnectDB.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, cccd);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        kh = mapResultSetToKhachHang(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi tìm khách hàng bằng CCCD: " + e.getMessage());
        }
        return kh;
    }

    // Giả định hàm ánh xạ này tồn tại trong KhachHangDAO
    private static KhachHang mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("MaKhachHang"),
                rs.getString("HoTen"),
                rs.getString("CCCD"),
                rs.getInt("Tuoi"),
                rs.getString("SoDienThoai"),
                rs.getString("GioiTinh")
        );
    }

    // Giả định hàm ánh xạ này tồn tại trong KhachHangDAO



    // Trong KhachHangDAO.java
    /**
     * Tạo Mã Khách hàng mới theo quy tắc: KH[DDMMYY][NNNN].
     * @return Mã Khách hàng mới, ví dụ: KH2810250001
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public String taoMaKhachHangMoi() throws SQLException {
        LocalDate homNay = LocalDate.now();
        // Định dạng ngày: DDMMYY
        String ngayStr = homNay.format(DateTimeFormatter.ofPattern("ddMMyy"));

        // Lấy số thứ tự lớn nhất hiện tại
        String lastSTTStr = getLastSoThuTuKhachHang(homNay);

        int nextNumber = 1;
        if (lastSTTStr != null) {
            try {
                // Chuyển "0015" thành 15, rồi cộng 1
                nextNumber = Integer.parseInt(lastSTTStr) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.err.println("Lỗi phân tích số thứ tự cuối cùng: " + e.getMessage());
            }
        }

        // Định dạng số thứ tự thành chuỗi 4 chữ số (0001)
        String soThuTuStr = String.format("%04d", nextNumber);

        return "KH" + ngayStr + soThuTuStr; // Ví dụ: KH2810250001
    }

    /**
     * Phương thức Helper: Truy vấn số thứ tự lớn nhất của khách hàng được tạo trong ngày.
     */
    private String getLastSoThuTuKhachHang(LocalDate ngayTaoKH) throws SQLException {
        String lastSTT = null;
        // Định dạng ngày: DDMMYY (Ví dụ: 281025)
        String ngayStr = ngayTaoKH.format(DateTimeFormatter.ofPattern("ddMMyy"));

        // Pattern truy vấn: KH[DDMMYY]%
        String maKhachHangPattern = "KH" + ngayStr + "%";

        // Truy vấn MaKhachHang lớn nhất trong ngày hiện tại
        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKhachHangPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastMaKH = rs.getString("MaKhachHang");
                    // Cắt lấy 4 ký tự cuối (số thứ tự)
                    lastSTT = lastMaKH.substring(lastMaKH.length() - 4);
                }
            }
        }
        return lastSTT; // Ví dụ trả về "0015" hoặc null
    }

    /**
     * Lấy giá trị số nguyên của số thứ tự Khách hàng lớn nhất trong ngày.
     * @return Giá trị số nguyên của STT lớn nhất, hoặc 0 nếu không tìm thấy.
     */
    public int getLastKhachHangSTTValue(LocalDate ngayTaoKH) throws SQLException {
        String ngayStr = ngayTaoKH.format(DateTimeFormatter.ofPattern("ddMMyy"));
        String maKhachHangPattern = "KH" + ngayStr + "%";
        String sql = "SELECT TOP 1 MaKhachHang FROM KhachHang WHERE MaKhachHang LIKE ? ORDER BY MaKhachHang DESC";
        String lastMaKH = null;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKhachHangPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaKH = rs.getString("MaKhachHang");
                }
            }
        }

        // Trích xuất và chuyển đổi
        if (lastMaKH != null && lastMaKH.length() == 12) {
            try {
                String soThuTuStr = lastMaKH.substring(8);
                return Integer.parseInt(soThuTuStr);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.err.println("Lỗi phân tích số thứ tự từ mã: " + lastMaKH);
                // Vẫn trả về 0 nếu mã hợp lệ về độ dài nhưng không phải số
            }
            System.out.println("Số thức tự của khách hàng là: " + lastMaKH);
        }
        return 0; // Trả về 0 nếu không tìm thấy hoặc lỗi
    }

// Giả định hàm tạo mã khách hàng (taoMaKhachHangMoi) cũng nằm ở đây

    /**
     * Thêm mới hoặc Cập nhật thông tin Khách hàng (UPSERT logic).
     * Phương thức này chạy trong Transaction và KHÔNG đóng kết nối.
     *
     * @param conn Kết nối CSDL (đang mở transaction, autoCommit=false).
     * @param kh Đối tượng KhachHang cần xử lý.
     * @return true nếu thao tác INSERT hoặc UPDATE thành công.
     * @throws SQLException Nếu có lỗi CSDL nghiêm trọng.
     */
    public boolean addOrUpdateKhachHang(Connection conn, KhachHang kh) throws SQLException {

        // 1. Kiểm tra Khách hàng vãng lai (KHVL001)
        if ("KHVL001".equals(kh.getMaKH())) {
            return true;
        }

        // 2. Kiểm tra sự tồn tại bằng MaKhachHang đã được gán (FIX: Logic tương thích với MaKH mới/cũ)
        boolean isUpdate = false;
        String checkSql = "SELECT MaKhachHang FROM KhachHang WHERE MaKhachHang = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, kh.getMaKH());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    isUpdate = true; // Khách hàng ĐÃ tồn tại (UPDATE)
                }
            }
        }
        // LƯU Ý: Lỗi trùng CCCD xảy ra do MaKH đã tồn tại nhưng bạn vẫn gọi INSERT!
        // -> Logic tìm kiếm ở UI/Controller đã bị lỗi, nhưng chúng ta sửa nó bằng cách
        //    chạy UPDATE nếu MaKH đã có.

        String sql;
        if (isUpdate) {
            // Cập nhật thông tin khách hàng hiện tại
            sql = "UPDATE KhachHang SET HoTen = ?, CCCD = ?, Tuoi = ?, SoDienThoai = ?, GioiTinh = ? " +
                    "WHERE MaKhachHang = ?";
        } else {
            // Thêm mới khách hàng
            sql = "INSERT INTO KhachHang (MaKhachHang, HoTen, CCCD, Tuoi, SoDienThoai, GioiTinh) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (isUpdate) {
                // Tham số cho UPDATE: HoTen, CCCD, Tuoi, SDT, GioiTinh, MaKhachHang (vị trí 1-6)
                pstmt.setString(1, kh.getHoTen());
                pstmt.setString(2, kh.getSoCCCD());
                pstmt.setInt(3, kh.getTuoi());
                pstmt.setString(4, kh.getSdt());

                if (kh.getGioiTinh() != null && !kh.getGioiTinh().isEmpty()) {
                    pstmt.setString(5, kh.getGioiTinh());
                } else {
                    pstmt.setNull(5, Types.NVARCHAR);
                }
                pstmt.setString(6, kh.getMaKH()); // MaKhachHang ở vị trí cuối cùng

            } else {
                // Tham số cho INSERT: MaKhachHang, HoTen, CCCD, Tuoi, SDT, GioiTinh (vị trí 1-6)
                pstmt.setString(1, kh.getMaKH());
                pstmt.setString(2, kh.getHoTen());
                pstmt.setString(3, kh.getSoCCCD());
                pstmt.setInt(4, kh.getTuoi());
                pstmt.setString(5, kh.getSdt());

                if (kh.getGioiTinh() != null && !kh.getGioiTinh().isEmpty()) {
                    pstmt.setString(6, kh.getGioiTinh());
                } else {
                    pstmt.setNull(6, Types.NVARCHAR);
                }
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm/cập nhật Khách hàng (MaKH: " + kh.getMaKH() + "): " + e.getMessage());
            throw e;
        }
    }


}