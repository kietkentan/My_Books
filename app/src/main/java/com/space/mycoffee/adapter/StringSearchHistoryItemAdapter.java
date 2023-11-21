package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemStringSearchHistoryBinding;
import com.space.mycoffee.listener.HistoryStringListener;

import java.util.List;

public class StringSearchHistoryItemAdapter extends ListAdapter<String, StringSearchHistoryItemAdapter.ViewHolder> {
    private final HistoryStringListener listener;

    public StringSearchHistoryItemAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback, HistoryStringListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStringSearchHistoryBinding itemView = ItemStringSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final ItemStringSearchHistoryBinding binding;
        public ViewHolder(@NonNull ItemStringSearchHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String item, int position, HistoryStringListener listener) {
            binding.setText(item);
            binding.ibRemoveStringSearch.setOnClickListener(view -> listener.itemRemoveClicked(position));
            binding.viewStringItem.setOnClickListener(view -> listener.itemClicked(item));
            binding.executePendingBindings();
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
