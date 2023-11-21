package com.space.mycoffee.view.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.AddressInListAdapter;
import com.space.mycoffee.databinding.FragmentAddressBinding;
import com.space.mycoffee.listener.AddressListener;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.view_model.address.AddressViewModel;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class AddressFragment extends Fragment {
    private FragmentAddressBinding binding;
    private AddressViewModel viewModel;

    private AddressInListAdapter addressInListAdapter;

    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater, container, false);
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
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backView();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callback != null) callback.remove();
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);
        addressInListAdapter = new AddressInListAdapter(AddressInListAdapter.itemCallback, requireContext(), new AddressListener() {
            @Override
            public void onSelectedChange(Address address) {
                viewModel.changeAddressNow(address);
                addressInListAdapter.changeAddressNow(address);
            }

            @Override
            public void onRemoveItem(int position) {
                viewModel.removeAddress(position, requireContext());
            }

            @Override
            public void onDefaultChange(int position) {
                viewModel.changeDefault(position);
            }

            @Override
            public void onItemClicked(Address address) {
                editAddressView(address);
            }
        });
        addressInListAdapter.changeAddressNow(AppSingleton.addressNow);
        binding.recListAddress.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListAddress.setAdapter(addressInListAdapter);
    }

    private void backView() {
        viewModel.clearAll();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void editAddressView(Address address) {
        viewModel.clearError();
        viewModel.setAddressSelected(address);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_addressFragment_to_addAddressFragment);
    }

    private void clickView() {
        binding.ibExitShipLocation.setOnClickListener(view -> backView());
        binding.btnChoseAddress.setOnClickListener(view -> {
            viewModel.saveAddressNow();
            backView();
        });
        binding.tvAddAddress.setOnClickListener(view -> editAddressView(null));
    }

    private void observe() {
        viewModel.getListAddress();
        viewModel.listAddress.observe(getViewLifecycleOwner(), list -> {
            if (list != null) addressInListAdapter.submitList(list);
        });
    }
}
