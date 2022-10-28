package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.khtn.mybooks.common.Common;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivMenuSetting;
    private ImageView ivChat;
    private TextView tvName;
    private TextView tvMyInformation;
    private LinearLayout layoutHomePage;
    private LinearLayout layoutSeeMore;
    private ShapeableImageView ivBackground;
    private ShapeableImageView ivAvatar;
    private AppCompatButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        init();

        layoutHomePage.setOnClickListener(UserActivity.this);
        checkUser();
    }

    public void init(){
        ivMenuSetting = (ImageView) findViewById(R.id.iv_setting);
        ivChat = (ImageView) findViewById(R.id.iv_chat);
        tvName = (TextView) findViewById(R.id.tv_name_user);
        tvMyInformation = (TextView) findViewById(R.id.tv_my_information);
        layoutHomePage = (LinearLayout) findViewById(R.id.layout_homePage);
        layoutSeeMore = (LinearLayout) findViewById(R.id.layout_see_more);
        ivBackground = (ShapeableImageView) findViewById(R.id.iv_background_user);
        ivAvatar = (ShapeableImageView) findViewById(R.id.iv_avatar_user);
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login_user);
    }

    private void checkUser(){
        if (Common.currentUser != null)
            loadUser();
        else {
            btnLogin.setOnClickListener(this);
            tvMyInformation.setVisibility(View.GONE);
            layoutSeeMore.setVisibility(View.GONE);
        }
    }

    private void loadUser(){
        btnLogin.setVisibility(View.INVISIBLE);
        ivMenuSetting.setImageResource(R.drawable.ic_setting_white);
        ivChat.setImageResource(R.drawable.ic_chat_white);
        if (Common.currentUser.getAvatar() != null)
            Picasso.get().load(Common.currentUser.getAvatar()).into(ivAvatar);

        if (Common.currentUser.getBackground() != null)
            Picasso.get().load(Common.currentUser.getBackground()).into(ivBackground);
        else
            ivBackground.setBackgroundResource(R.drawable.default_background);
        ivBackground.setAlpha(0.7F);
        tvName.setText(Common.currentUser.getName());
        tvName.setTextColor(Color.parseColor("#FFFFFFFF"));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_homePage)
            this.finishAfterTransition();
        if (view.getId() == R.id.iv_setting)
            ;// show menu setting
        if (view.getId() == R.id.btn_login_user)
            startLoginPage();
    }

    private void startLoginPage(){
        startActivity(new Intent(this, SignInSignUpActivity.class));
    }
}