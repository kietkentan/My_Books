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
import com.khtn.mybooks.Interface.OnOrderChangeSizeInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.OrderManagerItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.helper.VNCharacterUtils;
import com.khtn.mybooks.model.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderManagerFragment extends Fragment implements OnOrderChangeSizeInterface {
    private View view;
    private ProgressBar progressBar;
    private RecyclerView recListRequest;
    private ConstraintLayout layoutNoneRequest;

    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private List<Request> requestList;
    private OrderManagerItemAdapter adapter;
    private final int status;
    private final String keyword;

    private final OnOrderChangeSizeInterface anInterface;

    public OrderManagerFragment(int status, String keyword, OnOrderChangeSizeInterface anInterface) {
        this.status = status;
        this.keyword = keyword;
        this.anInterface = anInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_manager, container, false);

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
        adapter = new OrderManagerItemAdapter(requestList, getContext(), this);
        recListRequest.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recListRequest.setAdapter(adapter);
    }

    public void checkSearch(){
        if (keyword == null)
            loadData();
        else if (AppUtil.isNumberCode(keyword))
            loadDataWithCode();
        else
            loadDataWithKey();
    }

    private void loadDataWithKey() {
        progressBar.setVisibility(View.VISIBLE);
        reference.orderByChild("idPublisher")
                .equalTo(Common.currentUser.getStaff().getPublisherId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            boolean addedList = false;
                            for (int i = 0; i < dataSnapshot.child("orderList").getChildrenCount(); ++i){
                                if (addedList)
                                    break;
                                int thisStatus = dataSnapshot.child("status").getValue(Integer.class);

                                if (!(status == 0 || status == thisStatus))
                                    break;
                                String bookName = VNCharacterUtils.removeAccent(dataSnapshot.child("orderList").child(String.valueOf(i)).child("bookName").getValue(String.class)).toLowerCase();
                                List<String> keyList = VNCharacterUtils.removeAccent(Arrays.asList(keyword.trim().toLowerCase().split("\\s+")));

                                for (String key : keyList)
                                    if (bookName.contains(key)){
                                        requestList.add(dataSnapshot.getValue(Request.class));
                                        addedList = true;
                                        break;
                                    }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        setupRecyclerListRequest();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadDataWithCode() {
        progressBar.setVisibility(View.VISIBLE);
        reference.child(keyword).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (status == 0) {
                        if (snapshot.child("idPublisher").getValue(String.class).equals(Common.currentUser.getId()))
                            requestList.add(snapshot.getValue(Request.class));
                    } else {
                        if (snapshot.child("idPublisher").getValue(String.class).equals(Common.currentUser.getStaff().getPublisherId())
                                && snapshot.child("status").getValue(Integer.class) == status)
                            requestList.add(snapshot.getValue(Request.class));
                    }
                }
                adapter.notifyDataSetChanged();
                setupRecyclerListRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        reference.orderByChild("idPublisher").equalTo(Common.currentUser.getStaff().getPublisherId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
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
                adapter.notifyDataSetChanged();
                setupRecyclerListRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setupRecyclerListRequest(){
        progressBar.setVisibility(View.GONE);
        if (requestList.size() == 0){
            layoutNoneRequest.setVisibility(View.VISIBLE);
            recListRequest.setVisibility(View.INVISIBLE);
        } else {
            layoutNoneRequest.setVisibility(View.GONE);
            recListRequest.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onZero() {
        setupRecyclerListRequest();
        anInterface.onZero();
    }
}