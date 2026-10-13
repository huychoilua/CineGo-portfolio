package com.example.cinego;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {

    private Context context;
    private List<Notification> list;
    private OnNotiClickListener listener;

    // Định nghĩa Interface để xử lý Click
    public interface OnNotiClickListener {
        void onNotiClick(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> list, OnNotiClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotiViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Notification n = list.get(position);
        if (n == null) return;

        holder.tvTitle.setText(n.getTitle());
        holder.tvContent.setText(n.getContent());

        // Định dạng thời gian
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(n.getTimestamp())));

        // KIỂM TRA TRẠNG THÁI ĐÃ ĐỌC
        if (n.isRead()) {
            // Nếu đã đọc: ẩn chấm xanh, làm mờ chữ
            holder.unreadDot.setVisibility(View.GONE);
            holder.tvTitle.setAlpha(0.6f);
            holder.tvContent.setAlpha(0.6f);
        } else {
            // Nếu chưa đọc: hiện chấm xanh, chữ rõ nét
            holder.unreadDot.setVisibility(View.VISIBLE);
            holder.tvTitle.setAlpha(1.0f);
            holder.tvContent.setAlpha(1.0f);
        }

        // Bắt sự kiện nhấn giữ (Long Click) để xóa từng thông báo
        holder.itemView.setOnLongClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Xóa thông báo này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Gọi Firebase để xóa đúng cái ID này
                        FirebaseDatabase.getInstance().getReference("notifications")
                                .child(com.google.firebase.auth.FirebaseAuth.getInstance().getUid())
                                .child(n.getId()).removeValue();

                        Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });

        // Bắt sự kiện Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotiClick(n);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;
        View unreadDot;

        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotiTitle);
            tvContent = itemView.findViewById(R.id.tvNotiContent);
            tvTime = itemView.findViewById(R.id.tvNotiTime);
            unreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}