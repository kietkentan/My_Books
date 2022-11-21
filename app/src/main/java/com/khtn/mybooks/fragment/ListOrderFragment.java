package com.khtn.mybooks.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.RequestItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Request;

import java.util.ArrayList;
import java.util.List;

public class ListOrderFragment extends Fragment {
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recListRequest;
    private ConstraintLayout layoutNoneRequest;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private List<Request> requestList;
    private RequestItemAdapter adapter;
    private final int status;
    private final String code;

    public ListOrderFragment(int status, String code) {
        this.status = status;
        this.code = code;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_order, container, false);

        init();
        setupRecyclerViewListRequest();

        checkSearch();
        return view;
    }

    public void init(){
        requestList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progress_load_list_request);
        recListRequest = view.findViewById(R.id.rec_list_request);
        layoutNoneRequest = view.findViewById(R.id.layout_none_request);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("request");
    }

    public void setupRecyclerViewListRequest(){
        adapter = new RequestItemAdapter(requestList, getContext());
        recListRequest.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recListRequest.setAdapter(adapter);
    }

    public void checkSearch(){
        if (code == null)
            loadData();
        else
            loadDataWithCode();
    }

    private void loadDataWithCode() {
        progressBar.setVisibility(View.VISIBLE);
        reference.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (status == 0) {
                        if (snapshot.child("idUser").getValue(String.class).equals(Common.currentUser.getId()))
                            requestList.add(snapshot.getValue(Request.class));
                    } else {
                        if (snapshot.child("idUser").getValue(String.class).equals(Common.currentUser.getId())
                                && snapshot.child("status").getValue(Integer.class) == status)
                            requestList.add(snapshot.getValue(Request.class));
                    }
                }

                if (requestList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    layoutNoneRequest.setVisibility(View.VISIBLE);
                    recListRequest.setVisibility(View.INVISIBLE);
                } else {
                    layoutNoneRequest.setVisibility(View.GONE);
                    recListRequest.setVisibility(View.VISIBLE);
                    getNamePublisher();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        reference.orderByChild("idUser").equalTo(Common.currentUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (status == 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        requestList.add(dataSnapshot.getValue(Request.class));
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        if (dataSnapshot.child("status").getValue(Integer.class) == status)
                            requestList.add(dataSnapshot.getValue(Request.class));
                }

                if (requestList.size() == 0){
                    progressBar.setVisibility(View.GONE);
                    layoutNoneRequest.setVisibility(View.VISIBLE);
                    recListRequest.setVisibility(View.INVISIBLE);
                } else {
                    layoutNoneRequest.setVisibility(View.GONE);
                    recListRequest.setVisibility(View.VISIBLE);
                    getNamePublisher();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getNamePublisher(){
        DatabaseReference reference = database.getReference("publisher");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < requestList.size(); ++i) {
                    requestList.get(i).setNamePublisher(snapshot.child(requestList.get(i).getIdPublisher()).child("name").getValue(String.class));
                    adapter.notifyItemChanged(i);
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}