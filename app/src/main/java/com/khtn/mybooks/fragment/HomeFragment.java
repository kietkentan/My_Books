package com.khtn.mybooks.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.activity.SearchItemActivity;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.Interface.ViewPublisherClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.BookItemAdapter;
import com.khtn.mybooks.adapter.PublisherItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.BookItem;
import com.khtn.mybooks.model.PublisherItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener, ViewPublisherClickInterface {
    private View view;
    private TextView tvSearch;
    private RecyclerView rcPublisher;
    private RecyclerView rcBestSellerBooks;
    private RecyclerView rcNewBooks;
    private ShimmerFrameLayout shimmerBestSeller;
    private ShimmerFrameLayout shimmerNews;
    private ShimmerFrameLayout shimmerPublishers;

    private PublisherItemAdapter publisherItemAdapter;
    private BookItemAdapter bookItemAdapter;
    private BookItemAdapter bookItemAdapter2;

    private List<PublisherItem> publisherList;
    private Map<String, List<BookItem>> bookList;
    private List<BookItem> listBookItem;

    private FirebaseDatabase database;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        AppUtil.defaultStatusBarColor(getContext());

        init();

        tvSearch.setOnClickListener(this);

        setRecyclerViewBook();
        setRecyclerViewPublisher();
        loadData();
        return view;
    }

    public void init(){
        database = FirebaseDatabase.getInstance();

        publisherList = new ArrayList<>();
        bookList = new HashMap<>();

        tvSearch = view.findViewById(R.id.tv_search_item);
        rcPublisher = view.findViewById(R.id.rec_publishers);
        rcBestSellerBooks = view.findViewById(R.id.rec_best_sellers);
        rcNewBooks = view.findViewById(R.id.rec_news);
        shimmerBestSeller = view.findViewById(R.id.shimmer_best_seller);
        shimmerNews = view.findViewById(R.id.shimmer_news);
        shimmerPublishers = view.findViewById(R.id.shimmer_publishers);
    }

    public void setRecyclerViewPublisher(){
        rcPublisher.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        publisherItemAdapter = new PublisherItemAdapter(publisherList, this);
        rcPublisher.setAdapter(publisherItemAdapter);
    }

    public void setRecyclerViewBook(){
        rcBestSellerBooks.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rcNewBooks.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
    }

    public void startSearchItemPage(){
        Intent intent = new Intent(getActivity(), SearchItemActivity.class);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search_item:
                startSearchItemPage();
                break;
        }
    }

    private void loadData(){
        startShimmer();
        database.getReference("publisher").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    PublisherItem item = dataSnapshot.getValue(PublisherItem.class);
                    publisherList.add(item);
                }
                publisherItemAdapter.notifyDataSetChanged();
                stopPublisherShimmer();
                loadBook(publisherList.get(0).getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBook(String idPublisher){
        if (bookList.containsKey(idPublisher))
            changeData(idPublisher);
        else {
            database.getReference("book").orderByChild("publisher").limitToFirst(10).equalTo(idPublisher).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<BookItem> newList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookItem item = dataSnapshot.getValue(BookItem.class);
                        newList.add(item);
                    }
                    bookList.put(idPublisher, newList);
                    changeData(idPublisher);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void changeData(String idPublisher){
        listBookItem = bookList.get(idPublisher);
        bookItemAdapter = new BookItemAdapter(getContext(), listBookItem);
        rcBestSellerBooks.setAdapter(bookItemAdapter);
        bookItemAdapter2 = new BookItemAdapter(getContext(), listBookItem);
        rcNewBooks.setAdapter(bookItemAdapter2);

        stopShimmer();
    }

    @Override
    public void OnItemClick(String idPublisher) {
        loadBook(idPublisher);
    }

    public void startShimmer(){
        shimmerBestSeller.startShimmer();
        shimmerNews.startShimmer();
        shimmerPublishers.startShimmer();

        shimmerBestSeller.setVisibility(View.VISIBLE);
        shimmerNews.setVisibility(View.VISIBLE);
        shimmerPublishers.setVisibility(View.VISIBLE);

        rcBestSellerBooks.setVisibility(View.GONE);
        rcNewBooks.setVisibility(View.GONE);
        rcPublisher.setVisibility(View.GONE);
    }

    public void stopPublisherShimmer(){
        new Handler().postDelayed(() -> {
            shimmerPublishers.stopShimmer();
            shimmerPublishers.setVisibility(View.GONE);
            rcPublisher.setVisibility(View.VISIBLE);
        }, 500);
    }

    public void stopShimmer(){
        new Handler().postDelayed(() -> {
            shimmerBestSeller.stopShimmer();
            shimmerNews.stopShimmer();

            shimmerBestSeller.setVisibility(View.GONE);
            shimmerNews.setVisibility(View.GONE);

            rcBestSellerBooks.setVisibility(View.VISIBLE);
            rcNewBooks.setVisibility(View.VISIBLE);
        }, 500);
    }
}