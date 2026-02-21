package com.example.bpmnow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bpmnow.R;
import com.example.bpmnow.models.clubber.Club;

import java.util.List;

public class ClubberClubAdapter extends RecyclerView.Adapter<ClubberClubAdapter.ClubViewHolder> {

    public interface OnClubClickListener {
        void onClubClick(Club club);
    }

    private List<Club> clubs;
    private OnClubClickListener listener;

    public ClubberClubAdapter(List<Club> clubs) {
        this.clubs = clubs;
        this.listener = null;
    }

    public ClubberClubAdapter(List<Club> clubs, OnClubClickListener listener) {
        this.clubs = clubs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clubber_club_card, parent, false);
        return new ClubViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        holder.bind(clubs.get(position));
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public void updateData(List<Club> newItems) {
        this.clubs = newItems;
        notifyDataSetChanged();
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivClubImage;
        private TextView name, genre, distance, currentDJ;
        private OnClubClickListener listener;

        public ClubViewHolder(@NonNull View itemView, OnClubClickListener listener) {
            super(itemView);
            ivClubImage = itemView.findViewById(R.id.ivClubImage);
            name = itemView.findViewById(R.id.clubNameTextView);
            genre = itemView.findViewById(R.id.clubGenreTextView);
            distance = itemView.findViewById(R.id.tvDistance);
            currentDJ = itemView.findViewById(R.id.currentDJTextView);
            this.listener = listener;
        }

        public void bind(Club club) {
            name.setText(club.getName());
            genre.setText(String.join(", ", club.getGenres()));
            distance.setText(club.getDistance());
            currentDJ.setText(club.getCurrentDJ());

            String imageUrl = club.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ImageViewCompat.setImageTintList(ivClubImage, null);
                ivClubImage.setPadding(0, 0, 0, 0);
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_headphones)
                        .error(R.drawable.ic_headphones)
                        .centerCrop()
                        .into(ivClubImage);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClubClick(club);
            });
        }
    }
}
