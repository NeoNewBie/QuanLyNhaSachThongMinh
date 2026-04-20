package dao;

import model.Review;
import model.Borrow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import utils.DBContext;

public class BookDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    private void close() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {}
    }

    public Book getBookById(String id) {
        String query = "SELECT * FROM Books WHERE BookID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Book(
                    rs.getInt("BookID"),
                    rs.getString("Title"),
                    rs.getString("Author"),
                    rs.getDouble("Price"),
                    rs.getString("CoverImage"),
                    rs.getString("Description"),
                    rs.getInt("IsEbook"),
                    rs.getInt("Stock") 
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return null;
    }

    public List<Book> getTop4Newest() {
        String query = "SELECT TOP 4 * FROM Books WHERE IsEbook = 0 ORDER BY BookID DESC";
        return getListByQuery(query);
    }

    public List<Book> getBooksByPage(int pageIndex, int pageSize) {
        String query = "SELECT * FROM Books ORDER BY BookID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        List<Book> list = new ArrayList<>();
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, (pageIndex - 1) * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"), rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"), rs.getInt("IsEbook"), rs.getInt("Stock")));
            }
        } catch (Exception e) {} finally { close(); }
        return list;
    }

    public int countTotalBooks() {
        String query = "SELECT COUNT(*) FROM Books";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {} finally { close(); }
        return 0;
    }

    public List<model.Book> searchByName(String txt) {
        List<model.Book> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM Books WHERE Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, "%" + txt + "%"); 
            rs = ps.executeQuery();
            while (rs.next()) {
                model.Book b = new model.Book();
                b.setId(rs.getInt("BookID"));
                b.setTitle(rs.getString("Title"));
                b.setAuthor(rs.getString("Author"));
                b.setPrice(rs.getDouble("Price"));
                b.setCoverImage(rs.getString("CoverImage"));
                b.setIsEbook(rs.getInt("IsEbook"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertBorrow(int userID, int bookID, String returnDate) {
        String query = "INSERT INTO Borrows (UserID, BookID, ReturnDate, Status) VALUES (?, ?, ?, N'Pending')";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userID);
            ps.setInt(2, bookID);
            ps.setString(3, returnDate);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Borrow> getBorrowHistory(int userId) {
        List<Borrow> list = new ArrayList<>();
        String query = "SELECT b.BorrowID, bk.Title, b.BorrowDate, b.ReturnDate, b.Status "
                     + "FROM Borrows b JOIN Books bk ON b.BookID = bk.BookID WHERE b.UserID = ?";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Borrow(
                    rs.getInt(1), 
                    rs.getString(2), 
                    rs.getDate(3), 
                    rs.getDate(4), 
                    rs.getString(5)
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean checkUserPurchased(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM Orders o JOIN OrderDetails od ON o.OrderID = od.OrderID WHERE o.UserID = ? AND od.BookID = ? AND o.Status = 'Success'";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId); ps.setInt(2, bookId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {} finally { close(); }
        return false;
    }

    public void returnBook(int borrowID) {
        String query = "UPDATE Borrows SET Status = 'Returned', ActualReturnDate = GETDATE() WHERE BorrowID = ?";
    }

    public void addToWishlist(int accountID, int bookID) {
        String query = "IF NOT EXISTS (SELECT * FROM Wishlists WHERE AccountID = ? AND BookID = ?) " +
                       "INSERT INTO Wishlists (AccountID, BookID) VALUES (?, ?)";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID); ps.setInt(2, bookID);
            ps.setInt(3, accountID); ps.setInt(4, bookID);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void insertReview(int userId, int bookId, int rating, String comment) {
        String query = "INSERT INTO Reviews (UserID, BookID, Rating, Comment, ReviewDate) VALUES (?, ?, ?, ?, GETDATE())";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.executeUpdate();
            System.out.println("✅ Đã lưu bình luận thành công vào DB!");
        } catch (Exception e) { 
            System.out.println("❌ LỖI LƯU BÌNH LUẬN: " + e.getMessage());
        }
    }

    private List<Book> getListByQuery(String query) {
        List<Book> list = new ArrayList<>();
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
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
                    rs.getInt("Stock")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    public List<Book> getNewBooks() {
        String query = "SELECT TOP 8 BookID, title, author, price, coverImage, description, isEbook " +
                       "FROM Books ORDER BY ReleaseDate DESC";
        return getListByQuery(query);
    }

    public List<Book> getFeaturedBooks() {
        String query = "SELECT * FROM Books WHERE IsFeatured = 1";
        return getListByQuery(query);
    }

    public List<Book> getMostViewedBooks() {
        String query = "SELECT TOP 8 BookID, title, author, price, coverImage, description, isEbook " +
                       "FROM Books ORDER BY ViewCount DESC";
        return getListByQuery(query);
    }

    public List<Book> getBestSellerBooks() {
        String query = "SELECT TOP 8 b.BookID, b.title, b.author, b.price, b.coverImage, b.description, b.isEbook " +
                       "FROM Books b " +
                       "JOIN OrderDetails od ON b.BookID = od.BookID " + 
                       "GROUP BY b.BookID, b.title, b.author, b.price, b.coverImage, b.description, b.isEbook " +
                       "ORDER BY SUM(od.Quantity) DESC";
        return getListByQuery(query);
    }

    public List<Book> getEbooks() {
        String query = "SELECT * FROM Books WHERE isEbook = 1";
        return getListByQuery(query);
    }

    public List<Book> searchAdvanced(String txt, String[] cateIds, String min, String max, String sort) {
    List<Book> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder("SELECT b.* FROM Books b LEFT JOIN Categories c ON b.CategoryID = c.CategoryID WHERE 1=1 ");

    if (txt != null && !txt.isEmpty()) {
        // 🛑 ĐÃ SỬA: Đổi c.name thành c.CategoryName
        sql.append(" AND (b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%").append(txt).append("%' ");
        sql.append(" OR c.CategoryName COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%").append(txt).append("%') ");
    }
    
    if (cateIds != null && cateIds.length > 0) {
        sql.append(" AND b.CategoryID IN (").append(String.join(",", cateIds)).append(") ");
    }
    
    if (min != null && !min.isEmpty()) {
        sql.append(" AND b.Price >= ").append(min).append(" ");
    }
    if (max != null && !max.isEmpty()) {
        sql.append(" AND b.Price <= ").append(max).append(" ");
    }

    if ("priceAsc".equals(sort)) {
        sql.append(" ORDER BY b.Price ASC");
    } else if ("priceDesc".equals(sort)) {
        sql.append(" ORDER BY b.Price DESC");
    } else {
        sql.append(" ORDER BY b.BookID DESC"); 
    }

    try {
        conn = new utils.DBContext().getConnection();
        ps = conn.prepareStatement(sql.toString());
        rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Book(
                rs.getInt("BookID"),
                rs.getString("Title"),
                rs.getString("Author"),
                rs.getDouble("Price"),
                rs.getString("CoverImage"),
                rs.getString("Description"),
                rs.getInt("IsEbook"),
                rs.getInt("Stock") 
            ));
        }
    } catch (Exception e) { 
        e.printStackTrace(); 
    } finally {
        try { if(rs != null) rs.close(); if(ps != null) ps.close(); if(conn != null) conn.close(); } catch(Exception ex) {}
    }
    return list;
}

    public List<model.Review> getReviewsByBookId(int bookId) {
        List<model.Review> list = new ArrayList<>();
        
        String query = "SELECT r.*, u.username FROM Reviews r " +
                       "JOIN Users u ON r.UserID = u.UserID " +
                       "WHERE r.BookID = ? ORDER BY r.ReviewDate DESC";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                model.Review rev = new model.Review();
                
                rev.setUsername(rs.getString("username")); 
                
                rev.setRating(rs.getInt("Rating"));
                rev.setComment(rs.getString("Comment"));
                rev.setDate(rs.getTimestamp("ReviewDate"));
                list.add(rev);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Book> getNewEbooks() {
        String query = "SELECT TOP 4 BookID, title, author, price, coverImage, description, isEbook " +
                       "FROM Books WHERE IsEbook = 1 ORDER BY BookID DESC";
        return getListByQuery(query);
    }

    public List<Book> getRelatedBooks(int categoryId, int currentBookId) {
        List<Book> list = new ArrayList<>();
        String query = "SELECT TOP 4 * FROM Books WHERE CategoryID = ? AND BookID <> ? ORDER BY NEWID()"; 
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, categoryId);
            ps.setInt(2, currentBookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getInt("BookID"));
                b.setTitle(rs.getString("Title"));
                b.setPrice(rs.getDouble("Price"));
                b.setCoverImage(rs.getString("CoverImage"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Book> getLiveSearchResults(String txt) {
    List<Book> list = new ArrayList<>();
    // 🛑 ĐÃ SỬA: Đổi c.name thành c.CategoryName cho khớp với DB của sếp
    String query = "SELECT TOP 5 b.BookID, b.Title, b.Price, b.CoverImage " +
                   "FROM Books b LEFT JOIN Categories c ON b.CategoryID = c.CategoryID " +
                   "WHERE b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%' " +
                   "   OR c.CategoryName COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%'";
    try {
        conn = new utils.DBContext().getConnection();
        ps = conn.prepareStatement(query);
        ps.setString(1, txt); 
        ps.setString(2, txt); 
        rs = ps.executeQuery();
        while (rs.next()) {
            Book b = new Book();
            b.setId(rs.getInt("BookID"));
            b.setTitle(rs.getString("Title"));
            b.setPrice(rs.getDouble("Price"));
            b.setCoverImage(rs.getString("CoverImage"));
            list.add(b);
        }
    } catch (Exception e) { e.printStackTrace(); } 
    finally { close(); } 
    return list;
}
    
}