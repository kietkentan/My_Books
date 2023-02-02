package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.CartConfirmAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ProgressBar progressBar;

    private List<Integer> buyList;
    private List<Order> orderList;
    private Map<String, List<Order>> mapOrder;
    private List<Request> requestList;

    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseCart dataBaseOrder;

    private int tempTotal = 0;
    private int shipCost = 0;
    private boolean buyNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_payment);
        AppUtil.changeStatusBarColor(this, getColor(R.color.reduced_price));

        init();
        setupAddress();
        setTotalPrice();
        setupRecyclerViewCart();

        ibBack.setOnClickListener(this);
        layoutAddress.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
    }

    public void init(){
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
        progressBar = findViewById(R.id.progress_complete_payment);

        orderList = new ArrayList<>();
        mapOrder = new HashMap<>();
        requestList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");
        dataBaseOrder = new DatabaseCart(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        buyNow = bundle.getBoolean("buy_now");
        if (buyNow) {
            String myGson = bundle.getString("order");
            orderList.add(new Gson().fromJson(myGson, Order.class));
            mapOrder.put(orderList.get(0).getPublisherId(), new ArrayList<>());
            mapOrder.get(orderList.get(0).getPublisherId()).add(orderList.get(0));
        }
        else {
            buyList = bundle.getIntegerArrayList("list_buy");
            getListOrder();
        }
    }

    public void getListOrder(){
        List<Order> orderListFull = new DatabaseCart(this).getCarts();
        for (int i : buyList){
            if (!mapOrder.containsKey(orderListFull.get(i).getPublisherId()))
                mapOrder.put(orderListFull.get(i).getPublisherId(), new ArrayList<>());
            orderListFull.get(i).setSelected(true);
            mapOrder.get(orderListFull.get(i).getPublisherId()).add(orderListFull.get(i));
            orderList.add(orderListFull.get(i));
        }
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
        CartConfirmAdapter adapter = new CartConfirmAdapter(orderList, this);
        rcCart.setAdapter(adapter);
    }

    public void setTotalPrice(){
        int totalQuantity = 0;

        for (Map.Entry<String, List<Order>> entry : mapOrder.entrySet()){
            int ship = 20000;
            int tmpTotal = 0;
            int quantity = 0;

            for (Order order : entry.getValue()){
                tmpTotal += (order.getBookPrice()*(100 - order.getBookDiscount()) / 100)
                        * order.getBookQuantity();
                ship += 2000*order.getBookQuantity();
                quantity += order.getBookQuantity();
            }

            requestList.add(new Request(Common.currentUser.getId(),
                    entry.getValue().get(0).getPublisherId(),
                    AppUtil.getStringAddress(Common.addressNow),
                    Common.addressNow.getName(),
                    Common.addressNow.getPhone(),
                    entry.getValue(),
                    tmpTotal,
                    ship,
                    tmpTotal + ship,
                    1));

            shipCost += ship;
            tempTotal += tmpTotal;
            totalQuantity += quantity;

        }

        tvStringTempTotal.setText(String.format(getString(R.string.temp_total), totalQuantity));
        tvTempTotal.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(tempTotal)));
        tvShipCost.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(shipCost)));
        tvTempTotalPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(shipCost + tempTotal)));
        tvTotalPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(shipCost + tempTotal)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setupData(){
        progressBar.setVisibility(View.VISIBLE);
        btnOrder.setVisibility(View.INVISIBLE);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String date = dtf.format(now);
        for (Request request : requestList) {
            String idRequest = String.valueOf(System.currentTimeMillis());
            request.setIdRequest(idRequest);
            request.setDateTime(date);
            database.getReference("request").child(idRequest).setValue(request);
        }
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (Order order : orderList) {
                    int amount = snapshot.child(order.getBookId()).child("amount").getValue(Integer.class);
                    snapshot.child(order.getBookId()).child("amount").getRef().setValue(amount - order.getBookQuantity());
                    dataBaseOrder.removeCarts(order.getBookId());
                }
                pullRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setupSingleData(){
        progressBar.setVisibility(View.VISIBLE);
        btnOrder.setVisibility(View.INVISIBLE);

        String idRequest = String.valueOf(System.currentTimeMillis());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String date = dtf.format(now);
        requestList.get(0).setIdRequest(idRequest);
        requestList.get(0).setDateTime(date);
        database.getReference("request").child(idRequest).setValue(requestList.get(0));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int amount = snapshot.child(orderList.get(0).getBookId()).child("amount").getValue(Integer.class);
                snapshot.child(orderList.get(0).getBookId()).child("amount").getRef().setValue(amount - orderList.get(0).getBookQuantity());

                progressBar.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                Toast.makeText(CompletePaymentActivity.this, R.string.successful_ordering, Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void pullRequest(){
        String[] mode = getResources().getStringArray(R.array.mode_login);
        database.getReference("user").child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("cartList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                for (Order order : dataBaseOrder.getCarts())
                    snapshot.child(String.valueOf(snapshot.getChildrenCount())).getRef().setValue(order);

                progressBar.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                Toast.makeText(CompletePaymentActivity.this, R.string.successful_ordering, Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_exit_complete_payment:
                finish();
                break;
            case R.id.layout_start_address_page:
                startAddressPage();
                break;
            case R.id.btn_order:
                if (buyNow)
                    setupSingleData();
                else
                    setupData();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}