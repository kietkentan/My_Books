package com.space.mycoffee.view.cart;

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
import com.space.mycoffee.adapter.CardConfirmAdapter;
import com.space.mycoffee.adapter.CartAdapter;
import com.space.mycoffee.databinding.FragmentCompletePaymentBinding;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.cart.CartViewModel;

public class CompletePaymentFragment extends Fragment {
    private FragmentCompletePaymentBinding binding;
    private CartViewModel viewModel;

    private CardConfirmAdapter cardConfirmAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompletePaymentBinding.inflate(inflater, container, false);
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
    public void onDestroy() {
        super.onDestroy();
        if (viewModel.isBuyNow) {
            viewModel.clearViewModel();
        }
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        binding.setViewModel(viewModel);

        cardConfirmAdapter = new CardConfirmAdapter(CartAdapter.itemCallback);
        binding.recListConfirm.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.recListConfirm.setAdapter(cardConfirmAdapter);
        viewModel.setAddressNow(AppSingleton.addressNow);
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void addressView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_completePaymentFragment_to_addressFragment);
    }

    private void clickView() {
        binding.ibExitCompletePayment.setOnClickListener(view -> backView());
        binding.layoutStartAddressPage.setOnClickListener(view -> addressView());
        binding.btnOrder.setOnClickListener(view -> viewModel.createOrder());
    }

    private void observe() {
        viewModel.addressNow.observe(getViewLifecycleOwner(), address ->
                binding.tvNameUser.setText(address == null ? getString(R.string.add_address) : address.getName())
        );

        viewModel.orderListChecked.observe(getViewLifecycleOwner(), list -> {
            if (list != null) cardConfirmAdapter.submitList(list);
        });

        viewModel.stateAddRequest.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Failure) {
                Extensions.toast(requireActivity(), R.string.error_state);
                viewModel.setStateAddRequest(null);
            } else if (state == UiState.Success) {
                Extensions.toast(requireActivity(), R.string.successful_ordering);
                viewModel.setStateAddRequest(null);
                backView();
            }
        });
    }
}
