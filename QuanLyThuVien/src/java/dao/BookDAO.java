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
    try (java.sql.Connection conn = new utils.DBContext().getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, id);
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Book(
                    rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
                    rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"),
                    rs.getInt("IsEbook"), rs.getInt("Stock")
                );
            }
        }
    } catch (Exception e) { e.printStackTrace(); }
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
        conn = new utils.DBContext().getConnection();
        ps = conn.prepareStatement(query);
        rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

    // ==========================================
    // 🛑 ĐÃ ĐỘ LẠI: TÌM CẢ TÊN SÁCH VÀ TÊN CHƯƠNG TRUYỆN
    // ==========================================
    public List<model.Book> searchByName(String txt) {
        List<model.Book> list = new java.util.ArrayList<>();
        String query = "SELECT b.*, (SELECT TOP 1 Title FROM Chapters WHERE BookID = b.BookID AND Title LIKE ?) as MatchedChapter "
                     + "FROM Books b "
                     + "WHERE b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ? "
                     + "   OR EXISTS (SELECT 1 FROM Chapters WHERE BookID = b.BookID AND Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?)";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            String p = "%" + txt + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            rs = ps.executeQuery();
            while (rs.next()) {
                model.Book b = new model.Book();
                b.setId(rs.getInt("BookID"));
                b.setTitle(rs.getString("Title"));
                b.setAuthor(rs.getString("Author")); // 🛑 Thêm Author
                b.setPrice(rs.getDouble("Price"));
                b.setCoverImage(rs.getString("CoverImage"));
                b.setIsEbook(rs.getInt("IsEbook"));   // 🛑 Thêm IsEbook
                b.setStock(rs.getInt("Stock"));       // 🛑 Thêm Stock
                b.setDescription(rs.getString("MatchedChapter")); // Tên chương khớp
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
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
        String query = "SELECT TOP 8 BookID, title, author, price, coverImage, description, isEbook, Stock " + 
               "FROM Books ORDER BY ReleaseDate DESC";
        return getListByQuery(query);
    }

    public List<Book> getFeaturedBooks() {
        String query = "SELECT * FROM Books WHERE IsFeatured = 1";
        return getListByQuery(query);
    }

    public List<Book> getMostViewedBooks() {
        String query = "SELECT TOP 8 BookID, title, author, price, coverImage, description, isEbook, Stock " + 
               "FROM Books ORDER BY ViewCount DESC";
        return getListByQuery(query);
    }

    public List<Book> getBestSellerBooks() {
    String query = "SELECT TOP 8 BookID, Title, Author, Price, CoverImage, Description, IsEbook, Stock " + 
                   "FROM Books ORDER BY ViewCount DESC"; 
    return getListByQuery(query);
    }

    public List<Book> getEbooks() {
        String query = "SELECT * FROM Books WHERE isEbook = 1";
        return getListByQuery(query);
    }

    // ==========================================
    // 🛑 ĐÃ ĐỘ LẠI ADVANCED SEARCH: Hỗ trợ tìm tên Chương
    // ==========================================
    public List<Book> searchAdvanced(String txt, String[] cateIds, String min, String max, String sort, Boolean isFeatured) {
    List<Book> list = new ArrayList<>();
    List<Object> params = new ArrayList<>();
    // SQL khởi đầu an toàn với 1=1
    StringBuilder sql = new StringBuilder("SELECT DISTINCT b.* FROM Books b "
            + "LEFT JOIN Categories c ON b.CategoryID = c.CategoryID "
            + "LEFT JOIN Chapters ch ON b.BookID = ch.BookID WHERE 1=1 ");

    if (txt != null && !txt.trim().isEmpty()) {
        sql.append(" AND (b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ? ");
        sql.append(" OR ch.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ? ");
        sql.append(" OR c.CategoryName COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?) ");
        String searchVal = "%" + txt.trim() + "%";
        params.add(searchVal); params.add(searchVal); params.add(searchVal);
    }

    // FIX CHÍ MẠNG: Kiểm tra kỹ cateIds để không bị lỗi syntax near ')'
    if (cateIds != null && cateIds.length > 0) {
        StringBuilder inClause = new StringBuilder();
        boolean hasValidId = false;
        for (String id : cateIds) {
            if (id != null && !id.trim().isEmpty()) {
                if (hasValidId) inClause.append(",");
                inClause.append("?");
                params.add(id);
                hasValidId = true;
            }
        }
        if (hasValidId) {
            sql.append(" AND b.CategoryID IN (").append(inClause).append(") ");
        }
    }

    if (min != null && !min.isEmpty()) {
        sql.append(" AND b.Price >= ? ");
        params.add(Double.parseDouble(min));
    }
    if (max != null && !max.isEmpty()) {
        sql.append(" AND b.Price <= ? ");
        params.add(Double.parseDouble(max));
    }
    if (isFeatured != null && isFeatured) {
        sql.append(" AND b.IsFeatured = 1 "); //
    }

    sql.append(" ORDER BY b.Price ").append("priceDesc".equals(sort) ? "DESC" : "ASC");

    try (java.sql.Connection conn = new utils.DBContext().getConnection();
         java.sql.PreparedStatement ps = conn.prepareStatement(sql.toString())) {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        try (java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Book(
                    rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
                    rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"),
                    rs.getInt("IsEbook"), rs.getInt("Stock")
                ));
            }
        }
    } catch (Exception e) { e.printStackTrace(); }
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
        String query = "SELECT TOP 4 BookID, title, author, price, coverImage, description, isEbook, Stock " + 
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
        finally { close(); }
        return list;
    }

    // ==========================================
    // 🛑 ĐÃ ĐỘ LẠI LIVE SEARCH: Hỗ trợ tìm tên Chương
    // ==========================================
    public List<Book> getLiveSearchResults(String txt) {
        List<Book> list = new ArrayList<>();
        // Tìm cả 3 bảng: Sách, Thể loại, Chương truyện
        String query = "SELECT DISTINCT TOP 5 b.BookID, b.Title, b.Price, b.CoverImage " +
                       "FROM Books b LEFT JOIN Categories c ON b.CategoryID = c.CategoryID " +
                       "LEFT JOIN Chapters ch ON b.BookID = ch.BookID " +
                       "WHERE b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%' " +
                       "   OR c.CategoryName COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%' " +
                       "   OR ch.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%'";
        try {
            conn = new utils.DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, txt); 
            ps.setString(2, txt); 
            ps.setString(3, txt); // Dấu hỏi số 3 cho tên chương
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
    // Hàm dùng để CỘNG hoặc TRỪ số lượng tồn kho
    public void updateStock(int bookId, int quantityToChange) {
        // Nếu truyền quantityToChange là số âm (-) thì nó sẽ trừ kho
        // Nếu truyền số dương (+) thì nó sẽ cộng kho
        String query = "UPDATE Books SET Stock = Stock + ? WHERE BookID = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, quantityToChange);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}