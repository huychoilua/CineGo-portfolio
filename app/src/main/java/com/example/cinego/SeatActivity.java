package com.example.cinego;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SeatActivity extends AppCompatActivity {

    private TextView tvSelectedInfo, tvTotalPrice;
    private List<String> selectedSeats = new ArrayList<>();
    private long totalPrice = 0;

    // Biến trung chuyển
    private String movieName, posterUrl, selectedDate, selectedTime, selectedCinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);

        // NHẬN DỮ LIỆU TỪ BOOKINGACTIVITY
        movieName = getIntent().getStringExtra("movieName");
        posterUrl = getIntent().getStringExtra("posterUrl");
        selectedDate = getIntent().getStringExtra("selectedDate");
        selectedTime = getIntent().getStringExtra("selectedTime");
        selectedCinema = getIntent().getStringExtra("selectedCinema");

        tvSelectedInfo = findViewById(R.id.tvSelectedSeatsInfo);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);



        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Thiết lập sự kiện cho các ghế (A, B, M như cũ)
        setupAllSeats();

        // NÚT THANH TOÁN - GỬI TIẾP DỮ LIỆU
        // Trong SeatActivity.java
        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 ghế!", Toast.LENGTH_SHORT).show();
            } else {
                // ĐỔI HƯỚNG SANG SNACKS ACTIVITY
                Intent intent = new Intent(this, SnacksActivity.class);

                // Gửi toàn bộ dữ liệu đi (tiếp sức)
                intent.putExtra("movieName", movieName);
                intent.putExtra("posterUrl", posterUrl);
                intent.putExtra("selectedDate", selectedDate);
                intent.putExtra("selectedTime", selectedTime);
                intent.putExtra("selectedCinema", selectedCinema);
                intent.putExtra("totalPrice", totalPrice); // Đây là tiền ghế
                intent.putStringArrayListExtra("selectedSeats", (ArrayList<String>) selectedSeats);
                intent.putExtra("genre", getIntent().getStringExtra("genre"));
                startActivity(intent);
            }
        });
    }

    private void setupAllSeats() {
        int PRICE_NORMAL = 60000;
        int PRICE_VIP = 90000;
        int PRICE_DOUBLE = 150000;

        setupSeat(findViewById(R.id.seatA1), "A1", PRICE_NORMAL);
        setupSeat(findViewById(R.id.seatA2), "A2", PRICE_NORMAL);
        setupSeat(findViewById(R.id.seatA4), "A4", PRICE_NORMAL);
        setupSeat(findViewById(R.id.seatA5), "A5", PRICE_NORMAL);
        setupSeat(findViewById(R.id.seatA6), "A6", PRICE_NORMAL);

        setupSeat(findViewById(R.id.seatB1), "B1", PRICE_VIP);
        setupSeat(findViewById(R.id.seatB2), "B2", PRICE_VIP);
        setupSeat(findViewById(R.id.seatB3), "B3", PRICE_VIP);
        setupSeat(findViewById(R.id.seatB4), "B4", PRICE_VIP);
        setupSeat(findViewById(R.id.seatB5), "B5", PRICE_VIP);
        setupSeat(findViewById(R.id.seatB6), "B6", PRICE_VIP);

        setupSeat(findViewById(R.id.seatM1), "M1", PRICE_DOUBLE);
        setupSeat(findViewById(R.id.seatM2), "M2", PRICE_DOUBLE);
        setupSeat(findViewById(R.id.seatM3), "M3", PRICE_DOUBLE);
    }

    private void setupSeat(TextView seatView, String seatName, int price) {
        if (seatView == null) return;
        seatView.setOnClickListener(v -> {
            if (selectedSeats.contains(seatName)) {
                selectedSeats.remove(seatName);
                totalPrice -= price;
                seatView.setBackgroundResource(R.drawable.bg_input);
                seatView.setTextColor(Color.WHITE);
                seatView.setBackgroundTintList(null);
            } else {
                selectedSeats.add(seatName);
                totalPrice += price;
                seatView.setBackgroundResource(R.drawable.bg_neon_button);
                seatView.setTextColor(Color.parseColor("#0B0C10"));
                seatView.setBackgroundTintList(null);
            }
            tvSelectedInfo.setText("Tổng cộng (" + selectedSeats.size() + " ghế)");
            tvTotalPrice.setText(String.format("%,d VNĐ", totalPrice));
        });
    }
}