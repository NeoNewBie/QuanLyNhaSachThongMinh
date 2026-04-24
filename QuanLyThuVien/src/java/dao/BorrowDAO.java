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

    // 🛑 ĐÃ FIX: Hàm lấy toàn bộ danh sách mượn cho Admin (Bắt đúng cột BorrowID)
    public java.util.List<model.Borrow> getAllBorrows() {
        java.util.List<model.Borrow> list = new java.util.ArrayList<>();
        String query = "SELECT br.BorrowID, br.BorrowDate, br.ReturnDate, br.ActualReturnDate, br.Status, "
                     + "b.Title AS BookTitle "
                     + "FROM Borrows br "
                     + "JOIN Books b ON br.BookID = b.BookID "
                     + "ORDER BY br.BorrowDate DESC"; 
                     
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query);
             java.sql.ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                model.Borrow b = new model.Borrow();
                b.setId(rs.getInt("BorrowID"));
                b.setBookTitle(rs.getString("BookTitle"));
                b.setBorrowDate(rs.getDate("BorrowDate"));
                b.setReturnDate(rs.getDate("ReturnDate"));
                b.setActualReturnDate(rs.getDate("ActualReturnDate"));
                b.setStatus(rs.getString("Status"));
                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 🛑 ĐÃ FIX: Hàm thay đổi trạng thái (Bắt đúng cột BorrowID)
    public void updateBorrowStatus(int borrowId, String status) {
        String query = status.equals("4") 
            ? "UPDATE Borrows SET Status = ?, ActualReturnDate = GETDATE() WHERE BorrowID = ?" 
            : "UPDATE Borrows SET Status = ? WHERE BorrowID = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, borrowId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    // Lấy UserID từ mã Phiếu mượn (Để gửi thông báo cho đúng người)
    public int getUserIdByBorrowId(int borrowId) {
        String query = "SELECT UserID FROM Borrows WHERE BorrowID = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, borrowId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("UserID");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1; // Trả về -1 nếu lỗi
    }
    // Lấy thông tin chi tiết của 1 phiếu mượn bằng ID
    public model.Borrow getBorrowById(int borrowId) {
        String query = "SELECT br.BorrowID, br.BorrowDate, br.ReturnDate, br.ActualReturnDate, br.Status, "
                     + "b.Title AS BookTitle, b.CoverImage AS BookImage "
                     + "FROM Borrows br "
                     + "JOIN Books b ON br.BookID = b.BookID "
                     + "WHERE br.BorrowID = ?"; 
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, borrowId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    model.Borrow b = new model.Borrow();
                    b.setId(rs.getInt("BorrowID"));
                    b.setBookTitle(rs.getString("BookTitle"));
                    b.setBookImage(rs.getString("BookImage"));
                    b.setBorrowDate(rs.getDate("BorrowDate"));
                    b.setReturnDate(rs.getDate("ReturnDate"));
                    b.setActualReturnDate(rs.getDate("ActualReturnDate"));
                    b.setStatus(rs.getString("Status"));
                    return b;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    // Hàm Gia hạn thêm ngày mượn sách
    public boolean extendBorrow(int borrowId, int extraDays) {
        // SQL Server: DATEADD(day, số_ngày, cột_cần_cộng)
        String query = "UPDATE Borrows SET ReturnDate = DATEADD(day, ?, ReturnDate) WHERE BorrowID = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, extraDays);
            ps.setInt(2, borrowId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}