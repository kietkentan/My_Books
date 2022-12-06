package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;

import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private EditText edtEnterUser;
    private AppCompatButton btnGetPassword;
    private TextView tvContact;
    private TextView tvNotAccount;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        AppUtil.defaultStatusBarColor(this);

        init();

        ibBack.setOnClickListener(ForgetPasswordActivity.this);
        btnGetPassword.setOnClickListener(ForgetPasswordActivity.this);
        tvContact.setOnClickListener(ForgetPasswordActivity.this);

        edtEnterUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtEnterUser.getText().toString().equals("")){
                    btnGetPassword.setBackgroundResource(R.drawable.custom_button_continue_appear);
                    btnGetPassword.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    btnGetPassword.setEnabled(true);
                } else {
                    btnGetPassword.setBackgroundResource(R.drawable.custom_button_continue_hidden);
                    btnGetPassword.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
                    btnGetPassword.setEnabled(false);
                }
            }
        });
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        ibBack = findViewById(R.id.ib_exit_forget_password);
        edtEnterUser = findViewById(R.id.edt_enter_user);
        btnGetPassword = findViewById(R.id.btn_get_password);
        tvContact = findViewById(R.id.tv_hotline);
        tvNotAccount = findViewById(R.id.tv_not_have_account);
        progressBar = findViewById(R.id.progress_forget_password);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_forget_password)
            finish();
        if (view.getId() == R.id.btn_get_password)
            resetPassword();
        if (view.getId() == R.id.tv_hotline)
            callHotline();
    }

    private void resetPassword(){
        if (AppUtil.isPhoneNumber(edtEnterUser.getText().toString()))
            resetPasswordByPhone();
        else if (AppUtil.isEmail(edtEnterUser.getText().toString()))
            resetPasswordByEmail();
        else
            edtEnterUser.setError(getString(R.string.please_re_enter));
    }

    private void resetPasswordByPhone(){
        databaseReference.child("mybooks").orderByChild(edtEnterUser.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressBar.setVisibility(View.VISIBLE);
                    btnGetPassword.setVisibility(View.INVISIBLE);

                    senOPT(edtEnterUser.getText().toString());
                } else {
                    tvNotAccount.setVisibility(View.VISIBLE);
                    tvNotAccount.setText(R.string.not_have_account_for_phone_number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void resetPasswordByEmail(){
        databaseReference.child("mybooks").orderByChild("email").equalTo(edtEnterUser.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnGetPassword.setVisibility(View.INVISIBLE);

                        senOPT(dataSnapshot.child("phone").getValue(String.class));
                    }
                } else {
                    tvNotAccount.setVisibility(View.VISIBLE);
                    tvNotAccount.setText(R.string.not_have_account_for_email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void senOPT(String phone){
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder()
                .setPhoneNumber("+84" + phone.replaceFirst("0", ""))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(ForgetPasswordActivity.this)
                .setForceResendingToken(AppUtil.mForceResendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        btnGetPassword.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        btnGetPassword.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        btnGetPassword.setVisibility(View.VISIBLE);
                        AppUtil.mForceResendingToken = forceResendingToken;

                        Intent intent = new Intent(ForgetPasswordActivity.this, OTPVerificationActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString("mobile", phone);
                        bundle.putString("verificationId", s);
                        bundle.putBoolean("register", false);

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                .build());
    }

    private void callHotline(){
        String numberPhone = getString(R.string.hotline);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + numberPhone));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            return;
        startActivity(intent);
    }
}