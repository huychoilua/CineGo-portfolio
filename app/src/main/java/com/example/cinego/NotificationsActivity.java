package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNoti;
    private NotificationAdapter adapter;
    private List<Notification> allList = new ArrayList<>();
    private TextView tabAll, tabPromo, tabSystem;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // KẾT NỐI FIREBASE THEO USER ID (Dọn dẹp lại đoạn cũ của bạn)
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            dbRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("notifications").child(userId);

            anhXa();
            fetchData(); // Bắt đầu lấy dữ liệu
            setupBottomNavigation(); // Kích hoạt thanh Menu
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Xử lý nút Xóa (Nút icon thùng rác trên Header)
        findViewById(R.id.btnClearOld).setOnClickListener(v -> clearReadNotifications());

        // Xử lý nút Đánh dấu tất cả (Nút icon tích xanh trên Header)
        findViewById(R.id.btnMarkAllRead).setOnClickListener(v -> markAllAsRead());
    }

    private void anhXa() {
        rvNoti = findViewById(R.id.rvNotifications);
        tabAll = findViewById(R.id.tabAll);
        tabPromo = findViewById(R.id.tabPromo);
        tabSystem = findViewById(R.id.tabSystem);

        tabAll.setOnClickListener(v -> filter("ALL"));
        tabPromo.setOnClickListener(v -> filter("PROMO"));
        tabSystem.setOnClickListener(v -> filter("SYSTEM"));
    }

    private void fetchData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Notification n = ds.getValue(Notification.class);
                    if (n != null) {
                        // LẤY CÁI KEY TRÊN FIREBASE GÁN VÀO ID ĐỂ XÓA ĐƯỢC
                        n.setId(ds.getKey());
                        allList.add(n);
                    }
                }

                // SẮP XẾP: Tin mới nhất hiện lên đầu
                Collections.sort(allList, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                filter("ALL"); // Cập nhật giao diện
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filter(String type) {
        List<Notification> filtered = new ArrayList<>();
        for (Notification n : allList) {
            if (type.equals("ALL") || n.getType().equals(type)) {
                filtered.add(n);
            }
        }

        View layoutEmpty = findViewById(R.id.layoutEmptyNoti);
        if (filtered.isEmpty()) {
            rvNoti.setVisibility(View.GONE);
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNoti.setVisibility(View.VISIBLE);
            if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);

            adapter = new NotificationAdapter(this, filtered, n -> {
                // Đánh dấu đã đọc khi người dùng nhấn vào tin nhắn
                dbRef.child(n.getId()).child("read").setValue(true);
            });
            rvNoti.setLayoutManager(new LinearLayoutManager(this));
            rvNoti.setAdapter(adapter);
        }
        updateTabUI(type);
    }

    private void clearReadNotifications() {
        if (allList.isEmpty()) return;

        new android.app.AlertDialog.Builder(this)
                .setTitle("Dọn dẹp thông báo")
                .setMessage("Bạn muốn xóa tất cả thông báo đã đọc?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    for (Notification n : allList) {
                        if (n.isRead()) {
                            // Xóa thật sự trên Firebase
                            dbRef.child(n.getId()).removeValue();
                        }
                    }
                    Toast.makeText(this, "Đã dọn dẹp xong!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void markAllAsRead() {
        for (Notification n : allList) {
            if (!n.isRead()) {
                dbRef.child(n.getId()).child("read").setValue(true);
            }
        }
        Toast.makeText(this, "Đã đánh dấu tất cả!", Toast.LENGTH_SHORT).show();
    }

    private void updateTabUI(String type) {
        int activeColor = ContextCompat.getColor(this, R.color.bg_main);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_secondary);

        tabAll.setBackgroundResource(0);
        tabAll.setTextColor(inactiveColor);
        tabPromo.setBackgroundResource(0);
        tabPromo.setTextColor(inactiveColor);
        tabSystem.setBackgroundResource(0);
        tabSystem.setTextColor(inactiveColor);

        if (type.equals("ALL")) {
            tabAll.setBackgroundResource(R.drawable.bg_neon_button);
            tabAll.setTextColor(activeColor);
        } else if (type.equals("PROMO")) {
            tabPromo.setBackgroundResource(R.drawable.bg_neon_button);
            tabPromo.setTextColor(activeColor);
        } else {
            tabSystem.setBackgroundResource(R.drawable.bg_neon_button);
            tabSystem.setTextColor(activeColor);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_notifications);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(this, MoviesActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(this, AiChatActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_tickets) {
                    startActivity(new Intent(this, MyTicketsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return itemId == R.id.nav_notifications;
            });
        }
    }
}
