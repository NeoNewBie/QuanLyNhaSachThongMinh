package dao;

import utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import model.Cart;
import model.Item;
import model.Order;
import model.User;

public class OrderDAO {

    // ✅ FIX: Dùng try-with-resources thay vì instance fields để tránh resource leak

    // 1. Lưu đơn hàng mới
    public boolean addOrder(User u, Cart cart) {
        String queryOrder = "INSERT INTO Orders (OrderDate, TotalAmount, Status, UserID) VALUES (GETDATE(), ?, ?, ?)";
        String queryDetail = "INSERT INTO OrderDetails (OrderID, BookID, Quantity, Price) VALUES (?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(queryOrder, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, cart.getTotalMoney());
            ps.setString(2, "0"); // '0' = Chờ duyệt
            ps.setInt(3, u.getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int orderId = rs.getInt(1);
                    for (Item i : cart.getItems()) {
                        try (PreparedStatement ps2 = conn.prepareStatement(queryDetail)) {
                            ps2.setInt(1, orderId);
                            ps2.setInt(2, i.getBook().getId());
                            ps2.setInt(3, i.getQuantity());
                            ps2.setDouble(4, i.getPrice());
                            ps2.executeUpdate();
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 2. Lịch sử đơn hàng của 1 user
    public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Order(
                        rs.getInt("OrderID"),
                        rs.getDate("OrderDate"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status"),
                        rs.getInt("UserID")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Tất cả đơn hàng (Admin)
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM Orders ORDER BY OrderDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Order(
                    rs.getInt("OrderID"),
                    rs.getDate("OrderDate"),
                    rs.getDouble("TotalAmount"),
                    rs.getString("Status"),
                    rs.getInt("UserID")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 4. Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(int id, String status) {
        String query = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 5. Doanh thu admin - ✅ FIX: đếm status '4' (Hoàn thành) thay vì chuỗi tiếng Việt
    public double getTotalRevenue() {
        String query = "SELECT COALESCE(SUM(TotalAmount), 0) FROM Orders WHERE Status = '4'";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int countTotalOrders() {
        String query = "SELECT COUNT(*) FROM Orders";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // 6. Lấy thông tin 1 đơn hàng
    public Order getOrderById(int orderId) {
        String query = "SELECT * FROM Orders WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("OrderID"),
                        rs.getDate("OrderDate"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status"),
                        rs.getInt("UserID")
                    );
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 7. Chi tiết sản phẩm trong đơn
    public List<Item> getOrderDetails(int orderId) {
        List<Item> list = new ArrayList<>();
        String query = "SELECT b.BookID, b.Title, b.Author, b.CoverImage, od.Quantity, od.Price " +
                       "FROM OrderDetails od JOIN Books b ON od.BookID = b.BookID WHERE od.OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("BookID"));
                    b.setTitle(rs.getString("Title"));
                    b.setAuthor(rs.getString("Author"));
                    b.setCoverImage(rs.getString("CoverImage"));
                    Item item = new Item();
                    item.setBook(b);
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setPrice(rs.getDouble("Price"));
                    list.add(item);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 8. Kiểm tra user đã mua sách chưa - ✅ FIX: check status '4' (Hoàn thành)
    public boolean checkUserOwnsBook(int userId, int bookId) {
        String query = "SELECT COUNT(*) FROM OrderDetails od " +
                       "JOIN Orders o ON od.OrderID = o.OrderID " +
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

    // 9. Tạo đơn mua ebook (trạng thái Hoàn thành ngay)
    public boolean addEbookOrder(int userId, double totalAmount, int bookId) {
        try (Connection conn = new DBContext().getConnection()) {
            String sqlOrder = "INSERT INTO Orders (UserID, OrderDate, TotalAmount, Status) VALUES (?, GETDATE(), ?, '4')";
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setDouble(2, totalAmount);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt(1);
                        String sqlDetail = "INSERT INTO OrderDetails (OrderID, BookID, Quantity, Price) VALUES (?, ?, 1, ?)";
                        try (PreparedStatement ps2 = conn.prepareStatement(sqlDetail)) {
                            ps2.setInt(1, orderId);
                            ps2.setInt(2, bookId);
                            ps2.setDouble(3, totalAmount);
                            ps2.executeUpdate();
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 10. Lấy email user theo đơn hàng
    public String getUserEmailByOrderId(int orderId) {
        String query = "SELECT u.Email FROM Users u JOIN Orders o ON u.UserID = o.UserID WHERE o.OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("Email");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 11. Đổi đơn mới nhất sang trạng thái đã thanh toán QR ('1')
    public void updateLatestOrderToPaid(int userId) {
        String query = "UPDATE Orders SET Status = '1' WHERE OrderID = " +
                       "(SELECT TOP 1 OrderID FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 12. ✅ FIX: Trừ tiền voucher - đúng tên cột TotalAmount
    public void applyDiscountToLatestOrder(int userId, double discountAmount) {
        String query = "UPDATE Orders SET TotalAmount = CASE WHEN TotalAmount - ? < 0 THEN 0 ELSE TotalAmount - ? END " +
                       "WHERE OrderID = (SELECT TOP 1 OrderID FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, discountAmount);
            ps.setDouble(2, discountAmount);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 13. Chốt đơn: status '4' (Hoàn thành)
    public boolean completeOrder(String orderId) {
        String query = "UPDATE Orders SET Status = '4' WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 14. ✅ FIX: approveOrder - đúng tên cột OrderID, status '2' (Đang giao)
    public boolean approveOrder(int orderId) {
        String query = "UPDATE Orders SET Status = '2' WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 15. Kiểm tra đã thanh toán chưa
    public boolean checkIsPaid(String orderId) {
        String query = "SELECT Status FROM Orders WHERE OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("Status");
                    return "1".equals(status) || "4".equals(status);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}