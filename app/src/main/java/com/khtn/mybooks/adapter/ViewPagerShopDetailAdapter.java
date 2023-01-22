package com.khtn.mybooks.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khtn.mybooks.fragment.ListProductFragment;
import com.khtn.mybooks.fragment.ShopInformationFragment;
import com.khtn.mybooks.model.BookItem;
import com.khtn.mybooks.model.Publisher;

import java.util.List;

public class ViewPagerShopDetailAdapter extends FragmentStateAdapter {
    private final List<BookItem> bookItemList;
    private final Publisher publisher;

    public ViewPagerShopDetailAdapter(@NonNull FragmentActivity fragmentActivity, List<BookItem> bookItemList, Publisher publisher) {
        super(fragmentActivity);
        this.bookItemList = bookItemList;
        this.publisher = publisher;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment frag;
        if (position == 3)
            frag = new ShopInformationFragment(publisher);
        else
            frag = new ListProductFragment(bookItemList, position);

        return frag;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
