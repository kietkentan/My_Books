package com.khtn.mybooks.fragment;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.khtn.mybooks.AppUtil;
import com.khtn.mybooks.Interface.SwitchFavoritePageInterface;
import com.khtn.mybooks.MainActivity;
import com.khtn.mybooks.NoteAddressActivity;
import com.khtn.mybooks.OrderStatusActivity;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.squareup.picasso.Picasso;

public class UserFragment extends Fragment implements View.OnClickListener{
    private View view;
    private ImageView ivMenuSetting;
    private ImageView ivChat;
    private TextView tvName;
    private TextView tvMyInformation;
    private TextView tvAddress;
    private TextView tvFavorite;
    private LinearLayout layoutSeeMore;
    private LinearLayout layoutListAllOrder;
    private LinearLayout layoutListOrderWaitConfirm;
    private LinearLayout layoutListOderWaitShipping;
    private LinearLayout layoutListOrdersInTransit;
    private LinearLayout layoutListDelivered;
    private ShapeableImageView ivBackground;
    private ShapeableImageView ivAvatar;
    private AppCompatButton btnLogin;
    private SwitchFavoritePageInterface switchFavoritePageInterface;

    public UserFragment(SwitchFavoritePageInterface switchFavoritePageInterface) {
        this.switchFavoritePageInterface = switchFavoritePageInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        AppUtil.defaultStatusBarColor(getContext());

        init();

        tvAddress.setOnClickListener(this);
        tvFavorite.setOnClickListener(this);
        layoutListAllOrder.setOnClickListener(this);
        layoutListOrderWaitConfirm.setOnClickListener(this);
        layoutListOderWaitShipping.setOnClickListener(this);
        layoutListOrdersInTransit.setOnClickListener(this);
        layoutListDelivered.setOnClickListener(this);

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
        tvAddress = view.findViewById(R.id.tv_note_address);
        tvFavorite = view.findViewById(R.id.tv_favorite_item);
        layoutSeeMore = view.findViewById(R.id.layout_see_more);
        layoutListAllOrder = view.findViewById(R.id.layout_list_all_request);
        layoutListOrderWaitConfirm = view.findViewById(R.id.layout_list_order_wait_confirm);
        layoutListOderWaitShipping = view.findViewById(R.id.layout_list_oder_wait_shipping);
        layoutListOrdersInTransit = view.findViewById(R.id.layout_list_orders_in_transit);
        layoutListDelivered = view.findViewById(R.id.layout_list_delivered);
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

    public void startNoteAddress(){
        if (Common.currentUser != null) {
            Intent intent = new Intent(getActivity(), NoteAddressActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        } else
            AppUtil.startLoginPage(getActivity());
    }

    public void startOrderStatusPage(int tabSelect){
        Intent intent = new Intent(getContext(), OrderStatusActivity.class);
        intent.putExtra("tabSelect", tabSelect);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                break;
            case R.id.btn_login_user:
                AppUtil.startLoginPage(getActivity());
                break;
            case R.id.tv_note_address:
                startNoteAddress();
                break;
            case R.id.layout_list_all_request:
                startOrderStatusPage(0);
                break;
            case R.id.layout_list_order_wait_confirm:
                startOrderStatusPage(1);
                break;
            case R.id.layout_list_oder_wait_shipping:
                startOrderStatusPage(2);
                break;
            case R.id.layout_list_orders_in_transit:
                startOrderStatusPage(3);
                break;
            case R.id.layout_list_delivered:
                startOrderStatusPage(4);
                break;
            case R.id.tv_favorite_item:
                switchFavoritePageInterface.OnClickFavorite();
                break;
        }
    }
}