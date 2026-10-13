package com.example.cinego;

public class Notification {
    private String id;
    private String title;
    private String content;
    private String type;
    private long timestamp;
    private boolean read; // Khớp với trường 'read' trên Firebase

    // Bắt buộc phải có Constructor trống cho Firebase
    public Notification() {
    }

    public Notification(String id, String title, String content, String type, long timestamp, boolean read) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.read = read;
    }

    // Các hàm Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}