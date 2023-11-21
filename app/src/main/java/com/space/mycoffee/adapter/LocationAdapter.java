package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.databinding.ItemStringViewBinding;
import com.space.mycoffee.listener.ChoseLocationClickInterface;
import com.space.mycoffee.model.Location;

import java.util.List;
import java.util.Objects;

public class LocationAdapter extends ListAdapter<Location, LocationAdapter.ViewHolder> {
    private ChoseLocationClickInterface clickInterface;

    public LocationAdapter(@NonNull DiffUtil.ItemCallback<Location> diffCallback, ChoseLocationClickInterface clickInterface) {
        super(diffCallback);
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStringViewBinding itemView = ItemStringViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location item = getItem(position);
        holder.bind(item, clickInterface);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<Location> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemStringViewBinding binding;
        public ViewHolder(@NonNull ItemStringViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull Location item, ChoseLocationClickInterface clickInterface) {
            binding.setText(item.getName_with_type());
            binding.tvText.setOnClickListener(view -> clickInterface.onClick(item));
        }
    }

    public static DiffUtil.ItemCallback<Location> itemCallback = new DiffUtil.ItemCallback<Location>() {
        @Override
        public boolean areItemsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Location oldItem, @NonNull Location newItem) {
            return oldItem.getCode().equals(newItem.getCode());
        }
    };
}
