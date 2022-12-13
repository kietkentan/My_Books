package com.khtn.mybooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.BookItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.databases.DatabaseViewed;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.BookItem;

import java.util.List;

public class RecentlyViewedActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvNumCart;
    private ImageButton ibBack;
    private ImageButton ibSearch;
    private ShapeableImageView ibCleanViewed;
    private RecyclerView recListRecentlyViewed;
    private FrameLayout layoutCart;

    private DatabaseCart databaseCart;
    private DatabaseViewed databaseViewed;

    private List<BookItem> bookList;
    private BookItemAdapter adapter;

    float xDown = 0, yDown = 0;
    float maxWidthPixel;
    float maxHeightPixel;
    boolean moved = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_viewed);
        AppUtil.changeStatusBarColor(this, "#E32127");

        init();
        setupCart();
        setupRecyclerViewListViewed();

        ibBack.setOnClickListener(this);
        ibSearch.setOnClickListener(this);
        layoutCart.setOnClickListener(this);

        ibCleanViewed.setOnTouchListener(onTouch);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void init(){
        maxWidthPixel = getResources().getDisplayMetrics().widthPixels;
        maxHeightPixel = getResources().getDisplayMetrics().heightPixels;
        Log.i("TAG_U", "init: " + maxWidthPixel + "|" + maxHeightPixel);

        databaseCart = new DatabaseCart(RecentlyViewedActivity.this);
        databaseViewed = new DatabaseViewed(RecentlyViewedActivity.this);

        tvNumCart = findViewById(R.id.tv_num_cart);
        ibBack = findViewById(R.id.ib_exit_recently_viewed);
        ibSearch = findViewById(R.id.ib_search_item);
        ibCleanViewed = findViewById(R.id.ib_clean_list_viewed);
        recListRecentlyViewed = findViewById(R.id.rec_list_recently_viewed);
        layoutCart = findViewById(R.id.layout_shopping_cart);

        int spanCount = (int) (maxWidthPixel/450);
        recListRecentlyViewed.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_exit_recently_viewed:
                finish();
                break;
            case R.id.ib_search_item:
                startSearchItemPage();
                break;
            case R.id.layout_shopping_cart:
                startCart();
                break;
        }
    }

    protected View.OnTouchListener onTouch = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_UP:
                    if (!moved){
                        databaseViewed.cleanViewed();
                        setupRecyclerViewListViewed();
                    } else {
                        float pX = ibCleanViewed.getX();
                        float pY = ibCleanViewed.getY();

                        if (pX < (maxWidthPixel / 2 - 60))
                            ibCleanViewed.setX(30);
                        else
                            ibCleanViewed.setX(maxWidthPixel - 150);

                        if (pY < 180)
                            ibCleanViewed.setY(180);
                        else if (pY > maxHeightPixel - 230)
                            ibCleanViewed.setY(maxHeightPixel - 230);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    xDown = event.getX();
                    yDown = event.getY();
                    moved = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float movedX, movedY;
                    movedX = event.getX();
                    movedY = event.getY();

                    float distanceX = movedX - xDown;
                    float distanceY = movedY - yDown;

                    if (distanceX != 0 || distanceY != 0) {
                        ibCleanViewed.setX(ibCleanViewed.getX() + distanceX);
                        ibCleanViewed.setY(ibCleanViewed.getY() + distanceY);
                        moved = true;
                    }
                    break;
            }
            return true;
        }
    };

    @SuppressLint("DefaultLocale")
    public void setupCart(){
        int i = databaseCart.getCarts().size();
        if (i > 0) {
            tvNumCart.setVisibility(View.VISIBLE);
            tvNumCart.setText(String.format("%d", i));
        }
        else
            tvNumCart.setVisibility(View.GONE);
    }

    public void setupRecyclerViewListViewed(){
        bookList = databaseViewed.getListsViewed();
        if (bookList.size() > 0){
            recListRecentlyViewed.setVisibility(View.VISIBLE);
            adapter = new BookItemAdapter(this, bookList);
            recListRecentlyViewed.setAdapter(adapter);
            ibCleanViewed.setVisibility(View.VISIBLE);
        } else {
            recListRecentlyViewed.setVisibility(View.GONE);
            ibCleanViewed.setVisibility(View.GONE);
        }
    }

    public void startSearchItemPage(){
        Intent intent = new Intent(RecentlyViewedActivity.this, SearchItemActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startCart(){
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        Intent intent = new Intent(RecentlyViewedActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}