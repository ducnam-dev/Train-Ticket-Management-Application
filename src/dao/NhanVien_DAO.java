//package dao;
//
//import java.sql.*;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import database.ConnectDB;
//import entity.NhanVien;
//
//public class NhanVien_DAO {
//    private ArrayList<NhanVien> dsNhanVien;
//
//    public NhanVien_DAO() {
//        dsNhanVien = new ArrayList<NhanVien>();
//    }
//
//    // Lấy tất cả nhân viên từ cơ sở dữ liệu
//    public ArrayList<NhanVien> getAllNhanVien() {
//        dsNhanVien.clear(); // Clear the list to avoid duplicates
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            String sql = "SELECT * FROM NhanVien";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//
//            while (rs.next()) {
//                String maNv = rs.getString("ma_nv");
//                String hoTen = rs.getString("ho_ten");
//                String  cccd = rs.getString("cccd");
//                LocalDate ngaySinh = rs.getDate("ngay_sinh").toLocalDate();
//                String email = rs.getString("email");
//                String sdt = rs.getString("so_dien_thoai");
//                String gioiTinh = rs.getString("gioi_tinh");
//                String diaChi = rs.getString("dia_chi");
//                LocalDate ngayVaoLam = rs.getDate("ngay_vao_lam").toLocalDate();
//                String chucVu = rs.getString("chuc_vu");
//                String soDienThoai = rs.getString("so_dien_thoai");
//                NhanVien nv = new NhanVien(maNv, hoTen, cccd, ngaySinh.toString(), email, sdt, gioiTinh, diaChi, ngayVaoLam.toString(), chucVu);
//                dsNhanVien.add(nv);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return dsNhanVien;
//    }
//
//    // Thêm nhân viên
//    public boolean addNhanVien(NhanVien nv) {
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            String sql = "INSERT INTO nhan_vien (ma_nv, ho_ten, chuc_vu, so_dien_thoai) VALUES (?, ?, ?, ?)";
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, nv.getMaNv());
//            stmt.setString(2, nv.getHoTen());
//            stmt.setString(3, nv.getChucVu());
//            stmt.setString(4, nv.getSoDienThoai());
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected > 0) {
//                dsNhanVien.add(nv);
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // Tìm nhân viên theo mã
//    public NhanVien findByMaNv(String maNv) {
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            String sql = "SELECT * FROM nhan_vien WHERE ma_nv = ?";
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, maNv);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                return new NhanVien(
//                    rs.getString("ma_nv"),
//                    rs.getString("ho_ten"),
//                    rs.getString("chuc_vu"),
//                    rs.getString("so_dien_thoai")
//                );
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // Tìm kiếm nhân viên theo họ tên (partial match)
//    public ArrayList<NhanVien> searchByHoTen(String hoTen) {
//        ArrayList<NhanVien> result = new ArrayList<>();
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            String sql = "SELECT * FROM nhan_vien WHERE ho_ten LIKE ?";
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, "%" + hoTen + "%");
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                NhanVien nv = new NhanVien(
//                    rs.getString("ma_nv"),
//                    rs.getString("ho_ten"),
//                    rs.getString("chuc_vu"),
//                    rs.getString("so_dien_thoai")
//                );
//                result.add(nv);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    // Cập nhật nhân viên
//    public boolean updateNhanVien(NhanVien nv) {
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            String sql = "UPDATE nhan_vien SET ho_ten = ?, chuc_vu = ?, so_dien_thoai = ? WHERE ma_nv = ?";
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, nv.getHoTen());
//            stmt.setString(2, nv.getChucVu());
//            stmt.setString(3, nv.getSoDienThoai());
//            stmt.setString(4, nv.getMaNv());
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected > 0) {
//                // Update the in-memory list
//                for (int i = 0; i < dsNhanVien.size(); i++) {
//                    if (dsNhanVien.get(i).getMaNv().equals(nv.getMaNv())) {
//                        dsNhanVien.set(i, nv);
//                        break;
//                    }
//                }
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // Xóa nhân viên
//    public boolean deleteNhanVien(String maNv) {
//        try {
//            Connection con = ConnectDB.getInstance().getConnection();
//            String sql = "DELETE FROM nhan_vien WHERE ma_nv = ?";
//            PreparedStatement stmt = con.prepareStatement(sql);
//            stmt.setString(1, maNv);
//            int rowsAffected = stmt.executeUpdate();
//            if (rowsAffected > 0) {
//                // Remove from in-memory list
//                dsNhanVien.removeIf(nv -> nv.getMaNv().equals(maNv));
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}