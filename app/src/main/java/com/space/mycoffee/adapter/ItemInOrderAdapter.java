package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemInOrderRequestBinding;
import com.space.mycoffee.listener.CartListener;
import com.space.mycoffee.model.Order;

import java.util.List;

public class ItemInOrderAdapter extends ListAdapter<Order, ItemInOrderAdapter.ViewHolder> {
    private final CartListener listener;

    public ItemInOrderAdapter(@NonNull DiffUtil.ItemCallback<Order> diffCallback, CartListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInOrderRequestBinding itemView = ItemInOrderRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order item = getItem(position);
        holder.bind(item, listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<Order> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemInOrderRequestBinding binding;
        public ViewHolder(@NonNull ItemInOrderRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order item, CartListener listener) {
            binding.setOrder(item);
            binding.layoutBookInOrder.setOnClickListener(view ->
                    listener.onItemViewChecked(item.getIdCoffee())
            );
        }
    }
}
