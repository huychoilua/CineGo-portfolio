package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView rvTickets;
    private TextView tabUpcoming, tabHistory;

    private TicketAdapter ticketAdapter;
    private List<Ticket> allTicketsList = new ArrayList<>();
    private List<Ticket> upcomingList = new ArrayList<>();
    private List<Ticket> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        initViews();
        setupBottomNavigation();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Lấy dữ liệu THẬT từ Firebase
        fetchTicketsFromFirebase();

        // Thiết lập logic bấm chuyển Tab
        setupTabsLogic();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        rvTickets = findViewById(R.id.rvMyTickets);
        tabUpcoming = findViewById(R.id.tabUpcoming);
        tabHistory = findViewById(R.id.tabHistory);

        rvTickets.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchTicketsFromFirebase() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("booked_tickets").child(userId);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTicketsList.clear();
                upcomingList.clear();
                historyList.clear();

                long currentTime = System.currentTimeMillis();
                // Ngưỡng 2 tiếng (7.200.000 ms) để coi như phim đã chiếu xong
                long movieDurationBuffer = 2 * 60 * 60 * 1000;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Ticket ticket = data.getValue(Ticket.class);
                    if (ticket != null) {
                        allTicketsList.add(ticket);

                        // LOGIC MỚI: Dựa vào giờ chiếu (Timestamp)
                        // Nếu giờ hiện tại vẫn chưa vượt quá (Giờ chiếu + 2 tiếng)
                        if (currentTime < (ticket.getTimestamp() + movieDurationBuffer)) {
                            upcomingList.add(ticket); // Vé vẫn còn hiệu lực hoặc đang chiếu
                        } else {
                            historyList.add(ticket); // Phim đã kết thúc thật sự
                        }
                    }
                }

                // Sắp xếp: Vé có giờ chiếu gần nhất hiện lên đầu
                Collections.sort(upcomingList, (t1, t2) -> Long.compare(t1.getTimestamp(), t2.getTimestamp()));
                // Lịch sử thì vé mới xem xong hiện lên đầu
                Collections.sort(historyList, (t1, t2) -> Long.compare(t2.getTimestamp(), t1.getTimestamp()));

                // Mặc định hiển thị tab Sắp tới
                updateRecyclerView(upcomingList);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateRecyclerView(List<Ticket> list) {
        View layoutEmpty = findViewById(R.id.layoutEmptyTickets);

        if (list == null || list.isEmpty()) {
            // Nếu không có vé: Hiện màn hình trống, ẩn danh sách
            rvTickets.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            // Nếu có vé: Hiện danh sách, ẩn màn hình trống
            rvTickets.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);

            ticketAdapter = new TicketAdapter(this, list);
            rvTickets.setAdapter(ticketAdapter);
        }
    }

    private void setupTabsLogic() {
        tabUpcoming.setOnClickListener(v -> {
            // Đổi màu Tab
            tabUpcoming.setBackgroundResource(R.drawable.bg_neon_button);
            tabUpcoming.setTextColor(ContextCompat.getColor(this, R.color.bg_main));
            tabHistory.setBackgroundResource(0);
            tabHistory.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            // Hiện danh sách sắp tới
            updateRecyclerView(upcomingList);
        });

        tabHistory.setOnClickListener(v -> {
            // Đổi màu Tab
            tabHistory.setBackgroundResource(R.drawable.bg_neon_button);
            tabHistory.setTextColor(ContextCompat.getColor(this, R.color.bg_main));
            tabUpcoming.setBackgroundResource(0);
            tabUpcoming.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

            // Hiện danh sách lịch sử
            updateRecyclerView(historyList);
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_tickets);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    startActivity(new Intent(this, MoviesActivity.class));
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    startActivity(new Intent(this, AiChatActivity.class));
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    startActivity(new Intent(this, NotificationsActivity.class));
                    return true;
                }
                return itemId == R.id.nav_tickets;
            });
        }
    }
}
