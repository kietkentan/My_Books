package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {
    private final static int SPLASH_TIME_OUT = 2200;
    private final static int SHOW_TIME_SLOGAN = 200;

    private ImageView ivLogo;
    private TextView tvSlogan;
    private TextView tvAboutFrom;
    private Handler handler;
    private Context thisContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        handler = new Handler();
        thisContext = getApplicationContext();

        setAction();
        startHome();
    }

    public void init(){
        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        tvSlogan = (TextView) findViewById(R.id.tv_slogan);
        tvAboutFrom = (TextView) findViewById(R.id.tv_about_info);
    }

    public void setAction(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.view).setVisibility(View.INVISIBLE);
                ivLogo.setAnimation(AnimationUtils.loadAnimation(thisContext ,R.anim.scale_logo_x2));
                tvSlogan.setAnimation(AnimationUtils.loadAnimation(thisContext ,R.anim.alpha_hidden_100));
                tvAboutFrom.setAnimation(AnimationUtils.loadAnimation(thisContext ,R.anim.alpha_hidden_100));
            }
        }, SHOW_TIME_SLOGAN);
    }

    public void startHome(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(thisContext, HomeActivity.class));
                overridePendingTransition(R.anim.alpha_appear_100, R.anim.alpha_hidden_100);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}