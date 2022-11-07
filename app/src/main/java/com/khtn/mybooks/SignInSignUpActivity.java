package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Order;
import com.khtn.mybooks.model.User;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignInSignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private final int LENGTH_PHONE_NUMBER = 10;
    public static final int REQ_CODE_GG = 1000;
    private boolean usingPhoneNumber = true;

    private ImageButton ibBack;
    private AppCompatButton btnContinueLoginPhoneNumber;
    private AppCompatButton btnLoginWithGoogle;
    private AppCompatButton btnLoginWithFaceBook;
    private EditText edtEnterPhoneNumberOrEmail;
    private TextView tvLoginChoseUsing;
    private TextView tvUsingPhone;
    private ProgressBar progressBarContinue;

    private String tmpPhone = "";
    private String tmpEmail = "";

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private GoogleSignInClient gsc;

    // Facebook
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        init();

        ibBack.setOnClickListener(SignInSignUpActivity.this);
        btnContinueLoginPhoneNumber.setOnClickListener(SignInSignUpActivity.this);
        btnContinueLoginPhoneNumber.setEnabled(false);
        btnLoginWithGoogle.setOnClickListener(SignInSignUpActivity.this);
        btnLoginWithFaceBook.setOnClickListener(SignInSignUpActivity.this);
        tvLoginChoseUsing.setOnClickListener(SignInSignUpActivity.this);

        edtEnterPhoneNumberOrEmail.setFilters(new InputFilter[] {new InputFilter.LengthFilter(LENGTH_PHONE_NUMBER)});
        edtEnterPhoneNumberOrEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtEnterPhoneNumberOrEmail.getText().toString().equals("")) {
                    btnContinueLoginPhoneNumber.setBackgroundResource(R.drawable.custom_button_continue_appear);
                    btnContinueLoginPhoneNumber.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    btnContinueLoginPhoneNumber.setEnabled(true);
                }
                else {
                    btnContinueLoginPhoneNumber.setBackgroundResource(R.drawable.custom_button_continue_hidden);
                    btnContinueLoginPhoneNumber.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_hint));
                    btnContinueLoginPhoneNumber.setEnabled(false);
                }
            }
        });
    }

    public void init(){
        // Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        ibBack = findViewById(R.id.ib_exit_sign_in_sign_up);
        btnContinueLoginPhoneNumber = findViewById(R.id.btn_continue_login_phone_number);
        btnLoginWithGoogle = findViewById(R.id.btn_login_with_google);
        btnLoginWithFaceBook = findViewById(R.id.btn_login_with_facebook);
        edtEnterPhoneNumberOrEmail = findViewById(R.id.edt_enter_phone_number_or_mail);
        tvLoginChoseUsing = findViewById(R.id.tv_login_chose_using);
        tvUsingPhone = findViewById(R.id.tv_using_phone);
        progressBarContinue = findViewById(R.id.progress_sign_in_sign_up);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_continue_login_phone_number)
            continueWithUser();
        if (view.getId() == R.id.tv_login_chose_using)
            setUsingUser();
        if (view.getId() == R.id.ib_exit_sign_in_sign_up)
            finish();
        if (view.getId() == R.id.btn_login_with_google)
            loginWithGoogle();
        if (view.getId() == R.id.btn_login_with_facebook)
            loginWithFacebook();
    }

    // change the sign-in method (phone or email)
    public void setUsingUser(){
        if (usingPhoneNumber){
            tmpPhone = edtEnterPhoneNumberOrEmail.getText().toString();
            edtEnterPhoneNumberOrEmail.setText(tmpEmail);
            edtEnterPhoneNumberOrEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            edtEnterPhoneNumberOrEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0);
            edtEnterPhoneNumberOrEmail.setHint(R.string.enter_your_email);
            tvUsingPhone.setText(R.string.login_logout_with_email);
            tvLoginChoseUsing.setText(R.string.login_using_phone_number);
            edtEnterPhoneNumberOrEmail.setFilters(new InputFilter[] {});
            usingPhoneNumber = false;
        } else {
            tmpEmail = edtEnterPhoneNumberOrEmail.getText().toString();
            edtEnterPhoneNumberOrEmail.setText(tmpPhone);
            edtEnterPhoneNumberOrEmail.setInputType(InputType.TYPE_CLASS_PHONE);
            edtEnterPhoneNumberOrEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone, 0, 0, 0);
            edtEnterPhoneNumberOrEmail.setHint(R.string.enter_your_number_phone);
            tvUsingPhone.setText(R.string.login_logout_with_phone_number);
            tvLoginChoseUsing.setText(R.string.login_using_email);
            edtEnterPhoneNumberOrEmail.setFilters(new InputFilter[] {new InputFilter.LengthFilter(LENGTH_PHONE_NUMBER)});
            usingPhoneNumber = true;
        }
        edtEnterPhoneNumberOrEmail.setSelection(edtEnterPhoneNumberOrEmail.getText().length());
    }

    private void continueWithUser(){
        btnContinueLoginPhoneNumber.setVisibility(View.INVISIBLE);
        progressBarContinue.setVisibility(View.VISIBLE);
        Intent intentSignIn = new Intent(SignInSignUpActivity.this, SignInActivity.class);
        Intent intentSignUpEmail = new Intent(SignInSignUpActivity.this, SignUpEmailActivity.class);
        Intent intentOTPVerification = new Intent(SignInSignUpActivity.this, OTPVerificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user", edtEnterPhoneNumberOrEmail.getText().toString());
        if (AppUtil.isPhoneNumber(edtEnterPhoneNumberOrEmail.getText().toString())) {
            reference.child("mybooks").orderByChild("phone").equalTo(edtEnterPhoneNumberOrEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                        progressBarContinue.setVisibility(View.INVISIBLE);
                        intentSignIn.putExtras(bundle);
                        startActivity(intentSignIn);
                    } else {
                        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder()
                                .setPhoneNumber("+84" + edtEnterPhoneNumberOrEmail.getText().toString().replaceFirst("0", ""))
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(SignInSignUpActivity.this)
                                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBarContinue.setVisibility(View.GONE);
                                        btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBarContinue.setVisibility(View.GONE);
                                        btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignInSignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBarContinue.setVisibility(View.GONE);
                                        btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);

                                        bundle.putString("mobile", edtEnterPhoneNumberOrEmail.getText().toString());
                                        bundle.putString("verificationId", s);
                                        intentOTPVerification.putExtras(bundle);
                                        startActivity(intentOTPVerification);
                                        finish();
                                    }
                                })
                                .build());
                        progressBarContinue.setVisibility(View.VISIBLE);
                        btnContinueLoginPhoneNumber.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (AppUtil.isEmail(edtEnterPhoneNumberOrEmail.getText().toString())) {
            reference.child("mybooks").orderByChild("email").equalTo(edtEnterPhoneNumberOrEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                        progressBarContinue.setVisibility(View.INVISIBLE);
                        intentSignIn.putExtras(bundle);
                        startActivity(intentSignIn);
                    } else {
                        btnContinueLoginPhoneNumber.setVisibility(View.VISIBLE);
                        progressBarContinue.setVisibility(View.INVISIBLE);
                        intentSignUpEmail.putExtras(bundle);
                        startActivity(intentSignUpEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void loginWithGoogle(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE_GG);
    }

    private void loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(SignInSignUpActivity.this, Collections.singletonList("public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQ_CODE_GG) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                reference.child("google").child(acct.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            User user_gg = new User(acct.getPhotoUrl().toString(), null, acct.getDisplayName(), null, acct.getId(), acct.getEmail(), null);
                            Common.signIn(user_gg, 2);
                            reference.child("google").child(acct.getId()).getRef().setValue(user_gg);
                        }
                        else {
                            Common.signIn(snapshot.getValue(User.class), 2);
                            getMoreData();
                        }
                        Common.saveUser(SignInSignUpActivity.this);
                        Intent intent = new Intent(SignInSignUpActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putInt("fragment", 1);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("TAG_U", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        reference.child("facebook").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    User user_fb = new User(user.getPhotoUrl().toString(), null, user.getDisplayName(), null, user.getUid(), user.getEmail(), user.getPhoneNumber());
                                    Common.signIn(user_fb, 3);
                                    reference.child("facebook").child(user.getUid()).getRef().setValue(user_fb);
                                }
                                else {
                                    Common.signIn(snapshot.getValue(User.class), 3);
                                    getMoreData();
                                }
                                Common.saveUser(SignInSignUpActivity.this);
                                Intent intent = new Intent(SignInSignUpActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Bundle bundle = new Bundle();
                                bundle.putInt("fragment", 1);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignInSignUpActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getMoreData(){
        DatabaseCart databaseCart = new DatabaseCart(SignInSignUpActivity.this);
        databaseCart.cleanCarts();
        if (Common.currentUser.getCartList() != null)
            for (Order order:Common.currentUser.getCartList()){
                database.getReference("book").child(order.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        @SuppressWarnings("unchecked")
                        List<String> image = (List<String>) snapshot.child("image").getValue();
                        order.setBookImage(image.get(0));
                        order.setBookName(snapshot.child("name").getValue(String.class));
                        order.setBookDiscount(snapshot.child("discount").getValue(Integer.class));
                        order.setBookPrice(snapshot.child("originalPrice").getValue(Integer.class));
                        order.setPublisherId(snapshot.child("publisher").getValue(String.class));
                        databaseCart.addCart(order);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
    }
}