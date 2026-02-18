package com.example.bpmnow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.R;
import com.example.bpmnow.models.Dj;

import java.util.List;

public class DjAdapter extends RecyclerView.Adapter<DjAdapter.DjViewHolder> {

    public interface OnDjClickListener {
        void onDjClick(Dj dj);
    }

    private List<Dj> DJs;
    private OnDjClickListener listener;

    public DjAdapter(List<Dj> DJs) {
        this.DJs = DJs;
        this.listener = null;
    }

    public DjAdapter(List<Dj> DJs, OnDjClickListener listener) {
        this.DJs = DJs;
        this.listener = listener;
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
        notifyDataSetChanged();
    }

    public class DjViewHolder extends RecyclerView.ViewHolder {
        private TextView stageName, genre;

        public DjViewHolder(@NonNull View itemView) {
            super(itemView);
            stageName = itemView.findViewById(R.id.djNameTextView);
            genre = itemView.findViewById(R.id.djGenreTextView);
        }

        public void bind(Dj dj) {
            stageName.setText(dj.getStageName());
            if (dj.getGenres() != null) {
                genre.setText(String.join(", ", dj.getGenres()));
            }
//            itemView is the whole item itself, and we set an onClickListener on it(the legacy method) and we choosing that upon click we will call the "onDjClick"
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDjClick(dj);
            });
        }
    }
}
