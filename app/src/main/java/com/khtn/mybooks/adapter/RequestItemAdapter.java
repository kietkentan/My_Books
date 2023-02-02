package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.khtn.mybooks.Interface.OnOrderChangeSizeInterface;
import com.khtn.mybooks.activity.OrderDetailActivity;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Request;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.ViewHolder> {
    private final List<Request> requestList;
    private final Context context;
    private final OnOrderChangeSizeInterface anInterface;

    public RequestItemAdapter(List<Request> requestList, Context context, OnOrderChangeSizeInterface anInterface) {
        this.requestList = requestList;
        this.context = context;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public RequestItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false));
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull RequestItemAdapter.ViewHolder holder, int position) {
        Order order = requestList.get(position).getOrderList().get(0);
        int quantity = 0;
        int status = requestList.get(position).getStatus();
        for (Order order1:requestList.get(position).getOrderList())
            quantity += order1.getBookQuantity();

        Picasso.get().load(requestList.get(position).getOrderList().get(0).getBookImage()).into(holder.ivReview);

        holder.btnBuyAgain.setVisibility(status == 4 ? View.VISIBLE : View.GONE);
        holder.btnCancel.setVisibility((status == 2 || status == 3) ? View.VISIBLE : View.GONE);

        holder.tvStatus.setText(context.getResources().getStringArray(R.array.status)[requestList.get(position).getStatus() - 1]);
        holder.tvNamePublisher.setText(requestList.get(position).getNamePublisher());
        holder.tvNameCart.setText(order.getBookName());
        holder.tvQuantityCart.setText(String.format(context.getString(R.string.quantity), order.getBookQuantity()));
        holder.tvTotalPriceCart.setText(String.format(context.getString(R.string.book_price),
                AppUtil.convertNumber((order.getBookPrice()*(100 - order.getBookDiscount()) / 100) * order.getBookQuantity())));
        holder.tvQuantityList.setText(String.format(context.getString(R.string.quantity_list), quantity));
        holder.tvTotalPriceRequest.setText(String.format(context.getString(R.string.book_price),
                AppUtil.convertNumber(requestList.get(position).getTotal())));

        if (requestList.get(position).getOrderList().size() > 1){
            holder.tvMoreOrderProducts.setVisibility(View.VISIBLE);
            holder.tvMoreOrderProducts.setText(String.format(context.getString(R.string.more_order_products), requestList.get(position).getOrderList().size() - 1));
        } else
            holder.tvMoreOrderProducts.setVisibility(View.GONE);

        holder.layoutRequestItem.setOnClickListener(v -> startDetailRequest(position));
        holder.btnMoreDetail.setOnClickListener(v -> startDetailRequest(position));
        holder.btnCancel.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("request");
            reference.child(requestList.get(position).getIdRequest()).child("status").setValue(5);
            requestList.remove(position);
            notifyItemRemoved(position);
            anInterface.onZero();
        });
    }

    public void startDetailRequest(int position){
        Intent intent = new Intent(context, OrderDetailActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("request", new Gson().toJson(requestList.get(position)));

        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamePublisher;
        TextView tvStatus;
        TextView tvNameCart;
        TextView tvQuantityCart;
        TextView tvTotalPriceCart;
        TextView tvQuantityList;
        TextView tvTotalPriceRequest;
        TextView tvMoreOrderProducts;
        AppCompatButton btnMoreDetail;
        AppCompatButton btnBuyAgain;
        AppCompatButton btnCancel;
        ImageView ivReview;
        ConstraintLayout layoutRequestItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamePublisher = itemView.findViewById(R.id.tv_name_publisher);
            tvStatus = itemView.findViewById(R.id.tv_status_request);
            tvNameCart = itemView.findViewById(R.id.tv_cart_name);
            tvQuantityCart = itemView.findViewById(R.id.tv_cart_quantity);
            tvTotalPriceCart = itemView.findViewById(R.id.tv_cart_total_price);
            tvQuantityList = itemView.findViewById(R.id.tv_quantity_list);
            tvTotalPriceRequest = itemView.findViewById(R.id.tv_total_price);
            tvMoreOrderProducts = itemView.findViewById(R.id.tv_more_order_products);
            btnMoreDetail = itemView.findViewById(R.id.btn_see_more_detail);
            btnBuyAgain = itemView.findViewById(R.id.btn_buy_again);
            btnCancel = itemView.findViewById(R.id.btn_cancel_request);
            ivReview = itemView.findViewById(R.id.iv_cart_logo);
            layoutRequestItem = itemView.findViewById(R.id.layout_request_item);
        }
    }
}
