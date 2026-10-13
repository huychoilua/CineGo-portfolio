package com.example.cinego;

public class User {
    private String fullName, email, phone, dob, role;

    private String avatarName;


    public User() {
    } // Bắt buộc phải có để Firebase hoạt động

    public User(String fullName, String email, String phone, String dob, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.role = role;
    }

    // Các hàm Getter
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public String getRole() {
        return role;
    }

    public String getAvatarName() {
        return avatarName;
    }
}