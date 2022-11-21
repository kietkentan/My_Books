package com.khtn.mybooks;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.FavoriteClickInterface;
import com.khtn.mybooks.adapter.FavoriteItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Book;

import java.util.ArrayList;
import java.util.List;

public class FavoriteItemActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private AppCompatButton btnContinueShopping;
    private ProgressBar progressBar;
    private RecyclerView recListItem;
    private LinearLayout layoutListFavorite;
    private ConstraintLayout layoutNoneItem;

    private List<Book> listFavorite;
    private FavoriteItemAdapter adapter;

    private FirebaseDatabase database;
    private FavoriteClickInterface clickInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_item);
        AppUtil.changeStatusBarColor(this, "#E32127");

        init();

        setupRecyclerViewFavoriteList();
        loadData();

        ibBack.setOnClickListener(v -> finish());
        btnContinueShopping.setOnClickListener(v -> startHome());
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_favorite_item);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);
        progressBar = findViewById(R.id.progress_load_list_favorite_item);
        recListItem = findViewById(R.id.rec_list_favorite_item);
        layoutListFavorite = findViewById(R.id.layout_list_favorite_item);
        layoutNoneItem = findViewById(R.id.layout_none_favorite_item);

        database = FirebaseDatabase.getInstance();
        listFavorite = new ArrayList<>();
        clickInterface = () -> {
            layoutNoneItem.setVisibility(View.VISIBLE);
            layoutListFavorite.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        };
    }

    public void startHome(){
        Intent intent = new Intent(FavoriteItemActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();

        bundle.putInt("fragment", 1);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }

    public void setupRecyclerViewFavoriteList(){
        recListItem.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new FavoriteItemAdapter(listFavorite, this, clickInterface);
        recListItem.setAdapter(adapter);
    }

    public void loadData(){
        String[] mode = {"mybooks", "google", "facebook"};
        database.getReference("user").child(mode[Common.modeLogin - 1]).
                child(Common.currentUser.getId()).child("list_favorite").
                addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    layoutNoneItem.setVisibility(View.VISIBLE);
                    layoutListFavorite.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    listFavorite.add(dataSnapshot.getValue(Book.class));

                adapter.notifyDataSetChanged();
                layoutNoneItem.setVisibility(View.GONE);
                layoutListFavorite.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                layoutNoneItem.setVisibility(View.VISIBLE);
                layoutListFavorite.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}