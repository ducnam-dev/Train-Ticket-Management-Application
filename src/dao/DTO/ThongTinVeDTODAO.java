package dao.DTO;

import entity.DTO.ThongTinVeDTO;
import database.ConnectDB; // Class kết nối của bạn
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongTinVeDTODAO {

    // Câu SQL gốc lấy full thông tin (đã nối bảng sẵn)
    private static final String BASE_QUERY =
            "SELECT V.MaVe, TenLoaiVe, HoTen, CCCD, SoDienThoai, V.MaChuyenTau, " +
                    "GaDi, GaDen, NgayKhoiHanh, GioKhoiHanh, NgayDenDuKien, GioDenDuKien, " +
                    "SoHieuTau, T.MaToa, LoaiToa, SoCho, Khoang, Tang, GiaVe, V.TrangThai " +
                    "FROM KhachHang KH " +
                    "JOIN Ve V ON KH.MaKhachHang = V.MaKhachHang " +
                    "JOIN ChoDat CD ON V.MaChoDat = CD.MaCho " +
                    "JOIN ChuyenTau CT ON CT.MaChuyenTau = V.MaChuyenTau " +
                    "JOIN Toa T ON CD.MaToa = T.MaToa " +
                    "JOIN Tau TR ON TR.SoHieu = T.SoHieuTau " +
                    "JOIN LoaiVe L ON V.MaLoaiVe = L.MaLoaiVe ";

    /**
     * 1. Tìm kiếm theo MÃ VÉ
     */
    public ThongTinVeDTO getVeByMaVe(String maVe) {
        ThongTinVeDTO ve = null;
        String sql = BASE_QUERY + " WHERE V.MaVe = ?";

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, maVe);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ve = mapToDTO(rs);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ve;
    }

    /**
     * 2. Tìm kiếm theo SỐ ĐIỆN THOẠI
     */
    public List<ThongTinVeDTO> getVeBySDT(String sdt) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE KH.SoDienThoai = ?";

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, sdt);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapToDTO(rs));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 3. Tìm kiếm theo CĂN CƯỚC CÔNG DÂN
     */
    public List<ThongTinVeDTO> getVeByCCCD(String cccd) {
        List<ThongTinVeDTO> list = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE KH.CCCD = ?";

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cccd);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapToDTO(rs));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 4. Tìm kiếm theo MÃ HÓA ĐƠN
     */
    public List<ThongTinVeDTO> getVeByMaHoaDon(String maHoaDon) {
        List<ThongTinVeDTO> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "    HD.MaHD, V.MaVe, L.TenLoaiVe, " +
                        "    KHV.HoTen, KHV.CCCD, KHV.SoDienThoai, " +
                        "    V.MaChuyenTau, CT.GaDi, CT.GaDen, " +
                        "    CT.NgayKhoiHanh, CT.GioKhoiHanh, " +
                        "    CT.NgayDenDuKien, CT.GioDenDuKien, " +
                        "    TR.SoHieu AS SoHieuTau, " +
                        "    T.MaToa, T.LoaiToa, " +
                        "    CD.SoCho, CD.Khoang, CD.Tang, " +
                        "    V.GiaVe, V.TrangThai " +
                        "FROM HoaDon HD " +
                        "JOIN KhachHang KHHD      ON KHHD.MaKhachHang = HD.MaKhachHang " +
                        "JOIN ChiTietHoaDon CTHD  ON HD.MaHD = CTHD.MaHD " +
                        "JOIN Ve V                ON V.MaVe = CTHD.MaVe " +
                        "JOIN KhachHang KHV       ON KHV.MaKhachHang = V.MaKhachHang " +
                        "JOIN LoaiVe L            ON L.MaLoaiVe = V.MaLoaiVe " +
                        "JOIN ChuyenTau CT        ON CT.MaChuyenTau = V.MaChuyenTau " +
                        "JOIN ChoDat CD           ON CD.MaCho = V.MaChoDat " +
                        "JOIN Toa T               ON T.MaToa = CD.MaToa " +
                        "JOIN Tau TR              ON TR.SoHieu = T.SoHieuTau " +
                        "WHERE HD.MaHD = ?";

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapToDTO(rs));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 5. Tìm kiếm theo số điện thoại và lọc
     */
    public List<ThongTinVeDTO> getVeTheoSDTVaLoTrinh(String sdt, String gaDi, String gaDen, Date ngayKhoiHanh) {
        List<ThongTinVeDTO> list = new ArrayList<>();

        // Query giữ nguyên
        String sql = BASE_QUERY + " WHERE KH.SoDienThoai = ? AND CT.GaDi = ? AND CT.GaDen = ? AND CT.NgayKhoiHanh = ?";

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, sdt);
            pstmt.setString(2, gaDi);
            pstmt.setString(3, gaDen);
            pstmt.setDate(4, ngayKhoiHanh);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapToDTO(rs));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 6. Tìm kiếm theo CCCD và lọc
     */
    public List<ThongTinVeDTO> getVeTheoCCCDVaLoTrinh(String cccd, String gaDi, String gaDen, Date ngayKhoiHanh) {
        List<ThongTinVeDTO> list = new ArrayList<>();

        // Query giữ nguyên
        String sql = BASE_QUERY + " WHERE KH.CCCD = ? AND CT.GaDi = ? AND CT.GaDen = ? AND CT.NgayKhoiHanh = ?";

        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, cccd);
            pstmt.setString(2, gaDi);
            pstmt.setString(3, gaDen);
            pstmt.setDate(4, ngayKhoiHanh);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapToDTO(rs));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================================
    // HÀM PHỤ TRỢ: ÁNH XẠ TỪ RESULTSET VÀO DTO (ĐÃ SỬA LOGIC CẮT CHUỖI)
    // ==========================================================
    private ThongTinVeDTO mapToDTO(ResultSet rs) throws SQLException {
        // 1. Xử lý trạng thái hiển thị
        String trangThaiRaw = rs.getString("TrangThai");
        String trangThaiHienThi = trangThaiRaw;

        if ("DA_BAN".equalsIgnoreCase(trangThaiRaw)) {
            trangThaiHienThi = "Đã bán";
        } else if ("DA_HUY".equalsIgnoreCase(trangThaiRaw)) {
            trangThaiHienThi = "Đã hủy";
        }

        // 2. Xử lý Mã Toa (Cắt bỏ tiền tố trước dấu gạch ngang)
        // Ví dụ DB: "SPT2-5" -> Kết quả: "5"
        String rawMaToa = rs.getString("MaToa");
        String cleanMaToa = rawMaToa;

        if (rawMaToa != null && rawMaToa.contains("-")) {
            // Lấy phần sau dấu gạch ngang cuối cùng
            cleanMaToa = rawMaToa.substring(rawMaToa.lastIndexOf("-") + 1);
        }

        // 3. Tạo đối tượng DTO
        return new ThongTinVeDTO(
                rs.getString("MaVe"),
                rs.getString("TenLoaiVe"),
                rs.getString("HoTen"),
                rs.getString("CCCD"),
                rs.getString("SoDienThoai"),
                rs.getString("MaChuyenTau"),
                rs.getString("GaDi"),
                rs.getString("GaDen"),

                rs.getDate("NgayKhoiHanh").toLocalDate(),
                rs.getTime("GioKhoiHanh").toLocalTime(),
                rs.getDate("NgayDenDuKien").toLocalDate(),
                rs.getTime("GioDenDuKien").toLocalTime(),

                rs.getString("SoHieuTau"),
                cleanMaToa,             // <--- TRUYỀN MÃ TOA ĐÃ XỬ LÝ VÀO ĐÂY
                rs.getString("LoaiToa"),
                rs.getString("SoCho"),
                rs.getString("Khoang"),
                rs.getString("Tang"),
                rs.getDouble("GiaVe"),
                trangThaiHienThi
        );
    }

    public static void main(String[] args) {
        try { ConnectDB.getInstance().connect(); } catch(Exception e){}
        ThongTinVeDTODAO dao = new ThongTinVeDTODAO();

        System.out.println(">>> TEST MA VE: VE011412250001");
        print(dao.getVeByMaVe("VE011412250001"));

        System.out.println("\n>>> TEST CCCD: 060205006764");
        dao.getVeByCCCD("060205006764").forEach(ThongTinVeDTODAO::print);

        System.out.println("\n>>> TEST SDT: 0332534542");
        dao.getVeBySDT("0332534542").forEach(ThongTinVeDTODAO::print);

        System.out.println("\n>>> TEST HOA DON: HD0114122500010005");
        dao.getVeByMaHoaDon("HD0114122500010005").forEach(ThongTinVeDTODAO::print);
    }

    private static void print(ThongTinVeDTO v) {
        if (v == null) return;
        // In ra để kiểm tra xem MaToa đã được cắt đúng chưa
        System.out.printf("[%s] %s | %s - Toa: %s (%s) | K:%s T:%s | %.0f | %s\n",
                v.getMaVe(), v.getHoTen(), v.getSoHieuTau(), v.getMaToa(), v.getLoaiToa(),
                v.getKhoang(), v.getTang(), v.getGiaVe(), v.getTrangThai());
    }
}