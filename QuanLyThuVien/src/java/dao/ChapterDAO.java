package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Chapter;
import utils.DBContext;

public class ChapterDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Hàm đóng kết nối để tối ưu hệ thống
    private void close() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {}
    }

    public List<Chapter> getChaptersByBookId(int bookId) {
        List<Chapter> list = new ArrayList<>();
        // 🛑 ĐÃ CẬP NHẬT: Lấy thêm cột SourceURL từ Database
        String query = "SELECT * FROM Chapters WHERE BookID = ? ORDER BY ChapterNumber ASC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Chapter c = new Chapter();
                c.setChapterID(rs.getInt("ChapterID"));
                c.setBookID(rs.getInt("BookID"));
                c.setChapterNumber(rs.getInt("ChapterNumber"));
                c.setTitle(rs.getString("Title"));
                c.setContent(rs.getString("Content"));
                c.setIsFree(rs.getBoolean("IsFree"));
                c.setPrice(rs.getDouble("Price")); 
                
                // 🛑 QUAN TRỌNG: Gán link nguồn để Servlet có cái mà đi "cào" ảnh
                c.setSourceURL(rs.getString("SourceURL"));
                
                list.add(c);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            close();
        }
        return list;
    }

    // Hàm này dùng để lưu lịch sử mở khóa truyện của User
    public boolean unlockChapter(int userId, int chapterId) {
        String query = "INSERT INTO Unlocked_Chapters (UserID, ChapterID, UnlockDate) VALUES (?, ?, GETDATE())";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, chapterId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return false;
    }

    // Hàm kiểm tra xem User đã mở khóa tập truyện này chưa
    public boolean isChapterUnlocked(int userId, int chapterId) {
        String query = "SELECT * FROM Unlocked_Chapters WHERE UserID = ? AND ChapterID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, chapterId);
            rs = ps.executeQuery();
            if (rs.next()) return true; 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally {
            close();
        }
        return false;
    }
}