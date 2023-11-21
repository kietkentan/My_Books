package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemCoffeeFavoriteBinding;
import com.space.mycoffee.listener.CoffeeFavoriteListener;
import com.space.mycoffee.model.CoffeeDetail;

import java.util.List;
import java.util.Objects;

public class FavoriteItemAdapter extends ListAdapter<CoffeeDetail, FavoriteItemAdapter.ViewHolder> {
    private final CoffeeFavoriteListener listener;

    public FavoriteItemAdapter(@NonNull DiffUtil.ItemCallback<CoffeeDetail> diffCallback, CoffeeFavoriteListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCoffeeFavoriteBinding itemView = ItemCoffeeFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoffeeDetail item = getItem(position);
        holder.bind(item, listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<CoffeeDetail> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCoffeeFavoriteBinding binding;
        public ViewHolder(@NonNull ItemCoffeeFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CoffeeDetail item, CoffeeFavoriteListener listener) {
            binding.setDetail(item);
            binding.layoutItemFavorite.setOnClickListener(view -> listener.onItemClicked(item.getId()));
            binding.ibRemoveFavoriteItem.setOnClickListener(view -> listener.onFavoriteClicked(item.getId()));
            binding.ibAddToCart.setOnClickListener(view -> listener.onAddCartClicked(item));
        }
    }

    public static DiffUtil.ItemCallback<CoffeeDetail> itemCallback = new DiffUtil.ItemCallback<CoffeeDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull CoffeeDetail oldItem, @NonNull CoffeeDetail newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CoffeeDetail oldItem, @NonNull CoffeeDetail newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };
}
