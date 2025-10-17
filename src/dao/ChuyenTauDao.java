
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

    // Lấy tất cả đồ uống từ cơ sở dữ liệu
    public List<ChuyenTau> getAllTBChuyenTau() {
        // Luôn tạo danh sách mới để tránh thêm trùng lặp nếu phương thức được gọi nhiều lần
        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            // Lưu ý: Tùy thuộc vào cấu trúc ConnectDB, có thể là getInstance().getConnection() hoặc getConnection()
            con = ConnectDB.getInstance().getConnection(); 
            
            // Sửa lại tên bảng và các cột cần thiết để tạo đối tượng ChuyenTau theo constructor mới
            String sql = "SELECT MaChuyenTau, TenChuyenTau, NgayKhoiHanh, GioKhoiHanh, MaGaKhoiHanh, MaGaDen, MaTau, NgayDenDuKien, GioDenDuKien, MaNV, TrangThai FROM ChuyenTau";
            
            statement = con.createStatement();
            rs = statement.executeQuery(sql);

            while (rs.next()) {
                // 1. Lấy dữ liệu thô
                String maChuyenTau = rs.getString("MaChuyenTau");
                String tenChuyenTau = rs.getString("TenChuyenTau");
                
                // Chuyển đổi java.sql.Date/Time sang java.time.LocalDate/LocalTime
                LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                
                String maGaDi = rs.getString("MaGaKhoiHanh");
                String maGaDen = rs.getString("MaGaDen");
                String maTau = rs.getString("MaTau");
                
                LocalDate ngayDenDK = rs.getDate("NgayDenDuKien").toLocalDate();
                LocalTime gioDenDK = rs.getTime("GioDenDuKien").toLocalTime();
                
                String maNhanVien = rs.getString("MaNV");

                //Chuyến chuỗi của trạng thái chuyến tàu thành enum
                String trangThai = rs.getString("TrangThai");
                TrangThaiChuyenTau tt = TrangThaiChuyenTau.valueOf(trangThai);

                // 2. Tra cứu (Lookup) các đối tượng từ mã
                Ga gaDi = GaDao.getGaById(maGaDi);
                Ga gaDen = GaDao.getGaById(maGaDen);
                Tau tau = TauDAO.getTauById(maTau);
                NhanVien nhanVien = NhanVienDao.getNhanVienById(maNhanVien);

                // 3. Tạo đối tượng ChuyenTau bằng constructor phức tạp
                ChuyenTau chuyenTau = new ChuyenTau(
                        maChuyenTau,
                        tenChuyenTau,
                        ngayKH,
                        gioKH,
                        gaDi,
                        gaDen,
                        tau,
                        ngayDenDK,
                        gioDenDK,
                        nhanVien,
                        tt);
                danhSachChuyenTau.add(chuyenTau);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng nếu có lỗi
        } finally {
            // Đóng tài nguyên (rất quan trọng)
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // Cập nhật dsChuyenTau nội bộ và trả về danh sách
        this.danhSachChuyenTau = (ArrayList<ChuyenTau>) danhSachChuyenTau;
        return danhSachChuyenTau;
    }

    public boolean themChuyenTau(ChuyenTau ct) {
        String sql = "INSERT INTO ChuyenTau (MaChuyenTau, TenChuyenTau, NgayKhoiHanh, GioKhoiHanh, MaGaKhoiHanh, MaGaDen, MaTau, NgayDenDuKien, GioDenDuKien, MaNV, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, ct.getMaChuyenTau());
            stmt.setString(2, ct.getTenChuyenTau());
            stmt.setDate(3, Date.valueOf(ct.getNgayKhoiHanh()));
            stmt.setTime(4, Time.valueOf(ct.getGioKhoiHanh()));
            stmt.setString(5, ct.getGaDi().getMaGa());
            stmt.setString(6, ct.getGaDen().getMaGa());
            stmt.setString(7, ct.getTau().getMaTau());
            stmt.setDate(8, Date.valueOf(ct.getNgayDenDuKien()));
            stmt.setTime(9, Time.valueOf(ct.getGioDenDuKien()));
            stmt.setString(10, ct.getNhanVien().getMaNV());
            // assuming ChuyenTau has a getTrangThai() returning String or int; adapt if different
            stmt.setString(11, ct.getThct() == null ? "0" : ct.getThct().toString());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // keep in-memory list consistent
                if (this.danhSachChuyenTau == null) this.danhSachChuyenTau = new ArrayList<>();
                this.danhSachChuyenTau.add(ct);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ChuyenTau> timChuyenTauByGa(String maGaDi, String maGaDen) {
        List<ChuyenTau> result = new ArrayList<>();
        String sql = "SELECT MaChuyenTau, TenChuyenTau, NgayKhoiHanh, GioKhoiHanh, MaGaKhoiHanh, MaGaDen, MaTau, NgayDenDuKien, GioDenDuKien, MaNV, TrangThai FROM ChuyenTau WHERE MaGaKhoiHanh = ? AND MaGaDen = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maGaDi);
            stmt.setString(2, maGaDen);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maChuyenTau = rs.getString("MaChuyenTau");
                    String ten = rs.getString("TenChuyenTau");
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                    String maGaDiDb = rs.getString("MaGaKhoiHanh");
                    String maGaDenDb = rs.getString("MaGaDen");
                    String maTau = rs.getString("MaTau");
                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
                    String maNV = rs.getString("MaNV");
                    String trangThai = rs.getString("TrangThai");
                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.valueOf(trangThai);

                    Ga gaDi = GaDao.getGaById(maGaDiDb);
                    Ga gaDen = GaDao.getGaById(maGaDenDb);
                    Tau tau = TauDAO.getTauById(maTau);
                    NhanVien nv = NhanVienDao.getNhanVienById(maNV);

                    ChuyenTau ct = new ChuyenTau(maChuyenTau, ten, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                    result.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<ChuyenTau> timChuyenTauByNgay(LocalDate ngayDi) {
        List<ChuyenTau> result = new ArrayList<>();
        String sql = "SELECT MaChuyenTau, TenChuyenTau, NgayKhoiHanh, GioKhoiHanh, MaGaKhoiHanh, MaGaDen, MaTau, NgayDenDuKien, GioDenDuKien, MaNV, TrangThai FROM ChuyenTau WHERE NgayKhoiHanh = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(ngayDi));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maChuyenTau = rs.getString("MaChuyenTau");
                    String ten = rs.getString("TenChuyenTau");
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHanh").toLocalTime();
                    String maGaDiDb = rs.getString("MaGaKhoiHanh");
                    String maGaDenDb = rs.getString("MaGaDen");
                    String maTau = rs.getString("MaTau");
                    LocalDate ngayDen = rs.getDate("NgayDenDuKien").toLocalDate();
                    LocalTime gioDen = rs.getTime("GioDenDuKien").toLocalTime();
                    String maNV = rs.getString("MaNV");
                    String trangThai = rs.getString("TrangThai");
                    TrangThaiChuyenTau tt = TrangThaiChuyenTau.valueOf(trangThai);

                    Ga gaDi = GaDao.getGaById(maGaDiDb);
                    Ga gaDen = GaDao.getGaById(maGaDenDb);
                    Tau tau = TauDAO.getTauById(maTau);
                    NhanVien nv = NhanVienDao.getNhanVienById(maNV);

                    ChuyenTau ct = new ChuyenTau(maChuyenTau, ten, ngayKH, gioKH, gaDi, gaDen, tau, ngayDen, gioDen, nv, tt);
                    result.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<ChuyenTau> timChuyenTau(String gaXP, String gaKT, String ngayDi) {
        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
        // Định dạng ngày: SQL Server thường dùng yyyy-MM-dd. Đổi "30/09/2025" thành "2025-09-30"
        String sql = "SELECT * FROM ChuyenTau WHERE MaGaKhoiHanh = ? AND MaGaDen = ? AND NgayKhoiHanh = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, gaXP);
            stmt.setString(2, gaKT);
            stmt.setString(3, ngayDi); // Đã format lại
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String maChuyenTau = rs.getString("MaChuyenTau");
                    String maTau = rs.getString("MaTau");
                    String maNV = rs.getString("MaNV");
                    String maGaDiDb = rs.getString("MaGaKhoiHanh");
                    String maGaDenDb = rs.getString("MaGaDen");
                    LocalDate ngayKH = rs.getDate("NgayKhoiHanh").toLocalDate();
                    LocalTime gioKH = rs.getTime("GioKhoiHan").toLocalTime();
                    LocalDate ngayDen = rs.getDate("NgayGienDuKien").toLocalDate();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachChuyenTau;
    }


    public boolean capNhatChuyenTau(ChuyenTau ct) {
        String sql = "UPDATE ChuyenTau SET TenChuyenTau = ?, NgayKhoiHanh = ?, GioKhoiHanh = ?, MaGaKhoiHanh = ?, MaGaDen = ?, MaTau = ?, NgayDenDuKien = ?, GioDenDuKien = ?, MaNV = ?, TrangThai = ? WHERE MaChuyenTau = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, ct.getTenChuyenTau());
            stmt.setDate(2, Date.valueOf(ct.getNgayKhoiHanh()));
            stmt.setTime(3, Time.valueOf(ct.getGioKhoiHanh()));
            stmt.setString(4, ct.getGaDi().getMaGa());
            stmt.setString(5, ct.getGaDen().getMaGa());
            stmt.setString(6, ct.getTau().getMaTau());
            stmt.setDate(7, Date.valueOf(ct.getNgayDenDuKien()));
            stmt.setTime(8, Time.valueOf(ct.getGioDenDuKien()));
            stmt.setString(9, ct.getNhanVien().getMaNV());
            stmt.setString(10, ct.getThct() == null ? "0" : ct.getThct().toString());
            stmt.setString(11, ct.getMaChuyenTau());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // update in-memory list
                if (this.danhSachChuyenTau != null) {
                    for (int i = 0; i < this.danhSachChuyenTau.size(); i++) {
                        if (this.danhSachChuyenTau.get(i).getMaChuyenTau().equals(ct.getMaChuyenTau())) {
                            this.danhSachChuyenTau.set(i, ct);
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