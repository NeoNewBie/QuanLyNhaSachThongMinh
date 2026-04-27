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

    // [ĐÃ SỬA LỖI #6] Đổi từ bảng BorrowTickets (không tồn tại) sang Borrows
    public int getTicketStatus(int borrowId) {
        String query = "SELECT Status FROM Borrows WHERE BorrowID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, borrowId);
            rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("Status");
                try { return Integer.parseInt(status); } catch (Exception e) { return 0; }
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return -1;
    }

    // [ĐÃ SỬA LỖI #6] Đổi từ BorrowTickets sang Borrows, sửa tên cột cho khớp
    public List<Borrow> getHistoryByUserId(int userId) {
        List<Borrow> list = new ArrayList<>();
        String query = "SELECT b.BorrowID, bk.Title, b.BorrowDate, b.ReturnDate, b.Status "
                     + "FROM Borrows b JOIN Books bk ON b.BookID = bk.BookID "
                     + "WHERE b.UserID = ? ORDER BY b.BorrowDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Borrow(
                    rs.getInt(1),       // BorrowID
                    rs.getString(2),    // Title
                    rs.getDate(3),      // BorrowDate
                    rs.getDate(4),      // ReturnDate
                    rs.getString(5),    // Status
                    0.0                 // fineAmount (chưa có cột này trong Borrows)
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    // [ĐÃ SỬA LỖI #6] Đổi từ BorrowTickets sang Borrows
    public boolean updateStatus(int borrowId, int newStatus) {
        String query = "UPDATE Borrows SET Status = ? WHERE BorrowID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, String.valueOf(newStatus));
            ps.setInt(2, borrowId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return false;
    }

    // [ĐÃ SỬA LỖI #6] Đổi từ BorrowTickets sang Borrows
    public String getUserEmailByTicketId(int borrowId) {
        String query = "SELECT u.Email FROM Users u "
                     + "JOIN Borrows b ON u.UserID = b.UserID "
                     + "WHERE b.BorrowID = ?";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, borrowId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Email");
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return null;
    }

    // Lấy lịch sử mượn của user (dùng cho trang cá nhân)
    public List<Borrow> getBorrowHistory(int userId) {
        List<Borrow> list = new ArrayList<>();
        String query = "SELECT b.BorrowID, bk.Title, bk.CoverImage, b.BorrowDate, b.ReturnDate, b.ActualReturnDate, b.Status "
                     + "FROM Borrows b "
                     + "JOIN Books bk ON b.BookID = bk.BookID "
                     + "WHERE b.UserID = ? ORDER BY b.BorrowDate DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Borrow(
                    rs.getInt(1),       // BorrowID
                    rs.getString(2),    // Title
                    rs.getString(3),    // CoverImage
                    rs.getDate(4),      // BorrowDate
                    rs.getDate(5),      // ReturnDate
                    rs.getDate(6),      // ActualReturnDate
                    rs.getString(7)     // Status
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(); }
        return list;
    }

    // Lấy toàn bộ danh sách mượn cho Admin
    public List<Borrow> getAllBorrows() {
        List<Borrow> list = new ArrayList<>();
        String query = "SELECT br.BorrowID, br.BorrowDate, br.ReturnDate, br.ActualReturnDate, br.Status, "
                     + "b.Title AS BookTitle "
                     + "FROM Borrows br "
                     + "JOIN Books b ON br.BookID = b.BookID "
                     + "ORDER BY br.BorrowDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Borrow b = new Borrow();
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

    // [ĐÃ SỬA LỖI #15] Dùng hằng số thay vì so sánh magic string "4"
    public static final String STATUS_RETURNED = "4";

    public void updateBorrowStatus(int borrowId, String status) {
        String query = STATUS_RETURNED.equals(status)
            ? "UPDATE Borrows SET Status = ?, ActualReturnDate = GETDATE() WHERE BorrowID = ?"
            : "UPDATE Borrows SET Status = ? WHERE BorrowID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, borrowId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Lấy UserID từ mã phiếu mượn
    public int getUserIdByBorrowId(int borrowId) {
        String query = "SELECT UserID FROM Borrows WHERE BorrowID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("UserID");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    // Lấy thông tin chi tiết 1 phiếu mượn
    public Borrow getBorrowById(int borrowId) {
        String query = "SELECT br.BorrowID, br.BorrowDate, br.ReturnDate, br.ActualReturnDate, br.Status, "
                     + "b.Title AS BookTitle, b.CoverImage AS BookImage "
                     + "FROM Borrows br "
                     + "JOIN Books b ON br.BookID = b.BookID "
                     + "WHERE br.BorrowID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Borrow b = new Borrow();
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

    // Gia hạn thêm ngày mượn sách
    public boolean extendBorrow(int borrowId, int extraDays) {
        String query = "UPDATE Borrows SET ReturnDate = DATEADD(day, ?, ReturnDate) WHERE BorrowID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, extraDays);
            ps.setInt(2, borrowId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}