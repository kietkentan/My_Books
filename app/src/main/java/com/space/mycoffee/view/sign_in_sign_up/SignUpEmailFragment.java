package com.space.mycoffee.view.sign_in_sign_up;

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

import com.space.mycoffee.R;
import com.space.mycoffee.custom.dialog.DialogExistingAccount;
import com.space.mycoffee.databinding.FragmentSignUpEmailBinding;
import com.space.mycoffee.listener.DialogTwoButtonListener;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class SignUpEmailFragment extends Fragment {
    private final int LENGTH_PHONE_NUMBER = 10;
    private FragmentSignUpEmailBinding binding;
    private SignInViewModel viewModel;

    private OnBackPressedCallback callback;
    private DialogExistingAccount dialogExistingAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpEmailBinding.inflate(inflater, container, false);
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

    private void openDialogExisting() {
        dialogExistingAccount.showDialog();
    }

    private void backView() {
        viewModel.clearInfo();
        viewModel.clearPhone();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void OTPView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signUpEmailFragment_to_OTPVerificationFragment);
    }

    private void loginView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_signUpEmailFragment_to_signInSignUpFragment);
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(SignInViewModel.class);
        binding.setViewModel(viewModel);

        String act = getArguments() == null ? null : getArguments().getString(Constants.PHONE);
        if (act != null && !act.isEmpty()) {
            viewModel.setAccountPhone(act);
            viewModel.setIsPhone(true);
        }

        dialogExistingAccount = new DialogExistingAccount(requireContext(), new DialogTwoButtonListener() {
            @Override
            public void onCancelClicked() {}

            @Override
            public void onConfirmClicked() {
                viewModel.changeUsingPhone();
                loginView();
            }
        });
    }

    private void clickView() {
        binding.ibExitSignUpEmail.setOnClickListener(view -> backView());
        binding.btnContinueSignUpEmail.setOnClickListener(view -> viewModel.checkPhone(requireActivity(), false));

        binding.edtEnterPhoneNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(LENGTH_PHONE_NUMBER) });
        binding.edtEnterPhoneNumber.addTextChangedListener(new TextWatcher() {
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
        viewModel.stateUsingUser.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Success) {
                if (viewModel.showDialog) openDialogExisting();
            }
        });

        viewModel.verificationId.observe(getViewLifecycleOwner(), verificationId -> {
            if (verificationId != null) OTPView();
        });
    }
}
