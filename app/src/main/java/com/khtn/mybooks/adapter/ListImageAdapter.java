package com.khtn.mybooks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListImageAdapter extends RecyclerView.Adapter<ListImageAdapter.ViewHolder> {
    private final List<String> images;

    public ListImageAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_images, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListImageAdapter.ViewHolder holder, int position) {
        Picasso.get().load(images.get(position)).into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_list_photo_review);
        }
    }
}
