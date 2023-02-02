package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Publisher;

import java.util.ArrayList;

public class StaffHomePageActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private ImageView ivStore;
    private TextView tvStaff;
    private TextView tvEditShopInfo;
    private TextView tvProduct;
    private TextView tvAddProduct;
    private TextView tvAllProduct;
    private TextView tvOrder;
    private TextView tvOrderWaiting;
    private TextView tvOrderShipping;
    private TextView tvOrderCancel;
    private TextView tvAllOrder;
    private LinearLayout layoutOptionProduct;
    private LinearLayout layoutOptionOrder;

    private boolean clickProduct = false;
    private boolean clickOrder = false;
    private Publisher publisher;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home_page);
        AppUtil.changeStatusBarColor(this, getColor(R.color.reduced_price));

        init();
        getDataPublisher();
        setupPermission();

        ibBack.setOnClickListener(this);
        ivStore.setOnClickListener(this);
        tvStaff.setOnClickListener(this);
        tvProduct.setOnClickListener(this);
        tvAddProduct.setOnClickListener(this);
        tvAllProduct.setOnClickListener(this);
        tvOrder.setOnClickListener(this);
        tvOrderWaiting.setOnClickListener(this);
        tvOrderShipping.setOnClickListener(this);
        tvOrderCancel.setOnClickListener(this);
        tvAllOrder.setOnClickListener(this);
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_store_manager);
        ivStore = findViewById(R.id.iv_store);
        tvStaff = findViewById(R.id.tv_staff);
        tvEditShopInfo = findViewById(R.id.tv_edit_shop_info);
        tvProduct = findViewById(R.id.tv_product);
        tvAddProduct = findViewById(R.id.tv_option_add_product);
        tvAllProduct = findViewById(R.id.tv_option_all_product);
        tvOrder = findViewById(R.id.tv_order);
        tvOrderWaiting = findViewById(R.id.tv_option_order_waiting);
        tvOrderShipping = findViewById(R.id.tv_option_order_shipping);
        tvOrderCancel = findViewById(R.id.tv_option_order_cancel);
        tvAllOrder = findViewById(R.id.tv_option_all_order);
        layoutOptionProduct = findViewById(R.id.layout_option_product);
        layoutOptionOrder = findViewById(R.id.layout_option_order);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("publisher");
    }

    public void getDataPublisher(){
        reference.child(Common.currentUser.getStaff().getPublisherId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                publisher = snapshot.getValue(Publisher.class);
                ivStore.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startOrderManagerPage(int tabSelect){
        Intent intent = new Intent(StaffHomePageActivity.this, OrderManagerActivity.class);
        intent.putExtra("tabSelect", tabSelect);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void fullPermission(){
        tvStaff.setVisibility(View.VISIBLE);
        tvEditShopInfo.setVisibility(View.VISIBLE);
        tvProduct.setVisibility(View.VISIBLE);
        tvOrder.setVisibility(View.VISIBLE);
    }

    public void setupPermission(){
        if (Common.currentUser.getStaff().getPermission().isHighPermission()) {
            fullPermission();
            return;
        }

        tvStaff.setVisibility(Common.currentUser.getStaff().getPermission().isStaffManager() ? View.VISIBLE : View.GONE);
        tvEditShopInfo.setVisibility(Common.currentUser.getStaff().getPermission().isShopManager() ? View.VISIBLE : View.GONE);
        tvOrder.setVisibility(Common.currentUser.getStaff().getPermission().isOrderManager() ? View.VISIBLE : View.GONE);
        tvProduct.setVisibility(Common.currentUser.getStaff().getPermission().isProductManager() ? View.VISIBLE : View.GONE);
    }

    public void startThisShop(){
        Intent intent = new Intent(StaffHomePageActivity.this, ShopDetailActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("publisher", new Gson().toJson(publisher));
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void clickTextViewProduct(){
        if (clickProduct){
            tvProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_product, 0, R.drawable.ic_down, 0);
            layoutOptionProduct.setVisibility(View.GONE);
        } else {
            tvProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_product, 0, R.drawable.ic_up, 0);
            layoutOptionProduct.setVisibility(View.VISIBLE);

            tvOrder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notepad, 0, R.drawable.ic_down, 0);
            layoutOptionOrder.setVisibility(View.GONE);
        }
        clickProduct = !clickProduct;
        clickOrder = false;
    }

    public void clickTextViewOrder(){
        if (clickOrder){
            tvOrder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notepad, 0, R.drawable.ic_down, 0);
            layoutOptionOrder.setVisibility(View.GONE);
        } else {
            tvOrder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notepad, 0, R.drawable.ic_up, 0);
            layoutOptionOrder.setVisibility(View.VISIBLE);

            tvProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_product, 0, R.drawable.ic_down, 0);
            layoutOptionProduct.setVisibility(View.GONE);
        }
        clickOrder = !clickOrder;
        clickProduct = false;
    }

    public void startStaffManager(){
        Intent intent = new Intent(StaffHomePageActivity.this, StaffManagerActivity.class);
        Bundle bundle = new Bundle();

        bundle.putStringArrayList("staff", (ArrayList<String>) publisher.getStaff());
        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startAddProduct(){
        Intent intent = new Intent(StaffHomePageActivity.this, EditProductActivity.class);
        Bundle bundle = new Bundle();

        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startAllProductActivity(){
        Intent intent = new Intent(StaffHomePageActivity.this, ProductManagerActivity.class);

        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_exit_store_manager:
                finish();
                break;
            case R.id.iv_store:
                startThisShop();
                break;
            case R.id.tv_staff:
                startStaffManager();
                break;
            case R.id.tv_product:
                clickTextViewProduct();
                break;
            case R.id.tv_option_add_product:
                startAddProduct();
                break;
            case R.id.tv_option_all_product:
                startAllProductActivity();
                break;
            case R.id.tv_order:
                clickTextViewOrder();
                break;
            case R.id.tv_option_order_waiting:
                startOrderManagerPage(0);
                break;
            case R.id.tv_option_order_shipping:
                startOrderManagerPage(1);
                break;
            case R.id.tv_option_order_cancel:
                startOrderManagerPage(2);
                break;
            case R.id.tv_option_all_order:
                startOrderManagerPage(4);
                break;
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