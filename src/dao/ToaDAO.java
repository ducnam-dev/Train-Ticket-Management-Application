package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;
import entity.Toa; // Sử dụng tên Toa entity theo yêu cầu
import entity.Tau;

public class ToaDAO {

    // Phương thức LẤY DANH SÁCH TOA THEO MÃ TÀU (Cần thiết để biết các toa tàu có trong một chuyến tàu)
    // Lưu ý: Trong thiết kế DB của bạn, Toa liên kết với Tau (MaTau),
    // nên để tìm toa cho một chuyến tàu, ta cần tìm MaTau của chuyến tàu đó trước.
    public List<Toa> layToaTheoMaTau(String maTau) {
        List<Toa> danhSachToa = new ArrayList<>();

        // SQL Tối ưu: JOIN với bảng Tau để lấy thông tin (TrangThai) của tàu
        String sql = "SELECT T.MaToa, T.SoHieuTau, T.LoaiToa, "
                + "TAU.TrangThai AS TrangThaiTau "
                + "FROM Toa T "
                + "LEFT JOIN Tau TAU ON T.SoHieuTau = TAU.SoHieu "
                + "WHERE T.SoHieuTau = ?";

        // SỬ DỤNG TRY-WITH-RESOURCES CHO CONNECTION
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maTau);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Lấy dữ liệu thô
                    String maToa = rs.getString("MaToa");
                    String maTauDB = rs.getString("SoHieuTau");
                    String loaiToaStr = rs.getString("LoaiToa");

                    // 2. TẠO đối tượng Tau (Tận dụng dữ liệu JOIN)
                    // KHÔNG gọi TauDAO.getTauById() nữa!
                    String trangThaiTau = rs.getString("TrangThaiTau");
                    Tau tau = new Tau(maTauDB, trangThaiTau);

                    // 3. Tạo đối tượng Toa
                    Toa toa = new Toa(maToa, tau, loaiToaStr);
                    danhSachToa.add(toa);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách toa cho tàu " + maTau + ": ");
            e.printStackTrace();
        }

        return danhSachToa;
    }

    // Lấy thông tin một toa tàu theo Mã Toa
    public Toa getToaById(String maToa) {
        Toa toa = null;
        String sql = "SELECT MaToa, MaTau, LoaiToa FROM Toa WHERE MaToa = ?";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql);
        ) {
            pstmt.setString(1, maToa);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String maTauDB = rs.getString("SoHieuTau");
                    String loaiToaStr = rs.getString("LoaiToa");

                    Tau tau = TauDAO.getTauById(maTauDB);

                    toa = new Toa(maToa, tau, loaiToaStr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toa;
    }

    // Phương thức thêm Toa tàu mới (tham khảo)
    public boolean themToa(Toa toa) {
        String sql = "INSERT INTO Toa (MaToa, MaTau, LoaiToa) VALUES (?, ?, ?)";
        try (
                Connection con = ConnectDB.getInstance().getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, toa.getMaToa());
            stmt.setString(2, toa.getTau().getSoHieu());
            stmt.setString(3, toa.getLoaiToa());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<Toa> getAllToa() {
        List<Toa> danhSachToa = new ArrayList<>();
        // Truy vấn lấy các cột cần thiết, bao gồm mã tàu và hệ số toa
        String sql = "SELECT maToa, soHieuTau, loaiToa, heSoToa FROM Toa";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Toa toa = new Toa();
                toa.setMaToa(rs.getString("maToa"));
                toa.setLoaiToa(rs.getString("loaiToa"));

                // Xử lý thực thể Tau: Tạo đối tượng Tau mới và gán mã tàu vào
                // Nếu bạn có TauDAO, bạn có thể gọi TauDAO.getTauByMa() để lấy đầy đủ thông tin tàu
                Tau tau = new Tau();
                tau.setSoHieu(rs.getString("maTau"));
                toa.setTau(tau);

                danhSachToa.add(toa);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachToa;
    }
    public boolean capNhatHeSoToa(String maToa, double heSoMoi) {
        String sql = "UPDATE Toa SET heSoToa = ? WHERE maToa = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDouble(1, heSoMoi);
            stmt.setString(2, maToa);

            // Trả về true nếu có ít nhất một dòng được cập nhật thành công
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hệ số toa: " + e.getMessage());
            return false;
        }
    }


}