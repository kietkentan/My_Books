package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.space.mycoffee.databinding.ItemImageViewBinding;
import com.space.mycoffee.listener.PositionListener;

import java.util.List;

public class ImageAdapter extends ListAdapter<String, ImageAdapter.ViewHolder>  {
    private final PositionListener listener;

    public ImageAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback, PositionListener anInterface) {
        super(diffCallback);
        this.listener = anInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageViewBinding itemView = ItemImageViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String item = getItem(position);
        holder.bind(item, position, listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<String> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemImageViewBinding binding;

        public ViewHolder(@NonNull ItemImageViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull String item, int position, PositionListener listener) {
            if (item.contains("content:/"))
                binding.ivImage.setImageURI(Uri.parse(item));
            else Glide.with(binding.ivImage.getContext()).load(item).into(binding.ivImage);
            binding.ivImage.setOnClickListener(view -> listener.onItemClicked(position));
        }
    }

    public static DiffUtil.ItemCallback<String> itemCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };
}
