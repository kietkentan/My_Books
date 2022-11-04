package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.khtn.mybooks.common.Common;
import com.squareup.picasso.Picasso;

public class UserFragment extends Fragment implements View.OnClickListener{
    private View view;
    private ImageView ivMenuSetting;
    private ImageView ivChat;
    private TextView tvName;
    private TextView tvMyInformation;
    private LinearLayout layoutSeeMore;
    private ShapeableImageView ivBackground;
    private ShapeableImageView ivAvatar;
    private AppCompatButton btnLogin;

    public UserFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        init();
        checkUser();
        return view;
    }

    @Override
    public void onResume() {
        checkUser();
        super.onResume();
    }

    private void init(){
        ivMenuSetting = view.findViewById(R.id.iv_setting);
        ivChat = view.findViewById(R.id.iv_chat);
        tvName = view.findViewById(R.id.tv_name_user);
        tvMyInformation = view.findViewById(R.id.tv_my_information);
        layoutSeeMore = view.findViewById(R.id.layout_see_more);
        ivBackground = view.findViewById(R.id.iv_background_user);
        ivAvatar = view.findViewById(R.id.iv_avatar_user);
        btnLogin = view.findViewById(R.id.btn_login_user);
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
        ivMenuSetting.getDrawable().setTint(Color.WHITE);
        ivChat.getDrawable().setTint(Color.WHITE);
        if (Common.currentUser.getAvatar() != null)
            Picasso.get().load(Common.currentUser.getAvatar()).into(ivAvatar);

        if (Common.currentUser.getBackground() != null)
            Picasso.get().load(Common.currentUser.getBackground()).into(ivBackground);
        else
            ivBackground.setImageResource(R.drawable.default_background);
        ivBackground.setColorFilter(new LightingColorFilter(0xaaaaaaaa, 0x11111111));
        tvName.setText(Common.currentUser.getName());
        tvName.setTextColor(Color.parseColor("#FFFFFFFF"));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_setting)
            ;// show menu setting
        if (view.getId() == R.id.btn_login_user)
            AppUtil.startLoginPage(getActivity());
    }
}