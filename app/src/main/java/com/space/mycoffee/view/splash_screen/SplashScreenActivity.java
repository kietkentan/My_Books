package com.space.mycoffee.view.splash_screen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.R;
import com.space.mycoffee.databinding.ActivitySplashScreenBinding;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.model.SavedLogin;
import com.space.mycoffee.model.User;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.SharedPreferencesManager;
import com.space.mycoffee.view.main.MainActivity;
import com.space.mycoffee.view.manager.ManagerActivity;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;

    private final static int SPLASH_TIME_OUT = 1400;
    private final static int SHOW_TIME_SLOGAN = 200;

    private Handler handler;
    private SharedPreferencesManager preferences;
    private FirebaseDatabase database;

    private String[] mode;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
        setAction();
        getUser();
    }

    public void init() {
        handler = new Handler(Looper.getMainLooper());
        preferences = new SharedPreferencesManager(this);
        database = FirebaseDatabase.getInstance();
        mode = getResources().getStringArray(R.array.mode_login);
    }

    public void setAction() {
        handler.postDelayed(() -> {
            binding.ivLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_logo_x2));
            binding.tvSlogan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_hidden_100));
        }, SHOW_TIME_SLOGAN);
    }

    public void getUser() {
        SavedLogin savedLogin = preferences.getSavedLogin();
        if (savedLogin != null) {
            database.getReference(Constants.USER).child(mode[savedLogin.getModeLogin() - 1])
                    .child(savedLogin.getSavedId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child(Constants.PASSWORD).getValue(String.class)
                                        .equals(savedLogin.getSavedPassword())
                                ) {
                                    User user = snapshot.getValue(User.class);
                                    AppSingleton.signIn(user, SplashScreenActivity.this, savedLogin.getModeLogin());
                                    getMoreData();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        } else {
            AppSingleton.currentUser = null;
            startHome();
        }
    }

    public void getMoreData() {
        if (AppSingleton.currentUser.getCartList() != null) {
            List<Order> list = new ArrayList<>(AppSingleton.currentUser.getCartList());
            AppSingleton.currentUser.setCartList(new ArrayList<>());
            for (Order order : list) {
                database.getReference(Constants.COFFEE).child(order.getIdCoffee()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CoffeeDetail detail = snapshot.getValue(CoffeeDetail.class);
                        AppSingleton.currentUser.addCartList(new Order(detail, order.getCoffeeQuantity()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }
        startHome();
    }

    public void startHome() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, AppSingleton.currentUser == null || !AppSingleton.currentUser.isAdmin() ? MainActivity.class : ManagerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha_appear_100, R.anim.alpha_hidden_100);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
