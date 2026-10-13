package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class TicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // 1. Ánh xạ các thành phần giao diện
        TextView tvName = findViewById(R.id.tvMovieNameTicketDetail);
        TextView tvCinema = findViewById(R.id.tvCinemaTicketDetail);
        TextView tvDate = findViewById(R.id.tvDateTicketDetail);
        TextView tvTime = findViewById(R.id.tvTimeTicketDetail);
        TextView tvSeats = findViewById(R.id.tvSeatsTicketDetail);
        TextView tvSnacks = findViewById(R.id.tvSnacksTicketDetail);
        TextView tvCode = findViewById(R.id.tvTicketCode);
        ImageView imgPoster = findViewById(R.id.imgTicketPoster);

        // 2. Nhận dữ liệu từ CheckoutActivity gửi sang
        Intent intent = getIntent();
        if (intent != null) {
            tvName.setText(intent.getStringExtra("movieName"));
            tvCinema.setText(intent.getStringExtra("selectedCinema"));
            tvDate.setText(intent.getStringExtra("selectedDate"));
            tvTime.setText(intent.getStringExtra("selectedTime"));
            tvSeats.setText(intent.getStringExtra("seats"));
            tvSnacks.setText(intent.getStringExtra("snackDetails"));

            // Tạo mã vé giả định dựa trên ID Firebase
            String ticketId = intent.getStringExtra("ticketId");
            if (ticketId != null && ticketId.length() > 10) {
                tvCode.setText("MÃ VÉ: TKT-" + ticketId.substring(1, 10).toUpperCase());
            }

            // Nạp ảnh Poster phim
            String posterUrl = intent.getStringExtra("posterUrl");
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(this).load(posterUrl).into(imgPoster);
            }
        }

        // 3. Xử lý các nút bấm
        findViewById(R.id.btnBackToHome).setOnClickListener(v -> {
            // Quay về trang chủ và dọn dẹp các màn hình cũ
            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        });

        findViewById(R.id.btnClose).setOnClickListener(v -> {
            finish(); // Đóng màn hình này
        });
    }
}