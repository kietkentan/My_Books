package com.khtn.mybooks.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khtn.mybooks.fragment.ListOrderFragment;

public class ViewPagerOrderStatusAdapter extends FragmentStateAdapter {
    private final String keyword;

    public ViewPagerOrderStatusAdapter(@NonNull FragmentActivity fragmentActivity, String keyword) {
        super(fragmentActivity);
        this.keyword = keyword;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ListOrderFragment(position, keyword);
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
