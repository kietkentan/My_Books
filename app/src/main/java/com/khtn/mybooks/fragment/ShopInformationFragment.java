package com.khtn.mybooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.khtn.mybooks.R;
import com.khtn.mybooks.model.Publisher;

public class ShopInformationFragment extends Fragment {
    private View view;
    private TextView tvSince;
    private TextView tvTotalProducts;
    private TextView tvDescription;
    private TextView tvRatingScore;
    private TextView tvRatingTurn;
    private TextView tvFollowed;

    private final Publisher publisher;

    public ShopInformationFragment(Publisher publisher) {
        this.publisher = publisher;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop_information, container, false);

        init();
        setupInfo();

        return view;
    }

    public void init(){
        tvSince = view.findViewById(R.id.tv_member_since);
        tvTotalProducts = view.findViewById(R.id.tv_total_products);
        tvDescription = view.findViewById(R.id.tv_shop_description);
        tvRatingScore = view.findViewById(R.id.tv_rating_score);
        tvRatingTurn = view.findViewById(R.id.tv_rating_turn);
        tvFollowed = view.findViewById(R.id.tv_people_followed);
    }

    public void setupInfo(){
        tvSince.setText(String.format(getContext().getString(R.string.status),
                publisher.getWorked().substring(publisher.getWorked().lastIndexOf('/') + 1)));
        tvTotalProducts.setText(String.format(getContext().getString(R.string.num), publisher.getProduct()));
        tvDescription.setText(publisher.getDescription() == null ? "" : publisher.getDescription());
        tvRatingScore.setText(String.format(getContext().getString(R.string.float_fraction), publisher.getRating().getScore(), 5.0));
        tvRatingTurn.setText(String.format(getContext().getString(R.string.total_rating), Integer.toString(publisher.getRating().getTurn())));
        tvFollowed.setText(String.format(getContext().getString(R.string.status), Integer.toString(publisher.getFollowed())));
    }
}