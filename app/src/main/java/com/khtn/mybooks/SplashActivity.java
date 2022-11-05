package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DataBaseCart;
import com.khtn.mybooks.model.Address;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.User;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private final static int SPLASH_TIME_OUT = 2200;
    private final static int SHOW_TIME_SLOGAN = 200;

    private ImageView ivLogo;
    private TextView tvSlogan;
    private TextView tvAboutFrom;
    private Handler handler;
    private Context thisContext;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        handler = new Handler();
        thisContext = getApplicationContext();

        setAction();
        getUser();
        startHome();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        ivLogo = findViewById(R.id.iv_logo);
        tvSlogan = findViewById(R.id.tv_slogan);
        tvAboutFrom = findViewById(R.id.tv_about_info);
    }

    public void setAction(){
        handler.postDelayed(() -> {
            findViewById(R.id.view).setVisibility(View.INVISIBLE);
            ivLogo.setAnimation(AnimationUtils.loadAnimation(thisContext ,R.anim.scale_logo_x2));
            tvSlogan.setAnimation(AnimationUtils.loadAnimation(thisContext ,R.anim.alpha_hidden_100));
            tvAboutFrom.setAnimation(AnimationUtils.loadAnimation(thisContext ,R.anim.alpha_hidden_100));
        }, SHOW_TIME_SLOGAN);
    }

    public void getUser(){
        SharedPreferences preferences = Common.checkUser(SplashActivity.this);
        if (preferences != null){
            Common.modeLogin = preferences.getInt("mode_login", 0);
            String id = preferences.getString("saved_id", null);
            String password = preferences.getString("saved_password", null);
            if (Common.modeLogin == 1){
                database.getReference("user").child("mybooks").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if (user.getPassword().equals(password)) {
                                user.setPassword(null);
                                Common.currentUser = user;
                                snapshot.child("addressList").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        List<Address> addressList = new ArrayList<>();
                                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                                            Address address = snapshot1.getValue(Address.class);
                                            addressList.add(address);
                                        }
                                        Common.setAddressLists(addressList);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                snapshot.child("cartList").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        List<Order> orderList = new ArrayList<>();
                                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            Order order = dataSnapshot.getValue(Order.class);
                                            orderList.add(order);
                                        }
                                        DataBaseCart dataBase = new DataBaseCart(SplashActivity.this);
                                        dataBase.cleanCarts();
                                        for (Order order:orderList){
                                            database.getReference("book").child(order.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    @SuppressWarnings("unchecked")
                                                    List<String> image = (List<String>) snapshot.child("image").getValue();
                                                    order.setBookImage(image.get(0));
                                                    order.setBookName(snapshot.child("name").getValue(String.class));
                                                    order.setBookDiscount(snapshot.child("discount").getValue(Integer.class));
                                                    order.setBookPrice(snapshot.child("originalPrice").getValue(Integer.class));
                                                    order.setPublisherId(snapshot.child("publisher").getValue(String.class));
                                                    dataBase.addCart(order);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (Common.modeLogin == 2){
                database.getReference("user").child("google").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Common.currentUser = snapshot.getValue(User.class);
                            snapshot.child("addressList").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<Address> addressList = new ArrayList<>();
                                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                                        Address address = snapshot1.getValue(Address.class);
                                        addressList.add(address);
                                    }
                                    Common.setAddressLists(addressList);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (Common.modeLogin == 3){
                database.getReference("user").child("facebook").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Common.currentUser = snapshot.getValue(User.class);
                            snapshot.child("addressList").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    List<Address> addressList = new ArrayList<>();
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        Address address = snapshot1.getValue(Address.class);
                                        addressList.add(address);
                                    }
                                    Common.setAddressLists(addressList);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else
            Common.currentUser = null;
    }

    public void startHome(){
        handler.postDelayed(() -> {
            Intent intent = new Intent(thisContext, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("fm", true);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha_appear_100, R.anim.alpha_hidden_100);
            finish();
        }, SPLASH_TIME_OUT);
    }
}