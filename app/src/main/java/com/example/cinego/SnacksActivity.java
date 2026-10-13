package com.example.cinego;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SnacksActivity extends AppCompatActivity {

    private TextView tvGrandTotal, tvQtyVIP, tvQtyCouple, tvQtyCheese, tvQtyDrink;
    private View itemVIP, itemCouple, itemPopcorn, itemDrink;
    private TextView btnFiltCombo, btnFiltPop, btnFiltDrink;

    private long ticketPrice = 0, totalSnacks = 0;
    private final int P_VIP = 120000, P_COUPLE = 95000, P_CHEESE = 65000, P_DRINK = 40000;
    private int qVIP = 0, qCouple = 0, qCheese = 0, qDrink = 0;

    private String movieName, posterUrl, selectedDate, selectedTime, selectedCinema;
    private ArrayList<String> selectedSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snacks);

        nhanDuLieu();
        anhXaView();
        setupClickListeners();
        updateUI();
    }

    private void nhanDuLieu() {
        Intent intent = getIntent();
        movieName = intent.getStringExtra("movieName");
        posterUrl = intent.getStringExtra("posterUrl");
        selectedDate = intent.getStringExtra("selectedDate");
        selectedTime = intent.getStringExtra("selectedTime");
        selectedCinema = intent.getStringExtra("selectedCinema");
        ticketPrice = intent.getLongExtra("totalPrice", 0);
        selectedSeats = intent.getStringArrayListExtra("selectedSeats");
    }

    private void anhXaView() {
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvQtyVIP = findViewById(R.id.tvQtyVIP);
        tvQtyCouple = findViewById(R.id.tvQtyCouple);
        tvQtyCheese = findViewById(R.id.tvQtyCheese);
        tvQtyDrink = findViewById(R.id.tvQtyDrink);

        itemVIP = findViewById(R.id.itemComboVIP);
        itemCouple = findViewById(R.id.itemComboCouple);
        itemPopcorn = findViewById(R.id.itemPopcorn);
        itemDrink = findViewById(R.id.itemDrink);

        btnFiltCombo = findViewById(R.id.btnFilterCombo);
        btnFiltPop = findViewById(R.id.btnFilterPopcorn);
        btnFiltDrink = findViewById(R.id.btnFilterDrink);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinueToCheckout).setOnClickListener(v -> diDenThanhToan());
        findViewById(R.id.tvSkip).setOnClickListener(v -> {
            qVIP = 0;
            qCouple = 0;
            qCheese = 0;
            qDrink = 0;
            diDenThanhToan();
        });
    }

    private void setupClickListeners() {
        // Nút cộng trừ
        findViewById(R.id.btnPlusVIP).setOnClickListener(v -> {
            qVIP++;
            updateUI();
        });
        findViewById(R.id.btnMinusVIP).setOnClickListener(v -> {
            if (qVIP > 0) qVIP--;
            updateUI();
        });

        findViewById(R.id.btnPlusCouple).setOnClickListener(v -> {
            qCouple++;
            updateUI();
        });
        findViewById(R.id.btnMinusCouple).setOnClickListener(v -> {
            if (qCouple > 0) qCouple--;
            updateUI();
        });

        findViewById(R.id.btnPlusCheese).setOnClickListener(v -> {
            qCheese++;
            updateUI();
        });
        findViewById(R.id.btnMinusCheese).setOnClickListener(v -> {
            if (qCheese > 0) qCheese--;
            updateUI();
        });

        findViewById(R.id.btnPlusDrink).setOnClickListener(v -> {
            qDrink++;
            updateUI();
        });
        findViewById(R.id.btnMinusDrink).setOnClickListener(v -> {
            if (qDrink > 0) qDrink--;
            updateUI();
        });

        // Nút lọc
        btnFiltCombo.setOnClickListener(v -> filterSnacks("COMBO"));
        btnFiltPop.setOnClickListener(v -> filterSnacks("POP"));
        btnFiltDrink.setOnClickListener(v -> filterSnacks("DRINK"));
    }

    private void filterSnacks(String category) {
        // Reset màu nút lọc
        btnFiltCombo.setBackgroundResource(R.drawable.bg_input);
        btnFiltCombo.setTextColor(Color.WHITE);
        btnFiltPop.setBackgroundResource(R.drawable.bg_input);
        btnFiltPop.setTextColor(Color.WHITE);
        btnFiltDrink.setBackgroundResource(R.drawable.bg_input);
        btnFiltDrink.setTextColor(Color.WHITE);

        // Ẩn tất cả trước
        itemVIP.setVisibility(View.GONE);
        itemCouple.setVisibility(View.GONE);
        itemPopcorn.setVisibility(View.GONE);
        itemDrink.setVisibility(View.GONE);

        // Hiện theo loại và đổi màu nút được chọn
        if (category.equals("COMBO")) {
            itemVIP.setVisibility(View.VISIBLE);
            itemCouple.setVisibility(View.VISIBLE);
            btnFiltCombo.setBackgroundResource(R.drawable.bg_neon_button);
            btnFiltCombo.setTextColor(Color.parseColor("#0B0C10"));
        } else if (category.equals("POP")) {
            itemPopcorn.setVisibility(View.VISIBLE);
            btnFiltPop.setBackgroundResource(R.drawable.bg_neon_button);
            btnFiltPop.setTextColor(Color.parseColor("#0B0C10"));
        } else if (category.equals("DRINK")) {
            itemDrink.setVisibility(View.VISIBLE);
            btnFiltDrink.setBackgroundResource(R.drawable.bg_neon_button);
            btnFiltDrink.setTextColor(Color.parseColor("#0B0C10"));
        }
    }

    private void updateUI() {
        tvQtyVIP.setText(String.valueOf(qVIP));
        tvQtyCouple.setText(String.valueOf(qCouple));
        tvQtyCheese.setText(String.valueOf(qCheese));
        tvQtyDrink.setText(String.valueOf(qDrink));
        totalSnacks = (long) qVIP * P_VIP + (long) qCouple * P_COUPLE + (long) qCheese * P_CHEESE + (long) qDrink * P_DRINK;
        tvGrandTotal.setText(String.format("%,d đ", ticketPrice + totalSnacks));
    }

    private void diDenThanhToan() {
        Intent it = new Intent(this, CheckoutActivity.class);
        it.putExtras(getIntent().getExtras()); // Gửi data phim/ghế

        // Tạo chuỗi mô tả bắp nước
        StringBuilder details = new StringBuilder();
        if (qVIP > 0) details.append(qVIP).append(" Combo VIP, ");
        if (qCouple > 0) details.append(qCouple).append(" Combo Couple, ");
        if (qCheese > 0) details.append(qCheese).append(" Bắp Phô Mai, ");
        if (qDrink > 0) details.append(qDrink).append(" Coca Cola, ");

        String finalDetails = details.toString();
        if (finalDetails.endsWith(", "))
            finalDetails = finalDetails.substring(0, finalDetails.length() - 2);
        it.putExtra("selectedDate", selectedDate); // GỬI TIẾP NGÀY THẬT ĐI
        it.putExtra("movieName", movieName);
        it.putExtra("posterUrl", posterUrl);
        it.putExtra("snackPrice", totalSnacks);
        it.putExtra("totalPrice", ticketPrice);
        it.putExtra("snackDetails", finalDetails.isEmpty() ? "Không mua" : finalDetails);
        it.putExtra("genre", getIntent().getStringExtra("genre"));
        startActivity(it);
    }
}