package dao;

import database.ConnectDB;
import entity.KhuyenMai; // Đã đổi tên từ KhuyenMaiOptimized thành KhuyenMai

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter; // Để format LocalDateTime sang String cho SQL
import java.time.LocalDateTime;
/**
 * DAO cho KhuyenMai - Tương ứng với cấu trúc CSDL đã được tối ưu hóa.
 */
public class KhuyenMaiDAO {

    // Sử dụng định dạng DATETIME của SQL Server
    private static final DateTimeFormatter SQL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Lấy TẤT CẢ khuyến mãi (bao gồm cả Đã kết thúc và Chưa hoạt động) để hiển thị lên bảng quản lý.
     * Đây là phương thức cần dùng cho loadDataToTable() trong ManHinhQuanLyKhuyenMai.
     */
    public List<KhuyenMai> layTatCaKhuyenMai() {
        List<KhuyenMai> list = new ArrayList<>();
        // Sắp xếp theo NgayBD (mới nhất lên trên)
        String sql = "SELECT * FROM KhuyenMai ORDER BY NgayBD DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhuyenMai km = chuyenKetQuaSangKhuyenMai(rs);
                list.add(km);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn lấy TẤT CẢ Khuyến mãi: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy tất cả khuyến mãi "active" theo logic thời gian (NgayBD, NgayKT).
     */
    public List<KhuyenMai> layTatCaKMHoatDong() {
        List<KhuyenMai> activeList = new ArrayList<>();
        // LƯU Ý: Điều kiện TrangThai = 'HOAT_DONG' loại trừ các KM bị quản lý viên tạm dừng thủ công
        String sql = "SELECT * FROM KhuyenMai " +
                "WHERE TrangThai = 'HOAT_DONG' " +
                "  AND NgayBD <= GETDATE() " +
                "  AND NgayKT >= GETDATE()";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhuyenMai km = chuyenKetQuaSangKhuyenMai(rs);
                activeList.add(km);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn lấy Khuyến mãi hoạt động: " + e.getMessage());
        }
        return activeList;
    }
    /**
     * Tìm kiếm Khuyến mãi theo Mã KM. (Đổi tên cho rõ ràng)
     * @param maKM Mã Khuyến mãi cần tìm.
     * @return Đối tượng KhuyenMai nếu tìm thấy, null nếu không tìm thấy.
     */
    public KhuyenMai layKhuyenMaiTheoMa(String maKM) {
        KhuyenMai km = null;
        String sql = "SELECT * FROM KhuyenMai WHERE MaKM = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKM);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    km = chuyenKetQuaSangKhuyenMai(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm Khuyến mãi: " + e.getMessage());
        }
        return km;
    }
    /**
     * Phương thức thêm mới một Khuyến mãi vào CSDL.
     */
    public boolean themKhuyenMai(KhuyenMai km) {
        // ... (Giữ nguyên logic thêmKhuyenMai)
        String sql = "INSERT INTO KhuyenMai (MaKM, TenKM, LoaiKM, GiaTriGiam, DKApDung, GiaTriDK, NgayBD, NgayKT, TrangThai) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setNString(2, km.getTenKM());
            ps.setString(3, km.getLoaiKM());
            ps.setBigDecimal(4, km.getGiaTriGiam());
            ps.setString(5, km.getDkApDung());

            if (km.getGiaTriDK() != null) {
                ps.setBigDecimal(6, km.getGiaTriDK());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }

            ps.setTimestamp(7, Timestamp.valueOf(km.getNgayBD()));
            ps.setTimestamp(8, Timestamp.valueOf(km.getNgayKT()));
            ps.setString(9, km.getTrangThai());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm Khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Phương thức cập nhật toàn bộ thông tin Khuyến mãi.
     */
    public boolean suaKhuyenMai(KhuyenMai km) {
        // ... (Giữ nguyên logic suaKhuyenMai)
        String sql = "UPDATE KhuyenMai SET TenKM=?, LoaiKM=?, GiaTriGiam=?, DKApDung=?, GiaTriDK=?, NgayBD=?, NgayKT=?, TrangThai=? " +
                "WHERE MaKM=?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setNString(1, km.getTenKM());
            ps.setString(2, km.getLoaiKM());
            ps.setBigDecimal(3, km.getGiaTriGiam());
            ps.setString(4, km.getDkApDung());

            if (km.getGiaTriDK() != null) {
                ps.setBigDecimal(5, km.getGiaTriDK());
            } else {
                ps.setNull(5, Types.DECIMAL);
            }

            ps.setTimestamp(6, Timestamp.valueOf(km.getNgayBD()));
            ps.setTimestamp(7, Timestamp.valueOf(km.getNgayKT()));
            ps.setString(8, km.getTrangThai());
            ps.setString(9, km.getMaKM());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi sửa Khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    // =================================================================================
    // 3. CẬP NHẬT TRẠNG THÁI (Kết thúc/Gia hạn)
    // =================================================================================

    /**
     * Phương thức cập nhật trạng thái và/hoặc ngày kết thúc.
     * Dùng cho chức năng Kết Thúc KM (ngayKT = NgayHomNay) và Gia Hạn KM (ngayKT = NgayMoi).
     * @param maKM Mã KM cần cập nhật.
     * @param trangThai Trạng thái mới (ví dụ: 'HET_HAN' hoặc 'HOAT_DONG').
     * @param ngayKetThucMoi Ngày kết thúc mới.
     * @return true nếu thành công.
     */
    public boolean capNhatTrangThai(String maKM, String trangThai, LocalDateTime ngayKetThucMoi) {
        String sql = "UPDATE KhuyenMai SET TrangThai = ?, NgayKT = ? WHERE MaKM = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trangThai);
            ps.setTimestamp(2, Timestamp.valueOf(ngayKetThucMoi));
            ps.setString(3, maKM);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật Trạng thái Khuyến mãi: " + e.getMessage());
            return false;
        }
    }


    // =================================================================================
    // 4. PHƯƠNG THỨC TRỢ GIÚP
    // =================================================================================

    private KhuyenMai chuyenKetQuaSangKhuyenMai(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaKM(rs.getString("MaKM"));
        km.setTenKM(rs.getNString("TenKM")); // Dùng getNString cho NVARCHAR
        km.setLoaiKM(rs.getString("LoaiKM"));
        km.setGiaTriGiam(rs.getBigDecimal("GiaTriGiam"));
        km.setDkApDung(rs.getString("DKApDung"));
        km.setGiaTriDK(rs.getBigDecimal("GiaTriDK"));

        // Chuyển đổi từ SQL DATETIME sang Java LocalDateTime
        km.setNgayBD(rs.getTimestamp("NgayBD").toLocalDateTime());
        km.setNgayKT(rs.getTimestamp("NgayKT").toLocalDateTime());

        km.setTrangThai(rs.getString("TrangThai"));
        return km;
    }

    /**
     * Tự động khởi tạo mã khuyến mãi mới (KMxxxx) bằng cách tìm mã lớn nhất hiện có và tăng lên 1.
     * @return Mã khuyến mãi mới dưới dạng chuỗi (ví dụ: KM0005).
     */
    public String khoiTaoMaKMMoi() {
        String latestMaKM = null;
        String newMaKM = "KM0001"; // Mã mặc định nếu chưa có KM nào

        // Truy vấn lấy Mã KM lớn nhất (sắp xếp giảm dần và lấy 1)
        String sql = "SELECT TOP 1 MaKM FROM KhuyenMai ORDER BY MaKM DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                latestMaKM = rs.getString("MaKM");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn khởi tạo Mã Khuyến mãi: " + e.getMessage());
            // Trả về mã mặc định nếu có lỗi
            return newMaKM;
        }

        // Xử lý tạo mã mới từ mã lớn nhất tìm được
        if (latestMaKM != null && latestMaKM.startsWith("KM")) {
            try {
                // Lấy phần số từ chuỗi (ví dụ: "KM0004" -> 4)
                String numberPart = latestMaKM.substring(2);
                int number = Integer.parseInt(numberPart);

                // Tăng lên 1
                int newNumber = number + 1;

                // Format lại thành chuỗi 4 chữ số (ví dụ: 5 -> "0005")
                newMaKM = String.format("KM%04d", newNumber);

            } catch (NumberFormatException e) {
                System.err.println("Lỗi định dạng Mã Khuyến mãi: " + latestMaKM);
                // Nếu định dạng sai, vẫn trả về mã mặc định
            }
        }

        return newMaKM;
    }
}