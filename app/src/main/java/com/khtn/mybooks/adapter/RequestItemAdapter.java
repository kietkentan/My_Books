package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Request;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.ViewHolder> {
    private final List<Request> requestList;
    private final Context context;

    public RequestItemAdapter(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
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
        holder.tvStatus.setText(String.valueOf(status));
        if (status == 4)
            holder.btnBuyAgain.setVisibility(View.VISIBLE);
        else
            holder.btnBuyAgain.setVisibility(View.GONE);

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
        ImageView ivReview;

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
            ivReview = itemView.findViewById(R.id.iv_cart_logo);
        }
    }
}
