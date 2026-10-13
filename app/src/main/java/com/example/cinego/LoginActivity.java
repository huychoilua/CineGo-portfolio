package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Khai báo các biến View
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    // Khai báo Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. KHỞI TẠO FIREBASE VÀ KIỂM TRA AUTO-LOGIN
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Nếu đã đăng nhập trước đó -> Vào thẳng MainActivity
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Đóng LoginActivity
            return; // Dừng chạy các dòng code bên dưới
        }

        // 2. Nếu chưa đăng nhập thì mới nạp giao diện Login
        setContentView(R.layout.activity_login);

        // 3. Ánh xạ giao diện
        initViews();

        // 4. Bắt sự kiện click nút "Đăng nhập"
        btnLogin.setOnClickListener(v -> performLogin());

        // 5. Bắt sự kiện click chữ "Đăng ký ngay"
        tvRegister.setOnClickListener(v -> {
            // Chuyển sang màn hình Đăng ký
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // 6. Bắt sự kiện click chữ "Quên mật khẩu?"
        tvForgotPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập Email của bạn vào ô trống để khôi phục mật khẩu!", Toast.LENGTH_LONG).show();
            } else {
                // Nhờ Firebase gửi link đổi mật khẩu về email
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Đã gửi email khôi phục. Vui lòng kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void initViews() {
        // Ánh xạ khớp 100% với file activity_login.xml của bạn
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void performLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra bỏ trống
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi Firebase Auth để kiểm tra tài khoản
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Thành công -> Chuyển sang Trang chủ
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Thất bại -> Báo lỗi
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}