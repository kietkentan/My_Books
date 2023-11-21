package com.space.mycoffee.view.sign_in_sign_up;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentSignInSignUpBinding;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class SignInSignUpFragment extends Fragment {
    private FragmentSignInSignUpBinding binding;
    private SignInViewModel viewModel;

    private final int LENGTH_PHONE_NUMBER = 10;
    public static final int REQ_CODE_GG = 1000;

    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInSignUpBinding.inflate(inflater, container, false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GG) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isComplete()) {
                viewModel.handleSignInResult(task, requireContext());
            } else viewModel.setStateSignIn(null);
        }
    }

    public void setUsingUser(boolean isUsingPhone) {
        if (isUsingPhone) {
            binding.edtEnterPhoneNumberOrMail.setFilters(new InputFilter[] { new InputFilter.LengthFilter(LENGTH_PHONE_NUMBER) });
            binding.edtEnterPhoneNumberOrMail.setText(viewModel.accountPhone.getValue());
        } else {
            binding.edtEnterPhoneNumberOrMail.setFilters(new InputFilter[] {});
            binding.edtEnterPhoneNumberOrMail.setText(viewModel.accountMail.getValue());
        }
        binding.edtEnterPhoneNumberOrMail.setSelection(binding.edtEnterPhoneNumberOrMail.getText().length());
    }

    private void backView() {
        requireActivity().getViewModelStore().clear();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void signInView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signInSignUpFragment_to_signInFragment);
    }

    private void signUpEmailView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signInSignUpFragment_to_signUpEmailFragment);
    }

    private void OTPView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signInSignUpFragment_to_OTPVerificationFragment);
    }

    @SuppressWarnings("deprecation")
    private void loginWithGoogle() {
        viewModel.setStateSignIn(UiState.Loading);
        binding.btnContinueLoginPhoneNumber.setVisibility(View.INVISIBLE);
        binding.progressSignInSignUp.setVisibility(View.VISIBLE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(requireContext(), gso);
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE_GG);
    }

    private void continueWithUser() {
        viewModel.checkUser(requireActivity());
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(SignInViewModel.class);
        binding.setViewModel(viewModel);
    }

    private void clickView() {
        binding.ibExitSignInSignUp.setOnClickListener(view -> backView());
        binding.tvLoginChoseUsing.setOnClickListener(view -> viewModel.changeUsingPhone());
        binding.btnLoginWithGoogle.setOnClickListener(view -> loginWithGoogle());
        binding.btnContinueLoginPhoneNumber.setOnClickListener(view -> continueWithUser());

        binding.edtEnterPhoneNumberOrMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.changeEnableSignIn(binding.edtEnterPhoneNumberOrMail.getText().toString());
            }
        });
    }

    private void observe() {
        viewModel.isUsingPhone.observe(getViewLifecycleOwner(), this::setUsingUser);

        viewModel.stateSignIn.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Loading) {
                binding.btnContinueLoginPhoneNumber.setVisibility(View.INVISIBLE);
                binding.progressSignInSignUp.setVisibility(View.VISIBLE);
            } else if (state == UiState.Success) {
                backView();
            } else {
                binding.btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                binding.progressSignInSignUp.setVisibility(View.GONE);
            }
        });

        viewModel.stateUsingUser.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Loading) {
                binding.btnContinueLoginPhoneNumber.setVisibility(View.INVISIBLE);
                binding.progressSignInSignUp.setVisibility(View.VISIBLE);
            } else if (state == UiState.Success) {
                binding.btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                binding.progressSignInSignUp.setVisibility(View.GONE);
                boolean isExist = viewModel.isExistAccount.getValue();
                boolean isPhone = viewModel.isUsingPhone.getValue();
                if (isExist) signInView();
                else if (!isPhone) signUpEmailView();
            } else {
                binding.btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                binding.progressSignInSignUp.setVisibility(View.GONE);
            }
        });

        viewModel.verificationId.observe(getViewLifecycleOwner(), verificationId -> {
            if (verificationId != null && !verificationId.isEmpty()) OTPView();
        });

        viewModel.stateLogin.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) backView();
        });
    }
}
