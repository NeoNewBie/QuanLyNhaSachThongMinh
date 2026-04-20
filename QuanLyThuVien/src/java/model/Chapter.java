package model;

public class Chapter {
    private int chapterID;
    private int bookID;
    private int chapterNumber;
    private String title;
    private String content;
    private boolean isFree;

    // Sếp tự tạo Getter/Setter và Constructor nhé
    
    public Chapter() {
    }

    public Chapter(int chapterID, int bookID, int chapterNumber, String title, String content, boolean isFree) {
        this.chapterID = chapterID;
        this.bookID = bookID;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.content = content;
        this.isFree = isFree;
    }

    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIsFree() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }
    
}