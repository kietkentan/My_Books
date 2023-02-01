package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.ProductManagerInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.activity.EditProductActivity;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Book;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListProductManagerAdapter extends RecyclerView.Adapter<ListProductManagerAdapter.ViewHolder>{
    private final List<String> idList;
    private final Context context;
    private final ProductManagerInterface anInterface;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference reference = database.getReference("book");

    public ListProductManagerAdapter(List<String> idList, Context context, ProductManagerInterface anInterface) {
        this.idList = idList;
        this.context = context;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public ListProductManagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListProductManagerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_manager, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ListProductManagerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.layoutShimmer.setVisibility(View.VISIBLE);
        holder.layoutInfo.setVisibility(View.GONE);
        holder.layoutShimmer.startShimmer();
        getData(holder, idList.get(position));
        if (position == idList.size() - 2) {
            anInterface.onLoading();
        }
        holder.ibEdit.setOnClickListener(v -> showPopupMenu(holder, position));
    }

    public void getData(ListProductManagerAdapter.ViewHolder holder, String id) {
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book = new Book();
                book.setId(snapshot.child("id").getValue(String.class));
                book.setName(snapshot.child("name").getValue(String.class));
                book.setDiscount(snapshot.child("discount").getValue(Integer.class));
                book.setOriginalPrice(snapshot.child("originalPrice").getValue(Integer.class));
                book.setAmount(snapshot.child("amount").getValue(Integer.class));
                //noinspection unchecked
                book.setImage((List<String>) snapshot.child("image").getValue());
                setValue(holder, book);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void setValue(ListProductManagerAdapter.ViewHolder holder, Book book) {
        Picasso.get().load(book.getImage().get(0)).into(holder.ivReview);
        holder.tvName.setText(book.getName());
        holder.tvPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(book.getOriginalPrice())));
        if (book.getDiscount() == 0)
            holder.tvDiscount.setVisibility(View.GONE);
        else
            holder.tvDiscount.setText(String.format(context.getString(R.string.book_discount), book.getDiscount()));
        holder.tvId.setText(book.getId());
        holder.tvAmount.setText(String.format("%d", book.getAmount()));

        holder.layoutInfo.setVisibility(View.VISIBLE);
        holder.layoutShimmer.stopShimmer();
        holder.layoutShimmer.setVisibility(View.GONE);
    }

    public void openDialogRemoveProduct(int position, String id){
        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_confirm_remove_cart);

        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnRemove = dialog.findViewById(R.id.btn_remove);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnRemove.setOnClickListener(v -> {
            dialog.dismiss();
            reference.child(id).removeValue((error, ref) -> {
                Toast.makeText(context, "Đã xóa sản phẩm có id: " + id, Toast.LENGTH_SHORT).show();
                idList.remove(position);
                notifyItemRemoved(position);
            });
        });

        dialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopupMenu(ListProductManagerAdapter.ViewHolder holder, int position){
        PopupMenu popupMenu = new PopupMenu(context, holder.ibEdit);
        popupMenu.getMenuInflater().inflate(R.menu.in_product_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.m_edit_product:
                    startEditProduct(holder.tvId.getText().toString());
                    break;
                case R.id.m_remove_product:
                    openDialogRemoveProduct(position, holder.tvId.getText().toString());
                    break;
            }
            return true;
        });
    }

    public void startEditProduct(String id) {
        Intent intent1 = new Intent(context, EditProductActivity.class);
        Bundle bundle1 = new Bundle();

        bundle1.putString("book", id);
        intent1.putExtras(bundle1);

        context.startActivity(intent1);
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReview;
        ImageButton ibEdit;
        TextView tvName;
        TextView tvId;
        TextView tvAmount;
        TextView tvPrice;
        TextView tvDiscount;
        ShimmerFrameLayout layoutShimmer;
        ConstraintLayout layoutInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReview = itemView.findViewById(R.id.iv_review_item);
            ibEdit = itemView.findViewById(R.id.ib_edit_product);
            tvName = itemView.findViewById(R.id.tv_name_product_item);
            tvId = itemView.findViewById(R.id.tv_id_product);
            tvAmount = itemView.findViewById(R.id.tv_amount_product);
            tvPrice = itemView.findViewById(R.id.tv_price_product_item);
            tvDiscount = itemView.findViewById(R.id.tv_discount_product_item);
            layoutShimmer = itemView.findViewById(R.id.shimmer_product_manager);
            layoutInfo = itemView.findViewById(R.id.layout_product_manager);
        }
    }
}
