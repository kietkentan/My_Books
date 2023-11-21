package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.ItemOrderManagerBinding;
import com.space.mycoffee.listener.RequestItemListener;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.model.Request;

import java.util.List;

public class ItemOrderManager extends ListAdapter<Request, ItemOrderManager.ViewHolder> {
    private final RequestItemListener listener;
    private final Context context;

    public ItemOrderManager(@NonNull DiffUtil.ItemCallback<Request> diffCallback, Context context, RequestItemListener listener) {
        super(diffCallback);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderManagerBinding itemView = ItemOrderManagerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request item = getItem(position);
        holder.bind(item, position, context, listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<Request> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderManagerBinding binding;
        public ViewHolder(@NonNull ItemOrderManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull Request item, int position, Context context, RequestItemListener listener) {
            int quantity = 0;
            for (Order order : item.getOrderList())
                quantity += order.getCoffeeQuantity();
            binding.setOrder(item.getOrderList().get(0));
            binding.setRequest(item);
            binding.tvStatusOrder.setText(context.getResources().getStringArray(R.array.status)[item.getStatus() - 1]);
            binding.tvQuantityList.setText(String.format(context.getString(R.string.quantity_list), quantity));

            binding.btnCancelRequest.setOnClickListener(view -> listener.onItemRemove(position));
            binding.btnSeeMoreDetail.setOnClickListener(view -> listener.onItemClicked(item));
            binding.btnAcceptRequest.setOnClickListener(view -> listener.onItemAccept(item, position));
            binding.layoutRequestItem.setOnClickListener(view -> listener.onItemClicked(item));
        }
    }
}
