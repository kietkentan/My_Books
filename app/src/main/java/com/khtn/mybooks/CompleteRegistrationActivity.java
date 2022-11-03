package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khtn.mybooks.model.User;

import java.util.UUID;

public class CompleteRegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvComplete;
    private ImageButton ibBack;
    private EditText edtEnterName;
    private EditText edtEnterPassword;
    private EditText edtReEnterPassword;
    private AppCompatButton btnComplete;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    private String phoneNumber;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

        init();

        ibBack.setOnClickListener(this);
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
        edtEnterName = findViewById(R.id.edt_enter_create_name);
        edtEnterPassword = findViewById(R.id.edt_enter_create_password);
        edtReEnterPassword = findViewById(R.id.edt_reenter_create_password);
        btnComplete = findViewById(R.id.btn_create_account);
        progressBar = findViewById(R.id.progressbar_complete_registration);
    }

    public void countdownBackToLogin(){
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
                    uniqueID, email, phoneNumber, null);
        databaseReference.child("mybooks").child(uniqueID).getRef().setValue(user);

        btnComplete.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        Intent intent1 = new Intent(CompleteRegistrationActivity.this, SignInSignUpActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent1);
        finish();
    }

    private void checkInfo(){
        if (edtEnterName.getText().toString().isEmpty()
                || edtEnterPassword.getText().toString().isEmpty()
                || edtReEnterPassword.getText().toString().isEmpty()) {
            if (edtEnterName.getText().toString().isEmpty())
                edtEnterName.setError(getString(R.string.name_not_empty));
            if (edtEnterPassword.getText().toString().isEmpty())
                edtEnterPassword.setError(getString(R.string.password_not_empty));
            if (edtReEnterPassword.getText().toString().isEmpty())
                edtReEnterPassword.setError(getString(R.string.password_not_empty));
        } else {
            if (edtReEnterPassword.getText().toString().equals(edtEnterPassword.getText().toString())) {
                btnComplete.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tvComplete.setVisibility(View.VISIBLE);

                countdownBackToLogin();
                createAccount();
            } else edtReEnterPassword.setError(getString(R.string.password_not_same));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_complete_registration)
            finish();
        if (view.getId() == R.id.btn_create_account)
            checkInfo();
    }
}