package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.khtn.mybooks.common.Common;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivMenuSetting;
    private TextView tvName;
    private LinearLayout layoutHomePage;
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
        ivMenuSetting = (ImageView) findViewById(R.id.iv_menu);
        tvName = (TextView) findViewById(R.id.tv_name_user);
        layoutHomePage = (LinearLayout) findViewById(R.id.layout_homePage);
        ivBackground = (ShapeableImageView) findViewById(R.id.iv_background_user);
        ivAvatar = (ShapeableImageView) findViewById(R.id.iv_avatar_user);
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login_user);
    }

    private void checkUser(){
        if (Common.currentUser != null)
            loadUser();
        else
            btnLogin.setOnClickListener(this);
    }

    private void loadUser(){
        btnLogin.setVisibility(View.INVISIBLE);
        if (Common.currentUser.getAvatar() != null)
            Picasso.get().load(Common.currentUser.getAvatar()).into(ivAvatar);
        tvName.setText(Common.currentUser.getName());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_homePage)
            this.finishAfterTransition();
        if (view.getId() == R.id.iv_menu)
            ;// show menu setting
        if (view.getId() == R.id.btn_login_user)
            startLoginPage();
    }

    private void startLoginPage(){
        startActivity(new Intent(this, SignInSignUpActivity.class));
    }
}