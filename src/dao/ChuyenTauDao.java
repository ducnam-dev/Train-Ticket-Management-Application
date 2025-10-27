
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

//    Ga bằng mã GA (gaXP, gaKT), ngày đi
//     Sửa lỗi: Đảm bảo Connection được quản lý trong try-with-resources

//    public List<ChuyenTau> timChuyenTau(String gaXP, String gaKT, String ngayDi) {
//        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
//
//        // Lưu ý: Đảm bảo format ngàyDi là yyyy-MM-dd nếu CSDL yêu cầu
//        String sql = "SELECT * FROM ChuyenTau WHERE GaDi = ? AND GaDen = ? AND NgayKhoiHanh = ?";
//
//        // SỬ DỤNG TRY-WITH-RESOURCES CHO CONNECTION
//        try (Connection con = ConnectDB.getConnection();
//             PreparedStatement pstmt = con.prepareStatement(sql)) {
//
//            // 1. Set tham số
//            pstmt.setString(1, gaXP);
//            pstmt.setString(2, gaKT);
//            pstmt.setString(3, ngayDi);
//
//            // 2. Thực thi truy vấn và xử lý ResultSet
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    // ... (Các bước ánh xạ dữ liệu)
//                    String maChuyenTau = rs.getString("MaChuyenTau");
//                    String maTau = rs.getString("MaTau");
//                    String maNV = rs.getString("MaNV");
//                    String maGaDiDb = rs.getString("GaDi");
//                    String maGaDenDb = rs.getString("GaDen");
//                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
//                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
//                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
//                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
//                    String trangThai = rs.getString("TrangThai");
//
//                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);
//
//                    // GỌI DAO PHỤ TRỢ MỞ KẾT NỐI MỚI!
//                    Ga gaDi = GaDao.layGaBangMa(maGaDiDb);
//                    Ga gaDen = GaDao.layGaBangMa(maGaDenDb);
//                    Tau tau = TauDAO.getTauById(maTau);
//                    NhanVien nv = NhanVienDao.getNhanVienById(maNV);
//
//                    ChuyenTau ct = new ChuyenTau(maChuyenTau, maTau, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
//                    System.out.println("Chuyến tàu tìm thấy:" + ct.toString());
//                    danhSachChuyenTau.add(ct);
//                }
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Lỗi khi tìm chuyến tàu: ");
//            e.printStackTrace();
//        }
//        return danhSachChuyenTau;
//    }

    public List<ChuyenTau> timChuyenTau(String gaXP, String gaKT, String ngayDi) {
        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();

        // SQL Tối ưu (JOIN tất cả các bảng phụ)
        String sql = "SELECT CT.*, " +
                "GA_DI.TenGa AS TenGaDi, GA_DI.DiaChi AS DiaChiGaDi, " +
                "GA_DEN.TenGa AS TenGaDen, GA_DEN.DiaChi AS DiaChiGaDen, " +
                "T.TrangThai AS TrangThaiTau, " +
                "NV.HoTen AS TenNV, NV.SDT AS SDTNV, NV.Email AS EmailNV " +
                "FROM ChuyenTau CT " +
                "LEFT JOIN Ga GA_DI ON CT.GaDi = GA_DI.MaGa " +
                "LEFT JOIN Ga GA_DEN ON CT.GaDen = GA_DEN.MaGa " +
                "LEFT JOIN Tau T ON CT.MaTau = T.SoHieu " +
                "LEFT JOIN NhanVien NV ON CT.MaNV = NV.MaNV " +
                "WHERE CT.GaDi = ? AND CT.GaDen = ? AND CT.NgayKhoiHanh = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            // 1. Set tham số
            pstmt.setString(1, gaXP);
            pstmt.setString(2, gaKT);
            pstmt.setString(3, ngayDi);

            // 2. Thực thi truy vấn và ánh xạ
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Lấy thông tin cơ bản
                    String maChuyenTau = rs.getString("MaChuyenTau");
                    String maTau = rs.getString("MaTau");
                    String maNV = rs.getString("MaNV");
                    // ... (các trường khác từ CT.*)
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
                    String trangThai = rs.getString("TrangThai");
                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);

                    // TẠO ĐỐI TƯỢNG GA DI
                    Ga gaDi = new Ga(rs.getString("GaDi"), rs.getString("TenGaDi"), rs.getString("DiaChiGaDi"));
                    // TẠO ĐỐI TƯỢNG GA ĐẾN
                    Ga gaDen = new Ga(rs.getString("GaDen"), rs.getString("TenGaDen"), rs.getString("DiaChiGaDen"));
                    // TẠO ĐỐI TƯỢNG TÀU
                    Tau tau = new Tau(rs.getString("MaTau"), rs.getString("TrangThaiTau"));
                    // TẠO ĐỐI TƯỢNG NHÂN VIÊN
                    // 4. Nhân Viên (Gọi phương thức tĩnh từ NhanVienDao)

                    // Lưu ý: Cần đảm bảo rs.getString("MaNV") không phải NULL trước khi tạo NV
                    NhanVien nv = null;
//                    if (maNV != null) {
//                        nv = NhanVienDao.taoDoiTuongNhanVienTuResultSet(rs);
//                    }

                    ChuyenTau ct = new ChuyenTau(maChuyenTau, maTau, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                    danhSachChuyenTau.add(ct);
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

//    layChuyenTauBangMa
    public static ChuyenTau layChuyenTauBangMa(String maChuyenTau) {
        ChuyenTau ct = null;
        String sql = "SELECT * FROM ChuyenTau WHERE MaChuyenTau = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, maChuyenTau);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String maTau = rs.getString("MaTau");
                    String maNV = rs.getString("MaNV");
                    String maGaDiDb = rs.getString("GaDi");
                    String maGaDenDb = rs.getString("GaDen");
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
                    String trangThai = rs.getString("TrangThai");

                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.fromString(trangThai);

                    System.out.println(maGaDiDb + " - " + maGaDenDb);

                    Ga gaDi = GaDao.layGaBangTen(maGaDiDb);
                    Ga gaDen = GaDao.layGaBangTen(maGaDenDb);
                    Tau tau = TauDAO.getTauById(maTau);
                    NhanVien nv = NhanVienDao.getNhanVienById(maNV);

                    ct = new ChuyenTau(maChuyenTau, maTau, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                   System.out.println("Chuyến tàu tìm thấy:" + ct.toString());

                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tra cứu Chuyến Tàu theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return ct;
    }
}