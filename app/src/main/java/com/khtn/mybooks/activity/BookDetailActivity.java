package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
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
import com.google.gson.Gson;
import com.khtn.mybooks.databases.DatabaseViewed;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.BookDetailAdapter;
import com.khtn.mybooks.adapter.ListImageAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Book;
import com.khtn.mybooks.model.BookItem;
import com.khtn.mybooks.model.Detail;
import com.khtn.mybooks.model.Order;
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
    private TextView tvNumCart;
    private TextView tvSearch;
    private TextView tvFollow;
    private ImageButton ibAddFavorite;
    private ImageButton ibBack;
    private RatingBar barRatingBook;
    private ShapeableImageView ivLogoPublisher;
    private RecyclerView viewListDetails;
    private AppCompatButton btnAddCart;
    private AppCompatButton btnBuyNow;
    private FrameLayout layoutUpcoming;
    private FrameLayout layoutCart;
    private ConstraintLayout layoutPublisher;
    private ConstraintLayout layoutSeeData;
    private FrameLayout layoutLoading;

    public String id;
    public String publisher;
    private Book dataBook;
    private Publisher dataPublisher;
    private List<List<String>> listDetails;
    private String[] mode;

    private DatabaseCart databaseCart;
    private DatabaseViewed databaseViewed;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        init();

        getData();

        tvSearch.setOnClickListener(BookDetailActivity.this);
        tvFollow.setOnClickListener(BookDetailActivity.this);
        ivMenu.setOnClickListener(BookDetailActivity.this);
        layoutCart.setOnClickListener(BookDetailActivity.this);
        layoutPublisher.setOnClickListener(BookDetailActivity.this);
        ibBack.setOnClickListener(BookDetailActivity.this);
        btnAddCart.setOnClickListener(BookDetailActivity.this);
        btnBuyNow.setOnClickListener(BookDetailActivity.this);
        ibAddFavorite.setOnClickListener(BookDetailActivity.this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupCart();
    }

    public void getData(){
        layoutLoading.setVisibility(View.VISIBLE);
        layoutSeeData.setVisibility(View.GONE);
        database.getReference("publisher").child(publisher).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    dataPublisher = snapshot.getValue(Publisher.class);
                database.getReference("book").child(id).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            dataBook = snapshot.getValue(Book.class);

                            databaseViewed.addViewed(new BookItem(dataBook.getImage(),
                                    dataBook.getOriginalPrice(),
                                    dataBook.getDiscount(),
                                    dataBook.getAmount(),
                                    dataBook.getName(),
                                    dataBook.getDatePosted(),
                                    dataBook.getId(),
                                    dataBook.getPublisher()));

                            listDetails = getListDetail(dataBook.getDetail());
                            setAboutDetails();
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

    public List<List<String>> getListDetail(Detail detail){
        List<List<String>> lists = new ArrayList<>();
        if (detail.getAuthor() != null){
            List<String> list = new ArrayList<>();
            list.add("author");
            list.add(detail.getAuthor());
            lists.add(list);
        }

        if (detail.getAgeRange() != null){
            List<String> list = new ArrayList<>();
            list.add("ageRange");
            list.add(detail.getAgeRange());
            lists.add(list);
        }

        if (detail.getPages() > 0){
            List<String> list = new ArrayList<>();
            list.add("pages");
            list.add(String.valueOf(detail.getPages()));
            lists.add(list);
        }

        if (detail.getSize() != null){
            List<String> list = new ArrayList<>();
            list.add("size");
            list.add(detail.getSize());
            lists.add(list);
        }

        if (detail.getType() != null){
            List<String> list = new ArrayList<>();
            list.add("type");
            list.add(detail.getType());
            lists.add(list);
        }

        if (detail.getWeight() > 0){
            List<String> list = new ArrayList<>();
            list.add("weight");
            list.add(String.valueOf(detail.getWeight()));
            lists.add(list);
        }

        return lists;
    }

    public void init(){
        databaseCart = new DatabaseCart(BookDetailActivity.this);
        databaseViewed = new DatabaseViewed(BookDetailActivity.this);
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        publisher = intent.getStringExtra("publisher");
        listDetails = new ArrayList<>();
        mode = getResources().getStringArray(R.array.mode_login);

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
        tvSearch = findViewById(R.id.tv_search_item);
        tvFollow = findViewById(R.id.tv_follow);
        barRatingBook = findViewById(R.id.bar_rating_book);
        ibAddFavorite = findViewById(R.id.ib_add_favorite);
        ibBack = findViewById(R.id.ib_exit_detail);
        ivLogoPublisher = findViewById(R.id.iv_avatar_publisher);
        viewListDetails = findViewById(R.id.list_details);
        btnAddCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        layoutUpcoming = findViewById(R.id.layout_upcoming);
        layoutCart = findViewById(R.id.layout_shopping_cart);
        layoutPublisher = findViewById(R.id.layout_publisher);
        layoutLoading = findViewById(R.id.layout_loading_detail);
        layoutSeeData = findViewById(R.id.layout_see_detail);
    }

    @SuppressLint({"DefaultLocale", "ResourceType"})
    public void setDetails(){
        ListImageAdapter imageAdapter = new ListImageAdapter(dataBook.getImage());
        rcImages.setAdapter(imageAdapter);
        rcImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvPosition.setText(String.format(getString(R.string.fraction), position + 1, dataBook.getImage().size()));
            }
        });

        if (dataBook.getDiscount() == 0){
            tvPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(dataBook.getOriginalPrice())));
            tvPrice.setTextColor(getColor(R.color.black));
            tvOriginalPrice.setVisibility(View.INVISIBLE);
            tvDiscount.setVisibility(View.INVISIBLE);
        } else {
            tvPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(dataBook.getReducedPrice())));
            tvOriginalPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(dataBook.getOriginalPrice())));
            tvOriginalPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvDiscount.setText(String.format(getString(R.string.book_discount), dataBook.getDiscount()));
        }

        tvNameBook.setText(dataBook.getName());
        barRatingBook.setRating(dataBook.getRating().getScore());
        tvTotalRating.setText(String.format(getString(R.string.rating_score), dataBook.getRating().getScore()));
        tvTotalNumberPeopleRating.setText(String.format(getString(R.string.people_rating), dataBook.getRating().getTurn()));
        tvQuantitySold.setText(String.format(getString(R.string.sold), dataBook.getSold()));

        if (dataBook.getAmount() == 0){
            tvInStockOrNot.setText(getString(R.string.out_of_stock));
            tvInStockOrNot.setTextColor(getColor(R.color.text_hint));
        } else {
            tvInStockOrNot.setText(getString(R.string.in_stock));
        }

        Picasso.get().load(dataPublisher.getLogo()).into(ivLogoPublisher);
        tvShopName.setText(dataPublisher.getName());
        tvShopLocation.setText(dataPublisher.getLocation().getProvinces_cities().getName_with_type().replace(getString(R.string.cities), getString(R.string.cities_sort)));

        if (dataPublisher.getReply() < 60)
            tvShopReplyWithin.setText(String.format(getString(R.string.reply_within), dataPublisher.getReply(), getString(R.string.minute)));
        else
            tvShopReplyWithin.setText(String.format(getString(R.string.reply_within), dataPublisher.getReply() / 60, getString(R.string.hour)));
        tvShopRating.setText(String.format(getString(R.string.rating_score), dataPublisher.getRating().getScore()));

        if (AppUtil.numDays(dataPublisher.getWorked()) < 30)
            tvShopWorked.setText(String.format(getString(R.string.shop_worked), AppUtil.numDays(dataPublisher.getWorked()), getString(R.string.day)));
        else if (AppUtil.numDays(dataPublisher.getWorked()) < 365)
            tvShopWorked.setText(String.format(getString(R.string.shop_worked), (int) AppUtil.numDays(dataPublisher.getWorked())/30, getString(R.string.month)));
        else
            tvShopWorked.setText(String.format(getString(R.string.shop_worked), (int) AppUtil.numDays(dataPublisher.getWorked()) / 365, getString(R.string.year)));

        if (AppUtil.numDays(dataBook.getDatePosted()) < 30) {
            tvDatePosted.setText(String.format(getString(R.string.shop_worked), AppUtil.numDays(dataBook.getDatePosted()), getString(R.string.day)));
            layoutUpcoming.setVisibility(View.VISIBLE);
        }
        else if (AppUtil.numDays(dataBook.getDatePosted()) < 365) {
            tvDatePosted.setText(String.format(getString(R.string.shop_worked), (int) AppUtil.numDays(dataBook.getDatePosted()) / 30, getString(R.string.month)));
            layoutUpcoming.setVisibility(View.INVISIBLE);
        }
        else {
            tvDatePosted.setText(String.format(getString(R.string.shop_worked), (int) AppUtil.numDays(dataBook.getDatePosted())/365, getString(R.string.year)));
            layoutUpcoming.setVisibility(View.INVISIBLE);
        }

        setupFavoriteButton();

        setupCart();
        setButton();
    }

    private boolean checkFavoriteList(){
        if (Common.currentUser == null || Common.currentUser.getList_favorite() == null || Common.currentUser.getList_favorite().size() == 0)
            return false;

        for (Book book : Common.currentUser.getList_favorite()) {
            if (book.getId().equals(dataBook.getId()))
                return true;
        }
        return false;
    }

    public void setupFavoriteButton(){
        if (checkFavoriteList())
            ibAddFavorite.setBackgroundResource(R.drawable.ic_favorite_added);
        else
            ibAddFavorite.setBackgroundResource(R.drawable.ic_favorite);
    }

    @SuppressLint("DefaultLocale")
    public void setupCart(){
        int i = databaseCart.getCarts().size();
        if (i > 0) {
            tvNumCart.setVisibility(View.VISIBLE);
            tvNumCart.setText(String.format(getString(R.string.num), i));
        }
        else
            tvNumCart.setVisibility(View.GONE);
    }

    public void setAboutDetails(){
        if (dataBook.getDetail().getDescribe() != null) {
            tvDescribe.setMovementMethod(LinkMovementMethod.getInstance());
            tvDescribe.setText(Html.fromHtml(dataBook.getDetail().getDescribe(), new URIImagePasser(BookDetailActivity.this, tvDescribe), null));
        }
        BookDetailAdapter detailAdapter = new BookDetailAdapter(listDetails, this);
        viewListDetails.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        viewListDetails.setAdapter(detailAdapter);
        layoutLoading.setVisibility(View.GONE);
        layoutSeeData.setVisibility(View.VISIBLE);
    }

    public void setButton(){
        if (Common.currentUser != null && Common.currentUser.getStaff() != null) {
            btnAddCart.setEnabled(false);
            btnAddCart.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
            btnAddCart.setBackgroundResource(R.drawable.custom_button_hidden);

            btnBuyNow.setEnabled(false);
            btnBuyNow.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
            btnBuyNow.setBackgroundResource(R.drawable.custom_button_hidden);

            btnBuyNow.setText(String.format(getString(R.string.status), getString(R.string.buy_now)));
            btnAddCart.setText(String.format(getString(R.string.status), getString(R.string.add_shopping_cart)));
            return;
        }

        boolean checkDateTimeSell = AppUtil.checkDateTimeSell(dataBook.getTimeSell());
        boolean checkStaffInShop = Common.currentUser != null && Common.currentUser.getStaff() != null;

        if (dataBook.getAmount() > 0 && checkDateTimeSell && !checkStaffInShop){
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

            btnBuyNow.setText(String.format(getString(R.string.status), checkDateTimeSell ? getString(R.string.buy_now) : getString(R.string.coming_soon)));
            btnAddCart.setText(String.format(getString(R.string.status), checkDateTimeSell ? getString(R.string.add_shopping_cart) : getString(R.string.coming_soon)));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menu_in_detail:
                showMenuPopup();
                break;
            case R.id.layout_shopping_cart:
                startCart();
                break;
            case R.id.ib_exit_detail:
                finish();
                break;
            case R.id.btn_add_cart:
                addCart();
                break;
            case R.id.ib_add_favorite:
                addFavoriteItem();
                break;
            case R.id.tv_search_item:
                startSearchItemPage();
                break;
            case R.id.tv_follow:
            case R.id.layout_publisher:
                startShopDetail();
                break;
            case R.id.btn_buy_now:
                startCompletePayment();
                break;
        }
    }

    private void addFavoriteItem() {
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        database.getReference("user").child(mode[Common.modeLogin - 1]).
                child(Common.currentUser.getId()).child("list_favorite").
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Book> books = new ArrayList<>();
                boolean check = false;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Book book = snapshot1.getValue(Book.class);
                    if (!book.getId().equals(dataBook.getId())) {
                        books.add(book);
                        continue;
                    }
                    check = true;
                }
                if (!check) {
                    snapshot.child(String.valueOf(snapshot.getChildrenCount())).getRef().setValue(dataBook);
                    if (Common.currentUser.getList_favorite() == null)
                        Common.currentUser.setList_favorite(new ArrayList<>());
                    Common.currentUser.getList_favorite().add(dataBook);
                    setupFavoriteButton();
                    Toast.makeText(BookDetailActivity.this, R.string.added_favorite, Toast.LENGTH_SHORT).show();
                    return;
                }
                snapshot.getRef().removeValue();
                if (books.size() > 0)
                    snapshot.getRef().setValue(books);
                Toast.makeText(BookDetailActivity.this, R.string.un_added_favorite, Toast.LENGTH_SHORT).show();

                Common.currentUser.removeFavoriteById(dataBook.getId());
                setupFavoriteButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void startSearchItemPage(){
        Intent intent = new Intent(BookDetailActivity.this, SearchItemActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startCompletePayment(){
        if (Common.currentUser == null){
            AppUtil.startLoginPage(this);
            return;
        } else if (Common.addressNow == null) {
            openDialogAddAddress();
            return;
        }

        Intent intent = new Intent(BookDetailActivity.this, CompletePaymentActivity.class);
        Bundle bundle = new Bundle();

        bundle.putBoolean("buy_now", true);
        bundle.putString("order", new Gson().toJson(new Order(dataBook, 1)));
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startShopDetail(){
        Intent intent = new Intent(BookDetailActivity.this, ShopDetailActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("publisher", new Gson().toJson(dataPublisher));
        intent.putExtras(bundle);
        startActivity(intent);
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
        database.getReference("book").child(dataBook.getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
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

                    database.getReference("user").child(mode[Common.modeLogin - 1]).
                            child(Common.currentUser.getId()).child("cartList").
                            addListenerForSingleValueEvent(new ValueEventListener() {
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
        setupCart();

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
        tvBookPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(dataBook.getReducedPrice())));

        btnCartPage.setOnClickListener(view -> {
            dialog.dismiss();
            startCart();
        });
        dialog.show();
    }

    public void openDialogAddAddress(){
        Dialog dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_none_address);

        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnAddAddress = dialog.findViewById(R.id.btn_accept_add_address);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnAddAddress.setOnClickListener(v -> {
            startAddAddressPage();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void startAddAddressPage(){
        Intent intent = new Intent(BookDetailActivity.this, AddAddressActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("pos", -1);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
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