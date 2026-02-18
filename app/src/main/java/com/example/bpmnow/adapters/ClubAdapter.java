package com.example.bpmnow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.R;
import com.example.bpmnow.models.Club;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    public interface OnClubClickListener {
        void onClubClick(Club club);
    }

    private List<Club> clubs;
    private OnClubClickListener listener;

    public ClubAdapter(List<Club> clubs) {
        this.clubs = clubs;
        this.listener = null;
    }

    public ClubAdapter(List<Club> clubs, OnClubClickListener listener) {
        this.clubs = clubs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club_card, parent, false);
        return new ClubViewHolder(view);
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

    public class ClubViewHolder extends RecyclerView.ViewHolder {
        private TextView name, genre, distance, currentDJ;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.clubNameTextView);
            genre = itemView.findViewById(R.id.clubGenreTextView);
            distance = itemView.findViewById(R.id.tvDistance);
            currentDJ = itemView.findViewById(R.id.currentDJTextView);
        }

        public void bind(Club club) {
            name.setText(club.getName());
            genre.setText(String.join(", ", club.getGenres()));
            distance.setText(club.getDistance());
            currentDJ.setText(club.getCurrentDJ());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClubClick(club);
            });
        }
    }
}
