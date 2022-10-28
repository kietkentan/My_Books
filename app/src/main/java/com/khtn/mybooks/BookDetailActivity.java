package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.adapter.BookDetailAdapter;
import com.khtn.mybooks.adapter.ListImageAdapter;
import com.khtn.mybooks.model.Book;
import com.khtn.mybooks.model.Publisher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    private ImageButton ibAddFavorite;
    private ImageButton ibBack;
    private RatingBar barRatingBook;
    private ShapeableImageView ivLogoPublisher;
    private RecyclerView listDetails;
    private FrameLayout layoutUpcoming;

    private ListImageAdapter imageAdapter;
    private BookDetailAdapter detailAdapter;

    public String id;
    public String nhaxuatban;
    private Book book;
    private Publisher publisher;
    private List<List<String>> lists; // list details
    private String describe;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        init();
        getData();
        ivMenu.setOnClickListener(this);
        ibBack.setOnClickListener(this);
    }

    public void getData(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (dataSnapshot.getKey().equals(nhaxuatban)) {
                        publisher = dataSnapshot.getValue(Publisher.class);
                        dataSnapshot.child("sach").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    if (dataSnapshot1.getKey().equals(id)){
                                        book = dataSnapshot1.getValue(Book.class);
                                        dataSnapshot1.child("detail").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot2:snapshot.getChildren()) {
                                                    if (dataSnapshot2.getKey().equals("describe")){
                                                        describe = dataSnapshot2.getValue(String.class);
                                                    } else {
                                                        List<String> list = new ArrayList<>();
                                                        list.add(dataSnapshot2.getKey());
                                                        list.add(dataSnapshot2.getValue(String.class));
                                                        lists.add(list);
                                                    }
                                                }
                                                setAboutDetails();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                                setDetails();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference("nhaxuatban");
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        nhaxuatban = intent.getStringExtra("nhaxuatban");
        lists = new ArrayList<>();

        ivMenu = (ImageView) findViewById(R.id.iv_menu_in_detail);
        rcImages = (ViewPager2) findViewById(R.id.list_img);
        tvPrice = (TextView) findViewById(R.id.tv_price_book);
        tvOriginalPrice = (TextView) findViewById(R.id.tv_original_price_book);
        tvDiscount = (TextView) findViewById(R.id.tv_discount_book);
        tvNameBook = (TextView) findViewById(R.id.tv_name_book);
        tvTotalRating = (TextView) findViewById(R.id.tv_total_rating);
        tvTotalNumberPeopleRating = (TextView) findViewById(R.id.tv_total_number_people_rating);
        tvQuantitySold = (TextView) findViewById(R.id.tv_quantity_sold);
        tvInStockOrNot = (TextView) findViewById(R.id.tv_in_stock_or_not);
        tvShopName = (TextView) findViewById(R.id.tv_name_publisher);
        tvShopLocation = (TextView) findViewById(R.id.tv_location_publisher);
        tvShopReplyWithin = (TextView) findViewById(R.id.tv_reply_within);
        tvShopRating = (TextView) findViewById(R.id.tv_shop_rating);
        tvShopWorked = (TextView) findViewById(R.id.tv_worked);
        tvDatePosted = (TextView) findViewById(R.id.tv_date_posted);
        tvDescribe = (TextView) findViewById(R.id.tv_describe);
        tvPosition = (TextView) findViewById(R.id.tv_position);
        barRatingBook = (RatingBar) findViewById(R.id.bar_rating_book);
        ibAddFavorite = (ImageButton) findViewById(R.id.ib_add_favorite);
        ibBack = (ImageButton) findViewById(R.id.ib_exit_detail);
        ivLogoPublisher = (ShapeableImageView) findViewById(R.id.iv_avatar_publisher);
        listDetails = (RecyclerView) findViewById(R.id.list_details);
        layoutUpcoming = (FrameLayout) findViewById(R.id.layout_upcoming);
    }

    public void setDetails(){
        imageAdapter = new ListImageAdapter(book.getImage());
        rcImages.setAdapter(imageAdapter);
        rcImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvPosition.setText(String.format("%d/%d", position + 1, book.getImage().size()));
            }
        });
        if (book.getDiscountPercentage() == 0){
            tvPrice.setText(String.format("%sđ", AppUtil.convertNumber(book.getOriginalPrice())));
            tvPrice.setTextColor(Color.parseColor("#FF000000"));
            tvOriginalPrice.setVisibility(View.INVISIBLE);
            tvDiscount.setVisibility(View.INVISIBLE);
        } else {
            tvPrice.setText(String.format("%sđ", AppUtil.convertNumber(book.getReducedPrice())));
            tvOriginalPrice.setText(String.format("%sđ", AppUtil.convertNumber(book.getOriginalPrice())));
            tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvDiscount.setText(String.format("-%d%%", book.getDiscountPercentage()));
        }
        tvNameBook.setText(book.getName());
        barRatingBook.setRating(book.getTotalRatingScore());
        tvTotalRating.setText(String.format("%.1f", book.getTotalRatingScore()));
        tvTotalNumberPeopleRating.setText(String.format("(%s)", book.getTotalRatings()));
        tvQuantitySold.setText(String.format("%d %s", book.getSold(), getString(R.string.sold)));
        if (book.getAmount() == 0){
            tvInStockOrNot.setText(getString(R.string.out_of_stock));
            tvInStockOrNot.setTextColor(Color.parseColor("#BDBDBD"));
        } else {
            tvInStockOrNot.setText(getString(R.string.in_stock));
        }
        Picasso.get().load(publisher.getLogo()).into(ivLogoPublisher);
        tvShopName.setText(publisher.getName());
        tvShopLocation.setText(publisher.getLocation());
        if (publisher.getReply() < 60)
            tvShopReplyWithin.setText(String.format("%s %d %s", getString(R.string.reply_within), publisher.getReply(), getString(R.string.minute)));
        else
            tvShopReplyWithin.setText(String.format("%s %d %s", getString(R.string.reply_within), (int) publisher.getReply() / 60, getString(R.string.minute)));
        tvShopRating.setText(String.format("%.1f", publisher.getRating()));
        if (AppUtil.numDays(publisher.getWorked()) < 30)
            tvShopWorked.setText(String.format("%d %s", publisher.getWorked(), getString(R.string.day)));
        else if (AppUtil.numDays(publisher.getWorked()) < 365)
            tvShopWorked.setText(String.format("%d %s", (int) AppUtil.numDays(publisher.getWorked())/30, getString(R.string.month)));
        else
            tvShopWorked.setText(String.format("%d %s", (int) AppUtil.numDays(publisher.getWorked()) / 365, getString(R.string.year)));
        if (AppUtil.numDays(book.getDatePosted()) < 30) {
            tvDatePosted.setText(String.format("%d %s", AppUtil.numDays(book.getDatePosted()), getString(R.string.day)));
            layoutUpcoming.setVisibility(View.VISIBLE);
        }
        else if (AppUtil.numDays(book.getDatePosted()) < 365) {
            tvDatePosted.setText(String.format("%d %s", (int) AppUtil.numDays(book.getDatePosted()) / 30, getString(R.string.month)));
            layoutUpcoming.setVisibility(View.INVISIBLE);
        }
        else {
            tvDatePosted.setText(String.format("%d %s", (int) AppUtil.numDays(book.getDatePosted())/365, getString(R.string.year)));
            layoutUpcoming.setVisibility(View.INVISIBLE);
        }
    }

    public void setAboutDetails(){
        tvDescribe.setMovementMethod(LinkMovementMethod.getInstance());
        tvDescribe.setText(Html.fromHtml(describe, new URLImagePaser(BookDetailActivity.this, tvDescribe), null));
        detailAdapter = new BookDetailAdapter(lists, this);
        listDetails.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        listDetails.setAdapter(detailAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_menu_in_detail)
            showMenuPopup();
        if (view.getId() == R.id.ib_exit_detail)
            finish();
    }

    public void showMenuPopup(){
        PopupMenu popupMenu = new PopupMenu(this, ivMenu);
        popupMenu.getMenuInflater().inflate(R.menu.in_detail_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });
    }

    public class URLImagePaser implements Html.ImageGetter {
        Context context;
        TextView mTextView;

        public URLImagePaser(Context context, TextView mTextView) {
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
}