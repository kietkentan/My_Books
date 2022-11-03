package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.AppUtil;
import com.khtn.mybooks.Interface.ViewCartClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.databases.DataBase;
import com.khtn.mybooks.model.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private final List<Order> orders;
    private final ViewCartClickInterface viewCartClickInterface;
    private final Context context;

    public CartAdapter(List<Order> orders, ViewCartClickInterface viewCartClickInterface, Context context) {
        this.orders = orders;
        this.viewCartClickInterface = viewCartClickInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Picasso.get().load(orders.get(position).getBookImage()).into(holder.ivLogo);
        holder.tvName.setText(orders.get(position).getBookName());
        if (orders.get(position).getBookDiscount() != 0){
            holder.tvOriginalPrice.setText(String.format("%s₫", AppUtil.convertNumber(orders.get(position).getBookPrice())));
            holder.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvReducedPrice.setText(String.format("%s₫", AppUtil.convertNumber(orders.get(position).getBookPrice()*(100 - orders.get(position).getBookDiscount())/100)));
        } else {
            holder.tvReducedPrice.setText(String.format("%s₫", AppUtil.convertNumber(orders.get(position).getBookPrice())));
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }
        holder.tvQuantity.setText(String.format("%d", orders.get(position).getBookQuantity()));
        setOnClick(holder, position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOnClick(CartAdapter.ViewHolder holder, int position){
        holder.cbSelected.setChecked(orders.get(position).isSelected());
        holder.cbSelected.setOnClickListener(view -> {
            orders.get(position).setSelected(holder.cbSelected.isChecked());
            viewCartClickInterface.OnCheckedChanged(getSelectedCart());
        });
        holder.btnAdd.setOnClickListener(view -> {
            orders.get(position).setBookQuantity(orders.get(position).getBookQuantity() + 1);
            new DataBase(context).updateCart(orders.get(position).getBookId(), orders.get(position).getBookQuantity());
            notifyDataSetChanged();
        });
        holder.btnSub.setOnClickListener(view -> {
            int quantity = orders.get(position).getBookQuantity();
            if (quantity == 1) {
                new DataBase(context).removeCarts(orders.get(position).getBookId());
                orders.remove(position);
                notifyItemRemoved(position);
                viewCartClickInterface.OnRemoveCart();
            }
            else {
                orders.get(position).setBookQuantity(quantity - 1);
                new DataBase(context).updateCart(orders.get(position).getBookId(), quantity - 1);
                notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectedAllCart(){
        for (Order order:orders)
            order.setSelected(true);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void unSelectedAllCart(){
        for (Order order:orders)
            order.setSelected(false);
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedCart(){
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < orders.size(); ++i)
            if (orders.get(i).isSelected())
                selected.add(i);
        return selected;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedCart(List<Integer> selected){
        for (int i:selected)
            orders.get(i).setSelected(true);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
            cbSelected = itemView.findViewById(R.id.cb_selected);
            ivLogo = itemView.findViewById(R.id.iv_cart_logo);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvOriginalPrice = itemView.findViewById(R.id.tv_cart_original_price);
            tvReducedPrice = itemView.findViewById(R.id.tv_cart_reduced_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            btnSub = itemView.findViewById(R.id.btn_cart_sub);
            btnAdd = itemView.findViewById(R.id.btn_cart_add);
        }
    }
}
