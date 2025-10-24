package dao;

import entity.TaiKhoan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TaiKhoanDAO {

    // Khai báo biến connection ở phạm vi lớp/DAO nếu nó được dùng lại
    // Nếu không, khai báo nó trong phương thức để đảm bảo quản lý tài nguyên tốt hơn

    /**
     * Thực hiện chức năng đăng nhập, xác thực MaNV và MatKhau.
     * @param maNVOrTenDangNhap Mã nhân viên hoặc Tên đăng nhập
     * @param matKhau Mật khẩu thô
     * @return TaiKhoan object nếu đăng nhập thành công, null nếu thất bại.
     */
    public TaiKhoan dangNhap(String maNVOrTenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = null;
        Connection connection = null; // Khai báo Connection ở đây để đóng trong khối finally

        // LƯU Ý: Truy vấn của bạn đang tìm kiếm theo MaNV và MatKhau.
        // Cần đảm bảo cột đầu tiên trong SELECT là "TenDangNhap" để tạo đối tượng.
        // Nếu bạn muốn tìm kiếm theo Tên đăng nhập (TenDangNhap) và MatKhau (phổ biến hơn),
        // bạn nên sử dụng cột TenDangNhap trong WHERE clause.

        // GIẢ ĐỊNH THEO YÊU CẦU: tìm kiếm theo MaNV (giả định rằng MaNV = TenDangNhap)
        String sql = "SELECT MaNV, TenDangNhap, NgayTao, TrangThai FROM TaiKhoan WHERE MaNV = ? AND MatKhau = ?";

        try {
            // 1. Lấy kết nối CSDL (Sẽ ném SQLException)
            connection = database.ConnectDB.getConnection();

            // 2. Sử dụng try-with-resources cho PreparedStatement và ResultSet
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                // Đặt tham số. Giả định rằng giá trị đầu tiên nhập vào là MaNV
                preparedStatement.setString(1, maNVOrTenDangNhap);
                preparedStatement.setString(2, matKhau);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next()) {
                        // Đọc dữ liệu từ ResultSet
                        String maNV = resultSet.getString("MaNV");
                        String tenDangNhapDB = resultSet.getString("TenDangNhap");
                        String trangThai = resultSet.getString("TrangThai");

                        // Chuyển đổi từ java.sql.Date sang java.time.LocalDate
                        LocalDate ngayTao = resultSet.getDate("NgayTao").toLocalDate();

                        // Trả về đối tượng TaiKhoan
                        taiKhoan = new TaiKhoan(tenDangNhapDB, maNV, matKhau, ngayTao, trangThai);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi CSDL khi thực hiện đăng nhập: " + e.getMessage());
        }
        /** * Không tự động đóng kết nối
         */
//        finally {
//            // 3. Đảm bảo đóng kết nối trong khối finally
//            try {
//                if (connection != null) database.ConnectDB.closeConnection(connection);
//                // Sử dụng phương thức closeConnection() từ lớp ConnectDB của bạn
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        return taiKhoan;
    }
}