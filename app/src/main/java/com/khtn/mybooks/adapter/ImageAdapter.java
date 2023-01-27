package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.ImageClickInterface;
import com.khtn.mybooks.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{
    private final List<Uri> uriList;
    private final ImageClickInterface anInterface;

    public ImageAdapter(List<Uri> uriList, ImageClickInterface anInterface) {
        this.uriList = uriList;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (uriList.get(position).toString().contains("content:/"))
            holder.iv.setImageURI(uriList.get(position));
        else
            Picasso.get().load(uriList.get(position)).into(holder.iv);

        holder.iv.setOnClickListener(v -> anInterface.onClick(position));
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_image);
        }
    }
}
