package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MoviesActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> allMoviesList = new ArrayList<>(); // Lưu bản gốc từ Firebase
    private TextView tvMovieCount;
    private TextView btnAll, btnAction, btnHorror, btnRomance, btnAnimation;

    // Khai báo thêm biến cho tìm kiếm
    private EditText edtSearch;
    private View layoutSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // 1. Ánh xạ View cơ bản
        rvMovies = findViewById(R.id.rvAllMovies);
        tvMovieCount = findViewById(R.id.tvMovieCount);

        // 2. Ánh xạ các nút lọc
        btnAll = findViewById(R.id.btnGenreAll);
        btnAction = findViewById(R.id.btnGenreAction);
        btnHorror = findViewById(R.id.btnGenreHorror);
        btnRomance = findViewById(R.id.btnGenreRomance);
        btnAnimation = findViewById(R.id.btnGenreAnimation);

        // 3. Ánh xạ view tìm kiếm
        edtSearch = findViewById(R.id.edtSearchMovies);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);

        // 4. Lấy dữ liệu từ Firebase
        fetchMoviesFromFirebase();

        // 5. Thiết lập Menu
        setupBottomNavigation();

        // 6. Thiết lập logic tìm kiếm
        setupSearchLogic();
    }

    private void setupSearchLogic() {
        // Bấm vào nút kính lúp trên Header để hiện/ẩn thanh tìm kiếm
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            if (layoutSearchBar.getVisibility() == View.GONE) {
                layoutSearchBar.setVisibility(View.VISIBLE);
                edtSearch.requestFocus(); // Tự động mở bàn phím
            } else {
                layoutSearchBar.setVisibility(View.GONE);
                edtSearch.setText(""); // Xóa nội dung search khi đóng
                filterMovies("Tất cả"); // Quay về danh sách ban đầu
            }
        });

        // Lắng nghe sự kiện gõ chữ vào ô Search
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                filterMoviesByName(query);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Hàm loại bỏ dấu tiếng Việt để tìm kiếm linh hoạt
    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll("đ", "d");
    }

    // Hàm lọc phim theo tên (Dùng cho thanh Search)
    private void filterMoviesByName(String query) {
        String normalizedQuery = removeAccents(query).trim();
        List<Movie> filteredList = new ArrayList<>();

        if (normalizedQuery.isEmpty()) {
            filteredList.addAll(allMoviesList);
        } else {
            for (Movie m : allMoviesList) {
                if (m.getTitle() != null) {
                    String normalizedTitle = removeAccents(m.getTitle());
                    if (normalizedTitle.contains(normalizedQuery)) {
                        filteredList.add(m);
                    }
                }
            }
        }
        updateRecyclerView(filteredList);
    }

    private void fetchMoviesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMoviesList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Movie movie = postSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        movie.setId(postSnapshot.getKey());
                        allMoviesList.add(movie);
                    }
                }
                // Hiển thị lần đầu (Tất cả)
                filterMovies("Tất cả");
                // Gán sự kiện click cho các nút thể loại
                setupGenreClickListeners();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MoviesActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String buildMoviePrompt() {
        StringBuilder sb = new StringBuilder();

        sb.append("Bạn là trợ lý của app CineGo.\n");
        sb.append("Chỉ được phép gợi ý phim trong danh sách dưới đây.\n");
        sb.append("Nếu người dùng hỏi phim không có, trả lời: 'Hiện app chưa có phim này.'\n\n");

        for (Movie m : allMoviesList) {
            sb.append("- ").append(m.getTitle());

            if (m.getGenre() != null) {
                sb.append(" (").append(m.getGenre()).append(")");
            }

            sb.append("\n");
        }

        return sb.toString();
    }
    private void setupGenreClickListeners() {
        if (btnAll != null) btnAll.setOnClickListener(v -> filterMovies("Tất cả"));
        if (btnAction != null) btnAction.setOnClickListener(v -> filterMovies("Hành động"));
        if (btnHorror != null) btnHorror.setOnClickListener(v -> filterMovies("Kinh dị"));
        if (btnRomance != null) btnRomance.setOnClickListener(v -> filterMovies("Tình cảm"));
        if (btnAnimation != null) btnAnimation.setOnClickListener(v -> filterMovies("Hoạt hình"));
    }

    private void filterMovies(String genre) {
        List<Movie> filteredList = new ArrayList<>();
        if (genre.equals("Tất cả")) {
            filteredList.addAll(allMoviesList);
        } else {
            for (Movie m : allMoviesList) {
                if (m.getGenre() != null && m.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                    filteredList.add(m);
                }
            }
        }
        updateRecyclerView(filteredList);
        updateButtonStyles(genre);
    }

    // Hàm dùng chung để làm mới danh sách hiển thị
    private void updateRecyclerView(List<Movie> list) {
        if (tvMovieCount != null) {
            tvMovieCount.setText("Tìm thấy " + list.size() + " phim");
        }
        
        // Chỉ thiết lập LayoutManager một lần duy nhất
        if (rvMovies.getLayoutManager() == null) {
            rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        }

        // Tạo adapter mới và gán lại (Để tối ưu hơn bạn nên viết hàm updateData trong Adapter)
        movieAdapter = new MovieAdapter(this, list);
        rvMovies.setAdapter(movieAdapter);
    }

    private void updateButtonStyles(String selectedGenre) {
        int defaultColor = getResources().getColor(R.color.text_primary);
        int activeColor = getResources().getColor(R.color.neon_cyan);

        // 1. Reset tất cả các nút về trạng thái bình thường (màu trắng, chữ không đậm)
        TextView[] buttons = {btnAll, btnAction, btnHorror, btnRomance, btnAnimation};
        for (TextView btn : buttons) {
            if (btn != null) {
                btn.setTextColor(defaultColor);
                btn.setTypeface(null, android.graphics.Typeface.NORMAL);
                // Nếu bạn muốn reset luôn cả background thì thêm dòng dưới:
                // btn.setBackgroundResource(R.drawable.bg_input);
            }
        }

        // 2. Chỉ làm nổi bật cái nút đang được chọn
        if (selectedGenre.equals("Tất cả") && btnAll != null) {
            btnAll.setTextColor(activeColor);
            btnAll.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if (selectedGenre.equals("Hành động") && btnAction != null) {
            btnAction.setTextColor(activeColor);
            btnAction.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if (selectedGenre.equals("Kinh dị") && btnHorror != null) {
            btnHorror.setTextColor(activeColor);
            btnHorror.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if (selectedGenre.equals("Tình cảm") && btnRomance != null) {
            btnRomance.setTextColor(activeColor);
            btnRomance.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if (selectedGenre.equals("Hoạt hình") && btnAnimation != null) {
            btnAnimation.setTextColor(activeColor);
            btnAnimation.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_movies);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    return true;
                } else if (itemId == R.id.nav_movies) {
                    return true;
                } else if (itemId == R.id.nav_ai_chat) {
                    Intent intent = new Intent(MoviesActivity.this, AiChatActivity.class);
                    intent.putExtra("movie_prompt", buildMoviePrompt());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_notifications) {
                    startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                    return true;
                } else if (itemId == R.id.nav_tickets) {
                    startActivity(new Intent(getApplicationContext(), MyTicketsActivity.class));
                    return true;
                }
                return false;
            });
        }
    }
}
