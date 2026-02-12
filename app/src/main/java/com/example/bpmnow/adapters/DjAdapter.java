package com.example.bpmnow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.R;
import com.example.bpmnow.models.Club;
import com.example.bpmnow.models.Dj;

import java.util.List;

public class DjAdapter extends RecyclerView.Adapter<DjAdapter.DjViewHolder> {
    private List<Dj> DJs;

    public DjAdapter(List<Dj> DJs) {
        this.DJs = DJs;
    }

    @NonNull
    @Override
    public DjViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dj_card, parent, false);
        return new DjViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DjViewHolder holder, int position) {
        holder.bind(DJs.get(position));
    }

    @Override
    public int getItemCount() {
        return DJs.size();
    }

    public void updateData(List<Dj> newItems) {
        this.DJs = newItems;
    }

    public static class DjViewHolder extends RecyclerView.ViewHolder {
        private TextView stageName, genre;

        public DjViewHolder(@NonNull View itemView) {
            super(itemView);
            stageName = itemView.findViewById(R.id.djNameTextView);
            genre = itemView.findViewById(R.id.djGenreTextView);
        }

        public void bind(Dj Dj) {
            stageName.setText(Dj.getStageName());
            genre.setText(Dj.getGenres().toString());
        }


    }
}
