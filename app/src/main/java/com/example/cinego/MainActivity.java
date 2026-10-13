package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAiSuggested, rvNowPlaying, rvHotMovies, rvUpcomingMovies;
    private ShapeableImageView imgAvatar;
    private ImageView imgSearch;
    private TextView tvViewAll;

    // --- KHAI BÁO CHO BANNER ---
    private ViewPager2 vpBanners;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();
        fetchMoviesFromFirebase();
        setupBanners();
        setupBottomNavigation();
    }

    private void initViews() {
        rvAiSuggested = findViewById(R.id.rvAiSuggested);
        rvNowPlaying = findViewById(R.id.rvNowPlaying);
        rvHotMovies = findViewById(R.id.rvHotMovies);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgSearch = findViewById(R.id.imgSearch);
        tvViewAll = findViewById(R.id.tvViewAllMovies);
        vpBanners = findViewById(R.id.vpBanners);
    }

    private void setupClickListeners() {
        // Mở Profile
        imgAvatar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        // Nhấn Kính lúp hoặc Xem tất cả -> Nhảy sang Tab Phim
        View.OnClickListener goToMovies = v -> {
            startActivity(new Intent(MainActivity.this, MoviesActivity.class));
            overridePendingTransition(0, 0);
        };
        imgSearch.setOnClickListener(goToMovies);
        if (tvViewAll != null) tvViewAll.setOnClickListener(goToMovies);
    }

    private void fetchMoviesFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("movies");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> movieList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Movie movie = postSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        movie.setId(postSnapshot.getKey());
                        movieList.add(movie);
                    }
                }

                // Nạp dữ liệu vào các danh sách khác nhau
                setupRecyclerView(rvAiSuggested, movieList);

                List<Movie> list2 = new ArrayList<>(movieList);
                Collections.shuffle(list2);
                setupRecyclerView(rvNowPlaying, list2);

                List<Movie> list3 = new ArrayList<>(movieList);
                Collections.shuffle(list3);
                setupRecyclerView(rvHotMovies, list3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBanners() {
        List<Integer> bannerList = new ArrayList<>();
        bannerList.add(R.drawable.img_banner_01);
        bannerList.add(R.drawable.img_banner_02);
        bannerList.add(R.drawable.img_banner_03);
        bannerList.add(R.drawable.img_banner_04);
        bannerList.add(R.drawable.img_banner_05);

        BannerAdapter bannerAdapter = new BannerAdapter(bannerList);
        vpBanners.setAdapter(bannerAdapter);
        vpBanners.setCurrentItem(bannerList.size() * 1000, false);

        vpBanners.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private Runnable sliderRunnable = () -> vpBanners.setCurrentItem(vpBanners.getCurrentItem() + 1);

    private void setupRecyclerView(RecyclerView recyclerView, List<Movie> list) {
        if (recyclerView == null) return;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new MovieAdapter(this, list));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) return true;

                Intent intent = null;
                if (id == R.id.nav_movies) intent = new Intent(this, MoviesActivity.class);
                else if (id == R.id.nav_ai_chat) intent = new Intent(this, AiChatActivity.class);
                else if (id == R.id.nav_notifications)
                    intent = new Intent(this, NotificationsActivity.class);
                else if (id == R.id.nav_tickets) intent = new Intent(this, MyTicketsActivity.class);

                if (intent != null) {
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}
