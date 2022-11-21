package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.khtn.mybooks.adapter.CartConfirmAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        progressBar = findViewById(R.id.progress_complete_payment);

        orderList = new ArrayList<>();
        mapOrder = new HashMap<>();
        requestList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");
        dataBaseOrder = new DatabaseCart(this);

        getListOrder();
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

    public void setupData(){
        progressBar.setVisibility(View.VISIBLE);
        btnOrder.setVisibility(View.INVISIBLE);

        for (Request request : requestList)
            database.getReference("request").child(String.valueOf(System.currentTimeMillis())).setValue(request);
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

    public void pullRequest(){
        String[] mode = {"mybooks", "google", "facebook"};
        database.getReference("user").child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("cartList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                for (Order order : dataBaseOrder.getCarts())
                    snapshot.child(String.valueOf(snapshot.getChildrenCount())).getRef().setValue(order);

                progressBar.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                Toast.makeText(CompletePaymentActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_exit_complete_payment)
            finish();
        if (v.getId() == R.id.layout_start_address_page)
            startAddressPage();
        if (v.getId() == R.id.btn_order)
            setupData();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}