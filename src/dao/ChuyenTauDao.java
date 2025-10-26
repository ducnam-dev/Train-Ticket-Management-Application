
package dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import entity.ChuyenTau;
import entity.Ga;
import entity.NhanVien;
import entity.Tau;
import database.ConnectDB;
import entity.lopEnum.TrangThaiChuyenTau;

public class ChuyenTauDao {
    private ArrayList<ChuyenTau> danhSachChuyenTau;

    public ChuyenTauDao() {
        danhSachChuyenTau = new ArrayList<ChuyenTau>();
    }

    public List<ChuyenTau> timChuyenTau(String gaXP, String gaKT, String ngayDi) {
        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
        // Định dạng ngày: SQL Server thường dùng yyyy-MM-dd. Đổi "30/09/2025" thành "2025-09-30"

        String sql = "SELECT * FROM ChuyenTau WHERE MaGaKhoiHanh = ? AND MaGaDen = ? AND NgayKhoiHanh = ?";

        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, gaXP);
                pstmt.setString(2, gaKT);
                pstmt.setString(3, ngayDi); // Đã format lại
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String maChuyenTau = rs.getString("MaChuyenTau");
                        String maTau = rs.getString("MaTau");
                        String maNV = rs.getString("MaNV");
                        String maGaDiDb = rs.getString("MaGaKhoiHanh");
                        String maGaDenDb = rs.getString("MaGaDen");
                        LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                        LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                        LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                        LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
                        String trangThai = rs.getString("TrangThai");

                        TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);

                        Ga gaDi = GaDao.getGaById(maGaDiDb);
                        Ga gaDen = GaDao.getGaById(maGaDenDb);
                        Tau tau = TauDAO.getTauById(maTau);
                        NhanVien nv = NhanVienDao.getNhanVienById(maNV);

                        ChuyenTau ct = new ChuyenTau(maChuyenTau, maTau, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                        danhSachChuyenTau.add(ct);
                    }
                }
            }// PreparedStatement is closed here
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm chuyến tàu: ");
            e.printStackTrace();
        }
        return danhSachChuyenTau;
    }

    public boolean chuyenTrangThaiChuyenTau(String maChuyenTau, String trangThai) {
        String sql = "UPDATE ChuyenTau SET TrangThai = ? WHERE MaChuyenTau = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setString(2, maChuyenTau);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                if (this.danhSachChuyenTau != null) {
                    for (ChuyenTau ct : this.danhSachChuyenTau) {
                        if (ct.getMaChuyenTau().equals(maChuyenTau)) {
                            // assuming setter exists
                            ct.setThct(TrangThaiChuyenTau.valueOf(trangThai));
                            break;
                        }
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}