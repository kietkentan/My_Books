package com.space.mycoffee.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemCoffeeBinding;
import com.space.mycoffee.listener.CoffeeItemListener;
import com.space.mycoffee.model.CoffeeItem;

import java.util.Objects;

public class CoffeeItemAdapter extends ListAdapter<CoffeeItem, CoffeeItemAdapter.ViewHolder> {
    private final CoffeeItemListener coffeeItemListener;

    public CoffeeItemAdapter(@NonNull DiffUtil.ItemCallback<CoffeeItem> diffCallback, CoffeeItemListener listener) {
        super(diffCallback);
        this.coffeeItemListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCoffeeBinding itemView = ItemCoffeeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoffeeItem item = getItem(position);
        holder.bind(item, coffeeItemListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCoffeeBinding binding;
        public ViewHolder(@NonNull ItemCoffeeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CoffeeItem item, CoffeeItemListener listener) {
            binding.setCoffee(item);
            if (item.getDiscount() >= 0){
                binding.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            binding.csItemCoffee.setOnClickListener(view -> listener.onItemClicked(item));
        }
    }

    public static DiffUtil.ItemCallback<CoffeeItem> itemCallback = new DiffUtil.ItemCallback<CoffeeItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CoffeeItem oldItem, @NonNull CoffeeItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CoffeeItem oldItem, @NonNull CoffeeItem newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };
}