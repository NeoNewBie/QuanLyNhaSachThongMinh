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
import utils.DBContext;

public class OrderDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // 1. Hàm lưu đơn hàng (Dùng cho CheckoutServlet)
   public boolean addOrder(User u, Cart cart) {
        String queryOrder = "INSERT INTO Orders (OrderDate, TotalAmount, Status, UserID) VALUES (GETDATE(), ?, ?, ?)";
        String queryDetail = "INSERT INTO OrderDetails (OrderID, BookID, Quantity, Price) VALUES (?, ?, ?, ?)";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(queryOrder, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, cart.getTotalMoney());
            ps.setString(2, "0"); // 0: Chờ duyệt (Chuẩn logic Stepper)
            ps.setInt(3, u.getId());
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            int orderId = (rs.next()) ? rs.getInt(1) : 0;
            
            for (Item i : cart.getItems()) {
                ps = conn.prepareStatement(queryDetail);
                ps.setInt(1, orderId);
                ps.setInt(2, i.getBook().getId());
                ps.setInt(3, i.getQuantity());
                ps.setDouble(4, i.getPrice());
                ps.executeUpdate();
            }
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    // 2. Hàm lấy lịch sử đơn hàng của 1 người (Dùng cho OrderHistoryServlet)
    // Tên hàm phải khớp 100% với cái list = dao.getOrdersByUser(...) của ông
  public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
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

    // 3. Hàm lấy TẤT CẢ đơn hàng (Dùng cho ManageOrderServlet của Admin)
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM Orders ORDER BY OrderDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
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
    // 4. Hàm cập nhật trạng thái (Dùng cho nút Duyệt đơn của Admin)
    // 4. Hàm cập nhật trạng thái (Dùng cho Hủy đơn & Admin duyệt đơn)
    public void updateOrderStatus(int id, String status) {
        String query = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, status);
            ps.setInt(2, id);
            int result = ps.executeUpdate();
            if(result > 0) System.out.println("✅ Đã update thành công đơn #" + id);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 5. Các hàm cho Admin Dashboard (Doanh thu, đếm đơn)
    public double getTotalRevenue() {
        String query = "SELECT SUM(TotalAmount) FROM Orders";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {}
        return 0;
    }

    public int countTotalOrders() {
        String query = "SELECT COUNT(*) FROM Orders";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
    // 6. Hàm lấy thông tin chung của 1 đơn hàng
    public Order getOrderById(int orderId) {
        String query = "SELECT * FROM Orders WHERE OrderID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Order(
                    rs.getInt("OrderID"),
                    rs.getDate("OrderDate"),
                    rs.getDouble("TotalAmount"),
                    rs.getString("Status"),
                    rs.getInt("UserID")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 7. Hàm lấy danh sách các cuốn sách trong 1 đơn hàng
    // 7. Hàm lấy danh sách các cuốn sách trong 1 đơn hàng (ĐÃ FIX TÊN BẢNG OrderDetails)
    public List<Item> getOrderDetails(int orderId) {
        List<Item> list = new ArrayList<>();
        String query = "SELECT b.BookID, b.Title, b.Author, b.CoverImage, od.Quantity, od.Price " +
                       "FROM OrderDetails od " + 
                       "JOIN Books b ON od.BookID = b.BookID " +
                       "WHERE od.OrderID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
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
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // Hàm kiểm tra xem User đã mua thành công cuốn sách này chưa
public boolean checkUserOwnsBook(int userId, int bookId) {
    // Tìm trong chi tiết đơn hàng xem có BookID này không, 
    // và đơn hàng đó phải thuộc về UserID này, trạng thái phải là "Đã giao" hoặc "Hoàn thành"
    String query = "SELECT COUNT(*) FROM OrderDetails od " +
                   "JOIN Orders o ON od.OrderID = o.OrderID " +
                   "WHERE o.UserID = ? AND od.BookID = ? AND (o.Status = N'Đã giao' OR o.Status = N'Hoàn thành')";
    try {
        conn = new utils.DBContext().getConnection();
        ps = conn.prepareStatement(query);
        ps.setInt(1, userId);
        ps.setInt(2, bookId);
        rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Trả về true nếu Count > 0 (Tức là đã mua)
        }
    } catch (Exception e) { e.printStackTrace(); }
    return false;
}
public boolean addEbookOrder(int txt_id_nguoidung, double txt_tongtien, int txt_id_sach) {
        try {
            conn = new utils.DBContext().getConnection();
            String sql_order = "INSERT INTO Orders (UserID, OrderDate, TotalAmount, Status) VALUES (?, GETDATE(), ?, N'Hoàn thành')";
            ps = conn.prepareStatement(sql_order, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, txt_id_nguoidung);
            ps.setDouble(2, txt_tongtien);
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int txt_id_donhang = rs.getInt(1);
                String sql_detail = "INSERT INTO OrderDetails (OrderID, BookID, Quantity, Price) VALUES (?, ?, 1, ?)";
                java.sql.PreparedStatement ps_detail = conn.prepareStatement(sql_detail);
                ps_detail.setInt(1, txt_id_donhang);
                ps_detail.setInt(2, txt_id_sach);
                ps_detail.setDouble(3, txt_tongtien);
                ps_detail.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
public String getUserEmailByOrderId(int orderId) {
        String query = "SELECT u.Email FROM Users u "
                     + "JOIN Orders o ON u.UserID = o.UserID " 
                     + "WHERE o.OrderID = ?"; 
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Email");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
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