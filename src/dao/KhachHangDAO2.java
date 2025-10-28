package dao;

import database.ConnectDB;
import entity.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO2 {
    private List<KhachHang> dsKhachHang = new ArrayList<>();

    public static KhachHang timKhachHangTheoMaHoaDon(String maHoaDon) {
        String sql = """
        SELECT DISTINCT KH.HoTen, KH.SoDienThoai, KH.CCCD
        FROM HoaDon HD 
        JOIN ChiTietHoaDon CT ON HD.MaHD = CT.MaHD
        JOIN Ve V ON V.MaVe = CT.MaVe
        JOIN KhachHang KH ON KH.MaKhachHang = HD.MaKhachHang
        JOIN ChuyenTau J ON J.MaChuyenTau = V.MaChuyenTau
        WHERE HD.MaHD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHoaDon);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.hoTen = rs.getString("HoTen");
                kh.sdt = rs.getString("SoDienThoai");
                kh.soCCCD = rs.getString("CCCD");
                // Các trường khác để mặc định
                return kh;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<KhachHang> timKhachHangTheoSoDienThoai(String giaTriTimKiem) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
        SELECT KH.HoTen, KH.SoDienThoai, KH.CCCD, HD.MaHD
        FROM HoaDon HD 
        JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
        WHERE KH.SoDienThoai = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.hoTen = rs.getString("HoTen");
                kh.sdt = rs.getString("SoDienThoai");
                kh.soCCCD = rs.getString("CCCD");
                // Có thể thêm MaHD vào nếu cần (tạo thêm thuộc tính hoặc dùng Map)
                danhSach.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public static ArrayList<KhachHang> timKhachHangTheoCCCD(String giaTriTimKiem) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
        SELECT KH.HoTen, KH.SoDienThoai, KH.CCCD, HD.MaHD
        FROM HoaDon HD 
        JOIN KhachHang KH ON HD.MaKhachHang = KH.MaKhachHang
        WHERE KH.CCCD = ?
        """;

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, giaTriTimKiem);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.hoTen = rs.getString("HoTen");
                kh.sdt = rs.getString("SoDienThoai");
                kh.soCCCD = rs.getString("CCCD");
                // Nếu cần MaHD, thêm thuộc tính hoặc dùng class khác
                danhSach.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Lọc Khách Hàng theo SĐT + Tháng/Năm hóa đơn
    public static ArrayList<KhachHang> timKhachHangTheoSDTLocThangNam(String sdt, int thang, int nam) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
            SELECT DISTINCT HD.MaHD, KH.MaKhachHang, KH.HoTen, KH.SoDienThoai, KH.CCCD
            FROM KhachHang KH
            JOIN HoaDon HD ON KH.MaKhachHang = HD.MaKhachHang
            WHERE KH.SoDienThoai = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(); // Giả sử có setters hoặc constructor phù hợp
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SoDienThoai"));
                kh.setSoCCCD(rs.getString("CCCD"));
                danhSach.add(kh);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // Lọc Khách Hàng theo CCCD + Tháng/Năm hóa đơn
    public static ArrayList<KhachHang> timKhachHangTheoCCCDLocThangNam(String cccd, int thang, int nam) {
        ArrayList<KhachHang> danhSach = new ArrayList<>();
        String sql = """
            SELECT DISTINCT HD.MaHD, KH.MaKhachHang, KH.HoTen, KH.SoDienThoai, KH.CCCD
            FROM KhachHang KH
            JOIN HoaDon HD ON KH.MaKhachHang = HD.MaKhachHang
            WHERE KH.CCCD = ? AND MONTH(HD.NgayLap) = ? AND YEAR(HD.NgayLap) = ?
                
            """;
        try (Connection conn = ConnectDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cccd);
            pstmt.setInt(2, thang);
            pstmt.setInt(3, nam);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setHoTen(rs.getString("HoTen"));
                kh.setSdt(rs.getString("SoDienThoai"));
                kh.setSoCCCD(rs.getString("CCCD"));
                danhSach.add(kh);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }
}
