package dao;

import database.ConnectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
     * Tìm vé theo thông tin Khách hàng và/hoặc Mã vé.
     */
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

    /**
     * Lấy chi tiết vé theo Mã vé, bao gồm các thực thể phụ thuộc.
     */
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

}