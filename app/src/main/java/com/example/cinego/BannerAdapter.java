package com.example.cinego;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Integer> bannerList;

    public BannerAdapter(List<Integer> bannerList) {
        this.bannerList = bannerList;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // Thủ thuật lặp vô tận: Dùng phép chia lấy dư (%)
        int realPosition = position % bannerList.size();
        holder.imgBanner.setImageResource(bannerList.get(realPosition));
    }

    @Override
    public int getItemCount() {
        // Trả về một con số cực lớn để vuốt mỏi tay không hết
        return Integer.MAX_VALUE;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}