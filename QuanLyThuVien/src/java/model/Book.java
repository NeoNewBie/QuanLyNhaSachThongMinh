package model;

public class Book {
    private int id;
    private String title;
    private String author;
    private double price;
    private String coverImage;
    private String description;
    private int isEbook; 
    private int categoryId;
    private int stock; // 🛑 MỚI THÊM

    public Book() {}

    // Constructor đầy đủ cho DAO gọi
    public Book(int id, String title, String author, double price, String coverImage, String description, int isEbook, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.coverImage = coverImage;
        this.description = description;
        this.isEbook = isEbook;
        this.stock = stock;
    }

    // --- Getters và Setters ---
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getCategoryId() { return categoryId; }   
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getIsEbook() { return isEbook; }
    public void setIsEbook(int isEbook) { this.isEbook = isEbook; }
}