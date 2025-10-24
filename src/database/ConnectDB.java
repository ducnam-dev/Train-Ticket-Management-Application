package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    public static Connection con = null;
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }

    public void connect() {
        // CẦN SỬA ĐỔI: Kiểm tra xem có nên tạo lại kết nối hay không.
        boolean isConnectionValid = false;
        try {
            if (con != null && !con.isClosed()) {
                isConnectionValid = true;
            }
        } catch (SQLException e) {
            // Nếu kiểm tra isClosed() ném lỗi, kết nối đã chết và cần tạo lại.
            isConnectionValid = false;
        }

        if(!isConnectionValid) {
            // Logic tạo kết nối chỉ chạy khi kết nối là NULL hoặc không hợp lệ/đã đóng
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanVeTau;trustServerCertificate=true";
            String user = "sa";
            String password = "sapassword";
            try {
                con = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                System.out.println("Kết nối chưa được thiết lập. Đang thực hiện kết nối...");
                e.printStackTrace();
                con = null; // Đảm bảo con là null nếu thất bại
            }
        }
    }

    public static void disconnect() {
        if(con != null)
            try {
                con.close();
                con = null;
                System.out.println("Đã ngắt kết nối!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    // Phương thức này đã đúng và gọi connect() khi cần thiết
    public static Connection getConnection() throws SQLException {
        if (con == null || con.isClosed()) { // Kiểm tra trạng thái hiện tại
            getInstance().connect();
            if (con == null){
                throw new SQLException("Failed to establish a database connection.");
            }
        }
        return con;
    }
}