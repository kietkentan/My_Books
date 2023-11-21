package com.space.mycoffee.view.sign_in_sign_up;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentEnterNewPasswordBinding;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.sign_in.NewPasswordViewModel;

public class EnterNewPasswordFragment extends Fragment {
    private FragmentEnterNewPasswordBinding binding;
    private NewPasswordViewModel viewModel;

    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterNewPasswordBinding.inflate(inflater, container, false);
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
        viewModel = new NewPasswordViewModel();
        binding.setViewModel(viewModel);
        viewModel.setPhone(getArguments().getString(Constants.PHONE));
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_enterNewPasswordFragment_to_forgetPasswordFragment);
    }

    private void loginPage() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PHONE, viewModel.getPhone());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_enterNewPasswordFragment_to_signInSignUpFragment, bundle);
    }

    private void checkInfo() {
        viewModel.clearError();
        boolean isValid = true;

        String password = viewModel.accountPassword.getValue();
        if (password == null || password.isEmpty()) {
            viewModel.setErrorPassword(getString(R.string.password_not_empty));
            isValid = false;
        } else if (password.length() < 8) {
            viewModel.setErrorPassword(getString(R.string.enter_password_correctly_formatted));
            isValid = false;
        }

        String password2 = viewModel.accountPassword2.getValue();
        if (password2 == null || password2.isEmpty()) {
            viewModel.setErrorPassword2(getString(R.string.password_not_empty));
            isValid = false;
        } else if (!password.equals(password2)) {
            viewModel.setErrorPassword2(getString(R.string.password_not_same));
            isValid = false;
        }

        if (isValid) viewModel.changePassword();
    }

    private void clickView() {
        binding.ibExitChangePassword.setOnClickListener(view -> backView());
        binding.btnChangePassword.setOnClickListener(view -> checkInfo());
        binding.ibHiddenShowNewPassword.setOnClickListener(view -> viewModel.changeShowPassword());
        binding.ibHiddenShowReNewPassword.setOnClickListener(view -> viewModel.changeShowPassword2());

        binding.edtEnterNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAccountPassword(editable.toString());
            }
        });

        binding.edtReenterNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAccountPassword2(editable.toString());
            }
        });
    }

    private void observe() {
        viewModel.stateChange.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                Extensions.toast(requireContext(), R.string.change_password_success);
                loginPage();
            } else if (state == UiState.Failure) Extensions.toast(requireContext(), R.string.error_state);
        });

        viewModel.showPassword.observe(getViewLifecycleOwner(), isShow -> {
            binding.edtEnterNewPassword.setTransformationMethod(isShow ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            binding.edtEnterNewPassword.setSelection(binding.edtEnterNewPassword.getText().length());
        });

        viewModel.showPassword2.observe(getViewLifecycleOwner(), isShow -> {
            binding.edtReenterNewPassword.setTransformationMethod(isShow ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            binding.edtReenterNewPassword.setSelection(binding.edtReenterNewPassword.getText().length());
        });

        viewModel.errorPassword.observe(getViewLifecycleOwner(), error ->
                binding.edtEnterNewPassword.setError(error)
        );

        viewModel.errorPassword2.observe(getViewLifecycleOwner(), error ->
                binding.edtReenterNewPassword.setError(error)
        );
    }
}
