
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import entity.ChuyenTau;
import entity.Ga;
import entity.NhanVien;
import entity.Tau;
import src.database1.ConnectDB;

public class ChuyenTauDao {
	private ArrayList<ChuyenTau> danhSachChuyenTau;


//    public List<ChuyenTau> layTatCaChuyenTau() {
//        List<ChuyenTau> danhSachChuyenTau = new ArrayList<>();
//        Connection con = ConnectDB.getConnection();
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        try {
//            String sql = "SELECT MaChuyenTau MaTau MaNV MaGaKhoiHanh MaGaDen NgayKhoiHanh GioKHoiHan NgayDenDuKien GioDenDuKien TrangThai";
//            ps = con.prepareStatement(sql);
//            rs = ps.executeQuery();
//            while (rs.next()) {
//            	// 1. Lấy dữ liệu thô từ ResultSet
//                String maChuyenTau = rs.getString("MaChuyenTau");
//                String tenChuyenTau = rs.getString("TenChuyenTau");
//                
//                // Chuyển đổi java.sql.Date/Time sang java.time.LocalDate/LocalTime
//                LocalDate ngayKhoiHanh = rs.getDate("NgayKhoiHanh").toLocalDate();
//                LocalTime gioKhoiHanh = rs.getTime("GioKhoiHanh").toLocalTime();
//                
//                String maGaDi = rs.getString("MaGaKhoiHanh");
//                String maGaDen = rs.getString("MaGaDen");
//                String maTau = rs.getString("MaTau");
//                
//                LocalDate ngayDenDuKien = rs.getDate("NgayDenDuKien").toLocalDate();
//                LocalTime gioDenDuKien = rs.getTime("GioDenDuKien").toLocalTime();
//                
//                String maNhanVien = rs.getString("MaNV");
//                
//                // 2. Tra cứu (Lookup) các đối tượng từ mã
//                Ga gaDi = GaDao.getGaById(maGaDi);
//                Ga gaDen = GaDao.getGaById(maGaDen);
//                Tau tau = TauDAO.getTauById(maTau);
//                NhanVien nhanVien = NhanVienDao.getNhanVienById(maNhanVien);
//
//            	// 3. Sử dụng constructor MỚI của bạn
//            	ChuyenTau ct = new ChuyenTau(
//                        maChuyenTau,
//                        tenChuyenTau,
//                        ngayKhoiHanh,
//                        gioKhoiHanh,
//                        gaDi,
//                        gaDen,
//                        tau,
//                        ngayDenDuKien,
//                        gioDenDuKien,
//                        nhanVien
//                );
//            	danhSachChuyenTau.add(ct);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        } 
//        // ... phần còn lại của finally và các phương thức khác ...
//        // (Tôi đã lược bỏ phần còn lại của finally và các phương thức khác để tập trung vào sửa lỗi, 
//        // bạn nên giữ lại phần đóng kết nối đã được sửa chữa từ câu trả lời trước)
//        finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//                if (ps != null) {
//                    ps.close();
//                }
//                if (con != null) { 
//                    con.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return danhSachChuyenTau;
//    }

    
    
    
    
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
            String sql = "SELECT MaChuyenTau, TenChuyenTau, NgayKhoiHanh, GioKhoiHanh, MaGaKhoiHanh, MaGaDen, MaTau, NgayDenDuKien, GioDenDuKien, MaNV FROM ChuyenTau";
            
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
                        nhanVien
                );
                
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
}