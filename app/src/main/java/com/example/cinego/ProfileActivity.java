package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvDob, tvPhone, tvWatchedMoviesCount, tvBoughtTicketsCount, tvFavoriteGenre, tvAIConclusion;
    private View layoutAIInsight;
    private ImageView imgAvatar, btnChangeAvatar, btnEditProfile;
    private AppCompatButton btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            dbRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("users").child(currentUser.getUid());

            anhXaView();
            loadUserInfo(); // Gọi hàm tải dữ liệu

            // 1. Đổi Avatar
            btnChangeAvatar.setOnClickListener(v -> showAvatarDialog());

            // 2. Chỉnh sửa thông tin (Bút chì)
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

            // 3. Đăng xuất
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(this, "Hẹn gặp lại bạn! 👋", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void anhXaView() {
        tvName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvDob = findViewById(R.id.tvDob);
        tvPhone = findViewById(R.id.tvPhone);
        tvWatchedMoviesCount = findViewById(R.id.tvWatchedMoviesCount);
        tvBoughtTicketsCount = findViewById(R.id.tvBoughtTicketsCount);
        tvFavoriteGenre = findViewById(R.id.tvFavoriteGenre);
        tvAIConclusion = findViewById(R.id.tvAIConclusion);
        layoutAIInsight = findViewById(R.id.layoutAIInsight);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserInfo() {
        if (dbRef == null) return;

        // --- PHẦN 1: NẠP THÔNG TIN CÁ NHÂN (Tên, Email, Avatar) ---
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvName.setText(user.getFullName());
                        tvEmail.setText("📧 " + user.getEmail());
                        tvDob.setText("📅 " + user.getDob());
                        tvPhone.setText("📞 " + user.getPhone());

                        // Nạp ảnh đại diện
                        String avtName = snapshot.child("avatarName").getValue(String.class);
                        if (avtName != null && !avtName.isEmpty()) {
                            int resId = getResources().getIdentifier(avtName, "drawable", getPackageName());
                            if (resId != 0) imgAvatar.setImageResource(resId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // --- PHẦN 2: NẠP VÀ ĐẾM SỐ VÉ THẬT ---
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference ticketsRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("booked_tickets").child(userId);

            ticketsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long totalTickets = snapshot.getChildrenCount();
                    tvBoughtTicketsCount.setText(String.valueOf(totalTickets));
                    tvWatchedMoviesCount.setText(String.valueOf(totalTickets));

                    if (totalTickets == 0) {
                        tvFavoriteGenre.setText("---");
                        layoutAIInsight.setVisibility(View.GONE);
                    } else {
                        layoutAIInsight.setVisibility(View.VISIBLE);

                        // 1. Dùng Map để đếm TỰ ĐỘNG mọi thể loại có trên đời
                        Map<String, Integer> genreMap = new HashMap<>();
                        int eveningCount = 0;

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Ticket t = data.getValue(Ticket.class);
                            if (t != null) {
                                // Tách chuỗi thể loại (ví dụ: "Hành động, Viễn tưởng" -> ["Hành động", "Viễn tưởng"])
                                String rawGenre = (t.getGenre() != null) ? t.getGenre() : "Khác";
                                String[] genres = rawGenre.split(",\\s*");

                                for (String g : genres) {
                                    String cleanGenre = g.trim();
                                    // Cộng dồn số lần xuất hiện của thể loại này
                                    genreMap.put(cleanGenre, genreMap.getOrDefault(cleanGenre, 0) + 1);
                                }

                                // Phân tích giờ xem
                                if (t.getDateTime() != null && (t.getDateTime().contains("18:") || t.getDateTime().contains("20:"))) {
                                    eveningCount++;
                                }
                            }
                        }

                        // 2. Sắp xếp danh sách thể loại từ nhiều nhất đến ít nhất
                        List<Map.Entry<String, Integer>> sortedGenres = new ArrayList<>(genreMap.entrySet());
                        Collections.sort(sortedGenres, (a, b) -> b.getValue().compareTo(a.getValue()));

                        // 3. Hiện thể loại số 1 lên mục "Yêu thích nhất"
                        tvFavoriteGenre.setText(sortedGenres.get(0).getKey());

                        // 4. Gọi hàm vẽ biểu đồ Động (Top 3)
                        updateDynamicChartsUI(sortedGenres, totalTickets);

                        // 5. Kết luận thông minh
                        if (eveningCount > totalTickets / 2) {
                            tvAIConclusion.setText("✨ Bạn là 'Cú đêm' chính hiệu, rất thích xem phim vào buổi tối.");
                        } else {
                            tvAIConclusion.setText("✨ Bạn thích trải nghiệm điện ảnh vào ban ngày để nạp năng lượng.");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void updateDynamicChartsUI(List<Map.Entry<String, Integer>> sortedGenres, long total) {
        // Danh sách các ProgressBar và TextView đã có trong XML
        ProgressBar[] pbs = {findViewById(R.id.pbAction), findViewById(R.id.pbRomance), findViewById(R.id.pbHorror)};
        TextView[] tvs = {findViewById(R.id.tvPAction), findViewById(R.id.tvPRomance), findViewById(R.id.tvPHorror)};

        // Duyệt qua Top 3 thể loại
        for (int i = 0; i < 3; i++) {
            if (i < sortedGenres.size()) {
                // Lấy thông tin thể loại thứ i
                String name = sortedGenres.get(i).getKey();
                int count = sortedGenres.get(i).getValue();
                int percent = (int) ((count * 100) / total);

                // Hiển thị và cập nhật thanh biểu đồ
                tvs[i].setVisibility(View.VISIBLE);
                pbs[i].setVisibility(View.VISIBLE);

                tvs[i].setText(name + " (" + percent + "%)");
                pbs[i].setProgress(percent);
            } else {
                // Nếu không có đủ 3 thể loại thì ẩn các thanh thừa đi
                tvs[i].setVisibility(View.GONE);
                pbs[i].setVisibility(View.GONE);
            }
        }
    }

    private void updateDynamicCharts(List<Map.Entry<String, Integer>> sorted, long total) {
        ProgressBar[] pbs = {findViewById(R.id.pbAction), findViewById(R.id.pbRomance), findViewById(R.id.pbHorror)};
        TextView[] tvs = {findViewById(R.id.tvPAction), findViewById(R.id.tvPRomance), findViewById(R.id.tvPHorror)};

        for (int i = 0; i < 3; i++) {
            if (i < sorted.size()) {
                int percent = (int) ((sorted.get(i).getValue() * 100) / total);
                pbs[i].setVisibility(View.VISIBLE);
                pbs[i].setProgress(percent);
                tvs[i].setVisibility(View.VISIBLE);
                tvs[i].setText(sorted.get(i).getKey() + " (" + percent + "%)");
            } else {
                pbs[i].setVisibility(View.GONE);
                tvs[i].setVisibility(View.GONE);
            }
        }
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa thông tin cá nhân");
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(60, 20, 60, 20);
        final EditText n = new EditText(this);
        n.setHint("Tên mới");
        l.addView(n);
        final EditText p = new EditText(this);
        p.setHint("SĐT mới");
        l.addView(p);
        builder.setView(l).setPositiveButton("Lưu", (d, w) -> {
            if (!n.getText().toString().isEmpty())
                dbRef.child("fullName").setValue(n.getText().toString());
            if (!p.getText().toString().isEmpty())
                dbRef.child("phone").setValue(p.getText().toString());
            Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
        }).show();
    }

    private void showAvatarDialog() {
        android.app.Dialog d = new android.app.Dialog(this);
        d.setContentView(R.layout.dialog_select_avatar);
        int[] ids = {R.id.avt1, R.id.avt2, R.id.avt3, R.id.avt4};
        String[] names = {"image1", "image2", "image3", "image4"};
        for (int i = 0; i < ids.length; i++) {
            final String name = names[i];
            View v = d.findViewById(ids[i]);
            if (v != null) v.setOnClickListener(view -> {
                dbRef.child("avatarName").setValue(name);
                d.dismiss();
            });
        }
        d.show();
    }
}
