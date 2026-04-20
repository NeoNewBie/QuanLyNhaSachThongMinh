package model;

import java.sql.Date;

public class Order {
    private int id;
    private Date orderDate;
    private double totalPrice;
    private String status;
    private int userID;

    public Order() {}

    public Order(int id, Date orderDate, double totalPrice, String status, int userID) {
        this.id = id;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.userID = userID;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
}