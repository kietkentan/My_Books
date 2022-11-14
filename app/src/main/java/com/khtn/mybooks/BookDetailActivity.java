package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.adapter.BookDetailAdapter;
import com.khtn.mybooks.adapter.ListImageAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Book;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Publisher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivMenu;
    private ViewPager2 rcImages;
    private TextView tvPrice;
    private TextView tvOriginalPrice;
    private TextView tvDiscount;
    private TextView tvNameBook;
    private TextView tvTotalRating;
    private TextView tvTotalNumberPeopleRating;
    private TextView tvQuantitySold;
    private TextView tvInStockOrNot;
    private TextView tvShopName;
    private TextView tvShopLocation;
    private TextView tvShopReplyWithin;
    private TextView tvShopRating;
    private TextView tvShopWorked;
    private TextView tvDatePosted;
    private TextView tvDescribe;
    private TextView tvPosition;
    private TextView tvNumCart;
    private ImageButton ibAddFavorite;
    private ImageButton ibBack;
    private RatingBar barRatingBook;
    private ShapeableImageView ivLogoPublisher;
    private RecyclerView viewListDetails;
    private FrameLayout layoutUpcoming;
    private FrameLayout layoutCart;
    private AppCompatButton btnAddCart;
    private AppCompatButton btnBuyNow;

    public String id;
    public String publisher;
    private Book dataBook;
    private Publisher dataPublisher;
    private List<List<String>> listDetails;
    private String describe;

    private DatabaseCart databaseCart;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        init();

        getData();
        ivMenu.setOnClickListener(BookDetailActivity.this);
        layoutCart.setOnClickListener(BookDetailActivity.this);
        ibBack.setOnClickListener(BookDetailActivity.this);
        btnAddCart.setOnClickListener(BookDetailActivity.this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupCart();
    }

    public void getData(){
        database.getReference("publisher").child(publisher).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    dataPublisher = snapshot.getValue(Publisher.class);
                database.getReference("book").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            dataBook = snapshot.getValue(Book.class);
                            snapshot.getRef().child("detail").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot2:snapshot.getChildren()) {
                                        if (Objects.equals(dataSnapshot2.getKey(), "describe")){
                                            describe = dataSnapshot2.getValue(String.class);
                                        } else {
                                            List<String> list = new ArrayList<>();
                                            list.add(dataSnapshot2.getKey());
                                            list.add(dataSnapshot2.getValue(String.class));
                                            listDetails.add(list);
                                        }
                                    }
                                    setAboutDetails();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        setDetails();
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
    }

    public void init(){
        databaseCart = new DatabaseCart(BookDetailActivity.this);
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        publisher = intent.getStringExtra("publisher");
        listDetails = new ArrayList<>();

        ivMenu = findViewById(R.id.iv_menu_in_detail);
        rcImages = findViewById(R.id.list_img);
        tvPrice = findViewById(R.id.tv_price_book);
        tvOriginalPrice = findViewById(R.id.tv_original_price_book);
        tvDiscount = findViewById(R.id.tv_discount_book);
        tvNameBook = findViewById(R.id.tv_name_book);
        tvTotalRating = findViewById(R.id.tv_total_rating);
        tvTotalNumberPeopleRating = findViewById(R.id.tv_total_number_people_rating);
        tvQuantitySold = findViewById(R.id.tv_quantity_sold);
        tvInStockOrNot = findViewById(R.id.tv_in_stock_or_not);
        tvShopName = findViewById(R.id.tv_name_publisher);
        tvShopLocation = findViewById(R.id.tv_location_publisher);
        tvShopReplyWithin = findViewById(R.id.tv_reply_within);
        tvShopRating = findViewById(R.id.tv_shop_rating);
        tvShopWorked = findViewById(R.id.tv_worked);
        tvDatePosted = findViewById(R.id.tv_date_posted);
        tvDescribe = findViewById(R.id.tv_describe);
        tvPosition = findViewById(R.id.tv_position);
        tvNumCart = findViewById(R.id.tv_num_cart);
        barRatingBook = findViewById(R.id.bar_rating_book);
        ibAddFavorite = findViewById(R.id.ib_add_favorite);
        ibBack = findViewById(R.id.ib_exit_detail);
        ivLogoPublisher = findViewById(R.id.iv_avatar_publisher);
        viewListDetails = findViewById(R.id.list_details);
        layoutUpcoming = findViewById(R.id.layout_upcoming);
        layoutCart = findViewById(R.id.layout_shopping_cart);
        btnAddCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
    }

    @SuppressLint("DefaultLocale")
    public void setDetails(){
        ListImageAdapter imageAdapter = new ListImageAdapter(dataBook.getImage());
        rcImages.setAdapter(imageAdapter);
        rcImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvPosition.setText(String.format("%d/%d", position + 1, dataBook.getImage().size()));
            }
        });

        if (dataBook.getDiscount() == 0){
            tvPrice.setText(String.format("%sđ", AppUtil.convertNumber(dataBook.getOriginalPrice())));
            tvPrice.setTextColor(Color.parseColor("#FF000000"));
            tvOriginalPrice.setVisibility(View.INVISIBLE);
            tvDiscount.setVisibility(View.INVISIBLE);
        } else {
            tvPrice.setText(String.format("%sđ", AppUtil.convertNumber(dataBook.getReducedPrice())));
            tvOriginalPrice.setText(String.format("%sđ", AppUtil.convertNumber(dataBook.getOriginalPrice())));
            tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvDiscount.setText(String.format("-%d%%", dataBook.getDiscount()));
        }

        tvNameBook.setText(dataBook.getName());
        barRatingBook.setRating(dataBook.getTotalRatingScore());
        tvTotalRating.setText(String.format("%.1f", dataBook.getTotalRatingScore()));
        tvTotalNumberPeopleRating.setText(String.format("(%s)", dataBook.getTotalRatings()));
        tvQuantitySold.setText(String.format("%d %s", dataBook.getSold(), getString(R.string.sold)));

        if (dataBook.getAmount() == 0){
            tvInStockOrNot.setText(getString(R.string.out_of_stock));
            tvInStockOrNot.setTextColor(Color.parseColor("#BDBDBD"));
        } else {
            tvInStockOrNot.setText(getString(R.string.in_stock));
        }

        Picasso.get().load(dataPublisher.getLogo()).into(ivLogoPublisher);
        tvShopName.setText(dataPublisher.getName());
        tvShopLocation.setText(dataPublisher.getLocation());

        if (dataPublisher.getReply() < 60)
            tvShopReplyWithin.setText(String.format("%s %d %s", getString(R.string.reply_within), dataPublisher.getReply(), getString(R.string.minute)));
        else
            tvShopReplyWithin.setText(String.format("%s %d %s", getString(R.string.reply_within), dataPublisher.getReply() / 60, getString(R.string.minute)));
        tvShopRating.setText(String.format("%.1f", dataPublisher.getRating()));

        if (AppUtil.numDays(dataPublisher.getWorked()) < 30)
            tvShopWorked.setText(String.format("%d %s", AppUtil.numDays(dataPublisher.getWorked()), getString(R.string.day)));
        else if (AppUtil.numDays(dataPublisher.getWorked()) < 365)
            tvShopWorked.setText(String.format("%d %s", (int) AppUtil.numDays(dataPublisher.getWorked())/30, getString(R.string.month)));
        else
            tvShopWorked.setText(String.format("%d %s", (int) AppUtil.numDays(dataPublisher.getWorked()) / 365, getString(R.string.year)));

        if (AppUtil.numDays(dataBook.getDatePosted()) < 30) {
            tvDatePosted.setText(String.format("%d %s", AppUtil.numDays(dataBook.getDatePosted()), getString(R.string.day)));
            layoutUpcoming.setVisibility(View.VISIBLE);
        }
        else if (AppUtil.numDays(dataBook.getDatePosted()) < 365) {
            tvDatePosted.setText(String.format("%d %s", (int) AppUtil.numDays(dataBook.getDatePosted()) / 30, getString(R.string.month)));
            layoutUpcoming.setVisibility(View.INVISIBLE);
        }
        else {
            tvDatePosted.setText(String.format("%d %s", (int) AppUtil.numDays(dataBook.getDatePosted())/365, getString(R.string.year)));
            layoutUpcoming.setVisibility(View.INVISIBLE);
        }

        setupCart();
        setButton();
    }

    @SuppressLint("DefaultLocale")
    public void setupCart(){
        if (databaseCart.getCarts().size() != 0){
            tvNumCart.setText(String.format("%d", databaseCart.getCarts().size()));
        } else
            tvNumCart.setVisibility(View.GONE);
    }

    public void setAboutDetails(){
        tvDescribe.setMovementMethod(LinkMovementMethod.getInstance());
        tvDescribe.setText(Html.fromHtml(describe, new URIImagePasser(BookDetailActivity.this, tvDescribe), null));
        BookDetailAdapter detailAdapter = new BookDetailAdapter(listDetails, this);
        viewListDetails.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        viewListDetails.setAdapter(detailAdapter);
    }

    public void setButton(){
        if (dataBook.getAmount() > 0){
            btnAddCart.setEnabled(true);
            btnAddCart.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            btnAddCart.setBackgroundResource(R.drawable.custom_button_add_shopping_cart);

            btnBuyNow.setEnabled(true);
            btnBuyNow.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            btnBuyNow.setBackgroundResource(R.drawable.custom_button_buy_now);
        } else {
            btnAddCart.setEnabled(false);
            btnAddCart.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
            btnAddCart.setBackgroundResource(R.drawable.custom_button_hidden);

            btnBuyNow.setEnabled(false);
            btnBuyNow.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
            btnBuyNow.setBackgroundResource(R.drawable.custom_button_hidden);

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_menu_in_detail)
            showMenuPopup();
        if (view.getId() == R.id.layout_shopping_cart)
            startCart();
        if (view.getId() == R.id.ib_exit_detail)
            finish();
        if (view.getId() == R.id.btn_add_cart)
            addCart();
    }

    public void startCart() {
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @SuppressLint("NonConstantResourceId")
    public void showMenuPopup(){
        PopupMenu popupMenu = new PopupMenu(this, ivMenu);
        popupMenu.getMenuInflater().inflate(R.menu.in_detail_menu, popupMenu.getMenu());
        if (Common.currentUser == null)
            popupMenu.getMenu().getItem(2).setTitle(R.string.login);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(BookDetailActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            switch (item.getItemId()){
                case R.id.m_share:
                    Toast.makeText(BookDetailActivity.this, "Share Link", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.m_home:
                    bundle.putInt("fragment", 1);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.m_my_account:
                    if (Common.currentUser == null) {
                        AppUtil.startLoginPage(this);
                        return true;
                    }
                    bundle.putInt("fragment", 3);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.m_help:
                    Toast.makeText(BookDetailActivity.this, "Help Page", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
    }

    public void addCart() {
        if (Common.currentUser == null){
            AppUtil.startLoginPage(this);
            return;
        }
        List<Order> orderList = databaseCart.getCarts();
        int quantity = 0;
        boolean exists = false;
        for (int i = 0; i < orderList.size(); ++i) {
            if (orderList.get(i).getBookId().equals(dataBook.getId())) {
                exists = true;
                quantity = orderList.get(i).getBookQuantity();
            }
        }

        int finalQuantity = quantity;
        boolean finalExists = exists;
        database.getReference("book").child(dataBook.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int amount = snapshot.child("amount").getValue(Integer.class);
                if (amount > finalQuantity) {
                    databaseCart.addCart(new Order(
                            dataBook.getId(),
                            dataBook.getName(),
                            dataBook.getImage().get(0),
                            dataPublisher.getId(),
                            1,
                            dataBook.getOriginalPrice(),
                            dataBook.getDiscount()
                    ));
                    Common.currentUser.setCartList(databaseCart.getCarts());

                    String[] mode = {"mybooks", "google", "facebook"};
                    database.getReference("user").child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("cartList").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (finalExists) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    if (snapshot1.child("bookId").getValue(String.class).equals(dataBook.getId())) {
                                        int quantity = snapshot1.child("bookQuantity").getValue(Integer.class);
                                        snapshot1.child("bookQuantity").getRef().setValue(quantity + 1);
                                    }
                                }
                            } else {
                                snapshot.child(String.valueOf(snapshot.getChildrenCount())).child("bookId").getRef().setValue(dataBook.getId());
                                snapshot.child(String.valueOf(snapshot.getChildrenCount())).child("bookQuantity").getRef().setValue(1);
                            }
                            openDialog();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Toast.makeText(BookDetailActivity.this, String.format(getString(R.string.limit_product), amount), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openDialog(){
        Dialog dialog = new Dialog(this, R.style.FullScreenDialog);
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

        Picasso.get().load(dataBook.getImage().get(0)).into(ivImage);
        tvBookName.setText(dataBook.getName());
        tvPublisherName.setText(dataPublisher.getName());
        tvBookPrice.setText(String.format("%sđ", AppUtil.convertNumber(dataBook.getReducedPrice())));

        btnCartPage.setOnClickListener(view -> {
            dialog.dismiss();
            startCart();
        });
        dialog.show();
    }

    public static class URIImagePasser implements Html.ImageGetter {
        Context context;
        TextView mTextView;

        public URIImagePasser(Context context, TextView mTextView) {
            this.context = context;
            this.mTextView = mTextView;
        }

        @Override
        public Drawable getDrawable(String s) {
            final LevelListDrawable drawable = new LevelListDrawable();
            Glide.with(context).load(s).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null){
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                        drawable.addLevel(1, 1, bitmapDrawable);
                        drawable.setBounds(0, 0, context.getResources().getDisplayMetrics().widthPixels - (40*(context.getResources().getDisplayMetrics().densityDpi)/160), (context.getResources().getDisplayMetrics().widthPixels*resource.getHeight())/resource.getWidth());
                        drawable.setLevel(1);
                        mTextView.invalidate();
                        mTextView.setText(mTextView.getText());
                    }
                }
            });
            return drawable;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}