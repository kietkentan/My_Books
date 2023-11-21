package com.space.mycoffee.view.sign_in_sign_up;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentOtpVerificationBinding;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class OTPVerificationFragment extends Fragment {
    public final long TICK = 1000;

    private FragmentOtpVerificationBinding binding;
    private SignInViewModel viewModel;

    private EditText[] inputCodes;
    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false);
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
        viewModel.clearInfo();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void registerView() {
        String phone = viewModel.accountPhone.getValue();
        String mail = viewModel.accountMail.getValue();

        Bundle regisBundle = new Bundle();
        regisBundle.putString(Constants.PHONE, phone);
        if (mail != null && !mail.isEmpty()) regisBundle.putString(Constants.EMAIL, mail);

        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_OTPVerificationFragment_to_completeRegistrationFragment,
                regisBundle
        );
    }

    private void enterNewPasswordView() {
        String phone = viewModel.accountPhone.getValue();

        Bundle regisBundle = new Bundle();
        regisBundle.putString(Constants.PHONE, phone);

        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_OTPVerificationFragment_to_enterNewPasswordFragment,
                regisBundle
        );
    }

    private void countdownResendOTP() {
        binding.tvResendOTP.setEnabled(false);
        binding.tvTextResend.setVisibility(View.VISIBLE);
        new CountDownTimer(30000, TICK) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long l) {
                long remindSec = l/TICK;
                binding.tvResendOTP.setText(String.format("%ds", remindSec % 60));
            }

            @Override
            public void onFinish() {
                binding.tvResendOTP.setText(R.string.resend);
                binding.tvTextResend.setVisibility(View.GONE);
                binding.tvResendOTP.setEnabled(true);
            }
        }.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupOTPInputs(){
        for (int i = 0; i < inputCodes.length; i++){
            inputCodes[i].addTextChangedListener(new PinTextWatcher(i));
            inputCodes[i].setOnTouchListener(new PinOnTouchListener(i));
            if (i != 0) inputCodes[i].setOnKeyListener(new PinOnKeyListener(i));
        }
    }

    private void resetOTPInputs(){
        for (int i = 5; i >= 0; i--)
            inputCodes[i].setText("");
        inputCodes[0].requestFocus();
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(SignInViewModel.class);
        inputCodes = new EditText[]{ binding.inputOTPCode1, binding.inputOTPCode2, binding.inputOTPCode3, binding.inputOTPCode4, binding.inputOTPCode5, binding.inputOTPCode6 };
        setupOTPInputs();
        countdownResendOTP();
    }

    private void clickView() {
        binding.ibExitOptVerification.setOnClickListener(view -> backView());

        binding.tvResendOTP.setOnClickListener(view -> {
            viewModel.resendOTP(requireActivity());
            resetOTPInputs();
            countdownResendOTP();
        });
    }

    private void observe() {
        viewModel.stateVerifier.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Loading) {
                binding.progressbarEnterVerificationOtp.setVisibility(View.VISIBLE);
            } else if (state == UiState.Success) {
                binding.progressbarEnterVerificationOtp.setVisibility(View.GONE);
                boolean isRegister = viewModel.isRegister.getValue();
                viewModel.clearInfo();
                if (isRegister) registerView();
                else enterNewPasswordView();
            } else binding.progressbarEnterVerificationOtp.setVisibility(View.GONE);
        });
    }

    public class PinOnTouchListener implements View.OnTouchListener{
        private final int currentIndex;

        PinOnTouchListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            inputCodes[currentIndex].getText().clear();
            return false;
        }
    }

    public class PinOnKeyListener implements View.OnKeyListener {
        private final int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                if (inputCodes[currentIndex].getText().toString().equals("")) {
                    inputCodes[currentIndex - 1].getText().clear();
                    inputCodes[currentIndex - 1].requestFocus();
                }
            }
            return false;
        }
    }

    public class PinTextWatcher implements TextWatcher {
        private final int currentIndex;
        private boolean isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == inputCodes.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = newTypedString;

            if (text.length() > 1)
                text = String.valueOf(text.charAt(0));

            inputCodes[currentIndex].removeTextChangedListener(this);
            inputCodes[currentIndex].setText(text);
            inputCodes[currentIndex].setSelection(text.length());
            inputCodes[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
        }

        private void moveToNext() {
            if (!isLast) {
                inputCodes[currentIndex + 1].getText().clear();
                inputCodes[currentIndex + 1].requestFocus();
            }

            if (isAllEditTextsFilled() && isLast) {
                inputCodes[currentIndex].clearFocus();
                inputCodes[currentIndex].postDelayed(() -> {
                    InputMethodManager imm = (InputMethodManager)  inputCodes[currentIndex].getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow( inputCodes[currentIndex].getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }, 100);

                StringBuilder code = new StringBuilder();
                for (EditText edt : inputCodes) {
                    code.append(edt.getText().toString());
                }

                viewModel.checkOTP(code.toString());
            }
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : inputCodes)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }
    }
}
