package com.khtn.mybooks.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.R;
import com.khtn.mybooks.activity.BookDetailActivity;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Order;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookOrderItemAdapter extends RecyclerView.Adapter<BookOrderItemAdapter.ViewHolder>{
    private final List<Order> orderList;
    private final Context context;

    public BookOrderItemAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookOrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookOrderItemAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_in_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookOrderItemAdapter.ViewHolder holder, int position) {
        Picasso.get().load(orderList.get(position).getBookImage()).into(holder.ivLogo);
        holder.tvName.setText(orderList.get(position).getBookName());
        holder.tvPrice.setText(String.format(context.getString(R.string.book_price),
                AppUtil.convertNumber(orderList.get(position).getBookPrice() * (100 - orderList.get(position).getBookDiscount()) / 100)));
        holder.tvQuantity.setText(String.format(context.getString(R.string.quantity),
                orderList.get(position).getBookQuantity()));
        holder.layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("publisher", orderList.get(position).getPublisherId());
            intent.putExtra("id", orderList.get(position).getBookId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName;
        TextView tvQuantity;
        TextView tvPrice;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.iv_logo_book_in_order);
            tvName = itemView.findViewById(R.id.tv_name_book_in_order);
            tvQuantity = itemView.findViewById(R.id.tv_quantity_book_in_order);
            tvPrice = itemView.findViewById(R.id.tv_price_book_in_order);
            layout = itemView.findViewById(R.id.layout_book_in_order);
        }
    }
}
