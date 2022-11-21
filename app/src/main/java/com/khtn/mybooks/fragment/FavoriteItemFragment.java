package com.khtn.mybooks.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.AppUtil;
import com.khtn.mybooks.Interface.ContinueShoppingClickInterface;
import com.khtn.mybooks.Interface.FavoriteClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.FavoriteItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Book;

import java.util.ArrayList;
import java.util.List;

public class FavoriteItemFragment extends Fragment {
    private View view;
    private AppCompatButton btnContinueShopping;
    private ProgressBar progressBar;
    private RecyclerView recListItem;
    private LinearLayout layoutListFavorite;
    private ConstraintLayout layoutNoneItem;

    private List<Book> listFavorite;
    private FavoriteItemAdapter adapter;

    private FirebaseDatabase database;
    private FavoriteClickInterface clickInterface;
    private final ContinueShoppingClickInterface continueShoppingClickInterface;

    public FavoriteItemFragment(ContinueShoppingClickInterface continueShoppingClickInterface) {
        this.continueShoppingClickInterface = continueShoppingClickInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_item, container, false);
        AppUtil.changeStatusBarColor(getContext(), "#E32127");

        init();

        setupRecyclerViewFavoriteList();
        loadData();

        btnContinueShopping.setOnClickListener(v -> continueShoppingClickInterface.OnClick());

        return view;
    }

    public void init(){
        btnContinueShopping = view.findViewById(R.id.btn_continue_shopping);
        progressBar = view.findViewById(R.id.progress_load_list_favorite_item);
        recListItem = view.findViewById(R.id.rec_list_favorite_item);
        layoutListFavorite = view.findViewById(R.id.layout_list_favorite_item);
        layoutNoneItem = view.findViewById(R.id.layout_none_favorite_item);

        database = FirebaseDatabase.getInstance();
        listFavorite = new ArrayList<>();
        clickInterface = () -> {
            layoutNoneItem.setVisibility(View.VISIBLE);
            layoutListFavorite.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        };
    }

    public void setupRecyclerViewFavoriteList(){
        recListItem.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new FavoriteItemAdapter(listFavorite, getContext(), clickInterface);
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
}