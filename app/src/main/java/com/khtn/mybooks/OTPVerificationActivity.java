package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity implements View.OnClickListener{
    public final long TIME_RESEND_OTP = 30;
    public final long TICK = 1000;

    private TextView tvTextResend;
    private TextView tvTimeReSend;
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private EditText[] inputCodes;
    private ImageButton ibBack;
    private ProgressBar progressBar;

    private String verificationId;
    private String phoneNumber;
    private String email = "null";
    private Boolean isRegister = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        init();

        ibBack.setOnClickListener(OTPVerificationActivity.this);
        countdownResendOTP(TIME_RESEND_OTP*TICK, TICK);
        setupOTPInputs();
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        verificationId = bundle.getString("verificationId");
        phoneNumber = bundle.getString("mobile");
        if (bundle != null && bundle.containsKey("email"))
            email = bundle.getString("email");
        if (bundle != null && bundle.containsKey("register"))
            isRegister = bundle.getBoolean("register");

        inputCode1 = (EditText) findViewById(R.id.inputOTPCode1);
        inputCode2 = (EditText) findViewById(R.id.inputOTPCode2);
        inputCode3 = (EditText) findViewById(R.id.inputOTPCode3);
        inputCode4 = (EditText) findViewById(R.id.inputOTPCode4);
        inputCode5 = (EditText) findViewById(R.id.inputOTPCode5);
        inputCode6 = (EditText) findViewById(R.id.inputOTPCode6);
        inputCodes = new EditText[]{inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6};
        ibBack = (ImageButton) findViewById(R.id.ib_exit_opt_verification);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_enter_verification_otp);
        tvTextResend = (TextView) findViewById(R.id.tv_text_resend);
        tvTimeReSend = (TextView) findViewById(R.id.tv_resend_OTP);
    }

    private void countdownResendOTP(final long finish, long tick) {
        CountDownTimer countDownTimer;
        countDownTimer = new CountDownTimer(finish, tick) {
            @Override
            public void onTick(long l) {
                long remindSec = l/TICK;
                tvTimeReSend.setText(remindSec%60 + "s");
            }

            @Override
            public void onFinish() {
                tvTimeReSend.setText(R.string.resend);
                tvTextResend.setVisibility(View.GONE);
                tvTimeReSend.setOnClickListener(OTPVerificationActivity.this);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_opt_verification)
            finish();

        if (view.getId() == R.id.tv_resend_OTP){
            tvTimeReSend.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            Toast.makeText(OTPVerificationActivity.this, R.string.resending, Toast.LENGTH_SHORT).show();
            resetOTPInputs();
            countdownResendOTP(TIME_RESEND_OTP*TICK, TICK);
            // resendOTP();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupOTPInputs(){
        for (int i = 0; i < inputCodes.length; i++){
            inputCodes[i].addTextChangedListener(new PinTextWatcher(i));
            inputCodes[i].setOnTouchListener(new PinOnTouchListener(i));
            if (i != 0)
                inputCodes[i].setOnKeyListener(new PinOnKeyListener(i));
        }
    }

    public void resetOTPInputs(){
        for (int i = 5; i >= 0; i--)
            inputCodes[i].setText("");
    }

    // fail
    private void resendOTP(){
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+84" + phoneNumber.replaceFirst("0", ""))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPVerificationActivity.this)
                .setForceResendingToken(AppUtil.mForceResendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OTPVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        progressBar.setVisibility(View.GONE);
                        verificationId = s;
                        AppUtil.mForceResendingToken = forceResendingToken;

                        Toast.makeText(OTPVerificationActivity.this, R.string.resending, Toast.LENGTH_SHORT).show();
                        resetOTPInputs();
                        countdownResendOTP(TIME_RESEND_OTP*TICK, TICK);
                    }
                })
                .build());
    }

    // check the action onTouch the OTP input box
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

    // check the action of entering each input box and move to the next box
    // if at the end, then start checking the code
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
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

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
                inputCodes[currentIndex].postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)  inputCodes[currentIndex].getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow( inputCodes[currentIndex].getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }, 100);

                String code = inputCode1.getText().toString() + inputCode2.getText().toString()
                        + inputCode3.getText().toString() + inputCode4.getText().toString()
                        + inputCode5.getText().toString() + inputCode6.getText().toString();

                if (verificationId != null)
                    checkOTP(code);
            }
        }

        private void checkOTP(String code){
            progressBar.setVisibility(View.VISIBLE);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        AppUtil.mForceResendingToken = null;
                        if (isRegister)
                            startCreateUser();
                        else
                            startForgetPassword();
                    }
                }
            });
        }

        private void startCreateUser(){
            Intent intent = new Intent(OTPVerificationActivity.this, CompleteRegistrationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("mobile", phoneNumber);
            bundle.putString("email", email);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }

        private void startForgetPassword(){
            Intent intent = new Intent(OTPVerificationActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
            finish();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : inputCodes)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
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

}

