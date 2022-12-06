package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartConfirmAdapter extends RecyclerView.Adapter<CartConfirmAdapter.ViewHolder>{
    private final List<Order> orders;
    private final Context context;

    public CartConfirmAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public CartConfirmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_confirm, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartConfirmAdapter.ViewHolder holder, int position) {
        int price = orders.get(position).getBookPrice()*(100 - orders.get(position).getBookDiscount())/100;

        Picasso.get().load(orders.get(position).getBookImage()).into(holder.ivLogo);
        holder.tvName.setText(orders.get(position).getBookName());
        holder.tvQuantity.setText(String.format(context.getString(R.string.quantity), orders.get(position).getBookQuantity()));
        holder.tvPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(price)));
        holder.tvTotalPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(price * orders.get(position).getBookQuantity())));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotalPrice;
        TextView tvQuantity;
        TextView tvPrice;
        TextView tvName;
        ImageView ivLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTotalPrice = itemView.findViewById(R.id.tv_cart_total_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            ivLogo = itemView.findViewById(R.id.iv_cart_logo);
        }
    }
}
