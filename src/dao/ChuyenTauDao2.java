package dao;

import database.ConnectDB;
import entity.ChuyenTau;
import entity.Ga;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChuyenTauDao2 {

    // TIMKIEM THEO MAHOADON
    public static ChuyenTau timKiemChuyenTauTheoMaHoaDon(String maHoaDon) {
        String sql = """
                SELECT DISTINCT J.GaDi, J.GaDen, J.NgayKhoiHanh, J.GioKhoiHanh
                FROM HoaDon HD 
                JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                JOIN Ve V ON V.MaVe = CT.MaVe
                JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                WHERE HD.MaHD = ?
                """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                return ct;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TIMKIEM THEO SODIENTHOAI
    public static ArrayList<ChuyenTau> timKiemChuyenTauTheoSoDienThoai(String giaTriTimKiem) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
                
                                        
                                        SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                        FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                        JOIN Ve V ON V.MaVe = CT.MaVe
                                        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                        WHERE SoDienThoai = ?
                """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                danhSach.add(ct);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public static ArrayList<ChuyenTau> timChuyenTauTheoCCCD(String giaTriTimKiem) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
                                SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen,KH.CCCD, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                 FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                 JOIN Ve V ON V.MaVe = CT.MaVe
                                 JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                 JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                 WHERE KH.CCCD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                danhSach.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Lọc Chuyến Tàu theo SĐT Khách Hàng + Tháng/Năm hóa đơn
    public static ArrayList<ChuyenTau> timChuyenTauTheoSDTLocThangNam(String sdt, int thang, int nam) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
            SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                        FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                        JOIN Ve V ON V.MaVe = CT.MaVe
                                        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                        WHERE KH.SoDienThoai = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau(); // Giả sử có setters hoặc constructor phù hợp
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                // Set các thuộc tính khác nếu cần
                danhSach.add(ct);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // Lọc Chuyến Tàu theo CCCD Khách Hàng + Tháng/Năm hóa đơn
    public static ArrayList<ChuyenTau> timChuyenTauTheoCCCDLocThangNam(String cccd, int thang, int nam) {
        ArrayList<ChuyenTau> danhSach = new ArrayList<>();
        String sql = """
             SELECT DISTINCT HD.MaHD, KH.SoDienThoai, KH.HoTen, J.GaDi, J.GaDen , J.NgayKhoiHanh, J.GioKhoiHanh
                                        FROM HoaDon HD JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
                                        JOIN Ve V ON V.MaVe = CT.MaVe
                                        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
                                        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
                                        WHERE KH.CCCD = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
             """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChuyenTau ct = new ChuyenTau();
                ct.gaDi = new Ga(null, rs.getString("GaDi"), null);
                ct.gaDen = new Ga(null, rs.getString("GaDen"), null);
                ct.ngayKhoiHanh = rs.getObject("NgayKhoiHanh", LocalDate.class);
                ct.gioKhoiHanh = rs.getObject("GioKhoiHanh", LocalTime.class);
                danhSach.add(ct);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }
}