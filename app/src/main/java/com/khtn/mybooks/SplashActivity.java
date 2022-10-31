package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.User;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private final static int SPLASH_TIME_OUT = 2200;
    private final static int SHOW_TIME_SLOGAN = 200;

    private ImageView ivLogo;
    private TextView tvSlogan;
    private TextView tvAboutFrom;
    private Handler handler;
    private Context thisContext;

    private DatabaseReference reference;

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
        reference = FirebaseDatabase.getInstance().getReference("user");
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvSlogan = (TextView) findViewById(R.id.tv_slogan);
        tvAboutFrom = (TextView) findViewById(R.id.tv_about_info);
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
                reference.child("mybooks").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            if (Objects.requireNonNull(user).getPassword().equals(password)) {
                                user.setPassword(null);
                                Common.currentUser = user;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (Common.modeLogin == 2){
                reference.child("google").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            Common.currentUser = snapshot.<User>getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else if (Common.modeLogin == 3){
                reference.child("facebook").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            Common.currentUser = snapshot.<User>getValue(User.class);
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
            startActivity(new Intent(thisContext, HomeActivity.class));
            overridePendingTransition(R.anim.alpha_appear_100, R.anim.alpha_hidden_100);
            finish();
        }, SPLASH_TIME_OUT);
    }
}