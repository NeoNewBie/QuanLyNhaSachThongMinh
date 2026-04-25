package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Voucher;
import utils.DBContext;

public class VoucherDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    // Lấy tất cả Voucher còn hạn và còn lượt dùng để đưa lên Popup Shopee
    public List<Voucher> getValidVouchers() {
        List<Voucher> list = new ArrayList<>();
        String query = "SELECT * FROM Vouchers WHERE Quantity > 0 AND ExpiryDate >= GETDATE()";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Voucher(
                    rs.getString("VoucherCode"),
                    rs.getString("Title"),
                    rs.getDouble("DiscountAmount"),
                    rs.getDouble("MinOrderAmount"),
                    rs.getInt("Quantity"),
                    rs.getDate("ExpiryDate")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Kiểm tra và lấy chi tiết 1 mã cụ thể khi khách nhấn "Áp dụng"
    public Voucher getVoucherByCode(String code) {
        String query = "SELECT * FROM Vouchers WHERE VoucherCode = ? AND Quantity > 0 AND ExpiryDate >= GETDATE()";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, code);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Voucher(rs.getString("VoucherCode"), rs.getString("Title"), rs.getDouble("DiscountAmount"), rs.getDouble("MinOrderAmount"), rs.getInt("Quantity"), rs.getDate("ExpiryDate"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    // 🛑 Đốt Voucher: Đánh dấu mã này đã được sử dụng (IsUsed = 1)
    public void markVoucherAsUsed(int userId, String voucherCode) {
        String query = "UPDATE UserVouchers SET IsUsed = 1 WHERE UserID = ? AND VoucherCode = ?";
        try (java.sql.Connection conn = new utils.DBContext().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, voucherCode);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}