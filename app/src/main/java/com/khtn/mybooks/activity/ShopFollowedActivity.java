package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.ShopFollowedAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Publisher;

import java.util.ArrayList;
import java.util.List;

public class ShopFollowedActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private RecyclerView recListShopFollowed;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private List<Publisher> publisherList;
    private ShopFollowedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_followed);
        AppUtil.changeStatusBarColor(this, "#E32127");

        init();
        loadData();

        ibBack.setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        adapter.updateFollowed();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_shop_followed);
        recListShopFollowed = findViewById(R.id.rec_list_shop_followed);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("publisher");

        publisherList = new ArrayList<>();
    }

    public void setupRecyclerViewShopFollowed(){
        recListShopFollowed.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new ShopFollowedAdapter(publisherList, this);
        recListShopFollowed.setAdapter(adapter);
    }

    public void loadData(){
        if (Common.currentUser.getList_shopFollow() != null && Common.currentUser.getList_shopFollow().size() > 0){
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (String id:Common.currentUser.getList_shopFollow())
                        publisherList.add(snapshot.child(id).getValue(Publisher.class));

                    setupRecyclerViewShopFollowed();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}