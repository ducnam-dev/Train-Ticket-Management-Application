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
		if(con == null) {
			String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyVeTauTest;trustServerCertificate=true";
			String user = "sa";
			String password = "sapassword";
			try {
				con = DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				System.out.println("Kết nối chưa được thiết lập. Đang thực hiện kết nối...");
				e.printStackTrace();
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
	 public static Connection getConnection() throws SQLException {
	        if (con == null || con.isClosed()) {
	            getInstance().connect();
                if (con == null){
                    throw new SQLException("Failed to establish a database connection.");
                }
	        }
	        return con;
	    }
}