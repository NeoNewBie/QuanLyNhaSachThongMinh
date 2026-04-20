package model;

public class Item {
    private Book book;
    private int quantity;
    private double price;

    // Hàm tạo rỗng
    public Item() {
    }

    // Hàm tạo có tham số
    public Item(Book book, int quantity, double price) {
        this.book = book;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters và Setters (Bắt buộc phải có)
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}