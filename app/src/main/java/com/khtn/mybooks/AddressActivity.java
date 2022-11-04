package com.khtn.mybooks;

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
import com.khtn.mybooks.Interface.AddressClickInterface;
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

    private AddressClickInterface clickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

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
        if (Common.addressLists != null) {
            recListAddress.setLayoutManager(new LinearLayoutManager(AddressActivity.this, RecyclerView.VERTICAL, false));
            clickInterface = new AddressClickInterface() {
                @Override
                public void OnClick() {
                    addressAdapter.notifyDataSetChanged();
                }
            };
            addressAdapter = new AddressAdapter(Common.addressLists, clickInterface, this);
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
        saveDatabase();
    }

    private void saveDatabase(){
        int pos = 0;
        for (int i = 0; i < Common.addressLists.size(); ++i)
            if (Common.addressLists.get(i).isDefaultAddress()) {
                pos = i;
                break;
            }

        List<Address> list = new ArrayList<>();
        list.add(Common.addressLists.get(pos));
        for (int i = 0; i < Common.addressLists.size(); ++i)
            if (i != pos)
                list.add(Common.addressLists.get(i));
        Common.addressLists = list;

        String[] mode = {"mybooks", "google", "facebook"};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                for (int i = 0; i < Common.addressLists.size(); ++i) {
                    @SuppressLint("DefaultLocale") String count = String.format("%d", i);
                    snapshot.child(count).getRef().setValue(Common.addressLists.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startAddAddress(){
        startActivity(new Intent(AddressActivity.this, AddAddressActivity.class));
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
}