package com.space.mycoffee.view.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.CoffeeItemAdapter;
import com.space.mycoffee.databinding.FragmentHomePageBinding;
import com.space.mycoffee.model.CoffeeItem;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.view_model.home.CoffeeHomeViewModel;

public class HomePageFragment extends Fragment {
    private FragmentHomePageBinding binding;
    private CoffeeHomeViewModel viewModel;

    private CoffeeItemAdapter coffeeItemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        initView();
        clickView();
        observe();
    }

    private void initView() {
        viewModel = new CoffeeHomeViewModel();
        coffeeItemAdapter = new CoffeeItemAdapter(CoffeeItemAdapter.itemCallback, this::itemCoffeeClicked);
        viewModel.fetchCoffeeNew();

        binding.recNews.setLayoutManager(new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false));
        binding.recNews.setAdapter(coffeeItemAdapter);
    }

    private void itemCoffeeClicked(@NonNull CoffeeItem coffeeItem) {
        Bundle bunCoffee = new Bundle();
        bunCoffee.putString(Constants.ID_COFFEE, coffeeItem.getId());
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_homePageFragment_to_coffeeDetailFragment,
                bunCoffee
        );
    }

    private void searchView() {
        Bundle searchBundle = new Bundle();
        searchBundle.putBoolean(Constants.IS_DETAIL, false);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_homePageFragment_to_searchItemFragment,
                searchBundle
        );
    }

    private void clickView() {
        binding.tvSearchItem.setOnClickListener(view -> searchView());
    }

    private void observe() {
        viewModel.listCoffeeNew.observe(getViewLifecycleOwner(), coffeeItems -> {
            if (coffeeItems != null) {
                coffeeItemAdapter.submitList(coffeeItems);
                stopShimmerNew();
            }
        });
    }

    public void stopShimmerNew(){
        new Handler().postDelayed(() -> {
            binding.shimmerNews.stopShimmer();
            binding.shimmerNews.setVisibility(View.GONE);
            binding.recNews.setVisibility(View.VISIBLE);
        }, 200);
    }
}
