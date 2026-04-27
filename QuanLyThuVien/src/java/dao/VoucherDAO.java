package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Voucher;
import utils.DBContext;

public class VoucherDAO {

    public List<Voucher> getValidVouchers() {
        List<Voucher> list = new ArrayList<>();
        String query = "SELECT * FROM Vouchers WHERE Quantity > 0 AND ExpiryDate >= CAST(GETDATE() AS DATE)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Voucher(
                    rs.getString("VoucherCode"), rs.getString("Title"),
                    rs.getDouble("DiscountAmount"), rs.getDouble("MinOrderAmount"),
                    rs.getInt("Quantity"), rs.getDate("ExpiryDate")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Voucher getVoucherByCode(String code) {
        String query = "SELECT * FROM Vouchers WHERE VoucherCode = ? AND Quantity > 0 AND ExpiryDate >= CAST(GETDATE() AS DATE)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Voucher(rs.getString("VoucherCode"), rs.getString("Title"),
                        rs.getDouble("DiscountAmount"), rs.getDouble("MinOrderAmount"),
                        rs.getInt("Quantity"), rs.getDate("ExpiryDate"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ✅ FIX: Đánh dấu đã dùng VÀ trừ số lượng trong bảng Vouchers
    public void markVoucherAsUsed(int userId, String voucherCode) {
        // Bước 1: Đánh dấu IsUsed = 1 trong UserVouchers
        String queryUserVoucher = "UPDATE UserVouchers SET IsUsed = 1 WHERE UserID = ? AND VoucherCode = ?";
        // Bước 2: Trừ số lượng trong bảng Vouchers (đảm bảo không âm)
        String queryDecrement = "UPDATE Vouchers SET Quantity = Quantity - 1 WHERE VoucherCode = ? AND Quantity > 0";

        try (Connection conn = new DBContext().getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement(queryUserVoucher)) {
                ps1.setInt(1, userId);
                ps1.setString(2, voucherCode);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(queryDecrement)) {
                ps2.setString(1, voucherCode);
                ps2.executeUpdate();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}