package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.DatabaseReference;
import com.space.mycoffee.listener.OnOrderChangeSizeInterface;
import com.space.mycoffee.view.order.ListOrderFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerOrderStatusAdapter extends FragmentStateAdapter {
    private final int ITEM_COUNT = 6;

    private final List<ListOrderFragment> fragments = new ArrayList<>();
    private final DatabaseReference reference;
    private final OnOrderChangeSizeInterface anInterface;

    public ViewPagerOrderStatusAdapter(@NonNull FragmentActivity fragmentActivity, DatabaseReference reference, OnOrderChangeSizeInterface anInterface) {
        super(fragmentActivity);
        this.reference = reference;
        this.anInterface = anInterface;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeKey(String keyword) {
        for (int i = 0; i < fragments.size(); i++) {
            fragments.get(i).load(keyword);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ListOrderFragment fragment = new ListOrderFragment(position, null, reference, anInterface);
        fragments.add(fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}
