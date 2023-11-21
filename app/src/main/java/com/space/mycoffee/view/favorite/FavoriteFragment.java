package com.space.mycoffee.view.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.FavoriteItemAdapter;
import com.space.mycoffee.custom.dialog.DialogDoneAddCart;
import com.space.mycoffee.databinding.FragmentFavoriteBinding;
import com.space.mycoffee.listener.CoffeeFavoriteListener;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.favorite.FavoriteViewModel;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private FavoriteViewModel viewModel;

    private FavoriteItemAdapter favoriteItemAdapter;

    private DialogDoneAddCart dialogDoneAddCart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
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

    private void detailView(String idCoffee) {
        Bundle bunCoffee = new Bundle();
        bunCoffee.putString(Constants.ID_COFFEE, idCoffee);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_favoriteFragment_to_coffeeDetailFragment,
                bunCoffee
        );
    }

    private void cartView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_favoriteFragment_to_fragmentCart);
    }

    public void openDialog() {
        CoffeeDetail coffeeDetail = viewModel.currentCoffeeAddCart;
        if (coffeeDetail == null) return;
        dialogDoneAddCart.updateItem(coffeeDetail);
        dialogDoneAddCart.showDialog();
        viewModel.currentCoffeeAddCart = null;
    }

    private void initView() {
        viewModel = new FavoriteViewModel(requireContext());
        binding.setViewModel(viewModel);
        dialogDoneAddCart = new DialogDoneAddCart(requireContext(), this::cartView);
        favoriteItemAdapter = new FavoriteItemAdapter(FavoriteItemAdapter.itemCallback, new CoffeeFavoriteListener() {
            @Override
            public void onItemClicked(String idCoffee) {
                detailView(idCoffee);
            }

            @Override
            public void onFavoriteClicked(String idCoffee) {
                viewModel.removeFromFavorite(idCoffee);
            }

            @Override
            public void onAddCartClicked(CoffeeDetail coffeeDetail) {
                viewModel.addToCart(coffeeDetail, 1);
            }
        });

        binding.recListFavoriteItem.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListFavoriteItem.setAdapter(favoriteItemAdapter);
        viewModel.fetchListFavorite();
    }

    private void clickView() {
        binding.btnContinueShopping.setOnClickListener(view ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.homePageFragment)
        );
    }

    private void observe() {
        viewModel.listCoffee.observe(getViewLifecycleOwner(), coffees -> {
            if (coffees != null) {
                favoriteItemAdapter.submitList(coffees);
            }
        });

        viewModel.stateAddCart.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                viewModel.setStateAddCart(null);
                openDialog();
            }
        });
    }
}
