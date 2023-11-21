package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemStringSearchBinding;
import com.space.mycoffee.listener.PositionListener;

import java.util.List;

public class StringSearchItemAdapter extends ListAdapter<String, StringSearchItemAdapter.ViewHolder> {
    private final PositionListener listener;

    public StringSearchItemAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback, PositionListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStringSearchBinding itemView = ItemStringSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        private final ItemStringSearchBinding binding;
        public ViewHolder(@NonNull ItemStringSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String item, int position, PositionListener listener) {
            binding.setText(item);
            binding.tvStringSearch.setOnClickListener(view -> listener.onItemClicked(position));
            binding.executePendingBindings();
        }
    }
}
