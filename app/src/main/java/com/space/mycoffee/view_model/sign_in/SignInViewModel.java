package com.space.mycoffee.view_model.sign_in;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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
import com.space.mycoffee.R;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.model.User;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInViewModel extends ViewModel {
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.USER);
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    MutableLiveData<String> _accountPhone = new MutableLiveData<>("");
    public LiveData<String> accountPhone = _accountPhone;

    MutableLiveData<String> _accountMail = new MutableLiveData<>("");
    public LiveData<String> accountMail = _accountMail;

    MutableLiveData<String> _accountPassword = new MutableLiveData<>("");
    public LiveData<String> accountPassword = _accountPassword;

    MutableLiveData<Boolean> _showPassword = new MutableLiveData<>(false);
    public LiveData<Boolean> showPassword = _showPassword;

    MutableLiveData<String> _verificationId = new MutableLiveData<>();
    public LiveData<String> verificationId = _verificationId;

    MutableLiveData<Boolean> _isUsingPhone = new MutableLiveData<>(true);
    public LiveData<Boolean> isUsingPhone = _isUsingPhone;

    MutableLiveData<Boolean> _isRegister = new MutableLiveData<>(false);
    public LiveData<Boolean> isRegister = _isRegister;

    MutableLiveData<Boolean> _isEnableSignIn = new MutableLiveData<>(false);
    public LiveData<Boolean> isEnableSignIn = _isEnableSignIn;

    MutableLiveData<Boolean> _isExistAccount = new MutableLiveData<>(false);
    public LiveData<Boolean> isExistAccount = _isExistAccount;

    MutableLiveData<UiState> _stateSignIn = new MutableLiveData<>();
    public LiveData<UiState> stateSignIn = _stateSignIn;

    MutableLiveData<UiState> _stateUsingUser = new MutableLiveData<>();
    public LiveData<UiState> stateUsingUser = _stateUsingUser;

    MutableLiveData<UiState> _stateResend = new MutableLiveData<>();
    public LiveData<UiState> stateResend = _stateResend;

    MutableLiveData<UiState> _stateVerifier = new MutableLiveData<>();
    public LiveData<UiState> stateVerifier = _stateVerifier;

    MutableLiveData<UiState> _stateLogin = new MutableLiveData<>();
    public LiveData<UiState> stateLogin = _stateLogin;

    public boolean showDialog = false;
    public boolean changePassNotExit = false;
    public boolean isForget = false;

    public void changeUsingPhone() {
        _isUsingPhone.postValue(!_isUsingPhone.getValue());
    }

    public void setIsPhone(boolean isPhone) {
        _isUsingPhone.postValue(isPhone);
    }

    public void changeEnableSignIn(String text) {
        if (_isUsingPhone.getValue()) {
            _isEnableSignIn.postValue(text.length() > 9);
            setAccountPhone(text);
        }
        else {
            _isEnableSignIn.postValue(!text.isEmpty());
            setAccountMail(text);
        }
    }

    public void setStateSignIn(UiState state) {
        _stateSignIn.postValue(state);
    }

    public void setStateUsingUser(UiState state) {
        _stateUsingUser.postValue(state);
    }

    public void setAccountPhone(String str) {
        _accountPhone.postValue(str);
    }

    public void setAccountPassword(String str) {
        _accountPassword.postValue(str);
    }

    public void setAccountMail(String str) {
        _accountMail.postValue(str);
    }

    public void changeShowPassword() {
        _showPassword.postValue(!_showPassword.getValue());
    }

    public void clearInfo() {
        _verificationId.postValue(null);
        _stateUsingUser.postValue(null);
        _stateVerifier.postValue(null);
        _stateSignIn.postValue(null);
    }

    public void clearPhone() {
        _accountPhone.postValue("");
    }

    public void clearAll() {
        clearInfo();
        _stateLogin.postValue(null);
        _stateResend.postValue(null);
        _accountPassword.postValue(null);
        _accountMail.postValue(null);
        _accountPhone.postValue(null);
        _isUsingPhone.postValue(true);
        _showPassword.postValue(false);
    }

    public void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask, Context context) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            String avatar = null;
            try {
                avatar = acct.getPhotoUrl().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (acct != null) {
                String finalAvatar = avatar;
                reference.child(AppSingleton.mode[1]).child(acct.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            User user_gg = new User(finalAvatar, null, acct.getDisplayName(), "", acct.getId(), acct.getEmail(), null);
                            AppSingleton.signIn(user_gg, context, 2);
                            reference.child(AppSingleton.mode[1]).child(acct.getId()).getRef().setValue(user_gg);
                        }
                        else {
                            AppSingleton.signIn(snapshot.getValue(User.class),  context, 2);
                        }
                        setStateSignIn(UiState.Success);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        setStateSignIn(UiState.Failure);
                    }
                });
            }
        } catch (ApiException e) {
            e.printStackTrace();
            setStateSignIn(UiState.Failure);
        }
    }

    public void checkUser(Activity activity) {
        boolean isPhone = _isUsingPhone.getValue();
        String act = isPhone ? _accountPhone.getValue() : _accountMail.getValue();
        if (act == null || act.isEmpty()) return;
        _stateUsingUser.postValue(UiState.Loading);
        reference.child(AppSingleton.mode[0])
                .orderByChild(isPhone ? Constants.PHONE : Constants.EMAIL)
                .equalTo(act)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isPhone && !snapshot.exists()) sentOTP(activity, true);
                        else {
                            _isExistAccount.postValue(snapshot.exists());
                            _stateUsingUser.postValue(UiState.Success);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        _stateUsingUser.postValue(UiState.Failure);
                    }
                });
    }

    public void sentOTP(Activity activity, boolean register) {
        String act = _accountPhone.getValue();
        _isRegister.postValue(register);
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder()
                .setPhoneNumber("+84" + act.replaceFirst("0", ""))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        _stateUsingUser.postValue(UiState.Success);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        _stateUsingUser.postValue(UiState.Failure);
                        Extensions.toast(activity, e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        _verificationId.postValue(s);
                        mForceResendingToken = forceResendingToken;
                        _stateUsingUser.postValue(UiState.Success);
                    }
                })
                .build());
    }

    public void resendOTP(Activity activity) {
        String act = _accountPhone.getValue();
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+84" + act.replaceFirst("0", ""))
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setForceResendingToken(mForceResendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        _stateResend.postValue(UiState.Success);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        _stateResend.postValue(UiState.Failure);
                        Extensions.toast(activity, e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        _verificationId.postValue(s);
                        mForceResendingToken = forceResendingToken;
                        _stateResend.postValue(UiState.Success);
                        Extensions.toast(activity, R.string.resending);
                    }
                })
                .build());
    }

    public void checkOTP(String code) {
        _stateVerifier.postValue(UiState.Loading);
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(_verificationId.getValue(), code);
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                _stateVerifier.postValue(UiState.Success);
                mForceResendingToken = null;
            } else _stateVerifier.postValue(UiState.Failure);
        }).addOnFailureListener(e -> _stateVerifier.postValue(UiState.Failure));
    }

    public void login(Context context) {
        boolean isPhone = _isUsingPhone.getValue();
        String strAcc = isPhone ? _accountPhone.getValue() : _accountMail.getValue();
        String strPass = _accountPassword.getValue();
        if (strPass == null || strPass.isEmpty()) return;
        _stateLogin.postValue(UiState.Loading);
        reference.child(AppSingleton.mode[0])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            if (dataSnapshot.child(isPhone ? Constants.PHONE : Constants.EMAIL).getValue(String.class).equals(strAcc)) {
                                User user = dataSnapshot.getValue(User.class);
                                if (Objects.requireNonNull(user).getPassword().equals(strPass)) {
                                    AppSingleton.signIn(user, context, 1);
                                    getMoreData();
                                } else {
                                    Extensions.toast(context, R.string.incorrect_password);
                                    _stateLogin.postValue(null);
                                }
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        _stateLogin.postValue(UiState.Failure);
                    }
                });
    }

    private void getMoreData() {
        if (AppSingleton.currentUser.getCartList() != null) {
            List<Order> list = new ArrayList<>(AppSingleton.currentUser.getCartList());
            AppSingleton.currentUser.setCartList(new ArrayList<>());
            for (Order order : list) {
                database.getReference(Constants.COFFEE)
                        .child(order.getIdCoffee())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                CoffeeDetail detail = snapshot.getValue(CoffeeDetail.class);
                                AppSingleton.currentUser.addCartList(new Order(detail, order.getCoffeeQuantity()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                _stateLogin.postValue(UiState.Failure);
                            }
                        });
            }
            _stateLogin.postValue(UiState.Success);
        } else  _stateLogin.postValue(UiState.Success);
    }

    public void checkPhone(Activity activity, boolean isNewPass) {
        _stateUsingUser.postValue(UiState.Loading);
        showDialog = false;
        reference.child(AppSingleton.mode[0])
                .orderByChild(Constants.PHONE)
                .equalTo(_accountPhone.getValue())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (isNewPass) {
                        changePassNotExit = false;
                        sentOTP(activity, false);
                    } else {
                        showDialog = true;
                        _stateUsingUser.postValue(UiState.Success);
                    }
                } else {
                    if (!isNewPass) sentOTP(activity, true);
                    else {
                        changePassNotExit = true;
                        _stateUsingUser.postValue(UiState.Success);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _stateUsingUser.postValue(UiState.Success);
            }
        });
    }
}
