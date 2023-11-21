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
import com.space.mycoffee.adapter.AddressInNoteAdapter;
import com.space.mycoffee.databinding.FragmentNoteAddressBinding;
import com.space.mycoffee.listener.AddressListener;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.view_model.address.AddressViewModel;

public class NoteAddressFragment extends Fragment {
    private FragmentNoteAddressBinding binding;
    private AddressViewModel viewModel;

    private OnBackPressedCallback callback;
    private AddressInNoteAdapter inNoteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoteAddressBinding.inflate(inflater, container, false);
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

    private void backView() {
        requireActivity().getViewModelStore().clear();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void cartView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_noteAddressFragment_to_fragmentCart);
    }

    private void editAddressView(Address address) {
        viewModel.clearError();
        viewModel.setAddressSelected(address);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_noteAddressFragment_to_addAddressFragment);
    }

    public void setupCart() {
        int i = 0;
        if (AppSingleton.currentUser != null && AppSingleton.currentUser.getCartList() != null) {
            i = AppSingleton.currentUser.getCartList().size();
        }
        binding.setNumberCart(i);
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);
        inNoteAdapter = new AddressInNoteAdapter(AddressInListAdapter.itemCallback, requireContext(), new AddressListener() {
            @Override
            public void onSelectedChange(Address address) {

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

        binding.recListAddress.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListAddress.setAdapter(inNoteAdapter);
        setupCart();
    }

    private void clickView() {
        binding.ibExitNoteAddress.setOnClickListener(view -> backView());
        binding.layoutShoppingCart.setOnClickListener(view -> cartView());
        binding.btnNewAddress.setOnClickListener(view -> editAddressView(null));
    }

    private void observe() {
        viewModel.getListAddress();
        viewModel.listAddress.observe(getViewLifecycleOwner(), list -> {
            if (list != null) inNoteAdapter.submitList(list);
        });
    }
}
