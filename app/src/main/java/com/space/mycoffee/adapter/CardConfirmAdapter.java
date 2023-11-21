package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemCartConfirmBinding;
import com.space.mycoffee.model.Order;

import java.util.List;

public class CardConfirmAdapter extends ListAdapter<Order, CardConfirmAdapter.ViewHolder> {
    public CardConfirmAdapter(@NonNull DiffUtil.ItemCallback<Order> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartConfirmBinding itemView = ItemCartConfirmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order item = getItem(position);
        holder.bind(item);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<Order> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartConfirmBinding binding;
        public ViewHolder(@NonNull ItemCartConfirmBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order item) {
            binding.setOrder(item);
        }
    }
}
