package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.ProductManagerInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.ListProductManagerAdapter;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.helper.VNCharacterUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductManagerActivity extends AppCompatActivity implements ProductManagerInterface {
    private ImageButton ibBack;
    private EditText edtSearch;
    private RecyclerView recListProduct;

    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private List<String> idList;
    private ListProductManagerAdapter adapter;
    private String search = "";
    private int modeLoad = 1;
    boolean checkFinal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);
        AppUtil.changeStatusBarColor(this, getColor(R.color.reduced_price));

        init();
        setupRecyclerViewProductList();
        getData();

        ibBack.setOnClickListener(v -> finish());
        edtSearch.setOnKeyListener(onEnter);
    }

    public void init() {
        ibBack = findViewById(R.id.ib_exit_product_manager);
        edtSearch = findViewById(R.id.edt_search_by_product_name);
        recListProduct = findViewById(R.id.rec_list_product_manager);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");

        idList = new ArrayList<>();
    }

    protected View.OnKeyListener onEnter = (v, keyCode, event) -> {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager imm = (InputMethodManager)  edtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (search.equals(edtSearch.getText().toString()))
                return true;

            search = edtSearch.getText().toString();
            checkFinal = false;
            int size = idList.size() - 1;

            idList.clear();
            while (size >= 0) {
                adapter.notifyItemRemoved(size--);
            }
            if (search.isEmpty()) {
                modeLoad = 1;
                getData();
            } else if (search.toUpperCase().startsWith("TN")) {
                modeLoad = 2;
                getDataById(search.toUpperCase());
            } else {
                modeLoad = 3;
                getDataByName(search);
            }
            return true;
        }
        return false; // very important
    };

    public void getDataById(String id) {
        reference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkFinal = true;
                if (snapshot.exists()) {
                    idList.add(id);
                    adapter.notifyItemInserted(idList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDataByName(String str) {
        String[] keyList = VNCharacterUtils.removeAccent(str).toLowerCase().split(" ");
        reference.orderByChild("id").startAfter(idList.size() > 0 ? idList.get(idList.size() - 1) : getString(R.string.start_id_book)).limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            checkFinal = true;
                            return;
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = VNCharacterUtils.removeAccent(dataSnapshot.child("name").getValue(String.class)).toLowerCase();

                            for (String key : keyList){
                                if (name.contains(key)){
                                    idList.add(dataSnapshot.child("id").getValue(String.class));
                                    adapter.notifyItemInserted(idList.size() - 1);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getData() {
        reference.orderByChild("id").startAfter(idList.size() > 0 ? idList.get(idList.size() - 1) : getString(R.string.start_id_book)).limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            checkFinal = true;
                            return;
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            idList.add(dataSnapshot.child("id").getValue(String.class));
                            adapter.notifyItemInserted(idList.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setupRecyclerViewProductList(){
        recListProduct.setLayoutManager(new LinearLayoutManager(ProductManagerActivity.this, RecyclerView.VERTICAL, false));
        adapter = new ListProductManagerAdapter(idList, ProductManagerActivity.this, ProductManagerActivity.this);
        recListProduct.setAdapter(adapter);
    }

    @Override
    public void onLoading() {
        if (!checkFinal) {
            switch (modeLoad){
                case 1:
                    getData();
                    break;
                case 2:
                    getDataById(search);
                    break;
                case 3:
                    getDataByName(search);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}