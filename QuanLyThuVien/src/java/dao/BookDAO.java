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
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
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

    private List<Book> getListByQuery(String query) {
        List<Book> list = new ArrayList<>();
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
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
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    public List<Book> getTop4Newest() {
        return getListByQuery("SELECT TOP 4 * FROM Books WHERE IsEbook = 0 ORDER BY BookID DESC");
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
                list.add(new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
                    rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"),
                    rs.getInt("IsEbook"), rs.getInt("Stock")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    public int countTotalBooks() {
        String query = "SELECT COUNT(*) FROM Books WHERE IsEbook = 0"; // Chỉ đếm sách vật lý để phân trang đúng
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return 0;
    }

    public List<Book> searchByName(String txt) {
        List<Book> list = new ArrayList<>();
        String query = "SELECT b.*, (SELECT TOP 1 Title FROM Chapters WHERE BookID = b.BookID AND Title LIKE ?) as MatchedChapter "
                     + "FROM Books b "
                     + "WHERE b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ? "
                     + "   OR EXISTS (SELECT 1 FROM Chapters WHERE BookID = b.BookID AND Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            String p = "%" + txt + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            rs = ps.executeQuery();
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getInt("BookID"));
                b.setTitle(rs.getString("Title"));
                b.setAuthor(rs.getString("Author"));
                b.setPrice(rs.getDouble("Price"));
                b.setCoverImage(rs.getString("CoverImage"));
                b.setIsEbook(rs.getInt("IsEbook"));
                b.setStock(rs.getInt("Stock"));
                b.setDescription(rs.getString("MatchedChapter"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    public boolean insertBorrow(int userID, int bookID, String returnDate) {
        String query = "INSERT INTO Borrows (UserID, BookID, BorrowDate, ReturnDate, Status) VALUES (?, ?, GETDATE(), ?, N'1')";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ps.setInt(2, bookID);
            ps.setString(3, returnDate);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // [ĐÃ SỬA LỖI #16] Kiểm tra đúng các trạng thái thành công
    public boolean checkUserPurchased(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM Orders o JOIN OrderDetails od ON o.OrderID = od.OrderID " +
                       "WHERE o.UserID = ? AND od.BookID = ? AND o.Status = '4'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // [ĐÃ SỬA LỖI #3] Hàm returnBook bị bỏ dở — đã thêm code thực thi đầy đủ
    public void returnBook(int borrowID) {
        String query = "UPDATE Borrows SET Status = 'Returned', ActualReturnDate = GETDATE() WHERE BorrowID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, borrowID);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void insertReview(int userId, int bookId, int rating, String comment) {
        String query = "INSERT INTO Reviews (UserID, BookID, Rating, Comment, ReviewDate) VALUES (?, ?, ?, ?, GETDATE())";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setInt(3, rating);
            ps.setString(4, comment);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
    }

    public List<Book> getNewBooks() {
        return getListByQuery("SELECT TOP 8 BookID, Title, Author, Price, CoverImage, Description, IsEbook, Stock FROM Books ORDER BY ReleaseDate DESC");
    }

    public List<Book> getFeaturedBooks() {
        return getListByQuery("SELECT * FROM Books WHERE IsFeatured = 1");
    }

    public List<Book> getMostViewedBooks() {
        return getListByQuery("SELECT TOP 8 BookID, Title, Author, Price, CoverImage, Description, IsEbook, Stock FROM Books ORDER BY ViewCount DESC");
    }

    // [ĐÃ SỬA LỖI #11] getBestSellerBooks dùng đúng logic đếm doanh số thực tế
    public List<Book> getBestSellerBooks() {
        List<Book> list = new ArrayList<>();
        String query = "SELECT TOP 8 b.BookID, b.Title, b.Author, b.Price, b.CoverImage, b.Description, b.IsEbook, b.Stock "
                     + "FROM Books b "
                     + "JOIN OrderDetails od ON b.BookID = od.BookID "
                     + "JOIN Orders o ON od.OrderID = o.OrderID "
                     + "WHERE o.Status = N'Đã giao' "
                     + "GROUP BY b.BookID, b.Title, b.Author, b.Price, b.CoverImage, b.Description, b.IsEbook, b.Stock "
                     + "ORDER BY COUNT(od.BookID) DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Book(
                    rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
                    rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"),
                    rs.getInt("IsEbook"), rs.getInt("Stock")
                ));
            }
        } catch (Exception e) {
            // Nếu chưa có đơn nào thì fallback về ViewCount
            e.printStackTrace();
            return getListByQuery("SELECT TOP 8 BookID, Title, Author, Price, CoverImage, Description, IsEbook, Stock FROM Books ORDER BY ViewCount DESC");
        }
        finally { close(); }
        return list;
    }

    public List<Book> getEbooks() {
        return getListByQuery("SELECT * FROM Books WHERE IsEbook = 1");
    }

    public List<Book> searchAdvanced(String txt, String[] cateIds, String min, String max, String sort, Boolean isFeatured) {
        List<Book> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();
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

        if (cateIds != null && cateIds.length > 0) {
            StringBuilder inClause = new StringBuilder();
            boolean hasValidId = false;
            for (String id : cateIds) {
                if (id != null && !id.trim().isEmpty()) {
                    if (hasValidId) inClause.append(",");
                    inClause.append("?");
                    params.add(Integer.parseInt(id.trim()));
                    hasValidId = true;
                }
            }
            if (hasValidId) sql.append(" AND b.CategoryID IN (").append(inClause).append(") ");
        }

        if (min != null && !min.isEmpty()) { sql.append(" AND b.Price >= ? "); params.add(Double.parseDouble(min)); }
        if (max != null && !max.isEmpty()) { sql.append(" AND b.Price <= ? "); params.add(Double.parseDouble(max)); }
        if (isFeatured != null && isFeatured) { sql.append(" AND b.IsFeatured = 1 "); }

        sql.append(" ORDER BY b.Price ").append("priceDesc".equals(sort) ? "DESC" : "ASC");

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
                        rs.getDouble("Price"), rs.getString("CoverImage"), rs.getString("Description"),
                        rs.getInt("IsEbook"), rs.getInt("Stock")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Review> getReviewsByBookId(int bookId) {
        List<Review> list = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM Reviews r "
                     + "JOIN Users u ON r.UserID = u.UserID "
                     + "WHERE r.BookID = ? ORDER BY r.ReviewDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Review rev = new Review();
                rev.setUsername(rs.getString("username"));
                rev.setRating(rs.getInt("Rating"));
                rev.setComment(rs.getString("Comment"));
                rev.setDate(rs.getTimestamp("ReviewDate"));
                list.add(rev);
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    public List<Book> getNewEbooks() {
        return getListByQuery("SELECT TOP 4 BookID, Title, Author, Price, CoverImage, Description, IsEbook, Stock FROM Books WHERE IsEbook = 1 ORDER BY BookID DESC");
    }

    public List<Book> getRelatedBooks(int categoryId, int currentBookId) {
        List<Book> list = new ArrayList<>();
        String query = "SELECT TOP 4 * FROM Books WHERE CategoryID = ? AND BookID <> ? ORDER BY NEWID()";
        try {
            conn = new DBContext().getConnection();
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

    public List<Book> getLiveSearchResults(String txt) {
        List<Book> list = new ArrayList<>();
        String query = "SELECT DISTINCT TOP 5 b.BookID, b.Title, b.Price, b.CoverImage "
                     + "FROM Books b LEFT JOIN Categories c ON b.CategoryID = c.CategoryID "
                     + "LEFT JOIN Chapters ch ON b.BookID = ch.BookID "
                     + "WHERE b.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%' "
                     + "   OR c.CategoryName COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%' "
                     + "   OR ch.Title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE N'%' + ? + '%'";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, txt);
            ps.setString(2, txt);
            ps.setString(3, txt);
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

    public void updateStock(int bookId, int quantityToChange) {
        String query = "UPDATE Books SET Stock = Stock + ? WHERE BookID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, quantityToChange);
            ps.setInt(2, bookId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // [ĐÃ SỬA LỖI #7] getFilteredBooks — dùng PreparedStatement thay vì nối chuỗi để tránh SQL Injection
    public List<Book> getFilteredBooks(String categorySlug, String cateId, String searchTxt, String minPrice, String maxPrice, String sort) {
        List<Book> list = new ArrayList<>();
 
        StringBuilder query = new StringBuilder("SELECT * FROM Books WHERE IsEbook = 0");
        List<Object> params = new ArrayList<>();
 
        if (cateId != null && !cateId.trim().isEmpty()) {
            try {
                int catIdInt = Integer.parseInt(cateId.trim());
                query.append(" AND CategoryID = ?");
                params.add(catIdInt);
            } catch (NumberFormatException ignored) {}
        } else if ("new".equals(categorySlug)) {
            query.append(" AND DATEDIFF(day, ReleaseDate, GETDATE()) <= 30");
        }
 
        if (searchTxt != null && !searchTxt.trim().isEmpty()) {
            query.append(" AND Title LIKE ?");
            params.add("%" + searchTxt.trim() + "%");
        }
 
        if (minPrice != null && !minPrice.trim().isEmpty()) {
            try {
                double min = Double.parseDouble(minPrice.trim());
                query.append(" AND Price >= ?");
                params.add(min);
            } catch (NumberFormatException ignored) {}
        }
 
        if (maxPrice != null && !maxPrice.trim().isEmpty()) {
            try {
                double max = Double.parseDouble(maxPrice.trim());
                query.append(" AND Price <= ?");
                params.add(max);
            } catch (NumberFormatException ignored) {}
        }
 
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "bestseller": query.append(" ORDER BY BookID ASC"); break;
                case "popular":   query.append(" ORDER BY Title ASC"); break;
                case "newest":    query.append(" ORDER BY BookID DESC"); break;
                case "priceAsc":  query.append(" ORDER BY Price ASC"); break;
                case "priceDesc": query.append(" ORDER BY Price DESC"); break;
                default:          query.append(" ORDER BY BookID DESC"); break;
            }
        } else {
            query.append(" ORDER BY BookID DESC");
        }
 
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else if (p instanceof Double) ps.setDouble(i + 1, (Double) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("BookID"));
                    b.setTitle(rs.getString("Title"));
                    b.setAuthor(rs.getString("Author"));
                    b.setPrice(rs.getDouble("Price"));
                    b.setCoverImage(rs.getString("CoverImage"));
                    b.setIsEbook(rs.getInt("IsEbook"));
                    b.setStock(rs.getInt("Stock"));
                    list.add(b);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}