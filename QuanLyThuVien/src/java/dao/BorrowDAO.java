package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Borrow;
import utils.DBContext;

public class BorrowDAO {
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
        finally { close(); }
        return -1;
    }

    // 2. Lấy lịch sử mượn (Theo bảng BorrowTickets - dùng cho Admin/Tra cứu)
    public List<Borrow> getHistoryByUserId(int userId) {
        List<Borrow> list = new ArrayList<>();
        String query = "SELECT b.id, bk.title, b.borrow_date, b.due_date, b.status, b.fine_amount "
                     + "FROM BorrowTickets b JOIN Books bk ON b.book_id = bk.BookID "
                     + "WHERE b.user_id = ? ORDER BY b.borrow_date DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Sử dụng Constructor 3 (6 tham số)
                // Lưu ý: Status trong SQL là int, ta ép về String để khớp Model
                list.add(new Borrow(
                    rs.getInt(1),      // id
                    rs.getString(2),   // title
                    rs.getDate(3),     // borrow_date
                    rs.getDate(4),     // due_date
                    rs.getString(5),   // status (ép kiểu về String)
                    rs.getDouble(6)    // fine_amount
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    // 3. Hàm cập nhật trạng thái
    public boolean updateStatus(int ticketId, int newStatus) {
        String query = "UPDATE BorrowTickets SET status = ? WHERE id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, newStatus);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return false;
    }

    // 4. Lấy Email từ mã phiếu mượn
    public String getUserEmailByTicketId(int ticketId) {
        String query = "SELECT u.Email FROM Users u "
                     + "JOIN BorrowTickets b ON u.UserID = b.user_id "
                     + "WHERE b.id = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, ticketId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Email");
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return null;
    }

    // 5. Lấy lịch sử mượn (Theo bảng Borrows - dùng cho trang Cá nhân của User)
    public List<Borrow> getBorrowHistory(int userId) {
        List<Borrow> list = new ArrayList<>();
        String query = "SELECT b.BorrowID, bk.Title, bk.CoverImage, b.BorrowDate, b.ReturnDate, b.ActualReturnDate, b.Status " +
                       "FROM Borrows b " +
                       "JOIN Books bk ON b.BookID = bk.BookID " +
                       "WHERE b.UserID = ? ORDER BY b.BorrowDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Sử dụng Constructor 2 (7 tham số)
                list.add(new Borrow(
                    rs.getInt(1),      // BorrowID
                    rs.getString(2),   // Title
                    rs.getString(3),   // CoverImage
                    rs.getDate(4),     // BorrowDate
                    rs.getDate(5),     // ReturnDate
                    rs.getDate(6),     // ActualReturnDate
                    rs.getString(7)    // Status
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }
}