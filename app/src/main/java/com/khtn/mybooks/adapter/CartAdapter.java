package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.activity.BookDetailActivity;
import com.khtn.mybooks.Interface.ViewCartClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.databases.DatabaseCart;
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Picasso.get().load(orders.get(position).getBookImage()).into(holder.ivLogo);
        holder.tvName.setText(orders.get(position).getBookName());
        if (orders.get(position).getBookDiscount() != 0){
            holder.tvOriginalPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(orders.get(position).getBookPrice())));
            holder.tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvReducedPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(orders.get(position).getBookPrice()*(100 - orders.get(position).getBookDiscount())/100)));
        } else {
            holder.tvReducedPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(orders.get(position).getBookPrice())));
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }
        holder.tvQuantity.setText(String.format(context.getString(R.string.num), orders.get(position).getBookQuantity()));
        setOnClick(holder, position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOnClick(CartAdapter.ViewHolder holder, int position){
        holder.cbSelected.setChecked(orders.get(position).isSelected());
        holder.cbSelected.setOnClickListener(view -> {
            orders.get(position).setSelected(holder.cbSelected.isChecked());
            viewCartClickInterface.OnCheckedChanged();
        });
        holder.layoutCheckbox.setOnClickListener(v -> {
            orders.get(position).setSelected(!holder.cbSelected.isChecked());
            viewCartClickInterface.OnCheckedChanged();
        });
        holder.btnAdd.setOnClickListener(view -> {
            int quantity = orders.get(position).getBookQuantity() + 1;
            FirebaseDatabase.getInstance().getReference("book").child(orders.get(position).getBookId()).child("amount").addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int amount = snapshot.getValue(Integer.class);
                    if (amount >= quantity){
                        orders.get(position).setBookQuantity(quantity);
                        new DatabaseCart(context).updateCart(orders.get(position).getBookId(), quantity);
                        viewCartClickInterface.OnChangeDataCart(position, orders.get(position).getBookQuantity());
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(context, String.format(context.getString(R.string.limit_product), amount), Toast.LENGTH_SHORT).show();
                    }
                    viewCartClickInterface.OnCheckedChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        holder.btnSub.setOnClickListener(view -> {
            int quantity = orders.get(position).getBookQuantity() - 1;
            if (quantity < 1)
                openDialogRemoveCart(position);
            else {
                orders.get(position).setBookQuantity(quantity);
                new DatabaseCart(context).updateCart(orders.get(position).getBookId(), quantity);
                viewCartClickInterface.OnChangeDataCart(position, quantity);
                notifyItemChanged(position);
                viewCartClickInterface.OnCheckedChanged();
            }
        });
        holder.layoutItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("publisher", orders.get(position).getPublisherId());
            intent.putExtra("id", orders.get(position).getBookId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        });
    }

    public void openDialogRemoveCart(int position){
        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_confirm_remove_cart);

        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnRemove = dialog.findViewById(R.id.btn_remove);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnRemove.setOnClickListener(v -> {
            dialog.dismiss();
            new DatabaseCart(context).removeCarts(orders.get(position).getBookId());
            orders.remove(position);
            viewCartClickInterface.OnSaveAllCart(orders);
            notifyItemRemoved(position);
            viewCartClickInterface.OnCheckedChanged();
        });

        dialog.show();
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
        ConstraintLayout layoutItem;
        FrameLayout layoutCheckbox;
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
            layoutItem = itemView.findViewById(R.id.layout_item_cart);
            layoutCheckbox = itemView.findViewById(R.id.layout_checkbox);
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
