package model;

public class User {
    private int id; // UserID
    private String username;
    private String password;
    private String fullName;
    private int roleId;
    private String email;
    private String phone;
    private String address;
    // 🛑 THÊM BIẾN NÀY ĐỂ LƯU ĐƯỜNG DẪN ẢNH
    private String avatar;

    // 1. Hàm tạo rỗng
    public User() {}

    // 2. Hàm tạo đầy đủ (Đã cập nhật thêm Avatar ở cuối)
    public User(int id, String username, String password, String fullName, int roleId, String email, String phone, String address, String avatar) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.roleId = roleId;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
    }

    // --- CÁC HÀM GETTER VÀ SETTER ---
    
    // Mẹo: Tôi thêm cái getUserId() này để sếp xài cho tiện, khớp với Servlet
    public int getUserId() { return id; } 
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // 🛑 QUAN TRỌNG: Getter và Setter cho Avatar
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}