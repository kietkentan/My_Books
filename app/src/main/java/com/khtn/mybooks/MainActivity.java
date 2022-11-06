package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khtn.mybooks.Interface.CartFragmentClickInterface;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements CartFragmentClickInterface{
    private BottomNavigationView bottomNav;
    private Fragment fragment;
    private final Fragment homeFrag = new HomeFragment();
    private final Fragment userFrag = new UserFragment();
    private final Fragment cartFrag = new CartFragment(this);
    private Stack<Fragment> fragmentStack;

    private boolean fm = true;
    private boolean backPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        switchFragment();
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        fragmentStack = new Stack<>();

        fm = bundle.getBoolean("fm");
        bottomNav = findViewById(R.id.bottom_navigation);
        createFm();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragment).commit();
    }

    public void createFm(){
        if (fm) {
            fragment = homeFrag;
        }
        else {
            fragment = cartFrag;
            switchSelectItem();
            fragmentStack.add(homeFrag);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void switchFragment(){
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment freFrag = fragment;
            switch (item.getItemId()){
                case R.id.home_page:
                    fragment = homeFrag;
                    break;
                case R.id.favorite_page:
                    Toast.makeText(MainActivity.this, "FavoriteFragment", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.user_page:
                    fragment = userFrag;
                    break;
                case R.id.directory_page:
                    Toast.makeText(MainActivity.this, "DirectoryFragment", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.cart_page:
                    fragment = cartFrag;
                    break;
            }
            if (!freFrag.equals(fragment)) {
                fm = true;
                if (fragment.equals(homeFrag))
                    fragmentStack.clear();
                else
                    fragmentStack.add(freFrag);

                openFragment();
            }
            return true;
        });
    }

    public void switchSelectItem(){
        int id = 0;
        if (fragment.equals(homeFrag))
            id = R.id.home_page;
        else if (fragment.equals(userFrag))
            id = R.id.user_page;
        else if (fragment.equals(cartFrag))
            id = R.id.cart_page;
        bottomNav.setSelectedItemId(id);
    }

    @Override
    public void onBackPressed() {
        if (fragment.equals(homeFrag)){
            if (backPress) {
                super.onBackPressed();
                finishAffinity();
            }
            backPress = true;
            Toast.makeText(MainActivity.this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> backPress = false, 2000);
        } else if (!fm)
            finish();
        else {
            fragment = fragmentStack.pop();
            switchSelectItem();
            openFragment();
        }
    }

    public void openFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.switch_enter_activity, R.anim.switch_exit_activity, R.anim.switch_enter_activity, R.anim.switch_exit_activity);
        transaction.replace(R.id.fragment_view, fragment);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void OnClick() {
        fragment = homeFrag;
        switchSelectItem();
        openFragment();
    }
}