package com.khtn.mybooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.BookOrderItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Request;

public class OrderDetailActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private TextView tvNumCart;
    private TextView tvStatus;
    private TextView tvNameUser;
    private TextView tvAddressUser;
    private TextView tvPublisher;
    private TextView tvTotalPrice1;
    private TextView tvTotalPrice2;
    private TextView tvStringTotalTemp;
    private TextView tvTempTotal;
    private TextView tvShipCost;
    private TextView tvCodeRequest;
    private TextView tvCopyCodeRequest;
    private TextView tvDateTime;
    private RecyclerView recListOrder;
    private FrameLayout layoutCart;
    private ConstraintLayout layoutTotalPrice1;
    private LinearLayout layoutTotalPrice2;
    private Request request;

    private BookOrderItemAdapter adapter;
    private boolean copied = false;
    private DatabaseCart databaseCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        init();
        setAddress();
        setOrderList();
        setDetailRequest();

        ibBack.setOnClickListener(v -> finish());
        tvTotalPrice1.setOnClickListener(v -> {
            layoutTotalPrice1.setVisibility(View.GONE);
            layoutTotalPrice2.setVisibility(View.VISIBLE);
        });
        tvCopyCodeRequest.setOnClickListener(v -> {
            if (!copied) {
                setClipboard(request.getIdRequest());
                copied = true;
            }
        });
        layoutCart.setOnClickListener(v -> startCart());

    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_order_detail);
        tvNumCart = findViewById(R.id.tv_num_cart);
        tvStatus = findViewById(R.id.tv_status_in_request);
        tvNameUser = findViewById(R.id.tv_name_user);
        tvAddressUser = findViewById(R.id.tv_address_user);
        tvPublisher = findViewById(R.id.tv_name_publisher);
        tvTotalPrice1 = findViewById(R.id.tv_total_price1);
        tvTotalPrice2 = findViewById(R.id.tv_total_price2);
        tvStringTotalTemp = findViewById(R.id.tv_string_temp_total);
        tvTempTotal = findViewById(R.id.tv_temp_total);
        tvShipCost = findViewById(R.id.tv_ship_cost);
        tvCodeRequest = findViewById(R.id.tv_code_request);
        tvCopyCodeRequest = findViewById(R.id.tv_copy_code_request);
        tvDateTime = findViewById(R.id.tv_date_time_request);
        recListOrder = findViewById(R.id.rec_list_item);
        layoutCart = findViewById(R.id.layout_shopping_cart);
        layoutTotalPrice1 = findViewById(R.id.layout_total_price1);
        layoutTotalPrice2 = findViewById(R.id.layout_total_price2);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        request = new Gson().fromJson(bundle.getString("request"), Request.class);
        databaseCart = new DatabaseCart(this);
    }

    @SuppressLint("DefaultLocale")
    public void setUpCart(){
        int i = databaseCart.getCarts().size();
        if (i > 0) {
            tvNumCart.setVisibility(View.VISIBLE);
            tvNumCart.setText(String.format("%d", i));
        }
        else
            tvNumCart.setVisibility(View.GONE);
    }

    public void setAddress(){
        tvNameUser.setText(request.getName());
        tvAddressUser.setText(request.getAddress());
    }

    public void setOrderList(){
        tvPublisher.setText(request.getNamePublisher());
        adapter = new BookOrderItemAdapter(request.getOrderList(), this);
        recListOrder.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this, RecyclerView.VERTICAL, false));
        recListOrder.setAdapter(adapter);
    }

    public void setDetailRequest(){
        int quantity = 0;
        for (Order order : request.getOrderList())
            quantity += order.getBookQuantity();
        tvTotalPrice1.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(request.getTotal())));
        tvTotalPrice2.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(request.getTotal())));
        tvStringTotalTemp.setText(String.format(getString(R.string.temp_total), quantity));
        tvTempTotal.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(request.getTempTotal())));
        tvShipCost.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(request.getShipCost())));

        tvCodeRequest.setText(request.getIdRequest());
        tvDateTime.setText(request.getDateTime());
        tvStatus.setText(getResources().getStringArray(R.array.status)[request.getStatus() - 1]);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void setClipboard(String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public void startCart() {
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        Intent intent = new Intent(OrderDetailActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}