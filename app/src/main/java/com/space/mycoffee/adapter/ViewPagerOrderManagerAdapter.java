package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.DatabaseReference;
import com.space.mycoffee.listener.OnOrderChangeSizeInterface;
import com.space.mycoffee.view.manager.order.OrderManagerInListFragment;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerOrderManagerAdapter extends FragmentStateAdapter {
    private final int ITEM_COUNT = 5;

    private final List<OrderManagerInListFragment> fragments = new ArrayList<>();
    private final DatabaseReference reference;
    private final OnOrderChangeSizeInterface anInterface;

    public ViewPagerOrderManagerAdapter(@NonNull FragmentActivity fragmentActivity, DatabaseReference reference, OnOrderChangeSizeInterface anInterface) {
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
        OrderManagerInListFragment fragment;
        switch (position) {
            case 0:
            case 1:
                fragment = new OrderManagerInListFragment(position + 1, null, reference, anInterface);
                fragments.add(fragment);
                break;
            case 2:
            case 3:
                fragment = new OrderManagerInListFragment(position + 3, null, reference, anInterface);
                fragments.add(fragment);
                break;
            default:
                fragment = new OrderManagerInListFragment(0, null, reference, anInterface);
                fragments.add(fragment);
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }
}
