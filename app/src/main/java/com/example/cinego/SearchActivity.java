package com.example.cinego;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private ImageView btnBack, btnClear;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 1. Ánh xạ các View
        initViews();

        // 2. Xử lý nút Quay lại
        btnBack.setOnClickListener(v -> finish());

        // 3. Xử lý nút Xóa (Clear) trên thanh tìm kiếm
        btnClear.setOnClickListener(v -> {
            edtSearch.setText(""); // Xóa trắng ô nhập
            edtSearch.requestFocus(); // Đặt lại con trỏ chuột
            showKeyboard(); // Mở lại bàn phím
        });

        // 4. Lắng nghe từng thay đổi khi gõ phím
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nếu có chữ thì hiện nút X, nếu rỗng thì ẩn đi
                if (s.length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 5. Lắng nghe sự kiện bấm phím "Tìm kiếm" (Enter) trên bàn phím ảo
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true; // Báo cho hệ thống biết đã xử lý xong sự kiện
            }
            return false;
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);
        btnClear = findViewById(R.id.btnClear);

        // Mặc định ban đầu ẩn nút Xóa vì chưa có text
        btnClear.setVisibility(View.GONE);
    }

    private void performSearch(String query) {
        // Ẩn bàn phím sau khi bấm tìm kiếm
        hideKeyboard();

        Toast.makeText(this, "Đang tìm kiếm: " + query, Toast.LENGTH_SHORT).show();

        // Trong thực tế: Bạn sẽ lấy từ khóa này để lọc dữ liệu trong RecyclerView
        // và cập nhật lại danh sách kết quả hiển thị bên dưới.
    }

    // --- Các hàm tiện ích xử lý bàn phím ---

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}