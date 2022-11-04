package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SignUpEmailActivity extends AppCompatActivity implements View.OnClickListener{
    public final int LENGTH_PHONE_NUMBER = 10;

    private EditText edtEnterPhoneNumber;
    private AppCompatButton btnContinue;
    private ImageButton ibBack;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    private String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        init();

        btnContinue.setOnClickListener(SignUpEmailActivity.this);
        ibBack.setOnClickListener(SignUpEmailActivity.this);
        edtEnterPhoneNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(LENGTH_PHONE_NUMBER)});
        edtEnterPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtEnterPhoneNumber.getText().toString().equals("")){
                    btnContinue.setBackgroundResource(R.drawable.custom_button_continue_appear);
                    btnContinue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    btnContinue.setEnabled(true);
                } else {
                    btnContinue.setBackgroundResource(R.drawable.custom_button_continue_hidden);
                    btnContinue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
                    btnContinue.setEnabled(false);
                }
            }
        });

    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        strEmail = bundle.getString("user");

        edtEnterPhoneNumber = findViewById(R.id.edt_enter_phone_number);
        btnContinue = findViewById(R.id.btn_continue_sign_up_email);
        ibBack = findViewById(R.id.ib_exit_sign_up_email);
        progressBar = findViewById(R.id.progressbar_send_verification_otp);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_sign_up_email)
            finish();
        if (view.getId() == R.id.btn_continue_sign_up_email)
            signup();
    }

    private void signup(){
        if (AppUtil.isPhoneNumber(edtEnterPhoneNumber.getText().toString())){
            databaseReference.child("mybooks").orderByChild("phone").equalTo(edtEnterPhoneNumber.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        openDialog();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        btnContinue.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(SignUpEmailActivity.this, OTPVerificationActivity.class);
                        Bundle bundle = new Bundle();
                        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                .setPhoneNumber("+84" + edtEnterPhoneNumber.getText().toString().replaceFirst("0", ""))
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(SignUpEmailActivity.this)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignUpEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        super.onCodeSent(s, forceResendingToken);
                                        progressBar.setVisibility(View.GONE);
                                        btnContinue.setVisibility(View.VISIBLE);

                                        bundle.putString("mobile", edtEnterPhoneNumber.getText().toString());
                                        bundle.putString("verificationId", s);
                                        bundle.putString("email", strEmail);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                })
                                .build());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // if the account already exists, then a dialog box is displayed
    private void openDialog(){
        Dialog dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_existing_account);
        dialog.setCanceledOnTouchOutside(false);
        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnAccept = dialog.findViewById(R.id.btn_accept_dialog);

        btnClose.setOnClickListener(view -> dialog.dismiss());

        btnAccept.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(SignUpEmailActivity.this, SignInActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user", edtEnterPhoneNumber.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }
}