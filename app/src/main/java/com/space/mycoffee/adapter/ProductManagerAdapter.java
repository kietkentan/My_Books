package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.ItemProductManagerBinding;
import com.space.mycoffee.listener.OnAddressListener;
import com.space.mycoffee.listener.OnProductListener;
import com.space.mycoffee.listener.ProductManagerListener;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.utils.AppSingleton;

import java.util.List;
import java.util.Objects;

public class ProductManagerAdapter extends ListAdapter<CoffeeDetail, ProductManagerAdapter.ViewHolder> {
    private final Context context;
    private final ProductManagerListener listener;
    private final OnProductListener onAddressListener = this::showMenuPopup;

    public ProductManagerAdapter(@NonNull DiffUtil.ItemCallback<CoffeeDetail> diffCallback, Context context, ProductManagerListener listener) {
        super(diffCallback);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductManagerBinding itemView = ItemProductManagerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoffeeDetail item = getItem(position);
        holder.bind(item, position, onAddressListener, listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<CoffeeDetail> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    public void showMenuPopup(View view, int position, @NonNull CoffeeDetail detail){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setGravity(Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.in_product_menu, popupMenu.getMenu());
        popupMenu.getMenu().getItem(1).setTitle(detail.isHide() ? R.string.show : R.string.hide);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.m_edit_product:
                    listener.onItemClicked(detail);
                    break;
                case R.id.m_remove_product:
                    listener.onItemRemoveClicked(position);
                    break;
            }
            return true;
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductManagerBinding binding;
        public ViewHolder(@NonNull ItemProductManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CoffeeDetail item, int position, OnProductListener onAddressListener, ProductManagerListener listener) {
            binding.setDetail(item);
            binding.layoutProductManager.setOnClickListener(view -> {});
            binding.ibEditProduct.setOnClickListener(
                    view -> onAddressListener.openListOption(binding.ibEditProduct, position, item)
            );
            binding.layoutProductManager.setOnClickListener(view -> listener.onItemClicked(item));
        }
    }

    public static DiffUtil.ItemCallback<CoffeeDetail> itemCallback = new DiffUtil.ItemCallback<CoffeeDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull CoffeeDetail oldItem, @NonNull CoffeeDetail newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull CoffeeDetail oldItem, @NonNull CoffeeDetail newItem) {
            return oldItem.getId().equals(newItem.getId()) && oldItem.isHide() == newItem.isHide();
        }
    };
}
