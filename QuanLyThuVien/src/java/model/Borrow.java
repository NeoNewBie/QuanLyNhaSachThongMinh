package model;
import java.sql.Date;

public class Borrow {
    private int borrowID;
    private String title;
    private Date borrowDate;
    private Date returnDate;
    private String status;

    public Borrow() {}

    public Borrow(int borrowID, String title, Date borrowDate, Date returnDate, String status) {
        this.borrowID = borrowID;
        this.title = title;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters và Setters (Quan trọng để hiển thị lên JSP)
    public int getBorrowID() { return borrowID; }
    public void setBorrowID(int borrowID) { this.borrowID = borrowID; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}