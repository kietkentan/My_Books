package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.User;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvPleaseEnterPassword;
    private TextView tvEmailOrPhoneNumber;
    private TextView tvForgetPassword;
    private EditText edtPassword;
    private ImageButton ibBack;
    private ImageButton ibHiddenOrShowPassword;
    private AppCompatButton btnLogin;
    private ProgressBar progressBarLogin;
    private Context thisContext;

    private DatabaseReference databaseReference;
    private String strUser;
    public boolean hiddenPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        setAbout();

        tvForgetPassword.setOnClickListener(SignInActivity.this);
        ibBack.setOnClickListener(SignInActivity.this);
        ibHiddenOrShowPassword.setOnClickListener(SignInActivity.this);
        btnLogin.setOnClickListener(SignInActivity.this);

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtPassword.getText().toString().equals("")) {
                    btnLogin.setBackgroundResource(R.drawable.custom_button_continue_appear);
                    btnLogin.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setBackgroundResource(R.drawable.custom_button_continue_hidden);
                    btnLogin.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
                    btnLogin.setEnabled(false);
                }
            }
        });
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        strUser = bundle.getString("user");

        tvPleaseEnterPassword = (TextView) findViewById(R.id.tv_please_enter_password);
        tvEmailOrPhoneNumber = (TextView) findViewById(R.id.tv_email_phone_number);
        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        edtPassword = (EditText) findViewById(R.id.edt_enter_password);
        ibBack = (ImageButton) findViewById(R.id.ib_exit_sign_in);
        ibHiddenOrShowPassword = (ImageButton) findViewById(R.id.ib_hidden_show_password);
        btnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressbar_sign_in);
        thisContext = getApplicationContext();
    }

    public void setAbout(){
        if (AppUtil.isPhoneNumber(strUser))
            tvPleaseEnterPassword.setText(R.string.please_enter_password_of_number_phone);
        else if (AppUtil.isEmail(strUser))
            tvPleaseEnterPassword.setText(R.string.please_enter_password_of_email);

        tvEmailOrPhoneNumber.setText(strUser);
        ibHiddenOrShowPassword.setBackgroundResource(R.drawable.ic_eye_show);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_sign_in)
            finish();
        if (view.getId() == R.id.btn_login)
            login();
        if (view.getId() == R.id.ib_hidden_show_password)
            checkHiddenPassword();
        if (view.getId() == R.id.tv_forget_password){
            // start forget password page
            Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        }
    }

    public void checkHiddenPassword(){
        if (hiddenPassword){
            ibHiddenOrShowPassword.setBackgroundResource(R.drawable.ic_eye_show);
            ibHiddenOrShowPassword.setAnimation(AnimationUtils.loadAnimation(thisContext, R.anim.alpha_appear_50));
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            hiddenPassword = false;
        } else {
            ibHiddenOrShowPassword.setBackgroundResource(R.drawable.ic_eye_hidden);
            ibHiddenOrShowPassword.setAnimation(AnimationUtils.loadAnimation(thisContext, R.anim.alpha_hidden_50));
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            hiddenPassword = true;
        }
        edtPassword.setSelection(edtPassword.getText().length());
    }

    protected void login(){
        btnLogin.setVisibility(View.INVISIBLE);
        progressBarLogin.setVisibility(View.VISIBLE);
        databaseReference.child("mybooks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    if (dataSnapshot.child("phone").getValue(String.class).equals(strUser)
                            || dataSnapshot.child("email").getValue(String.class).equals(strUser)) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getPassword().equals(edtPassword.getText().toString())) {
                            Common.currentUser = user;
                            Common.modeLogin = 1;
                            Common.saveUser(SignInActivity.this);
                            Common.currentUser.setPassword(null);
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(thisContext, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                btnLogin.setVisibility(View.VISIBLE);
                progressBarLogin.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}