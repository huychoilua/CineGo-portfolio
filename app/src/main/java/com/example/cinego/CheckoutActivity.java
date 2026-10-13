package com.example.cinego;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvMovieName, tvCinema, tvDateTime, tvSeats, tvSnacksName;
    private TextView tvTempTicket, tvSnackPrice, tvDiscount, tvFinalTotal;
    private View layoutZalo, layoutMoMo, layoutCard;
    private ImageView imgPoster;

    private long ticketPrice = 0, snackPrice = 0, finalTotal = 0;
    private String selectedMethod = "ZaloPay";

    // Biến hứng dữ liệu thật từ Intent
    private String realDate, realTime, movieName, posterUrl, genre, selectedCinema, snackDetails;
    private ArrayList<String> selectedSeatsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        anhXaView();
        nhanVaHienThiData();
        setupPaymentSelection();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Áp dụng mã giảm giá
        findViewById(R.id.btnApplyPromo).setOnClickListener(v -> {
            EditText edt = findViewById(R.id.edtPromoCode);
            if (edt.getText().toString().equalsIgnoreCase("CINEGO")) {
                long discount = 20000;
                tvDiscount.setText("- " + String.format("%,d đ", discount));
                tvFinalTotal.setText(String.format("%,d đ", finalTotal - discount));
                Toast.makeText(this, "Đã giảm 20k!", Toast.LENGTH_SHORT).show();
            }
        });

        // XÁC NHẬN THANH TOÁN (BẢN HOÀN THIỆN)
        findViewById(R.id.btnConfirmPayment).setOnClickListener(v -> {
            String currentUserId = FirebaseAuth.getInstance().getUid();
            if (currentUserId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference dbRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("booked_tickets").child(currentUserId);

            String ticketId = dbRef.push().getKey();
            String fullDateTime = realDate + " • " + realTime;

            // Tạo đối tượng vé với dữ liệu THẬT
            Ticket ticket = new Ticket(
                    ticketId,
                    movieName,
                    selectedCinema,
                    fullDateTime,
                    tvSeats.getText().toString().replace("Ghế: ", ""),
                    snackDetails,
                    tvFinalTotal.getText().toString(),
                    posterUrl,
                    genre,
                    System.currentTimeMillis()
            );

            if (ticketId != null) {
                dbRef.child(ticketId).setValue(ticket).addOnSuccessListener(aVoid -> {

                    // TẠO THÔNG BÁO HỆ THỐNG
                    DatabaseReference notiRef = FirebaseDatabase.getInstance("https://YOUR_PROJECT-default-rtdb.asia-southeast1.firebasedatabase.app")
                            .getReference("notifications").child(currentUserId);
                    String notiId = notiRef.push().getKey();
                    if (notiId != null) {
                        Notification bookingNoti = new Notification(
                                notiId,
                                "Đặt vé thành công! 🎉",
                                "Bạn đã đặt thành công vé phim " + movieName + ". Xem chi tiết tại mục Vé của tôi nhé!",
                                "SYSTEM",
                                System.currentTimeMillis(),
                                false
                        );
                        notiRef.child(notiId).setValue(bookingNoti);
                    }

                    Toast.makeText(this, "Thanh toán thành công qua " + selectedMethod, Toast.LENGTH_LONG).show();

                    // CHUYỂN SANG TRANG VÉ ĐIỆN TỬ
                    Intent intent = new Intent(this, TicketActivity.class);
                    intent.putExtra("movieName", movieName);
                    intent.putExtra("selectedCinema", selectedCinema);
                    intent.putExtra("selectedDate", realDate);
                    intent.putExtra("selectedTime", realTime);
                    intent.putExtra("seats", tvSeats.getText().toString());
                    intent.putExtra("snackDetails", tvSnacksName.getText().toString());
                    intent.putExtra("posterUrl", posterUrl);
                    intent.putExtra("ticketId", ticketId);

                    startActivity(intent);
                    finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void anhXaView() {
        tvMovieName = findViewById(R.id.tvMovieNameCheckout);
        tvCinema = findViewById(R.id.tvCinemaCheckout);
        tvDateTime = findViewById(R.id.tvDateTimeCheckout);
        tvSeats = findViewById(R.id.tvSeatsCheckout);
        tvSnacksName = findViewById(R.id.tvSnacksNameCheckout);
        tvTempTicket = findViewById(R.id.tvTotalPrice);
        tvSnackPrice = findViewById(R.id.tvSnackPriceCheckout);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        imgPoster = findViewById(R.id.imgMoviePosterCheckout);
        layoutZalo = findViewById(R.id.layoutZaloPay);
        layoutMoMo = findViewById(R.id.layoutMoMo);
        layoutCard = findViewById(R.id.layoutCreditCard);
    }

    private void setupPaymentSelection() {
        View.OnClickListener paymentListener = v -> {
            layoutZalo.setBackgroundResource(R.drawable.bg_input);
            layoutMoMo.setBackgroundResource(R.drawable.bg_input);
            layoutCard.setBackgroundResource(R.drawable.bg_input);
            v.setBackgroundResource(R.drawable.bg_card_3d);
            if (v.getId() == R.id.layoutZaloPay) selectedMethod = "ZaloPay";
            else if (v.getId() == R.id.layoutMoMo) selectedMethod = "MoMo";
            else selectedMethod = "Thẻ ngân hàng";
        };
        layoutZalo.setOnClickListener(paymentListener);
        layoutMoMo.setOnClickListener(paymentListener);
        layoutCard.setOnClickListener(paymentListener);
    }

    private void nhanVaHienThiData() {
        Intent it = getIntent();

        // Hứng toàn bộ dữ liệu vào biến Class để dùng cho nút Thanh toán
        ticketPrice = it.getLongExtra("totalPrice", 0);
        snackPrice = it.getLongExtra("snackPrice", 0);
        snackDetails = it.getStringExtra("snackDetails");
        finalTotal = ticketPrice + snackPrice;

        movieName = it.getStringExtra("movieName");
        selectedCinema = it.getStringExtra("selectedCinema");
        realDate = it.getStringExtra("selectedDate");
        realTime = it.getStringExtra("selectedTime");
        posterUrl = it.getStringExtra("posterUrl");
        genre = it.getStringExtra("genre");
        selectedSeatsList = it.getStringArrayListExtra("selectedSeats");

        // Hiển thị lên giao diện
        if (movieName != null) tvMovieName.setText(movieName);
        else tvMovieName.setText("Lỗi nhận tên phim");

        tvCinema.setText(selectedCinema);
        tvDateTime.setText(realDate + " • " + realTime);

        if (selectedSeatsList != null) {
            tvSeats.setText("Ghế: " + android.text.TextUtils.join(", ", selectedSeatsList));
        }

        tvSnacksName.setText("Combo: " + (snackDetails != null ? snackDetails : "Không mua"));
        tvTempTicket.setText(String.format("%,d đ", ticketPrice));
        tvSnackPrice.setText(String.format("%,d đ", snackPrice));
        tvFinalTotal.setText(String.format("%,d đ", finalTotal));

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(imgPoster);
        }
    }
}
