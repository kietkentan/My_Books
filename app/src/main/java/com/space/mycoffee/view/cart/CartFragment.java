package com.space.mycoffee.view.cart;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.CartAdapter;
import com.space.mycoffee.custom.dialog.DialogConfirmRemoveCart;
import com.space.mycoffee.custom.dialog.DialogNoneAddress;
import com.space.mycoffee.custom.dialog.DialogNoneSelectedItem;
import com.space.mycoffee.databinding.FragmentCartBinding;
import com.space.mycoffee.listener.CartListener;
import com.space.mycoffee.listener.DialogConfirmRemoveCartListener;
import com.space.mycoffee.listener.DialogTwoButtonListener;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.view_model.address.AddressViewModel;
import com.space.mycoffee.view_model.cart.CartViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartViewModel viewModel;

    private DialogConfirmRemoveCart dialogConfirmRemoveCart;
    private DialogNoneAddress dialogNoneAddress;
    private DialogNoneSelectedItem dialogNoneSelectedItem;

    private CartAdapter cartAdapter;
    private boolean isNotClear = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        isNotClear = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isNotClear) viewModel.clearViewModel();
    }

    private void itemCoffeeClicked(@NonNull String idCoffee) {
        Bundle bunCoffee = new Bundle();
        bunCoffee.putString(Constants.ID_COFFEE, idCoffee);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_fragmentCart_to_coffeeDetailFragment,
                bunCoffee
        );
    }

    private void handleBuyClicked() {
        if (AppSingleton.addressNow == null)
            openDialogAddAddress();
        else if (cartAdapter.getSelectedCart().size() > 0)
            startCompletePayment();
        else openDialogUnselected();
    }

    private void openDialogRemoveCart(List<String> idCoffee) {
        dialogConfirmRemoveCart.showDialog(idCoffee);
    }

    private void openDialogAddAddress() {
        dialogNoneAddress.showDialog();
    }

    private void openDialogUnselected() {
        dialogNoneSelectedItem.showDialog();
    }

    private void startCompletePayment() {
        isNotClear = true;
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_fragmentCart_to_completePaymentFragment);
    }

    private void addressView() {
        isNotClear = true;
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_fragmentCart_to_addressFragment);
    }

    private void addAddressView() {
        isNotClear = true;
        (new ViewModelProvider(requireActivity()).get(AddressViewModel.class)).setAddressSelected(null);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_fragmentCart_to_addAddressFragment);
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        binding.setViewModel(viewModel);
        binding.setContext(requireContext());
        dialogConfirmRemoveCart = new DialogConfirmRemoveCart(requireContext(), new DialogConfirmRemoveCartListener() {
            @Override
            public void onCancelClick() {}

            @Override
            public void onConfirmClick(List<String> listRemove) {
                viewModel.changeListCartInDatabase(listRemove);
            }
        });

        dialogNoneAddress = new DialogNoneAddress(requireContext(), new DialogTwoButtonListener() {
            @Override
            public void onCancelClicked() {}

            @Override
            public void onConfirmClicked() {
                addAddressView();
            }
        });

        dialogNoneSelectedItem = new DialogNoneSelectedItem(requireContext(), () -> {});

        cartAdapter = new CartAdapter(CartAdapter.itemCallback, new CartListener() {
            @Override
            public void onCheckedChange(String idCoffee, boolean isCheck) {
                viewModel.changeChecked(idCoffee, isCheck);
                List<Order> orderList = viewModel.orderList.getValue();
                binding.cbCheckAllCart.setChecked(cartAdapter.getSelectedCart().size() == orderList.size());
                viewModel.getListChecked(cartAdapter.getSelectedCart());
            }

            @Override
            public void onQuantityChange(String idCoffee, int newQuantity) {
                if (newQuantity == 0) {
                    List<String> list = new ArrayList<>();
                    list.add(idCoffee);
                    openDialogRemoveCart(list);
                } else viewModel.changeQuantity(idCoffee, newQuantity, requireContext());
            }

            @Override
            public void onItemViewChecked(String idCoffee) {
                itemCoffeeClicked(idCoffee);
            }
        });
        binding.recCarts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recCarts.setAdapter(cartAdapter);
        if (viewModel.orderList.getValue() == null)
            viewModel.setOrderList(AppSingleton.currentUser.getCartList());
        viewModel.setAddressNow(AppSingleton.addressNow);
    }

    private void clickView() {
        binding.ibRemoveCart.setOnClickListener(view -> {
            List<String> list = cartAdapter.getSelectedCart();
            if (list.size() > 0) {
                openDialogRemoveCart(list);
            }
        });

        binding.cbCheckAllCart.setOnClickListener(view -> viewModel.checkedAll(binding.cbCheckAllCart.isChecked()));
        binding.tvAddress.setOnClickListener(view -> {
            if (AppSingleton.addressNow == null) addAddressView();
            else addressView();
        });
        binding.btnContinueShopping.setOnClickListener(view ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.homePageFragment)
        );
        binding.btnBuy.setOnClickListener(view -> handleBuyClicked());
    }

    private void observe() {
        viewModel.orderList.observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                cartAdapter.submitList(orders);
            }
        });

        viewModel.orderListChecked.observe(getViewLifecycleOwner(), orderListChecked -> {
            if (orderListChecked == null || orderListChecked.isEmpty()) {
                binding.tvTotalPrice.setTypeface(null, Typeface.NORMAL);
                binding.cbCheckAllCart.setChecked(false);
            } else {
                binding.tvTotalPrice.setTypeface(null, Typeface.BOLD);
                binding.cbCheckAllCart.setChecked(cartAdapter.getSelectedCart().size() == viewModel.orderList.getValue().size());
            }
        });

        viewModel.addressNow.observe(getViewLifecycleOwner(), address ->
                binding.tvAddress.setText(address == null ? getString(R.string.add_address) : Extensions.getStringFromAddress(address))
        );
    }
}
