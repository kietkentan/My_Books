package com.khtn.mybooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.User;

import java.util.UUID;

public class CompleteRegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvComplete;
    private ImageButton ibBack;
    private ImageButton ibHiddenShowPassword1;
    private ImageButton ibHiddenShowPassword2;
    private EditText edtEnterName;
    private EditText edtEnterPassword;
    private EditText edtReEnterPassword;
    private AppCompatButton btnComplete;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    private String phoneNumber;
    private String email;
    private Boolean hiddenPassword1 = false;
    private Boolean hiddenPassword2 = false;
    private Boolean complete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);
        AppUtil.defaultStatusBarColor(this);

        init();

        ibHiddenShowPassword1.setBackgroundResource(R.drawable.ic_eye_show);
        ibHiddenShowPassword2.setBackgroundResource(R.drawable.ic_eye_show);

        ibBack.setOnClickListener(this);
        ibHiddenShowPassword1.setOnClickListener(this);
        ibHiddenShowPassword2.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        phoneNumber = bundle.getString("mobile");
        email = bundle.getString("email");

        tvComplete = findViewById(R.id.tv_return_login_page);
        ibBack = findViewById(R.id.ib_exit_complete_registration);
        ibHiddenShowPassword1 = findViewById(R.id.ib_hidden_show_password);
        ibHiddenShowPassword2 = findViewById(R.id.ib_hidden_show_re_password);
        edtEnterName = findViewById(R.id.edt_enter_create_name);
        edtEnterPassword = findViewById(R.id.edt_enter_create_password);
        edtReEnterPassword = findViewById(R.id.edt_reenter_create_password);
        btnComplete = findViewById(R.id.btn_create_account);
        progressBar = findViewById(R.id.progressbar_complete_registration);
    }

    public void unEnableAll(){
        ibBack.setEnabled(false);
        btnComplete.setEnabled(false);
        edtEnterPassword.setEnabled(false);
        edtReEnterPassword.setEnabled(false);
    }

    public void countdownBackToLogin(){
        unEnableAll();
        new CountDownTimer(3*1000, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long l) {
                long remindSec = l/1000;
                tvComplete.setText(String.format("%s %ds", getString(R.string.return_login_page), remindSec % 60));
            }

            @Override
            public void onFinish() {
                btnComplete.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    private void createAccount(){
        String uniqueID = UUID.randomUUID().toString();
        User user = new User(null, null, edtEnterName.getText().toString(), edtEnterPassword.getText().toString(),
                    uniqueID, email, phoneNumber);
        databaseReference.child("mybooks").child(uniqueID).getRef().setValue(user);

        btnComplete.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Intent intent1 = new Intent(CompleteRegistrationActivity.this, SignInSignUpActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent1);
        finish();
    }

    private void checkInfo(){
        if (!AppUtil.isName(edtEnterName.getText().toString())
                || !AppUtil.checkValidPassword(edtEnterPassword.getText().toString())
                || edtReEnterPassword.getText().toString().isEmpty()) {
            if (edtEnterName.getText().toString().isEmpty())
                edtEnterName.setError(getString(R.string.name_not_empty));
            else if (!edtEnterName.getText().toString().contains(" "))
                edtEnterName.setError(getString(R.string.enter_fullname));
            else if (!AppUtil.isName(edtEnterName.getText().toString()))
                edtEnterName.setError(getString(R.string.name_not_valid));
            if (edtEnterPassword.getText().toString().isEmpty())
                edtEnterPassword.setError(getString(R.string.password_not_empty));
            else if (!AppUtil.checkValidPassword(edtEnterPassword.getText().toString()))
                edtEnterPassword.setError(getString(R.string.password_not_formatted_correctly));
            if (edtReEnterPassword.getText().toString().isEmpty())
                edtReEnterPassword.setError(getString(R.string.password_not_empty));
        } else {
            if (edtReEnterPassword.getText().toString().equals(edtEnterPassword.getText().toString())) {
                btnComplete.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvComplete.setVisibility(View.VISIBLE);

                complete = true;
                countdownBackToLogin();
                createAccount();
            } else
                edtReEnterPassword.setError(getString(R.string.password_not_same));
        }
    }

    public void checkHiddenPassword1(){
        if (hiddenPassword1){
            ibHiddenShowPassword1.setBackgroundResource(R.drawable.ic_eye_show);
            ibHiddenShowPassword1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_appear_50));
            edtEnterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            hiddenPassword1 = false;
        } else {
            ibHiddenShowPassword1.setBackgroundResource(R.drawable.ic_eye_hidden);
            ibHiddenShowPassword1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_hidden_50));
            edtEnterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            hiddenPassword1 = true;
        }
        edtEnterPassword.setSelection(edtEnterPassword.getText().length());
    }

    public void checkHiddenPassword2(){
        if (hiddenPassword2){
            ibHiddenShowPassword2.setBackgroundResource(R.drawable.ic_eye_show);
            ibHiddenShowPassword2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_appear_50));
            edtReEnterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            hiddenPassword2 = false;
        } else {
            ibHiddenShowPassword2.setBackgroundResource(R.drawable.ic_eye_hidden);
            ibHiddenShowPassword2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_hidden_50));
            edtReEnterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            hiddenPassword2 = true;
        }
        edtReEnterPassword.setSelection(edtReEnterPassword.getText().length());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_complete_registration)
            finish();
        if (view.getId() == R.id.btn_create_account)
            checkInfo();
        if (view.getId() == R.id.ib_hidden_show_password)
            checkHiddenPassword1();
        if (view.getId() == R.id.ib_hidden_show_re_password)
            checkHiddenPassword2();
    }

    @Override
    public void onBackPressed() {
        if (complete)
            return;
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}