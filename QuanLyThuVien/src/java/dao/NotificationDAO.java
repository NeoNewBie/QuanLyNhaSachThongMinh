package dao;

import model.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utils.DBContext;

public class NotificationDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Thêm thông báo mới
    public void addNotification(int userId, String message) {
        String query = "INSERT INTO Notifications (UserID, Message, IsRead, CreatedDate) VALUES (?, ?, 0, GETDATE())";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
    }

    // Lấy 5 thông báo mới nhất của 1 User
    public List<Notification> getTop5ByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        String query = "SELECT TOP 5 * FROM Notifications WHERE UserID = ? ORDER BY CreatedDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while(rs.next()){
                list.add(new Notification(
                    rs.getInt("NotifID"), rs.getInt("UserID"), rs.getString("Message"), 
                    rs.getBoolean("IsRead"), rs.getTimestamp("CreatedDate")
                ));
            }
        } catch(Exception e) { e.printStackTrace(); }
        return list;
    }

    // Đếm số lượng chưa đọc (Để hiện cục màu đỏ trên chuông)
    public int countUnread(int userId) {
        String query = "SELECT COUNT(*) FROM Notifications WHERE UserID = ? AND IsRead = 0";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if(rs.next()) return rs.getInt(1);
        } catch(Exception e) {}
        return 0;
    }

    // Lấy TẤT CẢ thông báo
    public java.util.List<model.Notification> getAllByUser(int userId) {
        java.util.List<model.Notification> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM Notifications WHERE UserID = ? ORDER BY CreatedDate DESC";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new model.Notification(
                        rs.getInt("NotifID"), 
                        rs.getInt("UserID"), 
                        rs.getString("Message"), 
                        rs.getBoolean("IsRead"), 
                        rs.getTimestamp("CreatedDate")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 🛑 HÀM MỚI: Đánh dấu là đã đọc
    public void markAsRead(int notifId) {
        String query = "UPDATE Notifications SET IsRead = 1 WHERE NotifID = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, notifId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}