package com.khtn.mybooks.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.BookItemAdapter;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.BookItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListProductFragment extends Fragment {
    private View view;
    private RadioGroup radioGroup;
    private RecyclerView recListProduct;

    private final List<BookItem> bookItems;
    private final int mode;
    int widthView;
    int spanCount;
    float maxWidthPixel;
    float maxHeightPixel;
    int lastId = 0;

    public ListProductFragment(List<BookItem> bookItems, int mode) {
        this.bookItems = bookItems;
        this.mode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_product, container, false);

        init();
        checkAdapter();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (lastId != radioGroup.getCheckedRadioButtonId())
                setProductForPrice();
        });

        return view;
    }

    public void init(){
        maxWidthPixel = getResources().getDisplayMetrics().widthPixels;
        maxHeightPixel = getResources().getDisplayMetrics().heightPixels;
        widthView = AppUtil.dpToPx(182, getContext());
        spanCount = (int) (maxWidthPixel/widthView);

        radioGroup = view.findViewById(R.id.group_select_price_sort);
        recListProduct = view.findViewById(R.id.rec_list_product);
    }

    public void checkAdapter(){
        switch (mode){
            case 0:
                radioGroup.setVisibility(View.GONE);
                setAdapter(bookItems);
                break;
            case 1:
                setNewProduct();
                break;
            case 2:
                setProductForPrice();
                break;
        }
    }

    public void setAdapter(List<BookItem> bookItemsTmp){
        BookItemAdapter adapter = new BookItemAdapter(getContext(), bookItemsTmp);
        recListProduct.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recListProduct.setAdapter(adapter);
    }

    public void setNewProduct(){
        radioGroup.setVisibility(View.GONE);
        List<BookItem> bookItemsTemp = new ArrayList<>(bookItems);
        Collections.sort(bookItemsTemp);
        for (int i = bookItemsTemp.size() - 1; i >= 0; --i){
            if (AppUtil.numDays(bookItemsTemp.get(i).getDatePosted()) > 100)
                bookItemsTemp.remove(i);
        }
        setAdapter(bookItemsTemp);
    }

    public void setProductForPrice(){
        radioGroup.setVisibility(View.VISIBLE);

        if (checkButton())
            Collections.sort(bookItems, BookItem.ascendingPrice);
        else
            Collections.sort(bookItems, BookItem.descendingPrice);
        setAdapter(bookItems);
    }

    public boolean checkButton(){
        lastId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = view.findViewById(lastId);
        return radioButton.getText().equals(getText(R.string.increase));
    }
}