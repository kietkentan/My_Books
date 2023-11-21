package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemCartBinding;
import com.space.mycoffee.listener.CartListener;
import com.space.mycoffee.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartAdapter extends ListAdapter<Order, CartAdapter.ViewHolder> {
    private final CartListener listener;

    public CartAdapter(@NonNull DiffUtil.ItemCallback<Order> diffCallback, CartListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding itemView = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    public List<String> getSelectedCart() {
        List<Order> list = getCurrentList();
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < list.size(); ++i)
            if (list.get(i).isSelected())
                selected.add(list.get(i).getIdCoffee());
        return selected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;
        public ViewHolder(@NonNull ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order item, CartListener listener) {
            binding.setOrder(item);
            if (item.getCoffeeDiscount() > 0) binding.tvCartOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            binding.btnCartAdd.setOnClickListener(view ->
                    listener.onQuantityChange(item.getIdCoffee(), item.getCoffeeQuantity() + 1)
            );
            binding.btnCartSub.setOnClickListener(view ->
                    listener.onQuantityChange(item.getIdCoffee(), item.getCoffeeQuantity() - 1)
            );
            binding.cbSelected.setOnClickListener(view ->
                    listener.onCheckedChange(item.getIdCoffee(), binding.cbSelected.isChecked())
            );
            binding.layoutCheckbox.setOnClickListener(view ->
                    listener.onCheckedChange(item.getIdCoffee(), !binding.cbSelected.isChecked())
            );
            binding.layoutItemCart.setOnClickListener(view ->
                    listener.onItemViewChecked(item.getIdCoffee())
            );
        }
    }

    public static DiffUtil.ItemCallback<Order> itemCallback = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getIdCoffee().equals(newItem.getIdCoffee()) && oldItem.isSelected() == newItem.isSelected();
        }
    };
}
