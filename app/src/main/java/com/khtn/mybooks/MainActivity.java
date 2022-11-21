package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.khtn.mybooks.Interface.ContinueShoppingClickInterface;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.fragment.CartFragment;
import com.khtn.mybooks.fragment.FavoriteItemFragment;
import com.khtn.mybooks.fragment.HomeFragment;
import com.khtn.mybooks.fragment.ListOrderFragment;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements ContinueShoppingClickInterface {
    private BottomNavigationView bottomNav;
    private Fragment fragment;
    private final Fragment homeFrag = new HomeFragment();
    private final Fragment favoriteFrag = new FavoriteItemFragment(this);
    private final Fragment userFrag = new ListOrderFragment.UserFragment();
    private final Fragment cartFrag = new CartFragment(this);
    private Stack<Fragment> fragmentStack;

    private int fm = 1;
                    // 1: Home Page
                    // 2: Favorite Page
                    // 3: User Page
                    // 4: danh muc
                    // 5: Cart Page
    private boolean backPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        switchFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchSelectItem();
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        fragmentStack = new Stack<>();

        fm = bundle.getInt("fragment");
        bottomNav = findViewById(R.id.bottom_navigation);
        createFm();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, fragment).commit();
    }

    public void createFm(){
        switch (fm) {
            case 1:
                fragment = homeFrag;
                break;
            case 2:
                fragment = favoriteFrag;
                break;
            case 3:
                fragment = userFrag;
                fm = 1;
                break;
            case 5:
                fragment = cartFrag;
                break;
        }
        switchSelectItem();
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
                    if (Common.currentUser != null)
                        fragment = favoriteFrag;
                    else {
                        AppUtil.startLoginPage(this);
                        return true;
                    }
                    break;
                case R.id.user_page:
                    fragment = userFrag;
                    break;
                case R.id.directory_page:
                    Toast.makeText(MainActivity.this, "DirectoryFragment", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.cart_page:
                    if (Common.currentUser != null)
                        fragment = cartFrag;
                    else {
                        AppUtil.startLoginPage(this);
                        return true;
                    }
                    break;
            }
            if (!freFrag.equals(fragment)) {
                if (fragment.equals(homeFrag))
                    fragmentStack.clear();
                else
                    fragmentStack.add(freFrag);
                fm = 1;
                openFragment();
            }
            return true;
        });
    }

    public void switchSelectItem(){
        int id = 0;
        if (fragment.equals(homeFrag))
            id = R.id.home_page;
        else if (fragment.equals(favoriteFrag))
            id = R.id.favorite_page;
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
        } else if (fm != 1)
            finish();
        else {
            if (fragmentStack.isEmpty())
                fragment = homeFrag;
            else
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}