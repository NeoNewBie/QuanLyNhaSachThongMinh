package model;

import java.util.Date;

public class Notification {
    private int notifId;
    private int userId;
    private String message;
    private boolean isRead;
    private Date createdDate;

    public Notification() {}

    public Notification(int notifId, int userId, String message, boolean isRead, Date createdDate) {
        this.notifId = notifId;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdDate = createdDate;
    }

    // --- GETTER & SETTER ---
    public int getNotifId() { return notifId; }
    public void setNotifId(int notifId) { this.notifId = notifId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}