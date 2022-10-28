package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.adapter.BookItemAdapter;
import com.khtn.mybooks.adapter.PublisherItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.BookItem;
import com.khtn.mybooks.model.PublisherItem;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ig;
    private ShapeableImageView ivGoUserPage;
    private RecyclerView rcPublisher;
    private RecyclerView rcBestSellerBooks;
    private RecyclerView rcNewBooks;
    private ProgressBar progressBarLoadData;
    private EditText edtNoticeChat;

    private PublisherItemAdapter publisherItemAdapter;
    private BookItemAdapter bookItemAdapter;
    private BookItemAdapter bookItemAdapter2;

    private List<PublisherItem> publisherList;
    private List<BookItem> bookList;

    private FirebaseDatabase database;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    private boolean isBackPressedOnce = false;
    private String idPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        ig.setOnClickListener(this);

        publisherList = new ArrayList<>();
        bookList = new ArrayList<>();

        ivGoUserPage.setOnClickListener(this);

        // checking network
        if (AppUtil.isNetworkAvailable(HomeActivity.this)) {
            loadData();
            setRecyclerViewPublisher();
            setRecyclerViewBook();
        } else {
            progressBarLoadData.setVisibility(View.VISIBLE);
            Toast.makeText(HomeActivity.this, R.string.network_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (isBackPressedOnce) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
        isBackPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressedOnce = false;
            }
        }, 2000);
    }

    public void init(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        database = FirebaseDatabase.getInstance();

        ig = (ImageView) findViewById(R.id.imageView);
        ivGoUserPage = (ShapeableImageView) findViewById(R.id.iv_go_user_page);
        rcPublisher = (RecyclerView) findViewById(R.id.rec_publishers);
        rcBestSellerBooks = (RecyclerView) findViewById(R.id.rec_bestSellers);
        rcNewBooks = (RecyclerView) findViewById(R.id.rec_news);
        progressBarLoadData = (ProgressBar) findViewById(R.id.progress_home);
        edtNoticeChat = (EditText) findViewById(R.id.notice_chat);
    }

    public void setRecyclerViewPublisher(){
        rcPublisher.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        publisherItemAdapter = new PublisherItemAdapter(publisherList);
        rcPublisher.setAdapter(publisherItemAdapter);
    }

    public void setRecyclerViewBook(){
        rcBestSellerBooks.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rcNewBooks.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        bookItemAdapter = new BookItemAdapter(this, bookList);
        rcBestSellerBooks.setAdapter(bookItemAdapter);
        bookItemAdapter2 = new BookItemAdapter(this, bookList);
        rcNewBooks.setAdapter(bookItemAdapter2);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_go_user_page)
            startUserPage();
        if (view.getId() == R.id.imageView)
            signOut();
    }

    public void loadData(){
        database.getReference("publisher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    PublisherItem item = dataSnapshot.getValue(PublisherItem.class);
                    publisherList.add(item);
                }
                idPublisher = publisherList.get(1).getId();
                publisherItemAdapter.notifyDataSetChanged();

                database.getReference("book").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            BookItem item = dataSnapshot.getValue(BookItem.class);
                            bookList.add(item);
                        }
                        bookItemAdapter.notifyDataSetChanged();
                        bookItemAdapter2.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startUserPage(){
        Intent intent = new Intent(HomeActivity.this, UserActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, ivGoUserPage, ViewCompat.getTransitionName(ivGoUserPage));
        startActivity(intent, optionsCompat.toBundle());
    }

    // because the user page is incomplete, it should be placed here temporarily
    public void signOut(){
        Common.clearUser(HomeActivity.this);
        if (Common.modeLogin == 1){
            Common.currentUser = null;
            Common.modeLogin = 0;
        } else if (Common.modeLogin == 2) {
            gsc.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Common.currentUser = null;
                            Common.modeLogin = 0;
                        }
                    });
        } else if (Common.modeLogin == 3) {
            FirebaseAuth.getInstance().signOut();
            Common.currentUser = null;
            Common.modeLogin = 0;
        }
    }
}