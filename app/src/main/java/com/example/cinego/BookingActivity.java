package com.example.cinego;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private LinearLayout btnDate1, btnDate2, btnDate3;
    private TextView tvDay1, tvDate1, tvDay2, tvDate2, tvDay3, tvDate3;
    private TextView btnTime1, btnTime2, btnTime3;
    private View btnCinema1, btnCinema2, barCinema1, barCinema2;
    private TextView tvCinemaName1, tvCinemaName2;

    private String selectedDate = "";
    private String selectedTime = "13:15"; // Mặc định
    private String selectedCinema = "CineGo Landmark 81"; // Mặc định
    private String movieName = "", posterUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 1. Ánh xạ View
        anhXaView();

        // 2. Nhận dữ liệu phim
        movieName = getIntent().getStringExtra("movieName");
        posterUrl = getIntent().getStringExtra("posterUrl");

        // 3. Thiết lập Ngày thật (Real-time)
        setupDynamicDates();

        // 4. Thiết lập Giờ và Rạp
        setupOtherClickListeners();

        // Nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Nút chuyển sang chọn ghế
        findViewById(R.id.btnNextToSeat).setOnClickListener(v -> {
            Intent intent = new Intent(this, SeatActivity.class);
            intent.putExtra("movieName", movieName);
            intent.putExtra("posterUrl", posterUrl);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("selectedTime", selectedTime);
            intent.putExtra("selectedCinema", selectedCinema);
            intent.putExtra("genre", getIntent().getStringExtra("genre"));
            startActivity(intent);
        });
    }

    private void anhXaView() {
        btnDate1 = findViewById(R.id.btnDate1);
        btnDate2 = findViewById(R.id.btnDate2);
        btnDate3 = findViewById(R.id.btnDate3);
        tvDay1 = findViewById(R.id.tvDay1);
        tvDate1 = findViewById(R.id.tvDate1);
        tvDay2 = findViewById(R.id.tvDay2);
        tvDate2 = findViewById(R.id.tvDate2);
        tvDay3 = findViewById(R.id.tvDay3);
        tvDate3 = findViewById(R.id.tvDate3);
        btnTime1 = findViewById(R.id.btnTime1);
        btnTime2 = findViewById(R.id.btnTime2);
        btnTime3 = findViewById(R.id.btnTime3);
        btnCinema1 = findViewById(R.id.btnCinema1);
        btnCinema2 = findViewById(R.id.btnCinema2);
        barCinema1 = findViewById(R.id.barCinema1);
        barCinema2 = findViewById(R.id.barCinema2);
        tvCinemaName1 = findViewById(R.id.tvCinemaName1);
        tvCinemaName2 = findViewById(R.id.tvCinemaName2);
    }

    private void setupDynamicDates() {
        SimpleDateFormat dateNumberFormat = new SimpleDateFormat("dd/MM", new Locale("vi", "VN"));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("vi", "VN"));

        // Ngày 1: Hôm nay
        Calendar cal1 = Calendar.getInstance();
        final String d1 = "Hôm nay, " + dateNumberFormat.format(cal1.getTime());
        tvDay1.setText("Hôm nay");
        tvDate1.setText(dateNumberFormat.format(cal1.getTime()));
        selectedDate = d1; // Mặc định chọn ngày 1

        // Ngày 2: Ngày mai
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_YEAR, 1);
        final String d2 = dayFormat.format(cal2.getTime()) + ", " + dateNumberFormat.format(cal2.getTime());
        tvDay2.setText(dayFormat.format(cal2.getTime()));
        tvDate2.setText(dateNumberFormat.format(cal2.getTime()));

        // Ngày 3: Ngày kia
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.DAY_OF_YEAR, 2);
        final String d3 = dayFormat.format(cal3.getTime()) + ", " + dateNumberFormat.format(cal3.getTime());
        tvDay3.setText(dayFormat.format(cal3.getTime()));
        tvDate3.setText(dateNumberFormat.format(cal3.getTime()));

        // GÁN SỰ KIỆN CLICK (Gộp chung vào đây cho chuẩn)
        btnDate1.setOnClickListener(v -> selectDateUI(btnDate1, d1));
        btnDate2.setOnClickListener(v -> selectDateUI(btnDate2, d2));
        btnDate3.setOnClickListener(v -> selectDateUI(btnDate3, d3));
    }

    private void selectDateUI(View v, String dateValue) {
        resetDates();
        v.setBackgroundResource(R.drawable.bg_neon_button);
        selectedDate = dateValue; // Cập nhật ngày thật vào biến gửi đi

        // Đổi màu chữ cho ô được chọn
        if (v.getId() == R.id.btnDate1) {
            tvDay1.setTextColor(Color.parseColor("#0B0C10"));
            tvDate1.setTextColor(Color.parseColor("#0B0C10"));
        } else if (v.getId() == R.id.btnDate2) {
            tvDay2.setTextColor(Color.parseColor("#0B0C10"));
            tvDate2.setTextColor(Color.parseColor("#0B0C10"));
        } else {
            tvDay3.setTextColor(Color.parseColor("#0B0C10"));
            tvDate3.setTextColor(Color.parseColor("#0B0C10"));
        }
        Toast.makeText(this, "Đã chọn: " + dateValue, Toast.LENGTH_SHORT).show();
    }

    private void setupOtherClickListeners() {
        // Xử lý chọn Giờ
        View.OnClickListener timeClick = v -> {
            resetTimes();
            v.setBackgroundResource(R.drawable.bg_neon_button);
            TextView tv = (TextView) v;
            tv.setTextColor(Color.parseColor("#0B0C10"));
            selectedTime = tv.getText().toString();
        };
        btnTime1.setOnClickListener(timeClick);
        btnTime2.setOnClickListener(timeClick);
        btnTime3.setOnClickListener(timeClick);

        // Xử lý chọn Rạp
        View.OnClickListener cinemaClick = v -> {
            resetCinemas();
            v.setBackgroundResource(R.drawable.bg_card_3d);
            if (v.getId() == R.id.btnCinema1) {
                selectedCinema = "CineGo Landmark 81";
                barCinema1.setVisibility(View.VISIBLE);
                tvCinemaName1.setTextColor(Color.parseColor("#66FCF1"));
            } else {
                selectedCinema = "CineGo GigaMall";
                barCinema2.setVisibility(View.VISIBLE);
                tvCinemaName2.setTextColor(Color.parseColor("#66FCF1"));
            }
        };
        btnCinema1.setOnClickListener(cinemaClick);
        btnCinema2.setOnClickListener(cinemaClick);
    }

    private void resetDates() {
        btnDate1.setBackgroundResource(R.drawable.bg_input);
        btnDate2.setBackgroundResource(R.drawable.bg_input);
        btnDate3.setBackgroundResource(R.drawable.bg_input);
        int colorNormal = Color.parseColor("#C5C6C7");
        tvDay1.setTextColor(colorNormal);
        tvDate1.setTextColor(Color.WHITE);
        tvDay2.setTextColor(colorNormal);
        tvDate2.setTextColor(Color.WHITE);
        tvDay3.setTextColor(colorNormal);
        tvDate3.setTextColor(Color.WHITE);
    }

    private void resetTimes() {
        btnTime1.setBackgroundResource(R.drawable.bg_input);
        btnTime2.setBackgroundResource(R.drawable.bg_input);
        btnTime3.setBackgroundResource(R.drawable.bg_input);
        btnTime1.setTextColor(Color.WHITE);
        btnTime2.setTextColor(Color.WHITE);
        btnTime3.setTextColor(Color.WHITE);
    }

    private void resetCinemas() {
        btnCinema1.setBackgroundResource(R.drawable.bg_input);
        btnCinema2.setBackgroundResource(R.drawable.bg_input);
        barCinema1.setVisibility(View.INVISIBLE);
        barCinema2.setVisibility(View.INVISIBLE);
        tvCinemaName1.setTextColor(Color.WHITE);
        tvCinemaName2.setTextColor(Color.WHITE);
    }
}