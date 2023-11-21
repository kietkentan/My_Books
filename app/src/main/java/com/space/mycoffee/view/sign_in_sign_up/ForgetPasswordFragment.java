package com.space.mycoffee.view.sign_in_sign_up;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentForgetPasswordBinding;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class ForgetPasswordFragment extends Fragment {
    private FragmentForgetPasswordBinding binding;
    private SignInViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false);
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
        viewModel = new ViewModelProvider(requireActivity()).get(SignInViewModel.class);
        binding.setViewModel(viewModel);
    }

    private void OTPView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_forgetPasswordFragment_to_OTPVerificationFragment);
    }

    private void backView() {
        viewModel.clearInfo();
        if (!viewModel.isForget) viewModel.setAccountPhone("");
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void clickView() {
        binding.btnGetPassword.setOnClickListener(view -> viewModel.checkPhone(requireActivity(), true));
        binding.ibExitForgetPassword.setOnClickListener(view -> backView());

        binding.edtEnterUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAccountPhone(editable.toString());
            }
        });
    }

    private void observe() {
        viewModel.verificationId.observe(getViewLifecycleOwner(), verificationId -> {
            if (verificationId != null) OTPView();
        });

        viewModel.stateUsingUser.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success && viewModel.changePassNotExit) {
                Extensions.toast(requireContext(), R.string.not_have_account_for_phone_number);
            }
        });
    }
}
