package dao;

import model.Book;
import model.Cart;
import model.Item;
import utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CartDAO {
    
    // 1. Lấy toàn bộ giỏ hàng của một User (Load từ DB lên)
    public Cart getCartByUserId(int userId) {
        Cart cart = new Cart();
        String query = "SELECT c.Quantity, b.* FROM Carts c JOIN Books b ON c.BookID = b.BookID WHERE c.UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book(
                        rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
                        rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"),
                        rs.getInt("IsEbook"), rs.getInt("Stock")
                    );
                    Item item = new Item(b, rs.getInt("Quantity"), b.getPrice());
                    cart.addItem(item); // Nhét vào đối tượng Cart của sếp
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return cart;
    }

    // 2. Thêm sách vào giỏ DB
    public void addToCart(int userId, int bookId, int quantity) {
        String checkQuery = "SELECT Quantity FROM Carts WHERE UserID = ? AND BookID = ?";
        String updateQuery = "UPDATE Carts SET Quantity = Quantity + ? WHERE UserID = ? AND BookID = ?";
        String insertQuery = "INSERT INTO Carts (UserID, BookID, Quantity) VALUES (?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            checkPs.setInt(1, userId); checkPs.setInt(2, bookId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) { // Đã có sách này -> Cộng dồn số lượng
                    try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
                        updatePs.setInt(1, quantity); updatePs.setInt(2, userId); updatePs.setInt(3, bookId);
                        updatePs.executeUpdate();
                    }
                } else { // Chưa có -> Thêm dòng mới
                    try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
                        insertPs.setInt(1, userId); insertPs.setInt(2, bookId); insertPs.setInt(3, quantity);
                        insertPs.executeUpdate();
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Cập nhật số lượng (+ / -)
    public void updateQuantity(int userId, int bookId, int quantity) {
        String query = "UPDATE Carts SET Quantity = ? WHERE UserID = ? AND BookID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, quantity); ps.setInt(2, userId); ps.setInt(3, bookId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. Xóa 1 cuốn khỏi giỏ
    public void removeCartItem(int userId, int bookId) {
        String query = "DELETE FROM Carts WHERE UserID = ? AND BookID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId); ps.setInt(2, bookId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 5. Xóa sạch giỏ hàng (DÙNG KHI ĐÃ THANH TOÁN XONG)
    public void clearCart(int userId) {
        String query = "DELETE FROM Carts WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}