package dao;

import utils.DBContext; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Book;

public class WishlistDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // 1. THÊM: Trả về 1 (Thành công), 0 (Đã có rồi), -1 (Lỗi)
    public int addToWishlist(int userId, int bookId) {
        String checkQuery = "SELECT * FROM Wishlists WHERE UserID = ? AND BookID = ?";
        String insertQuery = "INSERT INTO Wishlists (UserID, BookID) VALUES (?, ?)";
        try {
            conn = new DBContext().getConnection();
            
            // Bước 1: Kiểm tra xem Hải đã thích cuốn này chưa
            ps = conn.prepareStatement(checkQuery);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            rs = ps.executeQuery();
            if (rs.next()) return 0; // Đã tồn tại rồi sếp ơi

            // Bước 2: Nếu chưa có thì mới Insert
            ps = conn.prepareStatement(insertQuery);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.executeUpdate();
            return 1; // Thêm mới thành công
        } catch (Exception e) { 
            e.printStackTrace(); 
            return -1; 
        }
    }

    // 2. XEM: Lấy danh sách (Đã fix lỗi Invalid column name 'id')
    public List<Book> getWishlistByUser(int userId) {
    List<Book> list = new ArrayList<>();
    // 1. THÊM b.Stock VÀO CÂU LỆNH SQL NÈ SẾP
    String query = "SELECT b.BookID, b.title, b.author, b.price, b.coverImage, b.description, b.isEbook, b.Stock " +
                   "FROM Books b JOIN Wishlists w ON b.BookID = w.BookID WHERE w.UserID = ?";
    try {
        conn = new utils.DBContext().getConnection();
        ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Book(
                rs.getInt("BookID"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getDouble("price"),
                rs.getString("coverImage"),
                rs.getString("description"),
                rs.getInt("isEbook"),
                rs.getInt("Stock") // 2. THÊM CÁI NÀY VÀO CUỐI CHO ĐỦ 8 THAM SỐ
            ));
        }
    } catch (Exception e) {
        e.printStackTrace(); 
    }
    return list;
}

    // 3. XÓA: Gỡ bỏ khỏi danh sách yêu thích
    public void removeFromWishlist(int userId, int bookId) {
        String query = "DELETE FROM Wishlists WHERE UserID = ? AND BookID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}