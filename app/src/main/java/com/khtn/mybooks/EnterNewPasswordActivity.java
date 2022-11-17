package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterNewPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private ImageButton ibHiddenShowPassword1;
    private ImageButton ibHiddenShowPassword2;
    private EditText edtEnterNewPassword;
    private EditText edtReEnterNewPassword;
    private AppCompatButton btnChangePassword;
    private ProgressBar progressBar;

    private String phone;
    private Boolean hiddenPassword1 = false;
    private Boolean hiddenPassword2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_password);
        AppUtil.defaultStatusBarColor(this);

        init();

        ibBack.setOnClickListener(this);
        ibHiddenShowPassword1.setOnClickListener(this);
        ibHiddenShowPassword2.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        phone = bundle.getString("phone");

        ibBack = findViewById(R.id.ib_exit_change_password);
        ibHiddenShowPassword1 = findViewById(R.id.ib_hidden_show_new_password);
        ibHiddenShowPassword2 = findViewById(R.id.ib_hidden_show_re_new_password);
        edtEnterNewPassword = findViewById(R.id.edt_enter_new_password);
        edtReEnterNewPassword = findViewById(R.id.edt_reenter_new_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        progressBar = findViewById(R.id.progress_change_password);
    }

    public void checkHiddenPassword1(){
        if (hiddenPassword1){
            ibHiddenShowPassword1.setBackgroundResource(R.drawable.ic_eye_show);
            ibHiddenShowPassword1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_appear_50));
            edtEnterNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            hiddenPassword1 = false;
        } else {
            ibHiddenShowPassword1.setBackgroundResource(R.drawable.ic_eye_hidden);
            ibHiddenShowPassword1.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_hidden_50));
            edtEnterNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            hiddenPassword1 = true;
        }
        edtEnterNewPassword.setSelection(edtEnterNewPassword.getText().length());
    }

    public void checkHiddenPassword2(){
        if (hiddenPassword2){
            ibHiddenShowPassword2.setBackgroundResource(R.drawable.ic_eye_show);
            ibHiddenShowPassword2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_appear_50));
            edtReEnterNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            hiddenPassword2 = false;
        } else {
            ibHiddenShowPassword2.setBackgroundResource(R.drawable.ic_eye_hidden);
            ibHiddenShowPassword2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_hidden_50));
            edtReEnterNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            hiddenPassword2 = true;
        }
        edtReEnterNewPassword.setSelection(edtReEnterNewPassword.getText().length());
    }

    private void checkInfo(){
        if (!AppUtil.checkValidPassword(edtEnterNewPassword.getText().toString())){
            if (edtEnterNewPassword.getText().toString().isEmpty())
                edtEnterNewPassword.setError(getString(R.string.password_not_empty));
            else if (!AppUtil.checkValidPassword(edtEnterNewPassword.getText().toString()))
                edtEnterNewPassword.setError(getString(R.string.password_not_formatted_correctly));
            if (edtReEnterNewPassword.getText().toString().isEmpty())
                edtReEnterNewPassword.setError(getString(R.string.password_not_empty));
        } else
            if (edtEnterNewPassword.getText().toString().equals(edtReEnterNewPassword.getText().toString()))
                changePassword(edtEnterNewPassword.getText().toString());
            else
                edtReEnterNewPassword.setError(getString(R.string.password_not_same));
    }

    private void changePassword(String newPassword){
        btnChangePassword.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child("mybooks").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String oldPassword = dataSnapshot.child("password").getValue(String.class);
                    dataSnapshot.child("old_password").getRef().child(String.valueOf(dataSnapshot.child("old_password").getChildrenCount())).setValue(oldPassword);
                    dataSnapshot.child("password").getRef().setValue(newPassword);

                    btnChangePassword.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(EnterNewPasswordActivity.this, SignInSignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_exit_change_password)
            finish();
        if (v.getId() == R.id.btn_change_password)
            checkInfo();
        if (v.getId() == R.id.ib_hidden_show_new_password)
            checkHiddenPassword1();
        if (v.getId() == R.id.ib_hidden_show_re_new_password)
            checkHiddenPassword2();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}