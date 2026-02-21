package com.example.bpmnow.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmnow.R;
import com.example.bpmnow.models.clubber.DjCardItem;
import com.example.bpmnow.models.dj.Dj;
import com.example.bpmnow.utils.ImageUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ClubberDjAdapter extends RecyclerView.Adapter<ClubberDjAdapter.DjViewHolder> {

    public interface OnDjClickListener {
        void onDjClick(DjCardItem dj);
    }

    private List<DjCardItem> DJs;
    private OnDjClickListener listener;

    public ClubberDjAdapter(List<DjCardItem> DJs) {
        this.DJs = DJs;
        this.listener = null;
    }

    public ClubberDjAdapter(List<DjCardItem> DJs, OnDjClickListener listener) {
        this.DJs = DJs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DjViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clubber_dj_card, parent, false);
        return new DjViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DjViewHolder holder, int position) {
        holder.bind(DJs.get(position));
    }

    @Override
    public int getItemCount() {
        return DJs.size();
    }

    public void updateData(List<DjCardItem> newItems) {
        this.DJs = newItems;
        notifyDataSetChanged();
    }

    public static class DjViewHolder extends RecyclerView.ViewHolder {
        private TextView stageName, genre;
        private ShapeableImageView ivDjAvatar;
        private OnDjClickListener listener;
        public DjViewHolder(@NonNull View itemView, OnDjClickListener listener) {
            super(itemView);
            stageName = itemView.findViewById(R.id.djNameTextView);
            genre = itemView.findViewById(R.id.djGenreTextView);
            ivDjAvatar = itemView.findViewById(R.id.ivDjAvatar);
            this.listener = listener;
        }

        public void bind(DjCardItem dj) {
            stageName.setText(dj.getStageName());
            if (dj.getGenres() != null) {
                genre.setText(String.join(", ", dj.getGenres()));
            }

            // Load profile image from Base64
            String base64 = dj.getProfileImageBase64();
            if (base64 != null && !base64.isEmpty()) {
                Bitmap bitmap = ImageUtils.base64ToBitmap(base64);
                if (bitmap != null) {
                    ivDjAvatar.setImageBitmap(bitmap);
                }
            } else {
                ivDjAvatar.setImageResource(R.drawable.ic_default_avatar);
            }

//            itemView is the whole item itself, and we set an onClickListener on it(the legacy method) and we choosing that upon click we will call the "onDjClick"
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDjClick(dj);
            });
        }
    }
}
