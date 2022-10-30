package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.AppUtil;
import com.khtn.mybooks.BookDetailActivity;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.BookItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookItemAdapter extends RecyclerView.Adapter<BookItemAdapter.ViewHolder>{
    private List<BookItem> bookItemList;
    private Context context;

    public BookItemAdapter(Context context, List<BookItem> bookItemList) {
        this.context = context;
        this.bookItemList = bookItemList;
    }

    @NonNull
    @Override
    public BookItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(bookItemList.get(position).getName());
        Picasso.get().load(bookItemList.get(position).getImage().get(0)).into(holder.ivItemReview);

        if (bookItemList.get(position).getDiscount() == 0){
            holder.tvOriginalPrice.setText(AppUtil.convertNumber(bookItemList.get(position).getOriginalPrice()) + "đ");
            holder.layoutDiscount.setVisibility(View.INVISIBLE);
            holder.tvReducedPrice.setVisibility(View.INVISIBLE);
        } else {
            holder.tvDiscount.setText("-" + bookItemList.get(position).getDiscount() + "%");
            holder.tvOriginalPrice.setText(AppUtil.convertNumber(bookItemList.get(position).getOriginalPrice()) + "đ");
            holder.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvOriginalPrice.setTextColor(Color.parseColor("#BDBDBD"));
            holder.tvReducedPrice.setText(AppUtil.convertNumber(bookItemList.get(position).getReducedPrice()) + "đ");
        }
        if (!(bookItemList.get(position).getAmount() == 0))
            holder.layoutOutOfStock.setVisibility(View.INVISIBLE);
        else holder.layoutOutOfStock.setVisibility(View.VISIBLE);
        if (AppUtil.numDays(bookItemList.get(position).getDatePosted()) >= 30)
            holder.layoutUpcoming.setVisibility(View.INVISIBLE);
        else holder.layoutUpcoming.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                intent.putExtra("publisher", bookItemList.get(position).getPublisher());
                intent.putExtra("id", bookItemList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItemReview;
        TextView tvName;
        TextView tvOriginalPrice;
        TextView tvReducedPrice;
        TextView tvDiscount;
        FrameLayout layoutDiscount;
        FrameLayout layoutUpcoming;
        FrameLayout layoutOutOfStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemReview = itemView.findViewById(R.id.iv_item_review);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvReducedPrice = itemView.findViewById(R.id.tv_reduced_price);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            layoutDiscount = itemView.findViewById(R.id.layout_discount_percentage);
            layoutUpcoming = itemView.findViewById(R.id.layout_upcoming);
            layoutOutOfStock = itemView.findViewById(R.id.layout_out_of_stock);
        }
    }
}
