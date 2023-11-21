package com.space.mycoffee.view.manager.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.adapter.ItemOrderManager;
import com.space.mycoffee.adapter.RequestItemAdapter;
import com.space.mycoffee.databinding.FragmentListOrderManagerBinding;
import com.space.mycoffee.listener.OnOrderChangeSizeInterface;
import com.space.mycoffee.listener.RequestItemListener;
import com.space.mycoffee.model.Request;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.VNCharacterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderManagerInListFragment extends Fragment {
    private FragmentListOrderManagerBinding binding;

    private final DatabaseReference reference;
    private final int status;
    private String keyword;
    private List<Request> requestList;

    private final OnOrderChangeSizeInterface anInterface;
    private ItemOrderManager requestItemAdapter;

    public OrderManagerInListFragment(int status, String keyword, DatabaseReference reference, OnOrderChangeSizeInterface anInterface) {
        this.status = status;
        this.keyword = keyword;
        this.reference = reference;
        this.anInterface = anInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListOrderManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        load(keyword);
    }

    public void load(String key) {
        this.keyword = key;
        binding.recListRequestManager.setVisibility(View.GONE);
        requestList.clear();
        if (keyword == null) loadData();
        else if (Extensions.isNumberCode(keyword))
            loadDataWithCode();
        else loadDataWithKey();
    }

    private void initView() {
        requestList = new ArrayList<>();

        requestItemAdapter = new ItemOrderManager(RequestItemAdapter.itemCallback, requireContext(), new RequestItemListener() {
            @Override
            public void onItemClicked(Request request) {
                anInterface.onItemCLicked(request);
            }

            @Override
            public void onItemRemove(int position) {
                reference.child(requestList.get(position).getIdRequest()).child(Constants.STATUS).setValue(6);
                requestList.remove(position);
                setupRecyclerListRequest();
            }

            @Override
            public void onItemAccept(Request request, int position) {
                reference.child(requestList.get(position).getIdRequest()).child(Constants.STATUS).setValue(request.getStatus() + 1);
                requestList.remove(position);
                setupRecyclerListRequest();
            }
        });
        binding.recListRequestManager.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListRequestManager.setAdapter(requestItemAdapter);
    }

    private void loadDataWithKey() {
        binding.progressLoadListRequest.setVisibility(View.VISIBLE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    boolean addedList = false;
                    for (int i = 0; i < dataSnapshot.child(Constants.ORDER_LIST).getChildrenCount(); ++i) {
                        if (addedList)
                            break;
                        int thisStatus = dataSnapshot.child(Constants.STATUS).getValue(Integer.class);

                        if (!(status == 0 || status == thisStatus))
                            break;
                        String bookName =
                                VNCharacterUtils.removeAccent(dataSnapshot.child(Constants.ORDER_LIST).child(String.valueOf(i)).child(Constants.COFFEE_NAME).getValue(String.class)).toLowerCase();
                        List<String> keyList =
                                VNCharacterUtils.removeAccent(Arrays.asList(keyword.trim().toLowerCase().split("\\s+")));

                        for (String key : keyList)
                            if (bookName.contains(key)) {
                                requestList.add(dataSnapshot.getValue(Request.class));
                                addedList = true;
                                break;
                            }
                    }
                }
                setupRecyclerListRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setupRecyclerListRequest();
            }
        });
    }

    private void loadDataWithCode() {
        binding.progressLoadListRequest.setVisibility(View.VISIBLE);
        reference.child(keyword).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (status == 0) {
                        requestList.add(snapshot.getValue(Request.class));
                    } else {
                        if (snapshot.child(Constants.STATUS).getValue(Integer.class) == status)
                            requestList.add(snapshot.getValue(Request.class));
                    }
                }
                setupRecyclerListRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setupRecyclerListRequest();
            }
        });
    }

    public void loadData() {
        binding.progressLoadListRequest.setVisibility(View.VISIBLE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (status == 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        requestList.add(dataSnapshot.getValue(Request.class));
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        if (dataSnapshot.child(Constants.STATUS).getValue(Integer.class) == status)
                            requestList.add(dataSnapshot.getValue(Request.class));
                }
                setupRecyclerListRequest();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setupRecyclerListRequest();
            }
        });
    }

    private void setupRecyclerListRequest() {
        binding.progressLoadListRequest.setVisibility(View.GONE);
        if (requestList.size() == 0) {
            binding.layoutNoneRequest.setVisibility(View.VISIBLE);
            binding.recListRequestManager.setVisibility(View.INVISIBLE);
        } else {
            binding.layoutNoneRequest.setVisibility(View.GONE);
            binding.recListRequestManager.setVisibility(View.VISIBLE);
            requestItemAdapter.submitList(requestList);
        }
    }
}