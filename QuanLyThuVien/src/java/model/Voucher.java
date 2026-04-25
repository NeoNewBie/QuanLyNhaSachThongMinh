package model;

import java.sql.Date;

public class Voucher {
    private String code;
    private String title;
    private double discountAmount;
    private double minOrderAmount;
    private int quantity;
    private Date expiryDate;

    // Sếp tự Generate Constructor và Getter/Setter ở đây nhé (Alt + Insert)
    public Voucher() {}

    public Voucher(String code, String title, double discountAmount, double minOrderAmount, int quantity, Date expiryDate) {
        this.code = code;
        this.title = title;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }
    // ... Getter & Setter

    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}