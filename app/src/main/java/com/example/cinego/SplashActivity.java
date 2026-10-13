package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1. Tạo độ trễ 1500ms (1.5 giây)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserStatus();
            }
        }, 1500);
    }

    private void checkUserStatus() {
        // 2. Kiểm tra xem người dùng đã đăng nhập từ trước chưa
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Đã đăng nhập -> Vào thẳng Trang chủ
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // Chưa đăng nhập -> Vào trang Login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        // Đóng màn hình Splash để không cho quay lại khi nhấn nút Back
        finish();
    }
}