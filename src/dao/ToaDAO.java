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

        // Truy vấn lấy các toa thuộc một tàu
        String sql = "SELECT MaToa, MaTau, LoaiToa "
                + "FROM Toa "
                + "WHERE MaTau = ?";

        try {Connection con = ConnectDB.getConnection();

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maTau);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Lấy dữ liệu thô
                    String maToa = rs.getString("MaToa");
                    String maTauDB = rs.getString("MaTau");
                    String loaiToaStr = rs.getString("LoaiToa");

                    // 2. Tra cứu (Lookup) đối tượng Tau
                    Tau tau = TauDAO.getTauById(maTauDB);

                    // 3. Tạo đối tượng Toa
                    // Giả định Toa có constructor: (maToa, tau, loaiToa)
                    Toa toa = new Toa(maToa, tau, loaiToaStr);
                    danhSachToa.add(toa);
                   }
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
                    String maTauDB = rs.getString("MaTau");
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
            stmt.setString(2, toa.getTau().getMaTau());
            stmt.setString(3, toa.getLoaiToa());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}