package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.RecyclerViewClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.PublisherItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PublisherItemAdapter extends RecyclerView.Adapter<PublisherItemAdapter.ViewHolder> {
    private List<PublisherItem> publisherList;
    private RecyclerViewClickInterface recyclerViewClickInterface;

    public PublisherItemAdapter(List<PublisherItem> publisherList, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.publisherList = publisherList;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.publisher_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(publisherList.get(position).getLogo()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return publisherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_publisher);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClickInterface.OnItemClick(publisherList.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}
