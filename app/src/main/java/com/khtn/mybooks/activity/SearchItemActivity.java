package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.StringHistorySearchClickInterface;
import com.khtn.mybooks.Interface.StringSearchClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.BookItemAdapter;
import com.khtn.mybooks.adapter.StringSearchHistoryItemAdapter;
import com.khtn.mybooks.adapter.StringSearchItemAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.helper.VNCharacterUtils;
import com.khtn.mybooks.model.BookItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchItemActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private TextView tvNumCart;
    private TextView tvSeeMoreOrRemoveListSearch;
    private TextView tvNoProductsSearch;
    private TextView tvNoItem;
    private EditText edtSearch;
    private ProgressBar progressBar;
    private RecyclerView recListHistorySearch;
    private RecyclerView recListSearch;
    private RecyclerView recListItemSearch;
    private FrameLayout layoutCart;
    private ConstraintLayout layoutHistorySearch;
    private ConstraintLayout layoutStringSearch;
    private ConstraintLayout layoutListItem;

    private List<String> listSearchHistory;
    private List<String> listSearch;
    private List<BookItem> bookItemList;
    private DatabaseCart databaseCart;

    private StringSearchHistoryItemAdapter adapterListHistorySearch;

    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private static final String FILE_NAME = "history.txt";
    private boolean remove = false;
    int widthView;
    float maxWidthPixel;
    float maxHeightPixel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);

        init();
        setupSpanCount();
        loadSearchHistory();
        setupListHistorySearch();

        ibBack.setOnClickListener(this);
        edtSearch.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager)  edtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(edtSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            edtSearch.requestFocus();
        }, 300);
        edtSearch.setOnKeyListener(onEnter);
        edtSearch.addTextChangedListener(new SearchTextChange());

        layoutCart.setOnClickListener(this);
        tvSeeMoreOrRemoveListSearch.setOnClickListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (bookItemList.size() > 0) {
            setupListItemSearch();
            return;
        }
        setupListHistorySearch();
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_search_page);
        tvNumCart = findViewById(R.id.tv_num_cart);
        tvSeeMoreOrRemoveListSearch = findViewById(R.id.tv_see_more_remove_history_search);
        tvNoProductsSearch = findViewById(R.id.tv_no_products);
        tvNoItem = findViewById(R.id.tv_no_item);
        edtSearch = findViewById(R.id.edt_search_item);
        progressBar = findViewById(R.id.progress_search_item);
        recListHistorySearch = findViewById(R.id.rec_list_history_search);
        recListSearch = findViewById(R.id.rec_list_string_search);
        recListItemSearch = findViewById(R.id.rec_list_item_search);
        layoutCart = findViewById(R.id.layout_shopping_cart);
        layoutHistorySearch = findViewById(R.id.layout_history_search);
        layoutStringSearch = findViewById(R.id.layout_string_search);
        layoutListItem = findViewById(R.id.layout_item_search);

        listSearchHistory = new ArrayList<>();
        listSearch = new ArrayList<>();
        bookItemList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");
        recListHistorySearch.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recListSearch.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        databaseCart = new DatabaseCart(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupSpanCount();
    }

    public void setupSpanCount() {
        maxWidthPixel = getResources().getDisplayMetrics().widthPixels;
        maxHeightPixel = getResources().getDisplayMetrics().heightPixels;
        widthView = AppUtil.dpToPx(182, this);
        int spanCount = (int) (maxWidthPixel/widthView);
        recListItemSearch.setLayoutManager(new GridLayoutManager(this, spanCount));

        setupListItemSearch();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_exit_search_page:
                finish();
                break;
            case R.id.layout_shopping_cart:
                startCartPage();
                break;
            case R.id.tv_see_more_remove_history_search:
                seeMoreOreRemoveAllHistorySearch();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public void onBackPressed() {
        if (edtSearch.getText().toString().isEmpty())
            super.onBackPressed();
        else {
            edtSearch.setText("");
            bookItemList.clear();
            setupListHistorySearch();
        }
    }

    public void seeMoreOreRemoveAllHistorySearch(){
        if (remove)
            clearSearchHistory();
        else {
            tvSeeMoreOrRemoveListSearch.setText(getText(R.string.remove_history_search));
            remove = true;
            setupListHistorySearch();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setupListHistorySearch(){
        layoutCart.setVisibility(View.GONE);
        layoutStringSearch.setVisibility(View.GONE);
        layoutListItem.setVisibility(View.GONE);

        if (listSearchHistory.size() != 0) {
            layoutHistorySearch.setVisibility(View.VISIBLE);
            if (!remove && listSearchHistory.size() > 3) {
                tvSeeMoreOrRemoveListSearch.setText(getText(R.string.see_more_history_search));
                remove = false;
            } else {
                tvSeeMoreOrRemoveListSearch.setText(getText(R.string.remove_history_search));
                remove = true;
            }

            if (remove)
                adapterListHistorySearch = new StringSearchHistoryItemAdapter(listSearchHistory, stringHistorySearchClickInterface);
            else {
                List<String> listChild = new ArrayList<>();
                for (int i = 0; i < 4; ++i)
                    listChild.add(listSearchHistory.get(i));
                adapterListHistorySearch = new StringSearchHistoryItemAdapter(listChild, stringHistorySearchClickInterface);
            }
            recListHistorySearch.setAdapter(adapterListHistorySearch);
            adapterListHistorySearch.notifyDataSetChanged();
        } else {
            layoutHistorySearch.setVisibility(View.GONE);
        }
    }

    public void setupListSearch(){
        layoutCart.setVisibility(View.GONE);
        layoutStringSearch.setVisibility(View.VISIBLE);
        layoutHistorySearch.setVisibility(View.GONE);
        layoutListItem.setVisibility(View.GONE);

        StringSearchItemAdapter adapterListSearch = new StringSearchItemAdapter(listSearch, stringSearchClickInterface);
        if (listSearch.size() > 0) {
            recListSearch.setAdapter(adapterListSearch);
            recListSearch.setVisibility(View.VISIBLE);
            tvNoProductsSearch.setVisibility(View.GONE);
        } else {
            recListSearch.setVisibility(View.GONE);
            tvNoProductsSearch.setVisibility(View.VISIBLE);
        }
    }

    public void setupListItemSearch(){
        showLayoutCart();
        layoutListItem.setVisibility(View.VISIBLE);
        layoutHistorySearch.setVisibility(View.GONE);
        layoutStringSearch.setVisibility(View.GONE);

        BookItemAdapter adapterBookItem = new BookItemAdapter(this, bookItemList);
        if (bookItemList.size()> 0){
            recListItemSearch.setAdapter(adapterBookItem);
            recListItemSearch.setVisibility(View.VISIBLE);
            tvNoItem.setVisibility(View.GONE);
        } else {
            recListItemSearch.setVisibility(View.GONE);
            tvNoItem.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("DefaultLocale")
    public void showLayoutCart(){
        layoutCart.setVisibility(View.VISIBLE);
        int i = databaseCart.getCarts().size();
        if (i > 0) {
            tvNumCart.setVisibility(View.VISIBLE);
            tvNumCart.setText(String.format(getString(R.string.num), i));
        }
        else
            tvNumCart.setVisibility(View.GONE);
    }

    public void startCartPage(){
        if (Common.currentUser == null) {
            AppUtil.startLoginPage(this);
            return;
        }
        Intent intent = new Intent(SearchItemActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void loadSearchHistory(){
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader streamReader = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(streamReader);
            String str;

            while ((str = reader.readLine()) != null){
                if (str == null || str.length() < 1)
                    continue;
                listSearchHistory.add(str);
            }

            Collections.reverse(listSearchHistory);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void saveToSearchHistory(String str){
        FileOutputStream fos = null;

        try {
            boolean check = false;
            int index = 0;
            for (; index < listSearchHistory.size(); ++index)
                if (listSearchHistory.get(index).equals(str)) {
                    check = true;
                    break;
                }
            if (check && index == 0)
                return;
            else if (check) {
                fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                OutputStreamWriter streamWriter = new OutputStreamWriter(fos);
                BufferedWriter writer = new BufferedWriter(streamWriter);

                listSearchHistory.remove(index);

                for (int i = listSearchHistory.size() - 1; i >= 0; --i)
                    writer.write(listSearchHistory.get(i) + "\n");

                writer.write(str + "\n");
                writer.close();
            } else {
                fos = openFileOutput(FILE_NAME, MODE_APPEND);
                OutputStreamWriter streamWriter = new OutputStreamWriter(fos);
                BufferedWriter writer = new BufferedWriter(streamWriter);
                writer.write(str + "\n");
                writer.close();
            }

            listSearchHistory.add(0, str);
            if (listSearchHistory.size() < 2 || listSearchHistory.size() > 4) {
                remove = false;
                setupListHistorySearch();
            } else
                adapterListHistorySearch.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void removeLineSearchHistory(int line){
        listSearchHistory.remove(line);
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            for (String str : listSearchHistory)
                writer.write(str + "\n");

            setupListHistorySearch();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void clearSearchHistory(){
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write("".getBytes());
            listSearchHistory.clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        setupListHistorySearch();
    }

    public void readDataItem(String strKey){
        String[] keyList = VNCharacterUtils.removeAccent(strKey).toLowerCase().trim().split(" ");
        bookItemList.clear();
        progressBar.setVisibility(View.VISIBLE);
        layoutHistorySearch.setVisibility(View.GONE);
        layoutStringSearch.setVisibility(View.GONE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nameBook = VNCharacterUtils.removeAccent(dataSnapshot.child("name").getValue(String.class).trim().toLowerCase());

                    for (String key : keyList)
                        if (nameBook.contains(key)){
                            if (nameBook.contains(VNCharacterUtils.removeAccent(strKey).toLowerCase().trim()))
                                bookItemList.add(0, dataSnapshot.getValue(BookItem.class));
                            else
                                bookItemList.add(dataSnapshot.getValue(BookItem.class));
                            break;
                        }
                    if (bookItemList.size() > 30)
                        break;
                }
                progressBar.setVisibility(View.GONE);
                setupListItemSearch();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    protected View.OnKeyListener onEnter = (v, keyCode, event) -> {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager imm = (InputMethodManager)  edtSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (edtSearch.getText().toString().trim().isEmpty()) {
                edtSearch.setText("");
                return true;
            }
            saveToSearchHistory(edtSearch.getText().toString());
            readDataItem(edtSearch.getText().toString());
            return true;
        }
        return false; // very important
    };

    public class SearchTextChange implements TextWatcher {
        String stringBefore;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            stringBefore = edtSearch.getText().toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            bookItemList.clear();
            if (edtSearch.getText().toString().trim().isEmpty()){
                setupListHistorySearch();
                return;
            }
            if (stringBefore.equals(edtSearch.getText().toString().trim()))
                return;

            String key = VNCharacterUtils.removeAccent(edtSearch.getText().toString().trim()).toLowerCase();
            listSearch.clear();

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String nameBook = dataSnapshot.child("name").getValue(String.class);
                        if (VNCharacterUtils.removeAccent(nameBook.toLowerCase()).contains(key)) {
                            String str = nameBook.contains("-") ? nameBook.substring(0, nameBook.indexOf("-")).trim().toLowerCase() : nameBook.toLowerCase();
                            boolean contain = false;

                            for (String name : listSearch)
                                if (name.equals(str)) {
                                    contain = true;
                                    break;
                                }

                            if (!contain)
                                listSearch.add(str);
                        }
                    }
                    setupListSearch();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private final StringHistorySearchClickInterface stringHistorySearchClickInterface = new StringHistorySearchClickInterface() {
        @Override
        public void OnRemove(int line) {
            SearchItemActivity.this.removeLineSearchHistory(line);
        }

        @Override
        public void OnClick(String str) {
            edtSearch.setText(str);
            edtSearch.setSelection(str.length());
            saveToSearchHistory(str);
            readDataItem(str);
        }
    };

    private final StringSearchClickInterface stringSearchClickInterface = new StringSearchClickInterface() {
        @Override
        public void OnClick(String str) {
            edtSearch.setText(str);
            edtSearch.setSelection(str.length());
            saveToSearchHistory(str);
            readDataItem(str);
        }
    };
}