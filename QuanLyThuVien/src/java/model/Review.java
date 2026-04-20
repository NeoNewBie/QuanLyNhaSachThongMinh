package model;

import java.sql.Timestamp;

public class Review {
    private int reviewID;
    private int userID; // Hoặc accountID tùy SQL của sếp
    private int bookID;
    private int rating;
    private String comment;
    private Timestamp date;
    private String username;
    // Biến phụ này rất quan trọng để lúc JOIN bảng lấy được tên thật hiển thị lên web
    private String fullName; 

    // Hàm tạo rỗng
    public Review() {
    }

    // --- GETTER VÀ SETTER ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getReviewID() { return reviewID; }
    public void setReviewID(int reviewID) { this.reviewID = reviewID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getBookID() { return bookID; }
    public void setBookID(int bookID) { this.bookID = bookID; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}