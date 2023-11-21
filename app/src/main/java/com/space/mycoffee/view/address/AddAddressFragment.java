package com.space.mycoffee.view.address;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.custom.dialog.DialogExitAddAddress;
import com.space.mycoffee.databinding.FragmentAddAddressBinding;
import com.space.mycoffee.listener.DialogTwoButtonListener;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.view_model.address.AddressViewModel;

public class AddAddressFragment extends Fragment {
    private FragmentAddAddressBinding binding;
    private AddressViewModel viewModel;

    private DialogExitAddAddress dialogExitAddAddress;
    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false);
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
                openDialogExit();
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
        binding.setViewModel(viewModel);

        dialogExitAddAddress = new DialogExitAddAddress(requireContext(), new DialogTwoButtonListener() {
            @Override
            public void onCancelClicked() {
                backView();
            }

            @Override
            public void onConfirmClicked() {}
        });
    }

    private void openDialogExit() {
        dialogExitAddAddress.showDialog();
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void choseLocationView(int pos) {
        Address address = viewModel.addressSelected.getValue();
        if (pos == 1) viewModel.setCityLocation(null);
        else if (pos == 2) {
            viewModel.setDistrictsLocation(null);
            viewModel.setCityLocation(address.getProvinces_cities());
        } else {
            viewModel.setPrecinctLocation(null);
            viewModel.setCityLocation(address.getProvinces_cities());
            viewModel.setDistrictsLocation(address.getDistricts());
        }
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_addAddressFragment_to_choseAddressFragment);
    }

    private void checkInfo() {
        Address address = viewModel.addressSelected.getValue();
        if (address == null) return;
        viewModel.clearError();
        boolean isValid = true;
        if (address.getName() == null || address.getName().isEmpty()) {
            viewModel.setErrorName(getString(R.string.name_not_empty));
            isValid = false;
        } else if (!Extensions.isName(address.getName())) {
            viewModel.setErrorName(getString(R.string.name_not_valid));
            isValid = false;
        }
        if (address.getPhone() == null || address.getPhone().isEmpty()) {
            viewModel.setErrorPhone(getString(R.string.enter_phone_number));
            isValid = false;
        } else if (!Extensions.isPhoneNumber(address.getPhone())) {
            viewModel.setErrorPhone(getString(R.string.phone_not_valid));
            isValid = false;
        }
        if (address.getProvinces_cities() == null) {
            viewModel.setErrorLocation(getString(R.string.chose_address));
            isValid = false;
        }
        if (address.getAddress() == null || address.getAddress().isEmpty()) {
            viewModel.setErrorAddress(getString(R.string.enter_delivery_address));
            isValid = false;
        } else if (!address.getAddress().contains(" ")) {
            viewModel.setErrorAddress(getString(R.string.delivery_must_at_least_two_words));
            isValid = false;
        } else if (!Extensions.isString(address.getAddress())) {
            viewModel.setErrorAddress(getString(R.string.delivery_not_valid));
        }
        if (isValid) {
            viewModel.saveAddressSelected();
            backView();
        }
    }

    private void clickView() {
        binding.ibExitAddAddress.setOnClickListener(view -> openDialogExit());
        binding.tvChoseProvincesCities.setOnClickListener(view -> choseLocationView(1));
        binding.tvChoseDistricts.setOnClickListener(view -> choseLocationView(2));
        binding.tvChosePrecinct.setOnClickListener(view -> choseLocationView(3));
        binding.btnConfirmAddress.setOnClickListener(view -> checkInfo());

        binding.cbSetItDefault.setOnCheckedChangeListener((compoundButton, b) -> viewModel.setIsDefault(b));

        binding.edtEnterRecipientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setName(editable.toString());
            }
        });

        binding.edtEnterPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setPhone(editable.toString());
            }
        });

        binding.edtEnterDeliveryAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAddressString(editable.toString());
            }
        });
    }

    private void observe() {
        viewModel.errorName.observe(getViewLifecycleOwner(), error ->
                binding.edtEnterRecipientName.setError(error)
        );

        viewModel.errorPhone.observe(getViewLifecycleOwner(), error ->
                binding.edtEnterPhone.setError(error)
        );

        viewModel.errorAddress.observe(getViewLifecycleOwner(), error ->
                binding.edtEnterDeliveryAddress.setError(error)
        );

        viewModel.errorLocation.observe(getViewLifecycleOwner(), error -> {
            if (error != null) Extensions.toast(requireContext(), R.string.chose_address);
        });
    }
}
