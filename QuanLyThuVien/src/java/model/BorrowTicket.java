package model;

import java.sql.Date;

public class BorrowTicket {
    private int id;
    private String title;
    private Date borrowDate;
    private Date dueDate;
    private int status;
    private double fineAmount;

    // Constructor không tham số
    public BorrowTicket() {}

    // Constructor có tham số (Cái này để DAO gọi nè)
    public BorrowTicket(int id, String title, Date borrowDate, Date dueDate, int status, double fineAmount) {
        this.id = id;
        this.title = title;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
        this.fineAmount = fineAmount;
    }

    // Sếp chuột phải chọn Insert Code -> Getter and Setter cho tất cả nhé
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Date getBorrowDate() { return borrowDate; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }
}