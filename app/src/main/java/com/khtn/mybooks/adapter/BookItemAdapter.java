package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.activity.BookDetailActivity;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.BookItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookItemAdapter extends RecyclerView.Adapter<BookItemAdapter.ViewHolder>{
    private final List<BookItem> bookItemList;
    private final Context context;

    public BookItemAdapter(Context context, List<BookItem> bookItemList) {
        this.context = context;
        this.bookItemList = bookItemList;
    }

    @NonNull
    @Override
    public BookItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull BookItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(bookItemList.get(position).getName());
        Picasso.get().load(bookItemList.get(position).getImage().get(0)).into(holder.ivItemReview);

        if (bookItemList.get(position).getDiscount() == 0){
            holder.tvOriginalPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(bookItemList.get(position).getOriginalPrice())));
            holder.tvDiscount.setVisibility(View.INVISIBLE);
            holder.tvReducedPrice.setVisibility(View.INVISIBLE);
        } else {
            holder.tvDiscount.setText(String.format(context.getString(R.string.book_discount), bookItemList.get(position).getDiscount()));
            holder.tvOriginalPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(bookItemList.get(position).getOriginalPrice())));
            holder.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvOriginalPrice.setTextColor(Color.parseColor("#BDBDBD"));
            holder.tvReducedPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(bookItemList.get(position).getReducedPrice())));
        }
        if (!(bookItemList.get(position).getAmount() == 0))
            holder.tvOutOfStock.setVisibility(View.INVISIBLE);
        else holder.tvOutOfStock.setVisibility(View.VISIBLE);
        if (AppUtil.numDays(bookItemList.get(position).getDatePosted()) >= 30)
            holder.tvUpComing.setVisibility(View.INVISIBLE);
        else holder.tvUpComing.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("publisher", bookItemList.get(position).getPublisher());
            intent.putExtra("id", bookItemList.get(position).getId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        });
    }

    @Override
    public int getItemCount() {
        return bookItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemReview;
        TextView tvName;
        TextView tvOriginalPrice;
        TextView tvReducedPrice;
        TextView tvDiscount;
        TextView tvUpComing;
        TextView tvOutOfStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemReview = itemView.findViewById(R.id.iv_item_review);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvReducedPrice = itemView.findViewById(R.id.tv_reduced_price);
            tvDiscount = itemView.findViewById(R.id.tv_people_rating);
            tvUpComing = itemView.findViewById(R.id.tv_up_coming);
            tvOutOfStock = itemView.findViewById(R.id.tv_out_of_stock);
        }
    }
}
