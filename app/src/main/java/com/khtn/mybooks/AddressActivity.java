package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.khtn.mybooks.Interface.AddressClickInterface;
import com.khtn.mybooks.adapter.AddressAdapter;
import com.khtn.mybooks.common.Common;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recListAddress;
    private AddressAdapter addressAdapter;
    private AppCompatButton btnChoseAddress;

    private AddressClickInterface clickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        init();
        setRecyclerViewAddress();
        btnChoseAddress.setOnClickListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void init(){
        recListAddress = findViewById(R.id.rec_list_address);
        btnChoseAddress = findViewById(R.id.btn_chose_address);

        clickInterface = () -> addressAdapter.notifyDataSetChanged();
    }

    public void setRecyclerViewAddress(){
        recListAddress.setLayoutManager(new LinearLayoutManager(AddressActivity.this, RecyclerView.VERTICAL, false));
        addressAdapter = new AddressAdapter(Common.addressLists, clickInterface);
        recListAddress.setAdapter(addressAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_ship_location)
            finish();
        if (view.getId() == R.id.btn_chose_address){
            Common.addressNow = addressAdapter.getSelectedPosition();
            finish();
        }
    }
}