package src.database1;

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
			String url = "jdbc:sqlserver://localhost:1433;databasename=QuanLyBanVe";
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
	 public static Connection getConnection() {
	        if (con == null) {
	            
	            getInstance().connect();  // Nếu chưa kết nối thì gọi connect
	        }
	        return con;
	    }
}