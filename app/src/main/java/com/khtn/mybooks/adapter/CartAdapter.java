package com.khtn.mybooks.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Order> orders;
    private Context context;

    public CartAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Picasso.get().load(orders.get(position).getBookImage()).into(holder.ivLogo);
        holder.tvName.setText(orders.get(position).getBookName());
        if (orders.get(position).getBookDiscount() != 0){
            holder.tvOriginalPrice.setText(String.format("%sđ", AppUtil.convertNumber(orders.get(position).getBookPrice())));
            holder.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvReducedPrice.setText(String.format("%sđ", AppUtil.convertNumber(orders.get(position).getBookPrice() - (orders.get(position).getBookPrice() * (orders.get(position).getBookDiscount() / 100)))));
        } else {
            holder.tvReducedPrice.setText(String.format("%sđ", AppUtil.convertNumber(orders.get(position).getBookPrice())));
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }
        setChecked(holder, position);
    }

    public void setChecked(CartAdapter.ViewHolder holder, int position){
        holder.cbSelected.setChecked(orders.get(position).isSelected());
        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orders.get(position).setSelected(compoundButton.isChecked());
                Log.i("TAG_U", "onCheckedChanged: " + position);
            }
        });
    }

    public void selectedAllCart(){
        for (Order order:orders)
            order.setSelected(true);
        notifyDataSetChanged();
    }

    public void unSelectedAllCart(){
        for (Order order:orders)
            order.setSelected(false);
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedCart(){
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < orders.size(); ++i)
            if (orders.get(i).isSelected()) {
                selected.add(i);
                Log.i("TAG_U", "getSelectedCart: " + i);
            }
        return selected;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelected;
        ImageView ivLogo;
        TextView tvName;
        TextView tvOriginalPrice;
        TextView tvReducedPrice;
        TextView tvQuantity;
        AppCompatButton btnSub;
        AppCompatButton btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelected = (CheckBox) itemView.findViewById(R.id.cb_selected);
            ivLogo = (ImageView) itemView.findViewById(R.id.iv_cart_logo);
            tvName = (TextView) itemView.findViewById(R.id.tv_cart_name);
            tvOriginalPrice = (TextView) itemView.findViewById(R.id.tv_cart_original_price);
            tvReducedPrice = (TextView) itemView.findViewById(R.id.tv_cart_reduced_price);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_cart_quantity);
            btnSub = (AppCompatButton) itemView.findViewById(R.id.btn_cart_sub);
            btnAdd = (AppCompatButton) itemView.findViewById(R.id.btn_cart_add);
        }
    }
}
