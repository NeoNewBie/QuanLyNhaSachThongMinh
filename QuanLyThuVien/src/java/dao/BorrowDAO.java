package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utils.DBContext;
import model.BorrowTicket;

public class BorrowDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // 1. Lấy trạng thái đơn mượn
    public int getTicketStatus(int ticketId) {
        String query = "SELECT status FROM BorrowTickets WHERE id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("status");
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // 2. Lấy lịch sử mượn (Đã sửa để khớp với Model ở trên)
    public List<BorrowTicket> getHistoryByUserId(int userId) {
        List<BorrowTicket> list = new ArrayList<>();
        String query = "SELECT b.id, bk.title, b.borrow_date, b.due_date, b.status, b.fine_amount "
                     + "FROM BorrowTickets b JOIN Books bk ON b.book_id = bk.id "
                     + "WHERE b.user_id = ? ORDER BY b.borrow_date DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new BorrowTicket(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDate("borrow_date"),
                    rs.getDate("due_date"),
                    rs.getInt("status"),
                    rs.getDouble("fine_amount")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Hàm cập nhật trạng thái (Dùng trong Servlet để gửi mail)
    public boolean updateStatus(int ticketId, int newStatus) {
        String query = "UPDATE BorrowTickets SET status = ? WHERE id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, newStatus);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // 4. Lấy Email từ mã phiếu mượn (Để biết mail ai mà gửi)
    public String getUserEmailByTicketId(int ticketId) {
        String query = "SELECT u.Email FROM Users u " // 'Email' viết hoa chữ E nếu trong DB sếp viết hoa
             + "JOIN BorrowTickets b ON u.UserID = b.user_id " // Sửa id thành UserID
             + "WHERE b.id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getString("email");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}