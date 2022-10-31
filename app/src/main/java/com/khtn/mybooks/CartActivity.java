package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.khtn.mybooks.Interface.ViewCartClickInterface;
import com.khtn.mybooks.Interface.ViewPublisherClickInterface;
import com.khtn.mybooks.adapter.CartAdapter;
import com.khtn.mybooks.databases.DataBase;
import com.khtn.mybooks.model.Order;

import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, ViewCartClickInterface {
    private CheckBox cbAllCart;
    private TextView tvTotalPrice;
    private AppCompatButton btnBuy;
    private ImageButton ibRemoveCart;
    private ImageButton ibBack;
    private RecyclerView rcShowCart;
    private ConstraintLayout layoutNoneCart;
    private ConstraintLayout layoutViewCart;

    private CartAdapter adapter;
    private List<Order> orderList;
    private DataBase dataBaseOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
        getData();
        setupLayout();

        ibBack.setOnClickListener(CartActivity.this);
        btnBuy.setOnClickListener(CartActivity.this);
    }

    public void init(){
        dataBaseOrder = new DataBase(this);

        cbAllCart = (CheckBox) findViewById(R.id.cb_check_all_cart);
        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        btnBuy = (AppCompatButton) findViewById(R.id.btn_buy);
        ibRemoveCart = (ImageButton) findViewById(R.id.ib_remove_cart);
        ibBack = (ImageButton) findViewById(R.id.ib_exit_cart);
        rcShowCart = (RecyclerView) findViewById(R.id.rec_carts);
        layoutNoneCart = (ConstraintLayout) findViewById(R.id.layout_none_cart);
        layoutViewCart = (ConstraintLayout) findViewById(R.id.layout_view_cart);
    }

    public void getData(){
        rcShowCart.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderList = dataBaseOrder.getCarts();
        adapter = new CartAdapter(orderList, this);
        rcShowCart.setAdapter(adapter);
    }

    public void setupLayout(){
        if (orderList.size() == 0) {
            layoutNoneCart.setVisibility(View.VISIBLE);
            layoutViewCart.setVisibility(View.GONE);
        } else {
            layoutNoneCart.setVisibility(View.GONE);
            layoutViewCart.setVisibility(View.VISIBLE);
            ibRemoveCart.setOnClickListener(CartActivity.this);
        }
        setupCheckboxAllCart();
    }

    public void setupCheckboxAllCart(){
        cbAllCart.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                adapter.selectedAllCart();
            else
                adapter.unSelectedAllCart();
            setTotal(adapter.getSelectedCart());
        });
    }

    @SuppressLint("DefaultLocale")
    public void setTotal(List<Integer> selectedCart){
        if (selectedCart.size() == 0){
            tvTotalPrice.setText(getString(R.string.please_chose_product));
            tvTotalPrice.setTextSize(14);
            tvTotalPrice.setTypeface(null, Typeface.NORMAL);
            btnBuy.setText(getString(R.string.default_buy));
        } else {
            int total = 0;
            for (int i = 0; i < selectedCart.size(); ++i)
                total = total + (orderList.get(selectedCart.get(i)).getBookPrice()*(100 - orderList.get(selectedCart.get(i)).getBookDiscount())/100);
            tvTotalPrice.setText(String.format("%s₫", AppUtil.convertNumber(total)));
            tvTotalPrice.setTextSize(20);
            tvTotalPrice.setTypeface(null, Typeface.BOLD);
            btnBuy.setText(String.format("%s (%d)", getString(R.string.buy), selectedCart.size()));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeCart(){
        if (cbAllCart.isChecked())
            dataBaseOrder.cleanCarts();
        else {
            List<Integer> listRemove = adapter.getSelectedCart();
            if (listRemove.size() > 0)
                for (int i = 0; i < listRemove.size(); ++i)
                    dataBaseOrder.removeCarts(orderList.get(listRemove.get(i)).getBookId());
            else
                return;
        }
        orderList = dataBaseOrder.getCarts();
        adapter.notifyDataSetChanged();
        setupLayout();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_remove_cart)
            removeCart();
        if (view.getId() == R.id.ib_exit_cart)
            finish();
        if (view.getId() == R.id.btn_buy)
            ; // buy
    }

    @Override
    public void OnCheckedChanged(List<Integer> selectedCart) {
        if (selectedCart.size() == orderList.size())
            cbAllCart.setChecked(true);
        else {
            List<Integer> tempChecked = adapter.getSelectedCart();
            cbAllCart.setChecked(false);
            adapter.setSelectedCart(tempChecked);
        }
        setTotal(selectedCart);
    }
}