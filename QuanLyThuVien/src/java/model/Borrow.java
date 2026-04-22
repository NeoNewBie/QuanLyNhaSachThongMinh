package model;

import java.util.Date;

public class Borrow {
    private int id;
    private String bookTitle;
    private String bookImage;
    private Date borrowDate;
    private Date returnDate;
    private Date actualReturnDate; 
    private String status;
    private double fineAmount;

    public Borrow() {}

    // Constructor 1: Đầy đủ nhất (8 tham số)
    public Borrow(int id, String bookTitle, String bookImage, Date borrowDate, Date returnDate, Date actualReturnDate, String status, double fineAmount) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookImage = bookImage;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.fineAmount = fineAmount;
    }

    // Constructor 2: Dùng cho hàm getBorrowHistory (7 tham số)
    public Borrow(int id, String bookTitle, String bookImage, Date borrowDate, Date returnDate, Date actualReturnDate, String status) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.bookImage = bookImage;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
    }

    // Constructor 3: Dùng cho hàm getHistoryByUserId (6 tham số)
    public Borrow(int id, String bookTitle, Date borrowDate, Date returnDate, String status, double fineAmount) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount;
    }

    // --- GETTER & SETTER (Nét căng cho sếp) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBookImage() { return bookImage; }
    public void setBookImage(String bookImage) { this.bookImage = bookImage; }
    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public Date getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(Date actualReturnDate) { this.actualReturnDate = actualReturnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
}