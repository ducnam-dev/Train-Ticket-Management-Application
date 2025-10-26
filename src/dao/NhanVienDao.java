// java
package dao;

import database.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.util.Date;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class NhanVienDao {

    // =================================================================================
    // 1. GET NHÂN VIÊN BY ID (GET Chi tiết Nhân Viên)
    // =================================================================================

    public static NhanVien getNhanVienById(String maNV) throws SQLException {
        NhanVien nv = null;
        String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // FIX: Đảm bảo tham số được gán
            pstmt.setString(1, maNV);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nv = taoDoiTuongNhanVienTuResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin Nhân Viên theo ID: " + maNV);
            e.printStackTrace();
            throw e;
        }

        return nv;
    }

    // =================================================================================
    // 2. GET TẤT CẢ TÀI KHOẢN (Liên kết NhanVien và TaiKhoan)
    // =================================================================================

    /**
     * Lấy tất cả các tài khoản đang tồn tại, bao gồm cả thông tin nhân viên liên quan.
     */
    public List<TaiKhoan> getAllTaiKhoan() throws SQLException {
        List<TaiKhoan> danhSachTK = new ArrayList<>();

        // JOIN NhanVien và TaiKhoan để lấy tất cả dữ liệu trong một lần truy vấn
        String sql = "SELECT NV.*, TK.TenDangNhap, TK.MatKhau, TK.NgayTao, TK.TrangThai " +
                "FROM TaiKhoan TK JOIN NhanVien NV ON TK.MaNV = NV.MaNV";

        // Sử dụng try-with-resources
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Bước 1: Ánh xạ dữ liệu cột NhanVien
                NhanVien nv = taoDoiTuongNhanVienTuResultSet(rs);
                System.out.println("Đang xử lý NhanVien with MaNV: " + nv.getMaNV());
                // Bước 2: Ánh xạ dữ liệu cột TaiKhoan và liên kết nv vào tk
                // (Truyền nv vào hàm ánh xạ TaiKhoan để thiết lập thuộc tính nhanVien)
                TaiKhoan tk = taoDoiTuongTaiKhoanTuResultSet(rs, nv);
                System.out.println("Đang xử lý TaiKhaon with MaNV: " + tk.getTenDangNhap());
                if (tk != null) {
                    danhSachTK.add(tk);

                }else{
                    System.out.println("TaiKhoan null for MaNV: " + nv.getMaNV());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return danhSachTK;
    }

    //getAllNhanVien
    // Giả định phương thức này được thêm vào lớp NhanVienDao
    public List<NhanVien> getAllNhanVien() throws SQLException {
        List<NhanVien> danhSachNV = new ArrayList<>();

        // Câu truy vấn lấy tất cả các cột từ bảng NhanVien
        String sql = "SELECT * FROM NhanVien";

        // Sử dụng try-with-resources để đảm bảo Connection, PreparedStatement, ResultSet được đóng
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Ánh xạ từng dòng ResultSet sang đối tượng NhanVien
                NhanVien nv = taoDoiTuongNhanVienTuResultSet(rs);

                // Chỉ thêm vào danh sách nếu ánh xạ thành công
                if (nv != null) {
                    danhSachNV.add(nv);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tải toàn bộ danh sách Nhân Viên:");
            e.printStackTrace();
            throw e; // Ném lỗi để tầng trên xử lý
        }

        return danhSachNV;
    }
    // =================================================================================
    // 3. GET TÀI KHOẢN BY MÃ NV (Dùng cho fillFormFromTable)
    // =================================================================================

    /**
     * Lấy thông tin Tài Khoản và Nhân Viên liên quan bằng MaNV.
     */
//    public TaiKhoan findTaiKhoanByMaNV(String maNV) throws SQLException {
//        TaiKhoan tk = null;
//        String sql = "SELECT NV.*, TK.TenDangNhap, TK.MatKhau, TK.NgayTao, TK.TrangThai " +
//                "FROM TaiKhoan TK JOIN NhanVien NV ON TK.MaNV = NV.MaNV WHERE NV.MaNV = ?";
//
//        try (Connection conn = ConnectDB.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, maNV);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    NhanVien nv = taoDoiTuongNhanVienTuResultSet(rs);
//                    tk = taoDoiTuongTaiKhoanTuResultSet(rs, nv);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw e;
//        }
//        return tk;
//    }


    // =================================================================================
    // 4. GET LAST MÃ NV BY PREFIX
    // =================================================================================

    /**
     * Truy vấn cơ sở dữ liệu để tìm Mã Nhân Viên (MaNV) có giá trị lớn nhất
     * bắt đầu bằng một tiền tố cụ thể (ví dụ: NVBV, NVQL).
     */
    public String getLastMaNhanVienByPrefix(String prefix) throws SQLException {
        String lastMaNV = null;
        String sql = "SELECT TOP 1 MaNV FROM NhanVien WHERE MaNV LIKE ? ORDER BY MaNV DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, prefix + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lastMaNV = rs.getString("MaNV");
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return lastMaNV;
    }

    // =================================================================================
    // 5. CÁC PHƯƠNG THỨC ÁNH XẠ (MAPPING) - Đặt là private static
    // =================================================================================






    public boolean addNhanVien(NhanVien nv, TaiKhoan tk) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtNV = null;
        PreparedStatement pstmtTK = null;
        boolean success = false;
        if (nv == null || tk == null || nv.getHoTen() == null || nv.getHoTen().trim().isEmpty()
                || tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().isEmpty()
                || tk.getMatKhau() == null || tk.getMatKhau().isEmpty()
                || nv.getGioiTinh() == null) {
            throw new IllegalArgumentException("Thông tin Nhân viên hoặc Tài khoản không hợp lệ.");
        }
        String sqlNV = "INSERT INTO NhanVien (MaNV, HoTen, SoCCCD, NgaySinh, Email, SDT, GioiTinh, DiaChi, NgayVaoLam, ChucVu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlTK = "INSERT INTO TaiKhoan (TenDangNhap, MaNV, MatKhau, NgayTao, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);
            String maNV = generateNewMaNV(conn, tk.getTenDangNhap().substring(0, Math.min(4, tk.getTenDangNhap().length())));
            nv.setMaNV(maNV);
            pstmtNV = conn.prepareStatement(sqlNV);
            setNhanVienParameters(pstmtNV, nv, true);
            pstmtNV.executeUpdate();

            pstmtTK = conn.prepareStatement(sqlTK);
            setTaiKhoanParameters(pstmtTK, tk, nv.getMaNV());
            pstmtTK.executeUpdate();

            conn.commit();
            success = true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            System.err.println("Lỗi khi thêm nhân viên: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("PRIMARY KEY constraint") && e.getMessage().contains("PK_TaiKhoan"))
                throw new SQLException("Tên đăng nhập '" + tk.getTenDangNhap() + "' đã tồn tại.", e.getSQLState(), e);
            throw e;
        } finally {
            closeResource(pstmtNV);
            closeResource(pstmtTK);
            if (conn != null) conn.setAutoCommit(true);
        }
        return success;
    }

    public boolean updateNhanVien(NhanVien nv, TaiKhoan tk) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtNV = null;
        PreparedStatement pstmtTK = null;
        boolean success = false;
        if (nv == null || tk == null || nv.getMaNV() == null || nv.getMaNV().isEmpty())
            throw new IllegalArgumentException("Thông tin Nhân viên hoặc Mã NV không hợp lệ để cập nhật.");
        String sqlNV = "UPDATE NhanVien SET HoTen = ?, SoCCCD = ?, NgaySinh = ?, Email = ?, SDT = ?, GioiTinh = ?, DiaChi = ?, NgayVaoLam = ?, ChucVu = ? WHERE MaNV = ?";
        String sqlTK = "UPDATE TaiKhoan SET MatKhau = ?, TrangThai = ? WHERE MaNV = ?";
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            pstmtNV = conn.prepareStatement(sqlNV);
            setNhanVienParameters(pstmtNV, nv, false);
            pstmtNV.executeUpdate();

            pstmtTK = conn.prepareStatement(sqlTK);
            pstmtTK.setString(1, tk.getMatKhau());
            pstmtTK.setString(2, tk.getTrangThai());
            pstmtTK.setString(3, nv.getMaNV());
            pstmtTK.executeUpdate();

            conn.commit();
            success = true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            System.err.println("Lỗi khi cập nhật nhân viên: " + e.getMessage());
            throw e;
        } finally {
            closeResource(pstmtNV);
            closeResource(pstmtTK);
            if (conn != null) conn.setAutoCommit(true);
        }
        return success;
    }

    public boolean softDeleteNhanVien(String maNV) throws SQLException {
        String sql = "UPDATE TaiKhoan SET TrangThai = N'Ngừng hoạt động' WHERE MaNV = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rowsAffected = 0;
        try {
            conn = ConnectDB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maNV);
            rowsAffected = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa mềm nhân viên: " + e.getMessage());
            throw e;
        } finally {
            closeResource(pstmt);
        }
        return rowsAffected > 0;
    }

    public TaiKhoan findTaiKhoanByMaNV(String maNV) throws SQLException {
        TaiKhoan tk = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM NhanVien n JOIN TaiKhoan t ON n.MaNV = t.MaNV WHERE n.MaNV = ?";
        try {
            conn = ConnectDB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maNV);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                NhanVien nv = taoDoiTuongNhanVienTuResultSet(rs);
                tk = taoDoiTuongTaiKhoanTuResultSet(rs, nv);
            }
        } finally {
            closeResource(rs);
            closeResource(pstmt);
        }
        return tk;
    }

    public List<TaiKhoan> searchNhanVien(String searchBy, String searchTerm, String status) throws SQLException {
        List<TaiKhoan> danhSachTK = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder("SELECT * FROM NhanVien n JOIN TaiKhoan t ON n.MaNV = t.MaNV WHERE t.TrangThai = ? ");
        String searchColumn = "";
        switch (searchBy) {
            case "Mã nhân viên":
                searchColumn = "n.MaNV";
                break;
            case "Số điện thoại":
                searchColumn = "n.SDT";
                break;
            case "Số CCCD":
                searchColumn = "n.SoCCCD";
                break;
            case "Họ tên nhân viên":
                searchColumn = "n.HoTen";
                break;
        }
        boolean hasSearchTerm = searchTerm != null && !searchTerm.trim().isEmpty() && !searchColumn.isEmpty();
        if (hasSearchTerm) sql.append(" AND ").append(searchColumn).append(" LIKE ?");
        sql.append(" ORDER BY n.MaNV");
        try {
            conn = ConnectDB.getConnection();
            pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            pstmt.setString(paramIndex++, status);
            if (hasSearchTerm) pstmt.setString(paramIndex++, "%" + searchTerm + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                NhanVien nv = taoDoiTuongNhanVienTuResultSet(rs);
                TaiKhoan tk = taoDoiTuongTaiKhoanTuResultSet(rs, nv);
                if (nv != null && tk != null) danhSachTK.add(tk);
            }
        } finally {
            closeResource(rs);
            closeResource(pstmt);
        }
        return danhSachTK;
    }

    public Map<String, Integer> getStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sqlTotal = "SELECT COUNT(*) FROM TaiKhoan WHERE TrangThai = N'Đang hoạt động'";
        String sqlNV = "SELECT COUNT(*) FROM NhanVien nv JOIN TaiKhoan tk ON nv.MaNV = tk.MaNV WHERE nv.ChucVu = N'Nhân viên bán vé' AND tk.TrangThai = N'Đang hoạt động'";
        String sqlQL = "SELECT COUNT(*) FROM NhanVien nv JOIN TaiKhoan tk ON nv.MaNV = tk.MaNV WHERE nv.ChucVu IN (N'Quản lý', N'Trưởng phòng') AND tk.TrangThai = N'Đang hoạt động'";
        Connection conn = null;
        PreparedStatement pstmtTotal = null, pstmtNV = null, pstmtQL = null;
        ResultSet rsTotal = null, rsNV = null, rsQL = null;
        try {
            conn = ConnectDB.getConnection();
            pstmtTotal = conn.prepareStatement(sqlTotal);
            rsTotal = pstmtTotal.executeQuery();
            if (rsTotal.next()) stats.put("total", rsTotal.getInt(1));

            pstmtNV = conn.prepareStatement(sqlNV);
            rsNV = pstmtNV.executeQuery();
            if (rsNV.next()) stats.put("nhanVien", rsNV.getInt(1));

            pstmtQL = conn.prepareStatement(sqlQL);
            rsQL = pstmtQL.executeQuery();
            if (rsQL.next()) stats.put("quanLy", rsQL.getInt(1));
        } finally {
            closeResource(rsTotal);
            closeResource(rsNV);
            closeResource(rsQL);
            closeResource(pstmtTotal);
            closeResource(pstmtNV);
            closeResource(pstmtQL);
        }
        return stats;
    }

    private String generateNewMaNV(Connection conn, String prefix) throws SQLException {
        String sql = "SELECT TOP 1 MaNV FROM NhanVien WHERE MaNV LIKE ? ORDER BY MaNV DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int nextNumber = 1;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, prefix + "%");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String lastMaNV = rs.getString(1);
                try {
                    String numberPart = lastMaNV.substring(prefix.length());
                    if (!numberPart.isEmpty()) nextNumber = Integer.parseInt(numberPart) + 1;
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.err.println("Không thể phân tích số từ mã NV cuối: " + lastMaNV + ". Sử dụng số 1.");
                }
            }
        } finally {
            closeResource(rs);
            closeResource(pstmt);
        }
        return String.format("%s%03d", prefix, nextNumber);
    }

    private static void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try { resource.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private static NhanVien taoDoiTuongNhanVienTuResultSet(ResultSet rs) throws SQLException {
        // 1. Trích xuất LocalDate trực tiếp từ ResultSet.
        // JDBC 4.2+ (bắt buộc) hỗ trợ toLocalDate() từ java.sql.Date.

        // Đảm bảo cột NgaySinh/NgayVaoLam tồn tại và có kiểu DATE trong CSDL
        java.sql.Date sqlNgaySinh = rs.getDate("NgaySinh");
        java.sql.Date sqlNgayVaoLam = rs.getDate("NgayVaoLam");

        // Chuyển đổi sang LocalDate
        java.time.LocalDate ngaySinhLocal = (sqlNgaySinh != null) ? sqlNgaySinh.toLocalDate() : null;
        java.time.LocalDate ngayVaoLamLocal = (sqlNgayVaoLam != null) ? sqlNgayVaoLam.toLocalDate() : null;

        // 2. Sử dụng Constructor 10 tham số mới của NhanVien
        return new NhanVien(
                rs.getString("MaNV"),
                rs.getString("HoTen"),
                rs.getString("SoCCCD"),
                ngaySinhLocal, // <--- Đã sửa thành LocalDate
                rs.getString("Email"),
                rs.getString("SDT"),
                rs.getString("GioiTinh"),
                rs.getString("DiaChi"),
                ngayVaoLamLocal, // <--- Đã sửa thành LocalDate
                rs.getString("ChucVu")
        );
    }

    private static TaiKhoan taoDoiTuongTaiKhoanTuResultSet(ResultSet rs, NhanVien nv) throws SQLException {
        Timestamp sqlNgayTao = rs.getTimestamp("NgayTao");
        LocalDate localNgayTao = (sqlNgayTao != null)
                ? sqlNgayTao.toLocalDateTime().toLocalDate()
                : null; // Trả về null nếu CSDL là NULL, tránh gán ngày hiện tại một cách tùy tiện.

        // FIX LỜI GỌI CONSTRUCTOR:
        // Sửa lời gọi để khớp với constructor 5 tham số có chứa NhanVien,
        // và đảm bảo thứ tự tham số khớp với constructor đã được sửa trong entity.TaiKhoan.
        // Constructor mẫu: (String tenDangNhap, String matKhau, LocalDate ngayTao, String trangThai, NhanVien nhanVien)
        return new TaiKhoan(
                rs.getString("TenDangNhap"),
                rs.getString("MatKhau"),
                localNgayTao, // Tham số 3: LocalDate
                rs.getString("TrangThai"), // Tham số 4: String
                nv // Tham số 5: NhanVien (LIÊN KẾT)
        );
    }

    private void setNhanVienParameters(PreparedStatement pstmt, NhanVien nv, boolean isAdding) throws SQLException {
        int index = 1;
        if (isAdding) pstmt.setString(index++, nv.getMaNV());
        pstmt.setString(index++, nv.getHoTen());
        pstmt.setString(index++, nv.getSoCCCD());

        // NgaySinh may be String, LocalDate, Date; convert to java.sql.Date if possible
        java.sql.Date sqlNgaySinh = toSqlDateFromObject(nv.getNgaySinh());
        if (sqlNgaySinh != null) pstmt.setDate(index++, sqlNgaySinh);
        else pstmt.setNull(index++, Types.DATE);

        pstmt.setString(index++, nv.getEmail());
        pstmt.setString(index++, nv.getSdt());
        pstmt.setString(index++, nv.getGioiTinh());
        pstmt.setString(index++, nv.getDiaChi());

        java.sql.Date sqlNgayVaoLam = toSqlDateFromObject(nv.getNgayVaoLam());
        if (sqlNgayVaoLam != null) pstmt.setDate(index++, sqlNgayVaoLam);
        else pstmt.setNull(index++, Types.DATE);

        pstmt.setString(index++, nv.getChucVu());
        if (!isAdding) pstmt.setString(index++, nv.getMaNV());
    }

    private void setTaiKhoanParameters(PreparedStatement pstmt, TaiKhoan tk, String maNV) throws SQLException {
        pstmt.setString(1, tk.getTenDangNhap());
        pstmt.setString(2, maNV);
        pstmt.setString(3, tk.getMatKhau());

        Timestamp tsNgayTao = toTimestampFromObject(tk.getNgayTao());
        if (tsNgayTao != null) pstmt.setTimestamp(4, tsNgayTao);
        else pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

        pstmt.setString(5, tk.getTrangThai());
    }

    // Helper: convert various date representations to java.sql.Date
    private java.sql.Date toSqlDateFromObject(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.sql.Date) return (java.sql.Date) obj;
        if (obj instanceof java.util.Date) return new java.sql.Date(((java.util.Date) obj).getTime());
        if (obj instanceof LocalDate) return java.sql.Date.valueOf((LocalDate) obj);
        if (obj instanceof String) {
            String s = ((String) obj).trim();
            if (s.isEmpty()) return null;
            // try ISO first, then dd/MM/yyyy
            try {
                LocalDate ld = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
                return java.sql.Date.valueOf(ld);
            } catch (DateTimeParseException e) {
                try {
                    Date parsed = new SimpleDateFormat("dd/MM/yyyy").parse(s);
                    return new java.sql.Date(parsed.getTime());
                } catch (ParseException ex) {
                    // cannot parse
                    return null;
                }
            }
        }
        return null;
    }

    // Helper: convert LocalDate/Date/String to Timestamp for TaiKhoan.NgayTao
    private Timestamp toTimestampFromObject(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Timestamp) return (Timestamp) obj;
        if (obj instanceof java.util.Date) return new Timestamp(((java.util.Date) obj).getTime());
        if (obj instanceof LocalDate) return Timestamp.valueOf(((LocalDate) obj).atStartOfDay());
        if (obj instanceof String) {
            String s = ((String) obj).trim();
            if (s.isEmpty()) return null;
            try {
                LocalDate ld = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
                return Timestamp.valueOf(ld.atStartOfDay());
            } catch (DateTimeParseException e) {
                try {
                    Date parsed = new SimpleDateFormat("dd/MM/yyyy").parse(s);
                    return new Timestamp(parsed.getTime());
                } catch (ParseException ex) {
                    return null;
                }
            }
        }
        return null;
    }

}