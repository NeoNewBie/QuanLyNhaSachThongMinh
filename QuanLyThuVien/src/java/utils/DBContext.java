package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {
    
    // Cấu hình thông tin kết nối SQL Server
    private final String serverName = "localhost";
    private final String dbName = "SmartLibraryDB"; // Tên database đã tạo
    private final String portNumber = "1433";
    private final String userID = "sa"; // Tài khoản SQL Server
    private final String password = "1234"; // Đổi thành mật khẩu của bạn

    public Connection getConnection() throws Exception {
        String url = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";databaseName=" + dbName + ";encrypt=false;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, userID, password);
    }
}