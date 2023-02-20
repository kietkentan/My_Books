package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.StaffManagerCLickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.ListStaffAdapter;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Staff;
import com.khtn.mybooks.model.StaffPermission;

import java.util.ArrayList;
import java.util.List;

public class StaffManagerActivity extends AppCompatActivity {
    private ImageButton ibBack;
    private RecyclerView recListStaff;
    private List<Staff> list;
    private List<String> stringList;
    private List<Boolean> booleanList;
    private int numClick = -1;

    @SuppressWarnings("FieldCanBeLocal")
    private ListStaffAdapter adapter;

    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manager);
        AppUtil.changeStatusBarColor(this, getColor(R.color.reduced_price));

        init();

        loadData();
        ibBack.setOnClickListener(v -> finish());
    }

    public void init(){
        Bundle bundle = getIntent().getExtras();
        stringList = bundle.getStringArrayList("staff");

        ibBack = findViewById(R.id.ib_exit_staff_manager);
        recListStaff = findViewById(R.id.rec_list_staff);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
        list = new ArrayList<>();
        booleanList = new ArrayList<>();
    }

    public StaffManagerCLickInterface anInterface = new StaffManagerCLickInterface() {
        @Override
        public void onClickShowInfo(int position) {
            numClick = position;
            setupRecyclerViewListStaff();
        }

        @Override
        public void onClickSaveInfo(int position, StaffPermission permission) {
            savePermission(position, permission);
        }

        @Override
        public void onClickRemoveInfo(String userId) {
            removeStaff(userId);
        }
    };

    public void setupRecyclerViewListStaff(){
        booleanList.clear();
        for (int i = 0; i < list.size(); ++i)
            booleanList.add(false);

        if (numClick != -1)
            booleanList.set(numClick, true);

        adapter = new ListStaffAdapter(list, booleanList, anInterface, StaffManagerActivity.this);
        recListStaff.setLayoutManager(new LinearLayoutManager(StaffManagerActivity.this, RecyclerView.VERTICAL, false));
        recListStaff.setAdapter(adapter);
    }

    public void loadData(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (int i = stringList.size() - 1; i >= 0; --i)
                        if (dataSnapshot.child(stringList.get(i)).exists()) {
                            Staff staff = dataSnapshot.child(stringList.get(i)).child("staff").getValue(Staff.class);
                            staff.setUserId(stringList.get(i));
                            list.add(staff);
                            stringList.remove(i);
                        }

                    if (stringList.size() == 0)
                        break;
                }
                setupRecyclerViewListStaff();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void savePermission(int position, StaffPermission permission){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child(list.get(position).getUserId()).exists()){
                        dataSnapshot.child(list.get(position).getUserId()).child("staff").child("permission").getRef().setValue(permission);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeStaff(String userId){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child(userId).exists()){
                        dataSnapshot.child(userId).child("staff").getRef().removeValue();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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