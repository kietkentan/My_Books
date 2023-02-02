package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.Interface.AddressClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.AddressAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private RecyclerView recListAddress;
    private TextView tvAddAddress;
    private AddressAdapter addressAdapter;
    private AppCompatButton btnChoseAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        AppUtil.changeStatusBarColor(this, getColor(R.color.reduced_price));

        init();
        setRecyclerViewAddress();
        ibBack.setOnClickListener(this);
        btnChoseAddress.setOnClickListener(this);
        tvAddAddress.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerViewAddress();
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_ship_location);
        recListAddress = findViewById(R.id.rec_list_address);
        btnChoseAddress = findViewById(R.id.btn_chose_address);
        tvAddAddress = findViewById(R.id.tv_add_address);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecyclerViewAddress(){
        if (Common.currentUser.getAddressList() != null) {
            recListAddress.setLayoutManager(new LinearLayoutManager(AddressActivity.this, RecyclerView.VERTICAL, false));
            AddressClickInterface clickInterface = () -> addressAdapter.notifyDataSetChanged();
            addressAdapter = new AddressAdapter(Common.currentUser.getAddressList(), clickInterface, this);
            recListAddress.setAdapter(addressAdapter);
        } else {
            recListAddress.setVisibility(View.GONE);
            btnChoseAddress.setEnabled(false);
            btnChoseAddress.setBackgroundResource(R.drawable.custom_button_continue_hidden);
            btnChoseAddress.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Common.currentUser.getAddressList() == null)
            return;
        saveDatabase();
    }

    private void saveDatabase(){
        List<Address> list = new ArrayList<>();
        if (Common.currentUser.getAddressList().size() == 0)
            list = Common.currentUser.getAddressList();
        else {
            int pos = 0;
            for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i)
                if (Common.currentUser.getAddressList().get(i).isDefaultAddress()) {
                    pos = i;
                    break;
                }

            list.add(Common.currentUser.getAddressList().get(pos));
            for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i)
                if (i != pos) {
                    list.add(Common.currentUser.getAddressList().get(i));
                    list.get(list.size() - 1).setDefaultAddress(false);
                }
        }
        Common.currentUser.setAddressList(list);

        String[] mode = getResources().getStringArray(R.array.mode_login);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                if (Common.currentUser.getAddressList().size() == 0)
                    return;
                for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i) {
                    @SuppressLint("DefaultLocale") String count = String.format(getString(R.string.num), i);
                    snapshot.child(count).getRef().setValue(Common.currentUser.getAddressList().get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startAddAddress(){
        Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("pos", -1);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_ship_location)
            finish();
        if (view.getId() == R.id.btn_chose_address){
            Common.addressNow = addressAdapter.getSelectedPosition();
            finish();
        }
        if (view.getId() == R.id.tv_add_address)
            startAddAddress();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}