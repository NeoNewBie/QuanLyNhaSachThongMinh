package model;

import java.util.List;

public class Chapter {
    private int chapterID;
    private int bookID;
    private int chapterNumber;
    private String title;
    private String content;
    private double price;
    private boolean isFree;
    
    // --- 🛑 HAI BIẾN MỚI ĐỂ PHỤC VỤ CÀO TRUYỆN ---
    private String sourceURL; // Lưu link từ NetTruyen, MangaDex...
    private List<String> imageList; // Lưu danh sách link ảnh sau khi cào xong
    
    // --- 🛑 BIẾN ĐỂ KIỂM TRA TRẠNG THÁI KHÓA ---
    private boolean unlocked; 

    // Constructor không tham số
    public Chapter() {
    }

    // Constructor đầy đủ (Sếp có thể dùng cái này để map từ Database)
    public Chapter(int chapterID, int bookID, int chapterNumber, String title, String content, double price, boolean isFree, String sourceURL) {
        this.chapterID = chapterID;
        this.bookID = bookID;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
        this.price = price;
        this.isFree = isFree;
        this.sourceURL = sourceURL;
    }

    // --- GETTER VÀ SETTER (BẮT BUỘC PHẢI CÓ ĐỦ) ---

    public int getChapterID() { return chapterID; }
    public void setChapterID(int chapterID) { this.chapterID = chapterID; }

    public int getBookID() { return bookID; }
    public void setBookID(int bookID) { this.bookID = bookID; }

    public int getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(int chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isIsFree() { return isFree; } // Lưu ý: NetBeans hay sinh ra isIsFree
    public void setIsFree(boolean isFree) { this.isFree = isFree; }

    public String getSourceURL() { return sourceURL; }
    public void setSourceURL(String sourceURL) { this.sourceURL = sourceURL; }

    public List<String> getImageList() { return imageList; }
    public void setImageList(List<String> imageList) { this.imageList = imageList; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}