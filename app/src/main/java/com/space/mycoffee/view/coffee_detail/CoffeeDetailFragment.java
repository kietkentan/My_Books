package com.space.mycoffee.view.coffee_detail;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.ListImageAdapter;
import com.space.mycoffee.custom.dialog.DialogDoneAddCart;
import com.space.mycoffee.databinding.FragmentCoffeeDetailBinding;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.cart.CartViewModel;
import com.space.mycoffee.view_model.coffee.CoffeeViewModel;

public class CoffeeDetailFragment extends Fragment {
    private FragmentCoffeeDetailBinding binding;
    private CoffeeViewModel viewModel;

    private DialogDoneAddCart dialogDoneAddCart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoffeeDetailBinding.inflate(inflater, container, false);
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

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void signUpView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_coffeeDetailFragment_to_fragmentSignInSignUp);
    }

    private void cartView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_coffeeDetailFragment_to_fragmentCart);
    }

    private void userView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_coffeeDetailFragment_to_userFragment);
    }

    private void searchView() {
        Bundle searchBundle = new Bundle();
        searchBundle.putBoolean(Constants.IS_DETAIL, true);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_coffeeDetailFragment_to_searchItemFragment,
                searchBundle
        );
    }

    private void initView() {
        String idCoffee = getArguments().getString(Constants.ID_COFFEE, null);
        viewModel = new CoffeeViewModel(requireContext());
        binding.setViewModel(viewModel);
        dialogDoneAddCart = new DialogDoneAddCart(requireContext(), this::cartView);
        if (idCoffee == null) backView();
        else viewModel.fetchCoffeeDetail(idCoffee);
        setupCart();
    }

    private void clickView() {
        binding.ibExitDetail.setOnClickListener(view -> backView());
        binding.btnAddCart.setOnClickListener(view -> addCardClick());
        binding.btnBuyNow.setOnClickListener(view -> buyNowClick());
        binding.tvSearchItem.setOnClickListener(view -> searchView());
        binding.ivMenuInDetail.setOnClickListener(view -> showMenuPopup());
        binding.layoutShoppingCart.setOnClickListener(view -> cartView());
        binding.ibAddFavorite.setOnClickListener(view -> {
            if (AppSingleton.currentUser == null) signUpView();
            else {
                viewModel.setCheckFavorite(!viewModel.isFavorite.getValue());
                viewModel.addToFavorite();
            }
        });

        binding.listImg.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tvPosition.setText(String.format(getString(R.string.fraction), position + 1, viewModel.coffeeDetail.getValue().getImage().size()));
            }
        });
    }

    private void buyNowClick() {
        if (AppSingleton.currentUser != null)
            viewModel.buyNow(1);
        else signUpView();
    }

    private void addCardClick() {
        if (AppSingleton.currentUser != null)
            viewModel.addToCart(1);
        else signUpView();
    }

    @SuppressLint("NonConstantResourceId")
    public void showMenuPopup() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.ivMenuInDetail);
        popupMenu.getMenuInflater().inflate(R.menu.in_detail_menu, popupMenu.getMenu());
        popupMenu.getMenu().getItem(2).setTitle(AppSingleton.currentUser == null ? R.string.login : R.string.my_account);
        popupMenu.setGravity(Gravity.END);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.m_share:
                    Extensions.toast(requireContext(), R.string.share);
                    break;
                case R.id.m_home:
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.homePageFragment);
                    break;
                case R.id.m_my_account:
                    if (AppSingleton.currentUser == null) {
                        signUpView();
                        return true;
                    }
                    userView();
                    break;
                case R.id.m_help:
                    Extensions.toast(requireContext(), R.string.help);
                    break;
            }
            return true;
        });
        popupMenu.show();
    }

    public void openDialog() {
        setupCart();
        dialogDoneAddCart.updateItem(viewModel.coffeeDetail.getValue());
        dialogDoneAddCart.showDialog();
    }

    private void observe() {
        viewModel.coffeeDetail.observe(getViewLifecycleOwner(), coffeeDetail -> {
            if (coffeeDetail != null) {
                setupCoffeeData(coffeeDetail);
            }
        });

        viewModel.stateAddCart.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                viewModel.setStateAddCart(null);
                openDialog();
            }
        });

        viewModel.stateBuyNow.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                if (viewModel.tempOrder != null) {
                    (new ViewModelProvider(requireActivity()).get(CartViewModel.class)).setTempListBuyNow(viewModel.tempOrder);
                    viewModel.tempOrder = null;
                    viewModel.setStateBuyNow(null);
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_coffeeDetailFragment_to_completePaymentFragment);
                }
            }
        });
    }

    private void setupCoffeeData(@NonNull CoffeeDetail coffeeDetail) {
        ListImageAdapter imageAdapter = new ListImageAdapter(coffeeDetail.getImage());
        binding.listImg.setAdapter(imageAdapter);
        if (coffeeDetail.getDiscount() > 0) {
            binding.tvOriginalPriceBook.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        boolean checkDateTimeSell = Extensions.checkDateTimeSell(coffeeDetail.getTimeSell());
        boolean isFavorite = checkFavoriteList(coffeeDetail.getId());
        viewModel.setEnableBuy(coffeeDetail.getAmount() > 0 && checkDateTimeSell);
        viewModel.setSell(checkDateTimeSell);
        viewModel.setCheckFavorite(isFavorite);
    }

    public void setupCart() {
        int i = 0;
        if (AppSingleton.currentUser != null && AppSingleton.currentUser.getCartList() != null) {
            i = AppSingleton.currentUser.getCartList().size();
        }
        binding.setNumberCart(i);
    }

    private boolean checkFavoriteList(String id) {
        if (AppSingleton.currentUser == null || AppSingleton.currentUser.getList_favorite() == null || AppSingleton.currentUser.getList_favorite().size() == 0)
            return false;

        for (CoffeeDetail coffee : AppSingleton.currentUser.getList_favorite()) {
            if (coffee.getId().equals(id))
                return true;
        }
        return false;
    }
}
