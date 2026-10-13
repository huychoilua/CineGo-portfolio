package com.example.cinego;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    // Khai báo các biến View
    private EditText edtFullName, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private TextView tvDay, tvMonth, tvYear, tvLogin;
    private Button btnRegister;
    private LinearLayout layoutDob;
    private ProgressBar progressBar;

    // Khai báo Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Ánh xạ giao diện
        initViews();

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Sự kiện chọn Ngày Sinh
        layoutDob.setOnClickListener(v -> showDatePicker());
        tvDay.setOnClickListener(v -> showDatePicker());
        tvMonth.setOnClickListener(v -> showDatePicker());
        tvYear.setOnClickListener(v -> showDatePicker());

        // 3. Sự kiện bấm nút Đăng ký
        btnRegister.setOnClickListener(v -> performRegistration());

        // 4. Sự kiện bấm chữ "Đăng nhập" (Quay lại trang Login)
        tvLogin.setOnClickListener(v -> {
            finish(); // Đóng trang Đăng ký
        });
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        tvDay = findViewById(R.id.tvDay);
        tvMonth = findViewById(R.id.tvMonth);
        tvYear = findViewById(R.id.tvYear);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister = findViewById(R.id.btnRegister);
        layoutDob = findViewById(R.id.layoutDob);
        progressBar = findViewById(R.id.progressBar);
    }

    // Hàm hiển thị Bảng chọn Ngày Tháng
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Cập nhật text hiển thị (Lưu ý: Tháng bắt đầu từ 0 nên phải +1)
                    tvDay.setText(String.format("%02d", dayOfMonth));
                    tvMonth.setText(String.format("%02d", month + 1));
                    tvYear.setText(String.valueOf(year));
                }, currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }

    // Hàm xử lý Đăng ký
    private void performRegistration() {
        String name = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        String day = tvDay.getText().toString();
        String month = tvMonth.getText().toString();
        String year = tvYear.getText().toString();
        String dob = day + "/" + month + "/" + year;

        // --- BỘ LỌC KIỂM TRA LỖI NHẬP LIỆU ---
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (day.equals("Ngày sinh") || month.equals("Tháng sinh") || year.equals("Năm sinh")) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- HIỂN THỊ TRẠNG THÁI CHỜ ---
        setLoading(true);

        // --- GỌI FIREBASE ĐỂ TẠO TÀI KHOẢN ---
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Nếu tạo tài khoản thành công, lấy ID (UID)
                        String userId = mAuth.getCurrentUser().getUid();

                        // Đóng gói thông tin để đưa lên Database
                        User newUser = new User(name, email, phone, dob, "user");

                        // Đẩy lên nhánh "users" -> "Mã ID"
                        FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("users")
                                .child(userId)
                                .setValue(newUser)
                                .addOnCompleteListener(dbTask -> {
                                    setLoading(false);
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        // Chuyển sang trang chủ
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish(); // Đóng trang đăng ký lại
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Lỗi lưu dữ liệu: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        setLoading(false);
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Email này đã được sử dụng bởi tài khoản khác!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setText(""); // Ẩn chữ trên nút
            btnRegister.setEnabled(false); // Vô hiệu hóa nút
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setText("Đăng Ký Ngay");
            btnRegister.setEnabled(true);
        }
    }
}

