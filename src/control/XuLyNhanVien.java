package control;

import dao.NhanVienDao;
import entity.NhanVien;

public class XuLyNhanVien {
    // Khởi tạo đối tượng DAO để sử dụng
    private static final NhanVienDao nhanVienDAO = new NhanVienDao();

    /**
     * Lấy thông tin nhân viên để phục vụ ca làm việc
     */
    public static NhanVien layThongTinNhanVienChoCaLamViec(String maNV) {
        return NhanVienDao.getNhanVienByMaNV(maNV);
    }

    /**
     * Lấy Email của nhân viên phục vụ chức năng Quên mật khẩu
     */
    public static String layEmailNhanVien(String maNV) {
        return NhanVienDao.layEmailTheoMa(maNV);
    }
}

// --- Ví dụ tại màn hình đăng nhập ---
// TaiKhoan taiKhoan = XuLyTaiKhoan.authenticate(tenDangNhap, matKhau);
// if (taiKhoan != null) {
//     String maNV = taiKhoan.getMaNV();
//
//     XuLyNhanVien xuLy = new XuLyNhanVien();
//     NhanVien nv = xuLy.layThongTinNhanVienChoCaLamViec(maNV);
//
//     if (nv != null) {
//         CaLamViec.getInstance().batDauCa(nv);
//         // ... chuyển màn hình ...
//     }
// }