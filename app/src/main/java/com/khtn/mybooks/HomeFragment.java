package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.ViewPublisherClickInterface;
import com.khtn.mybooks.adapter.BookItemAdapter;
import com.khtn.mybooks.adapter.PublisherItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.BookItem;
import com.khtn.mybooks.model.PublisherItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener, ViewPublisherClickInterface {
    private View view;
    private ImageView ig;
    private RecyclerView rcPublisher;
    private RecyclerView rcBestSellerBooks;
    private RecyclerView rcNewBooks;

    private PublisherItemAdapter publisherItemAdapter;
    private BookItemAdapter bookItemAdapter;
    private BookItemAdapter bookItemAdapter2;

    private List<PublisherItem> publisherList;
    private Map<String, List<BookItem>> bookList;
    private List<BookItem> listBookItem;

    private FirebaseDatabase database;
    private GoogleSignInClient gsc;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();

        ig.setOnClickListener(this);
        setRecyclerViewBook();
        setRecyclerViewPublisher();
        loadData();
        return view;
    }

    public void init(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);
        database = FirebaseDatabase.getInstance();

        publisherList = new ArrayList<>();
        bookList = new HashMap<>();

        ig = (ImageView) view.findViewById(R.id.imageView);
        rcPublisher = (RecyclerView) view.findViewById(R.id.rec_publishers);
        rcBestSellerBooks = (RecyclerView) view.findViewById(R.id.rec_bestSellers);
        rcNewBooks = (RecyclerView) view.findViewById(R.id.rec_news);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageView)
            signOut();
    }

    private void loadData(){
        database.getReference("publisher").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    PublisherItem item = dataSnapshot.getValue(PublisherItem.class);
                    publisherList.add(item);
                }
                publisherItemAdapter.notifyDataSetChanged();
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
            database.getReference("book").orderByChild("publisher").equalTo(idPublisher).addListenerForSingleValueEvent(new ValueEventListener() {
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
    }

    // because the user page is incomplete, it should be placed here temporarily
    private void signOut(){
        Common.clearUser(getActivity());
        if (Common.modeLogin == 1){
            Common.currentUser = null;
            Common.modeLogin = 0;
        } else if (Common.modeLogin == 2) {
            gsc.signOut()
                    .addOnCompleteListener(getActivity(), task -> {
                        Common.currentUser = null;
                        Common.modeLogin = 0;
                    });
        } else if (Common.modeLogin == 3) {
            FirebaseAuth.getInstance().signOut();
            Common.currentUser = null;
            Common.modeLogin = 0;
        }
    }

    @Override
    public void OnItemClick(String idPublisher) {
        loadBook(idPublisher);
    }
}