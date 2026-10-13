package com.example.cinego;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> ticketList;

    public TicketAdapter(Context context, List<Ticket> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo file layout là item_ticket_list.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_list, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) { // Thêm 'int position' ở đây
        Ticket ticket = ticketList.get(position);

        // Đổ dữ liệu chữ
        if (ticket != null) {
            holder.tvMovieName.setText(ticket.getMovieName());
            holder.tvCinema.setText(ticket.getCinemaName());
            holder.tvDateTime.setText(ticket.getDateTime());
            holder.tvSeats.setText("Ghế: " + ticket.getSeats());

            // Dùng Glide để load ảnh từ URL
            if (ticket.getPosterUrl() != null && !ticket.getPosterUrl().isEmpty()) {
                Glide.with(context)
                        .load(ticket.getPosterUrl())
                        .placeholder(R.drawable.img_bg_login_low)
                        .into(holder.imgPosterTicket);
            } else {
                holder.imgPosterTicket.setImageResource(R.drawable.img_bg_login);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ticketList != null ? ticketList.size() : 0;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName, tvCinema, tvDateTime, tvSeats;
        ImageView imgPosterTicket;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = itemView.findViewById(R.id.tvMovieNameTicket);
            tvCinema = itemView.findViewById(R.id.tvCinemaTicket);
            tvDateTime = itemView.findViewById(R.id.tvDateTimeTicket);
            tvSeats = itemView.findViewById(R.id.tvSeatsTicket);
            imgPosterTicket = itemView.findViewById(R.id.imgPosterTicket);
        }
    }
}