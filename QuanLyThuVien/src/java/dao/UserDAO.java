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

    // Hàm dùng chung để dọn dẹp, đóng cửa Database (Đã tối ưu)
    private void closeConnections() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Hàm Đăng nhập duy nhất (Đã gộp checkLogin và login cũ)
    public User login(String user, String pass) {
        String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            rs = ps.executeQuery();
            if (rs.next()) {
                // Trả về đối tượng User đầy đủ nhất bao gồm cả Avatar
                return new User(
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("FullName"),
                    rs.getInt("RoleID"),
                    rs.getString("Email"),
                    rs.getString("Phone"),
                    rs.getString("address"),
                    rs.getString("Avatar")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return null;
    }

    // 2. Kiểm tra Username tồn tại (Dùng cho Đăng ký)
    public boolean checkUserExist(String user) {
        String query = "SELECT * FROM Users WHERE Username = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            rs = ps.executeQuery();
            return rs.next(); 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // 3. Kiểm tra Email tồn tại (Dùng cho Quên mật khẩu)
    public boolean checkEmailExist(String email) {
        String query = "SELECT * FROM Users WHERE Email = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // 4. Hàm Đăng ký người dùng mới (Mặc định Role 2 - Khách)
    public boolean register(String username, String password, String email) {
        String query = "INSERT INTO Users (Username, Password, Email, RoleID, FullName) VALUES (?, ?, ?, 2, ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password); // Password đã băm MD5 từ Servlet
            ps.setString(3, email);
            ps.setString(4, username); // Tạm lấy username làm FullName
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // 5. Cập nhật mật khẩu bằng ID (Dùng cho trang Đổi mật khẩu)
    public boolean changePassword(int id, String newPassword) {
        String query = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // 6. Cập nhật mật khẩu bằng Email (Dùng cho luồng OTP Quên mật khẩu)
    public boolean updatePasswordByEmail(String email, String newPassword) {
        String query = "UPDATE Users SET Password = ? WHERE Email = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return false;
    }

    // 7. Cập nhật Hồ sơ cá nhân (Bao gồm cả Avatar)
    public boolean updateProfile(int userId, String fullName, String email, String phone, String address, String avatar) {
        String query = "UPDATE Users SET FullName=?, Email=?, Phone=?, address=?, Avatar=? WHERE UserID=?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, avatar);
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            closeConnections();
        }
        return false;
    }
    // 🛑 Cập nhật địa chỉ nhận hàng vào Profile khách hàng
    public void updateShippingInfo(int userId, String fullName, String phone, String address) {
        String query = "UPDATE Users SET FullName = ?, Phone = ?, Address = ? WHERE UserID = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}