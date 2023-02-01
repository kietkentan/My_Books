package com.khtn.mybooks.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khtn.mybooks.Interface.OnOrderChangeSizeInterface;
import com.khtn.mybooks.fragment.OrderManagerFragment;

public class ViewPagerOrderManagerAdapter extends FragmentStateAdapter {
    private final String keyword;
    private final OnOrderChangeSizeInterface anInterface;

    public ViewPagerOrderManagerAdapter(@NonNull FragmentActivity fragmentActivity, String keyword, OnOrderChangeSizeInterface anInterface) {
        super(fragmentActivity);
        this.keyword = keyword;
        this.anInterface = anInterface;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
            case 1:
                fragment = new OrderManagerFragment(position + 1, keyword, anInterface);
                break;
            case 2:
            case 3:
                fragment = new OrderManagerFragment(position + 3, keyword, anInterface);
                break;
            case 4:
                fragment = new OrderManagerFragment(0, keyword, anInterface);
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
