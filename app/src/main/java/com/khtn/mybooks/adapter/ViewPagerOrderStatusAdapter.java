package com.khtn.mybooks.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khtn.mybooks.fragment.ListOrderFragment;

public class ViewPagerOrderStatusAdapter extends FragmentStateAdapter {
    private final String code;

    public ViewPagerOrderStatusAdapter(@NonNull FragmentActivity fragmentActivity, String code) {
        super(fragmentActivity);
        this.code = code;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ListOrderFragment(position, code);
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
