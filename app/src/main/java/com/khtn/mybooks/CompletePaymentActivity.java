package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khtn.mybooks.adapter.CartConfirmAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Request;

import java.util.ArrayList;
import java.util.List;

public class CompletePaymentActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private TextView tvNameUser;
    private TextView tvPhoneUser;
    private TextView tvAddress;
    private TextView tvStringTempTotal;
    private TextView tvTempTotal;
    private TextView tvShipCost;
    private TextView tvTempTotalPrice;
    private TextView tvTotalPrice;
    private AppCompatButton btnOrder;
    private LinearLayout layoutAddress;
    private RecyclerView rcCart;

    private List<Integer> buyList;
    private List<Order> orderList;
    private CartConfirmAdapter adapter;

    private int totalPrice;
    private int tempTotal;
    private int shipCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_payment);
        AppUtil.changeStatusBarColor(this, "#E32127");

        init();
        setupAddress();
        setTotalPrice();
        setupRecyclerViewCart();

        ibBack.setOnClickListener(this);
        layoutAddress.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        buyList = bundle.getIntegerArrayList("list_buy");

        ibBack = findViewById(R.id.ib_exit_complete_payment);
        tvNameUser = findViewById(R.id.tv_name_user);
        tvPhoneUser = findViewById(R.id.tv_phone_user);
        tvAddress = findViewById(R.id.tv_address_user);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvStringTempTotal = findViewById(R.id.tv_string_temp_total);
        tvTempTotal = findViewById(R.id.tv_temp_total);
        tvShipCost = findViewById(R.id.tv_ship_cost);
        tvTempTotalPrice = findViewById(R.id.tv_temp_total_price);
        btnOrder = findViewById(R.id.btn_order);
        layoutAddress = findViewById(R.id.layout_start_address_page);
        rcCart = findViewById(R.id.rec_list_confirm);

        orderList = new ArrayList<>();
    }

    public void setupAddress(){
        tvNameUser.setText(Common.addressNow.getName());
        tvPhoneUser.setText(Common.addressNow.getPhone());
        tvAddress.setText(AppUtil.getStringAddress(Common.addressNow));
    }

    public void startAddressPage(){
        startActivity(new Intent(CompletePaymentActivity.this, AddressActivity.class));
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void setupRecyclerViewCart(){
        rcCart.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new CartConfirmAdapter(orderList, this);
        rcCart.setAdapter(adapter);
    }

    public void setTotalPrice(){
        List<Order> orderListFull = new DatabaseCart(this).getCarts();
        tempTotal = 0;
        shipCost = 20000;
        int totalQuantity = 0;

        for (int i : buyList) {
            orderListFull.get(i).setSelected(true);
            orderList.add(orderListFull.get(i));
            tempTotal += (orderListFull.get(i).getBookPrice()
                    * (100 - orderListFull.get(i).getBookDiscount()) / 100)
                    * orderListFull.get(i).getBookQuantity();
            totalQuantity += orderListFull.get(i).getBookQuantity();
            shipCost += 2000*orderListFull.get(i).getBookQuantity();
        }
        totalPrice = shipCost + tempTotal;

        tvStringTempTotal.setText(String.format(getString(R.string.temp_total), totalQuantity));
        tvTempTotal.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(tempTotal)));
        tvShipCost.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(shipCost)));
        tvTempTotalPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(totalPrice)));
        tvTotalPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(totalPrice)));
    }

    public void startPutRequest(){
        Request request = new Request(Common.currentUser.getId(),
                                    AppUtil.getStringAddress(Common.addressNow),
                                    Common.addressNow.getName(),
                                    Common.addressNow.getPhone(),
                                    orderList,
                                    tempTotal,
                                    shipCost,
                                    totalPrice);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("request");
        DatabaseCart dataBaseOrder = new DatabaseCart(this);

        reference.child(String.valueOf(System.currentTimeMillis())).setValue(request);
        for (Order order : orderList)
            dataBaseOrder.removeCarts(order.getBookId());
        Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_exit_complete_payment)
            finish();
        if (v.getId() == R.id.layout_start_address_page)
            startAddressPage();
        if (v.getId() == R.id.btn_order)
            startPutRequest();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}