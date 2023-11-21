package com.space.mycoffee.view.sign_in_sign_up;

import android.content.Intent;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentSignInBinding;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view.main.MainActivity;
import com.space.mycoffee.view.manager.ManagerActivity;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private SignInViewModel viewModel;

    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
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
        viewModel.isForget = false;
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
        viewModel.clearInfo();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void forgetPasswordView() {
        viewModel.isForget = true;
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signInFragment_to_forgetPasswordFragment);
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(SignInViewModel.class);
        binding.setViewModel(viewModel);
    }

    private void clickView() {
        binding.ibExitSignIn.setOnClickListener(view -> backView());
        binding.btnLogin.setOnClickListener(view -> viewModel.login(requireContext()));
        binding.ibHiddenShowPassword.setOnClickListener(view -> viewModel.changeShowPassword());
        binding.tvForgetPassword.setOnClickListener(view -> forgetPasswordView());

        binding.edtEnterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setAccountPassword(editable.toString());
            }
        });
    }

    private void observe() {
        viewModel.showPassword.observe(getViewLifecycleOwner(), isShow -> {
            binding.edtEnterPassword.setTransformationMethod(isShow ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            binding.edtEnterPassword.setSelection(binding.edtEnterPassword.getText().length());
        });

        viewModel.stateLogin.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                viewModel.clearInfo();
                if (AppSingleton.currentUser.isAdmin()) {
                    Intent intent = new Intent(requireActivity(), AppSingleton.currentUser.isAdmin() ? ManagerActivity.class : MainActivity.class);
                    requireActivity().startActivity(intent);
                    requireActivity().overridePendingTransition(R.anim.alpha_appear_100, R.anim.alpha_hidden_100);
                    requireActivity().finish();
                } else Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signInFragment_to_signInSignUpFragment);
            }
        });
    }
}
