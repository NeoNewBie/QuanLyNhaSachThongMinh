package dao;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.DBContext;

public class UserDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // 1. Hàm Đăng nhập (Sửa lỗi cho LoginServlet)
    public User checkLogin(String user, String pass) {
    String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
    try {
        conn = new DBContext().getConnection();
        ps = conn.prepareStatement(query);
        ps.setString(1, user);
        ps.setString(2, pass);
        rs = ps.executeQuery();
        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("UserID"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("fullName"));
                u.setRoleId(rs.getInt("roleId"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setAddress(rs.getString("address"));
                return u;
            }
        } catch (Exception e) { 
        e.printStackTrace(); 
    } finally {
        // 🛑 BÍ QUYẾT ĐIỂM 10 Ở ĐÂY: Luôn gọi dọn dẹp dù code chạy đúng hay lỗi
        closeConnections();
    }
    return null;
}
    // 2. Hàm kiểm tra User tồn tại (Sửa lỗi cho RegisterServlet)
    public boolean checkUserExist(String user) {
        String query = "SELECT * FROM Users WHERE username = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            rs = ps.executeQuery();
            return rs.next(); 
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 3. Hàm Đăng ký (Sửa lỗi cho RegisterServlet)
    public boolean register(String user, String pass, String email) {
        String query = "INSERT INTO Users (username, password, email, roleId, fullName) VALUES (?, ?, ?, 2, ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, email);
            ps.setString(4, user); // Tạm lấy username làm fullName
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    // 1. Hàm cập nhật Hồ sơ cá nhân
    public boolean updateProfile(int id, String fullname, String email, String phone, String address) {
        String query = "UPDATE Users SET fullName = ?, email = ?, phone = ?, address = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, fullname);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 2. Hàm Đổi mật khẩu
    public boolean changePassword(int id, String newPassword) {
        String query = "UPDATE Users SET password = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    // Bổ sung hàm này vào UserDAO.java
    public boolean registerUser(String username, String password, String email) {
        // Mặc định roleId = 2 (Khách hàng), fullName tạm lấy theo username
        String query = "INSERT INTO Users (username, password, email, roleId, fullName) VALUES (?, ?, ?, 2, ?)";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password); // Pass lúc này đã bị mã hóa rồi mới truyền vào
            ps.setString(3, email);
            ps.setString(4, username); 
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // Hàm kiểm tra xem email đã tồn tại trong hệ thống chưa
public boolean checkEmailExist(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
public boolean updatePasswordByEmail(String email, String newPassword) {
        String query = "UPDATE Users SET password = ? WHERE email = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
// Hàm dùng chung để dọn dẹp, đóng cửa Database
private void closeConnections() {
    try {
        if (rs != null) rs.close();
        if (ps != null) ps.close();
        if (conn != null) conn.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}