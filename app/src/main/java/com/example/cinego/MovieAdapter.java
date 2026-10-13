package com.example.cinego;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);//lấy phim ở vị trí này ra xem. trong đối tợng movie có sẳn tên, ảnh ID....
        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieRating.setText(String.valueOf(movie.getRating()));
        holder.tvMovieGenre.setText(movie.getGenre());

        Glide.with(context).load(movie.getPosterUrl()).into(holder.imgPosterMovie);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            // Gửi ID để lấy dữ liệu chi tiết
            intent.putExtra("MOVIE_ID", movie.getId());

            // GỬI THÊM TÊN VÀ ẢNH ĐỂ TIẾP SỨC CHO CÁC MÀN HÌNH SAU
            intent.putExtra("movieName", movie.getTitle());
            intent.putExtra("posterUrl", movie.getPosterUrl());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return movieList != null ? movieList.size() : 0; }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPosterMovie;
        TextView tvMovieTitle, tvMovieRating, tvMovieGenre;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPosterMovie = itemView.findViewById(R.id.imgPoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = itemView.findViewById(R.id.tvRating);
            tvMovieGenre = itemView.findViewById(R.id.tvGenre);
        }
    }
}