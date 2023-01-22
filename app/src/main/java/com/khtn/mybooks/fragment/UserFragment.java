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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.activity.InformationUserActivity;
import com.khtn.mybooks.activity.RecentlyViewedActivity;
import com.khtn.mybooks.activity.ShopFollowedActivity;
import com.khtn.mybooks.databases.DatabaseViewed;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.Interface.SwitchFavoritePageInterface;
import com.khtn.mybooks.activity.NoteAddressActivity;
import com.khtn.mybooks.activity.OrderStatusActivity;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Request;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment implements View.OnClickListener{
    private View view;
    private View viewStoreManager;
    private ImageView ivMenuSetting;
    private ImageView ivChat;
    private TextView tvName;
    private TextView tvMyInformation;
    private TextView tvAddress;
    private TextView tvFavorite;
    private TextView tvRecentlyViewed;
    private TextView tvStoreManager;
    private TextView tvShopFollowed;
    private TextView tvNumWaitConfirm;
    private TextView tvNumWaitShipping;
    private TextView tvNumInTransit;
    private TextView tvNumDelivered;
    private TextView tvNumPacketReturn;
    private LinearLayout layoutSeeMore;
    private LinearLayout layoutListAllOrder;
    private LinearLayout layoutListOrderWaitConfirm;
    private LinearLayout layoutListOderWaitShipping;
    private LinearLayout layoutListOrdersInTransit;
    private LinearLayout layoutListDelivered;
    private LinearLayout layoutRecentlyViewed;
    private LinearLayout layoutShopFollowed;
    private ShapeableImageView ivBackground;
    private ShapeableImageView ivAvatar;
    private AppCompatButton btnLogin;
    private final SwitchFavoritePageInterface switchFavoritePageInterface;

    private List<Request> requestList;
    private FirebaseDatabase database;
    private DatabaseReference reference;

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
        tvMyInformation.setOnClickListener(this);
        layoutListAllOrder.setOnClickListener(this);
        layoutListOrderWaitConfirm.setOnClickListener(this);
        layoutListOderWaitShipping.setOnClickListener(this);
        layoutListOrdersInTransit.setOnClickListener(this);
        layoutListDelivered.setOnClickListener(this);
        layoutRecentlyViewed.setOnClickListener(this);
        layoutShopFollowed.setOnClickListener(this);

        checkUser();
        return view;
    }

    @Override
    public void onResume() {
        setupTextViewRecentlyViewed();
        setupTextViewShopFollowed();
        super.onResume();
    }

    private void init(){
        viewStoreManager = view.findViewById(R.id.view_store_manager);
        ivMenuSetting = view.findViewById(R.id.iv_setting);
        ivChat = view.findViewById(R.id.iv_chat);
        tvName = view.findViewById(R.id.tv_name_user);
        tvMyInformation = view.findViewById(R.id.tv_my_information);
        tvAddress = view.findViewById(R.id.tv_note_address);
        tvFavorite = view.findViewById(R.id.tv_favorite_item);
        tvRecentlyViewed = view.findViewById(R.id.tv_recently_viewed);
        tvStoreManager = view.findViewById(R.id.tv_store_manager);
        tvShopFollowed = view.findViewById(R.id.tv_shop_followed);
        tvNumWaitConfirm = view.findViewById(R.id.tv_num_order_wait_confirm);
        tvNumWaitShipping = view.findViewById(R.id.tv_num_order_wait_shipping);
        tvNumDelivered = view.findViewById(R.id.tv_num_order_delivered);
        tvNumInTransit = view.findViewById(R.id.tv_num_order_in_transit);
        tvNumPacketReturn = view.findViewById(R.id.tv_num_packet_return);
        layoutSeeMore = view.findViewById(R.id.layout_see_more);
        layoutListAllOrder = view.findViewById(R.id.layout_list_all_request);
        layoutListOrderWaitConfirm = view.findViewById(R.id.layout_list_order_wait_confirm);
        layoutListOderWaitShipping = view.findViewById(R.id.layout_list_oder_wait_shipping);
        layoutListOrdersInTransit = view.findViewById(R.id.layout_list_orders_in_transit);
        layoutListDelivered = view.findViewById(R.id.layout_list_delivered);
        layoutRecentlyViewed = view.findViewById(R.id.layout_recently_viewed);
        layoutShopFollowed = view.findViewById(R.id.layout_shop_followed);
        ivBackground = view.findViewById(R.id.iv_background_user);
        ivAvatar = view.findViewById(R.id.iv_avatar_user);
        btnLogin = view.findViewById(R.id.btn_login_user);

        requestList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("request");
    }

    private void checkUser(){
        if (Common.currentUser != null) {
            getData();
            loadUser();
        }
        else {
            btnLogin.setOnClickListener(this);
            tvMyInformation.setVisibility(View.GONE);
            layoutSeeMore.setVisibility(View.GONE);
        }
    }

    public void getData(){
        reference.orderByChild("idUser")
                .equalTo(Common.currentUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            requestList.add(dataSnapshot.getValue(Request.class));
                        }
                        setupNumStatus();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
        setupStaff();
    }

    public void setupStaff(){
        if (Common.currentUser.isStaff()){
            tvStoreManager.setVisibility(View.VISIBLE);
            tvStoreManager.setOnClickListener(this);
            viewStoreManager.setVisibility(View.VISIBLE);
        } else {
            tvStoreManager.setVisibility(View.GONE);
            viewStoreManager.setVisibility(View.GONE);
        }
    }

    @SuppressLint("DefaultLocale")
    public void setupTextViewRecentlyViewed(){
        if (Common.currentUser == null)
            return;
        DatabaseViewed databaseViewed = new DatabaseViewed(getActivity());
        int i = databaseViewed.getListsViewed().size();
        tvRecentlyViewed.setText(String.format("%d", i));
    }

    @SuppressLint("DefaultLocale")
    public void setupTextViewShopFollowed(){
        if (Common.currentUser == null)
            return;
        int i = 0;
        if (Common.currentUser.getList_shopFollow() != null)
            i = Common.currentUser.getList_shopFollow().size();
        tvShopFollowed.setText(String.format("%d", i));
    }

    @SuppressLint("DefaultLocale")
    public void setNumWaitConfirm(int num){
        if (num != 0){
            tvNumWaitConfirm.setVisibility(View.VISIBLE);
            tvNumWaitConfirm.setText(String.format("%d", num));
        } else
            tvNumWaitConfirm.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    public void setNumWaitShipping(int num){
        if (num != 0){
            tvNumWaitShipping.setVisibility(View.VISIBLE);
            tvNumWaitShipping.setText(String.format("%d", num));
        } else
            tvNumWaitShipping.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    public void setNumDelivered(int num){
        if (num != 0){
            tvNumDelivered.setVisibility(View.VISIBLE);
            tvNumDelivered.setText(String.format("%d", num));
        } else
            tvNumDelivered.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    public void setNumInTransit(int num){
        if (num != 0){
            tvNumInTransit.setVisibility(View.VISIBLE);
            tvNumInTransit.setText(String.format("%d", num));
        } else
            tvNumInTransit.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    public void setNumPacketReturn(int num){
        if (num != 0){
            tvNumPacketReturn.setVisibility(View.VISIBLE);
            tvNumPacketReturn.setText(String.format("%d", num));
        } else
            tvNumPacketReturn.setVisibility(View.GONE);
    }

    public void setupNumStatus(){
        int[] num = {0, 0, 0, 0, 0};

        for (Request request:requestList) {
            num[request.getStatus() - 1] += 1;
        }

        setNumWaitConfirm(num[0]);
        setNumWaitShipping(num[1]);
        setNumDelivered(num[2]);
        setNumInTransit(num[3]);
        setNumPacketReturn(num[4]);
    }

    public void startNoteAddress(){
        if (Common.currentUser != null) {
            Intent intent = new Intent(getActivity(), NoteAddressActivity.class);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        } else
            AppUtil.startLoginPage(getContext());
    }

    public void startOrderStatusPage(int tabSelect){
        if (Common.currentUser != null) {
            Intent intent = new Intent(getContext(), OrderStatusActivity.class);
            intent.putExtra("tabSelect", tabSelect);
            requireActivity().startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        } else
            AppUtil.startLoginPage(getContext());
    }

    public void startRecentlyViewed(){
        Intent intent = new Intent(getContext(), RecentlyViewedActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startShopFollowed(){
        Intent intent = new Intent(getContext(), ShopFollowedActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startInformationPage(){
        Intent intent = new Intent(getContext(), InformationUserActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
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
            case R.id.layout_recently_viewed:
                startRecentlyViewed();
                break;
            case R.id.tv_favorite_item:
                if (Common.currentUser != null)
                    switchFavoritePageInterface.OnClickFavorite();
                else
                    AppUtil.startLoginPage(getActivity());
                break;
            case R.id.layout_shop_followed:
                startShopFollowed();
                break;
            case R.id.tv_my_information:
                startInformationPage();
                break;
        }
    }
}