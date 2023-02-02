package com.khtn.mybooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.khtn.mybooks.Interface.OnOrderChangeSizeInterface;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.ViewPagerOrderStatusAdapter;

public class OrderStatusActivity extends AppCompatActivity implements OnOrderChangeSizeInterface{
    private ImageButton ibBack;
    private EditText edtSearchRequest;
    private TabLayout tabStatus;
    private ViewPager2 viewStatus;
    private ViewPagerOrderStatusAdapter adapter;

    private String search = "";
    private int currentSelectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        AppUtil.changeStatusBarColor(this, getColor(R.color.reduced_price));

        init();
        setupTabLayout();
        setCurrentItem();

        edtSearchRequest.setOnKeyListener(onEnter);
        ibBack.setOnClickListener(v -> finish());
    }

    protected View.OnKeyListener onEnter = (v, keyCode, event) -> {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager imm = (InputMethodManager)  edtSearchRequest.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearchRequest.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (search.equals(edtSearchRequest.getText().toString()))
                return true;

            currentSelectedTab = tabStatus.getSelectedTabPosition();
            search = edtSearchRequest.getText().toString();
            if (search.isEmpty())
                viewStatus.setAdapter(adapter);
            else
                viewStatus.setAdapter(new ViewPagerOrderStatusAdapter(this, edtSearchRequest.getText().toString(), this));

            setCurrentItem();
            return true;
        }
        return false; // very important
    };

    private void setCurrentItem(){
        tabStatus.setScrollPosition(currentSelectedTab, 0f, true);
        viewStatus.setCurrentItem(currentSelectedTab);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabStatus, viewStatus, (tab, position) -> {
            if (position == 0)
                tab.setText(R.string.all_request);
            else
                tab.setText(getResources().getStringArray(R.array.status)[position - 1]);
        }).attach();
    }

    public void init(){
        currentSelectedTab = getIntent().getIntExtra("tabSelect", 0);

        ibBack = findViewById(R.id.ib_exit_order_status);
        edtSearchRequest = findViewById(R.id.edt_search_by_order_code);
        tabStatus = findViewById(R.id.tab_order_status);
        viewStatus = findViewById(R.id.vp_order_status);

        adapter = new ViewPagerOrderStatusAdapter(this, null, this);
        viewStatus.setAdapter(adapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public void onZero() {
        currentSelectedTab = tabStatus.getSelectedTabPosition();
        if (edtSearchRequest.getText().toString().isEmpty())
            viewStatus.setAdapter(new ViewPagerOrderStatusAdapter(this, null, this));
        else
            viewStatus.setAdapter(new ViewPagerOrderStatusAdapter(this, edtSearchRequest.getText().toString(), this));
        setCurrentItem();
    }
}