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

    /**
     * Tra cứu chi tiết Chuyến tàu bằng Mã Chuyến Tàu.
     * ĐÃ HOÀN THIỆN LOGIC ÁNH XẠ.
     */
    public static ChuyenTau getChuyenTauById(String maChuyenTau) {
        ChuyenTau ct = null;
        String sql = "SELECT * FROM ChuyenTau WHERE MaChuyenTau = ?";

        Connection con = null; // KHAI BÁO BÊN NGOÀI (Đã sửa lỗi đóng kết nối sớm)
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {

                pstmt.setString(1, maChuyenTau);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // 1. Lấy các mã khóa ngoại và dữ liệu cơ bản
                        String maTau = rs.getString("MaTau");
                        String maNV = rs.getString("MaNV").trim(); // TRIM MaNV
                        String maGaDiDb = rs.getString("MaGaKhoiHanh");
                        String maGaDenDb = rs.getString("MaGaDen");
                        String trangThai = rs.getString("TrangThai");

                        // 2. Tra cứu Entity phụ thuộc (GIẢ ĐỊNH CÁC DAO NÀY ĐÃ TỒN TẠI VÀ CHẠY ĐƯỢC)
                        TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);

                        // LƯU Ý: Nếu GaDao.getGaById/TauDAO.getTauById/NhanVienDao.getNhanVienById là null,
                        // thì ct vẫn được tạo nhưng các Entity con là null.
                        Ga gaDi = GaDao.getGaById(maGaDiDb);
                        Ga gaDen = GaDao.getGaById(maGaDenDb);
                        Tau tau = TauDAO.getTauById(maTau);
                        NhanVien nv = NhanVienDao.getNhanVienById(maNV);

                        // 3. Khởi tạo đối tượng ChuyenTau
                        ct = new ChuyenTau(
                                maChuyenTau,
                                maTau,
                                rs.getDate("NgayKhoiHanh").toLocalDate(),
                                rs.getTime("GioKhoiHanh").toLocalTime(),
                                gaDi,
                                gaDen,
                                tau,
                                rs.getDate("NgayDenDuKien").toLocalDate(),
                                rs.getTime("GioDenDuKien").toLocalTime(),
                                nv,
                                tt
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Chuyến tàu theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return ct;
    }

    // ... (Các phương thức khác giữ nguyên cấu trúc) ...
    public List<ChuyenTau> timChuyenTau(String gaXP, String gaKT, String ngayDi) {
        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
        String sql = "SELECT * FROM ChuyenTau WHERE MaGaKhoiHanh = ? AND MaGaDen = ? AND NgayKhoiHanh = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, gaXP);
                pstmt.setString(2, gaKT);
                pstmt.setString(3, ngayDi);
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
            }
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