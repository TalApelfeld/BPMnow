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
    private List<Club> clubs;

    public ClubAdapter(List<Club> clubs) {
        this.clubs = clubs;
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

    // Method to update data
    public void updateData(List<Club> newItems) {
        this.clubs = newItems;
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        private TextView name, genre, distance, currentDJ;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.clubNameTextView);
            genre = itemView.findViewById(R.id.clubGenreTextView);
            distance = itemView.findViewById(R.id.tvDistance);
            currentDJ = itemView.findViewById(R.id.currentDJTextView);
        }

        //        Used in the "onBindViewHolder()"
        public void bind(Club club) {
            name.setText(club.getName());
            genre.setText(club.getGenres().toString());
            distance.setText(club.getDistance());
            currentDJ.setText(club.getCurrentDJ());

        }

    }

}
