package com.space.mycoffee.view.sign_in_sign_up;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentCompleteRegistrationBinding;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.sign_in.RegisterViewModel;

public class CompleteRegistrationFragment extends Fragment {
    private FragmentCompleteRegistrationBinding binding;
    private RegisterViewModel viewModel;

    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompleteRegistrationBinding.inflate(inflater, container, false);
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

    private void loginPage() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PHONE, viewModel.getPhone());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_completeRegistrationFragment_to_signInSignUpFragment, bundle);
    }

    private void backView() {
        String mail = viewModel.accountMail.getValue();
        if (mail == null || mail.isEmpty()) Navigation.findNavController(binding.getRoot()).navigate(R.id.action_completeRegistrationFragment_to_signInSignUpFragment);
        else Navigation.findNavController(binding.getRoot()).navigate(R.id.action_completeRegistrationFragment_to_signUpEmailFragment);
    }

    private void checkInfo() {
        viewModel.clearError();
        boolean isValid = true;
        String name = viewModel.accountName.getValue();
        if (name == null || name.isEmpty()) {
            viewModel.setErrorName(getString(R.string.name_not_empty));
            isValid = false;
        } else if (!Extensions.isName(name)) {
            viewModel.setErrorName(getString(R.string.name_not_valid));
            isValid = false;
        }

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

        if (isValid) viewModel.createAccount();
    }

    private void initView() {
        viewModel = new RegisterViewModel();
        binding.setViewModel(viewModel);
        viewModel.setAccountMail(getArguments().getString(Constants.EMAIL));
        viewModel.setAccountPhone(getArguments().getString(Constants.PHONE));
    }

    private void clickView() {
        binding.ibExitCompleteRegistration.setOnClickListener(view -> backView());
        binding.btnCreateAccount.setOnClickListener(view -> checkInfo());
        binding.ibHiddenShowPassword.setOnClickListener(view -> viewModel.changeShowPassword());
        binding.ibHiddenShowRePassword.setOnClickListener(view -> viewModel.changeShowPassword2());

        binding.edtEnterCreateName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAccountName(editable.toString());
            }
        });

        binding.edtEnterCreatePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAccountPassword(editable.toString());
            }
        });

        binding.edtReenterCreatePassword.addTextChangedListener(new TextWatcher() {
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
        viewModel.stateCreate.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                Extensions.toast(requireContext(), R.string.created_account);
                loginPage();
            } else if (state == UiState.Failure) Extensions.toast(requireContext(), R.string.error_state);
        });

        viewModel.showPassword.observe(getViewLifecycleOwner(), isShow -> {
            binding.edtEnterCreatePassword.setTransformationMethod(isShow ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            binding.edtEnterCreatePassword.setSelection(binding.edtEnterCreatePassword.getText().length());
        });

        viewModel.showPassword2.observe(getViewLifecycleOwner(), isShow -> {
            binding.edtReenterCreatePassword.setTransformationMethod(isShow ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            binding.edtReenterCreatePassword.setSelection(binding.edtReenterCreatePassword.getText().length());
        });

        viewModel.errorName.observe(getViewLifecycleOwner(), error ->
                binding.edtEnterCreateName.setError(error)
        );

        viewModel.errorPassword.observe(getViewLifecycleOwner(), error ->
                binding.edtEnterCreatePassword.setError(error)
        );

        viewModel.errorPassword2.observe(getViewLifecycleOwner(), error ->
                binding.edtReenterCreatePassword.setError(error)
        );
    }
}
