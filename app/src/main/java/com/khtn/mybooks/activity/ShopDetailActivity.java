package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.ViewPagerShopDetailAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.BookItem;
import com.khtn.mybooks.model.Publisher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TabLayout tab;
    private ViewPager2 view;
    private ImageButton ibBack;
    private ImageView ivLogo;
    private ImageView ivHome;
    private TextView tvSearch;              // not active
    private TextView tvNumCart;
    private TextView tvName;
    private TextView tvRatingScore;
    private TextView tvFollowed;
    private AppCompatButton btnFollow;
    private FrameLayout layoutCart;

    private List<BookItem> bookItemList;
    private Publisher publisher;
    private boolean check = false;
    private String[] mode;
    private int currentSelectedTab = 0;

    private DatabaseCart databaseCart;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        AppUtil.changeStatusBarColor(this, getColor(R.color.background_shop));

        init();
        setupPublisher();
        setupCart();
        setupButtonFollow();
        loadData();

        ibBack.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        layoutCart.setOnClickListener(this);
        ivHome.setOnClickListener(this);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentSelectedTab = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        setupViewPager();
        setCurrentItem();
    }

    public void init(){
        Bundle bundle = getIntent().getExtras();
        publisher = new Gson().fromJson(bundle.getString("publisher"), Publisher.class);
        databaseCart = new DatabaseCart(ShopDetailActivity.this);

        tab = findViewById(R.id.tab_in_shop);
        view = findViewById(R.id.vp_in_shop);
        ibBack = findViewById(R.id.ib_exit_shop_detail);
        ivLogo = findViewById(R.id.iv_publisher_avatar);
        ivHome = findViewById(R.id.iv_home_page);
        tvSearch = findViewById(R.id.tv_search_item_in_the_shop);
        tvNumCart = findViewById(R.id.tv_num_cart);
        tvName = findViewById(R.id.tv_name_publisher);
        tvRatingScore = findViewById(R.id.tv_total_rating_publisher);
        tvFollowed = findViewById(R.id.tv_total_follows);
        btnFollow = findViewById(R.id.btn_follow);
        layoutCart = findViewById(R.id.layout_shopping_cart);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");
        bookItemList = new ArrayList<>();
        mode = getResources().getStringArray(R.array.mode_login);
    }

    public void loadData(){
        reference.orderByChild("publisher").equalTo(publisher.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BookItem item = dataSnapshot.getValue(BookItem.class);
                    bookItemList.add(item);
                }
                setupViewPager();
                setupTabLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setupPublisher(){
        Picasso.get().load(publisher.getLogo()).into(ivLogo);
        tvName.setText(publisher.getName());
        tvRatingScore.setText(String.format(getString(R.string.rating_score), publisher.getRating().getScore()));
        tvFollowed.setText(String.format(getString(R.string.total_follows), publisher.getFollowed()));
    }

    public void setupViewPager(){
        ViewPagerShopDetailAdapter adapter = new ViewPagerShopDetailAdapter(ShopDetailActivity.this, bookItemList, publisher);
        view.setAdapter(adapter);
    }

    public void setCurrentItem(){
        tab.setScrollPosition(currentSelectedTab, 0f, true);
        view.setCurrentItem(currentSelectedTab);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tab, view, (tab, position) -> tab.setText(getResources().getStringArray(R.array.tab_shop)[position])).attach();
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

    public void setupButtonFollow(){
        if (Common.currentUser != null && Common.currentUser.getList_shopFollow() != null)
            if (Common.currentUser.getList_shopFollow().size() > 0)
                for (String str : Common.currentUser.getList_shopFollow())
                    if (str.equals(publisher.getId())) {
                        btnFollow.setText(getText(R.string.followed_this));
                        btnFollow.setBackgroundResource(R.drawable.custom_button_followed);
                        check = true;
                        return;
                    }

        check = false;
        btnFollow.setText(getText(R.string.follow));
        btnFollow.setBackgroundResource(R.drawable.custom_button_continue_appear);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_exit_shop_detail:
                finish();
                break;
            case R.id.layout_shopping_cart:
                startCart();
                break;
            case R.id.iv_home_page:
                startHome();
                finish();
                break;
            case R.id.btn_follow:
                follow();
                break;
        }
    }

    public void startCart() {
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        Intent intent = new Intent(ShopDetailActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startHome() {
        Intent intent = new Intent(ShopDetailActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("fragment", 1);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void follow(){
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        if (Common.currentUser.getList_shopFollow() != null) {
            if (check)
                for (int i = 0; i < Common.currentUser.getList_shopFollow().size(); ++i) {
                    if (publisher.getId().equals(Common.currentUser.getList_shopFollow().get(i))) {
                        Common.currentUser.getList_shopFollow().remove(i);
                        btnFollow.setText(getText(R.string.follow));
                        btnFollow.setBackgroundResource(R.drawable.custom_button_continue_appear);
                        check = false;
                        updateFollowed();
                        return;
                    }
                }
            else {
                Common.currentUser.getList_shopFollow().add(publisher.getId());
                check = true;
            }
        }
        else if (Common.currentUser.getList_shopFollow() == null){
            List<String> list = new ArrayList<>();
            list.add(publisher.getId());
            Common.currentUser.setList_shopFollow(list);
        }

        updateFollowed();
        btnFollow.setText(getText(R.string.followed_this));
        btnFollow.setBackgroundResource(R.drawable.custom_button_followed);
    }

    public void updateFollowed(){
        if (check){
            database.getReference("user").child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("list_shopFollow").child(String.valueOf(Common.currentUser.getList_shopFollow().size() - 1)).setValue(publisher.getId());
            return;
        }
        database.getReference("user").child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("list_shopFollow").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                if (Common.currentUser.getList_shopFollow().size() < 1)
                    return;

                for (int i = 0; i < Common.currentUser.getList_shopFollow().size(); ++i)
                    snapshot.getRef().child(String.format(getString(R.string.num), i)).setValue(Common.currentUser.getList_shopFollow().get(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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