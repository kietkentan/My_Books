package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.khtn.mybooks.activity.BookDetailActivity;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.Interface.FavoriteClickInterface;
import com.khtn.mybooks.activity.MainActivity;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Book;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Publisher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder>{
    private final List<Book> bookItemList;
    private final Context context;
    private final FavoriteClickInterface clickInterface;

    private final DatabaseCart databaseCart;
    private final FirebaseDatabase database;
    private final String[] mode = {"mybooks", "google", "facebook"};
    private Publisher dataPublisher;

    public FavoriteItemAdapter(List<Book> bookItemList, Context context, FavoriteClickInterface clickInterface) {
        this.bookItemList = bookItemList;
        this.context = context;
        this.clickInterface = clickInterface;
        this.databaseCart = new DatabaseCart(context);
        this.database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public FavoriteItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteItemAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(bookItemList.get(position).getImage().get(0)).into(holder.ivReview);
        holder.tvName.setText(bookItemList.get(position).getName());
        holder.tvPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(bookItemList.get(position).getReducedPrice())));
        if (bookItemList.get(position).getDiscount() == 0)
            holder.tvDiscount.setVisibility(View.GONE);
        else
            holder.tvDiscount.setText(String.format(context.getString(R.string.book_discount), bookItemList.get(position).getDiscount()));
        holder.ratingBar.setRating(bookItemList.get(position).getTotalRatingScore());
        holder.tvPeopleRating.setText(String.format(context.getString(R.string.people_rating), bookItemList.get(position).getTotalRatings()));

        holder.ibAddToCart.setOnClickListener(v -> addCart(position));
        holder.ibRemoveFavorite.setOnClickListener(v -> removeFavorite(position));
        holder.layoutFavoriteItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("publisher", bookItemList.get(position).getPublisher());
            intent.putExtra("id", bookItemList.get(position).getId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        });
    }

    public void removeFavorite(int position){
        database.getReference("user").child(mode[Common.modeLogin - 1]).
                child(Common.currentUser.getId()).child("list_favorite").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Book> books = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Book book = snapshot1.getValue(Book.class);
                            if (!book.getId().equals(bookItemList.get(position).getId()))
                                books.add(book);
                        }
                        snapshot.getRef().removeValue();
                        if (books.size() > 0)
                            snapshot.getRef().setValue(books);
                        Toast.makeText(context, R.string.un_added_favorite, Toast.LENGTH_SHORT).show();
                        bookItemList.remove(position);
                        notifyItemRemoved(position);
                        Common.currentUser.setList_favorite(bookItemList);
                        if (bookItemList.size() == 0)
                            clickInterface.OnRemove();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void addCart(int position) {
        List<Order> orderList = databaseCart.getCarts();
        int quantity = 0;
        boolean exists = false;
        for (int i = 0; i < orderList.size(); ++i) {
            if (orderList.get(i).getBookId().equals(bookItemList.get(position).getId())) {
                exists = true;
                quantity = orderList.get(i).getBookQuantity();
            }
        }

        int finalQuantity = quantity;
        boolean finalExists = exists;
        database.getReference("book").child(bookItemList.get(position).getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int amount = snapshot.child("amount").getValue(Integer.class);
                        if (amount > finalQuantity) {
                            databaseCart.addCart(new Order(
                                    bookItemList.get(position).getId(),
                                    bookItemList.get(position).getName(),
                                    bookItemList.get(position).getImage().get(0),
                                    bookItemList.get(position).getPublisher(),
                                    1,
                                    bookItemList.get(position).getOriginalPrice(),
                                    bookItemList.get(position).getDiscount()
                            ));
                            Common.currentUser.setCartList(databaseCart.getCarts());

                            database.getReference("user").child(mode[Common.modeLogin - 1]).
                                    child(Common.currentUser.getId()).child("cartList").
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (finalExists) {
                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                    if (snapshot1.child("bookId").getValue(String.class).equals(bookItemList.get(position).getId())) {
                                                        int quantity = snapshot1.child("bookQuantity").getValue(Integer.class);
                                                        snapshot1.child("bookQuantity").getRef().setValue(quantity + 1);
                                                    }
                                                }
                                            } else {
                                                snapshot.child(String.valueOf(snapshot.getChildrenCount())).child("bookId").getRef().setValue(bookItemList.get(position).getId());
                                                snapshot.child(String.valueOf(snapshot.getChildrenCount())).child("bookQuantity").getRef().setValue(1);
                                            }
                                            database.getReference("publisher").child(bookItemList.get(position).getPublisher()).
                                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        dataPublisher = snapshot.getValue(Publisher.class);
                                                        openDialog(position);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        } else {
                            Toast.makeText(context, String.format(context.getString(R.string.limit_product), amount), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void openDialog(int position){
        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_done_add_cart);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setAttributes(layoutParams);

        ImageView ivImage = dialog.findViewById(R.id.iv_dialog_book_review);
        TextView tvBookName = dialog.findViewById(R.id.tv_dialog_book_name);
        TextView tvPublisherName = dialog.findViewById(R.id.tv_dialog_publisher_name);
        TextView tvBookPrice = dialog.findViewById(R.id.tv_dialog_book_price);
        AppCompatButton btnCartPage = dialog.findViewById(R.id.btn_view_cart);

        Picasso.get().load(bookItemList.get(position).getImage().get(0)).into(ivImage);
        tvBookName.setText(bookItemList.get(position).getName());
        tvPublisherName.setText(dataPublisher.getName());
        tvBookPrice.setText(String.format(context.getString(R.string.book_price), AppUtil.convertNumber(bookItemList.get(position).getReducedPrice())));

        btnCartPage.setOnClickListener(view -> {
            dialog.dismiss();
            startCart();
        });
        dialog.show();
    }

    public void startCart() {
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(context);
            return;
        }
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public int getItemCount() {
        return bookItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReview;
        ImageButton ibRemoveFavorite;
        ImageButton ibAddToCart;
        TextView tvName;
        TextView tvPrice;
        TextView tvDiscount;
        TextView tvPeopleRating;
        RatingBar ratingBar;
        ConstraintLayout layoutFavoriteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReview = itemView.findViewById(R.id.iv_review_favorite_item);
            ibRemoveFavorite = itemView.findViewById(R.id.ib_remove_favorite_item);
            ibAddToCart = itemView.findViewById(R.id.ib_add_to_cart);
            tvName = itemView.findViewById(R.id.tv_name_favorite_item);
            tvPrice = itemView.findViewById(R.id.tv_price_favorite_item);
            tvDiscount = itemView.findViewById(R.id.tv_discount_favorite_item);
            tvPeopleRating = itemView.findViewById(R.id.tv_people_rating);
            ratingBar = itemView.findViewById(R.id.bar_rating_favorite_item);
            layoutFavoriteItem = itemView.findViewById(R.id.layout_item_favorite);
        }
    }
}
