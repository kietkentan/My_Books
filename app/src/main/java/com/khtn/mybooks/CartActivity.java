package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.khtn.mybooks.adapter.CartAdapter;
import com.khtn.mybooks.databases.DataBase;
import com.khtn.mybooks.model.Order;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener{
    private CheckBox cbAllCart;
    private FrameLayout ibRemoveCart;
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
    }

    public void init(){
        dataBaseOrder = new DataBase(this);

        cbAllCart = (CheckBox) findViewById(R.id.cb_check_all_cart);
        ibRemoveCart = (FrameLayout) findViewById(R.id.ib_remove_cart);
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
        cbAllCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    adapter.selectedAllCart();
                } else {
                    adapter.unSelectedAllCart();
                }
            }
        });
    }

    public void removeCart(){
        if (cbAllCart.isChecked()){
            orderList.clear();
            dataBaseOrder.cleanCarts();
            adapter.notifyDataSetChanged();
        } else {
            List<Integer> listRemove = adapter.getSelectedCart();
            if (listRemove.size() > 0)
                for (int i = listRemove.size() - 1; i >= 0; --i) {
                    dataBaseOrder.removeCarts(orderList.get(listRemove.get(i)));
                    orderList.clear();
                    orderList = dataBaseOrder.getCarts();
                }
            adapter.notifyDataSetChanged();
        }
        setupLayout();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_remove_cart)
            removeCart();
        if (view.getId() == R.id.ib_exit_cart)
            finish();
    }
}