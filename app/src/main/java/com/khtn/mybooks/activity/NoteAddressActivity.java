package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.Interface.NoteAddressRemoveInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.NoteAddressAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Address;

import java.util.ArrayList;
import java.util.List;

public class NoteAddressActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private AppCompatButton btnNewAddress;
    private TextView tvNumCart;
    private RecyclerView recListAddress;
    private FrameLayout layoutCart;
    private ConstraintLayout layoutNoneAddress;

    private DatabaseCart databaseCart;
    private NoteAddressAdapter noteAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_address);
        AppUtil.changeStatusBarColor(this, "#E32127");

        init();
        setupCart();
        setRecyclerViewAddress();

        ibBack.setOnClickListener(this);
        btnNewAddress.setOnClickListener(this);
        layoutCart.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCart();
        setRecyclerViewAddress();
    }

    @SuppressLint("DefaultLocale")
    public void setupCart(){
        if (databaseCart.getCarts().size() != 0){
            tvNumCart.setText(String.format("%d", databaseCart.getCarts().size()));
        } else
            tvNumCart.setVisibility(View.GONE);
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_note_address);
        btnNewAddress = findViewById(R.id.btn_new_address);
        tvNumCart = findViewById(R.id.tv_num_cart);
        recListAddress = findViewById(R.id.rec_list_address);
        layoutCart = findViewById(R.id.layout_shopping_cart);
        layoutNoneAddress = findViewById(R.id.layout_none_address);

        databaseCart = new DatabaseCart(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecyclerViewAddress(){
        if (Common.currentUser.getAddressList() == null || Common.currentUser.getAddressList().size() == 0) {
            layoutNoneAddress.setVisibility(View.VISIBLE);
            recListAddress.setVisibility(View.GONE);
        }
        else if (Common.currentUser.getAddressList().size() != 0) {
            NoteAddressRemoveInterface removeInterface = this::setRecyclerViewAddress;
            layoutNoneAddress.setVisibility(View.GONE);
            recListAddress.setVisibility(View.VISIBLE);
            recListAddress.setLayoutManager(new LinearLayoutManager(NoteAddressActivity.this, RecyclerView.VERTICAL, false));
            noteAddressAdapter = new NoteAddressAdapter(Common.currentUser.getAddressList(), this, removeInterface);
            recListAddress.setAdapter(noteAddressAdapter);
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

        String[] mode = {"mybooks", "google", "facebook"};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                if (Common.currentUser.getAddressList().size() == 0)
                    return;
                for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i) {
                    @SuppressLint("DefaultLocale") String count = String.format("%d", i);
                    snapshot.child(count).getRef().setValue(Common.currentUser.getAddressList().get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startAddAddress(){
        Intent intent = new Intent(NoteAddressActivity.this, AddAddressActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("pos", -1);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startCart(){
        Intent intent = new Intent(NoteAddressActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_exit_note_address)
            finish();
        if (v.getId() == R.id.btn_new_address)
            startAddAddress();
        if (v.getId() == R.id.layout_shopping_cart)
            startCart();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}