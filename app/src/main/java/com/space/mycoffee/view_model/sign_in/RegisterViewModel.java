package com.space.mycoffee.view_model.sign_in;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.model.User;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.UiState;

import java.util.UUID;

public class RegisterViewModel extends ViewModel {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.USER);

    MutableLiveData<String> _accountPhone = new MutableLiveData<>("");
    public LiveData<String> accountPhone = _accountPhone;

    MutableLiveData<String> _accountMail = new MutableLiveData<>("");
    public LiveData<String> accountMail = _accountMail;

    MutableLiveData<String> _accountName = new MutableLiveData<>("");
    public LiveData<String> accountName = _accountName;

    MutableLiveData<String> _accountPassword = new MutableLiveData<>("");
    public LiveData<String> accountPassword = _accountPassword;

    MutableLiveData<String> _accountPassword2 = new MutableLiveData<>("");
    public LiveData<String> accountPassword2 = _accountPassword2;

    MutableLiveData<String> _errorName = new MutableLiveData<>(null);
    public LiveData<String> errorName = _errorName;

    MutableLiveData<String> _errorPassword = new MutableLiveData<>(null);
    public LiveData<String> errorPassword = _errorPassword;

    MutableLiveData<String> _errorPassword2 = new MutableLiveData<>(null);
    public LiveData<String> errorPassword2 = _errorPassword2;

    MutableLiveData<Boolean> _showPassword = new MutableLiveData<>(false);
    public LiveData<Boolean> showPassword = _showPassword;

    MutableLiveData<Boolean> _showPassword2 = new MutableLiveData<>(false);
    public LiveData<Boolean> showPassword2 = _showPassword2;

    MutableLiveData<UiState> _stateCreate = new MutableLiveData<>();
    public LiveData<UiState> stateCreate = _stateCreate;

    public void setAccountPhone(String str) {
        _accountPhone.postValue(str);
    }

    public String getPhone() {
        return _accountPhone.getValue();
    }

    public void setAccountMail(String str) {
        _accountMail.postValue(str);
    }

    public void setAccountName(String str) {
        _accountName.postValue(str);
    }

    public void setAccountPassword(String str) {
        _accountPassword.postValue(str);
    }

    public void setAccountPassword2(String str) {
        _accountPassword2.postValue(str);
    }

    public void setErrorName(String str) {
        _errorName.postValue(str);
    }

    public void setErrorPassword(String str) {
        _errorPassword.postValue(str);
    }

    public void setErrorPassword2(String str) {
        _errorPassword2.postValue(str);
    }

    public void changeShowPassword() {
        _showPassword.postValue(!_showPassword.getValue());
    }

    public void changeShowPassword2() {
        _showPassword2.postValue(!_showPassword2.getValue());
    }

    public void clearError() {
        _errorName.postValue(null);
        _errorPassword.postValue(null);
        _errorPassword2.postValue(null);
    }

    public void createAccount() {
        _stateCreate.postValue(UiState.Loading);
        String uniqueID = UUID.randomUUID().toString();
        User user = new User(null, null, _accountName.getValue(), _accountPassword.getValue(), uniqueID, _accountMail.getValue(), _accountPhone.getValue());
        reference.child(AppSingleton.mode[0]).child(uniqueID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().setValue(user);
                _stateCreate.postValue(UiState.Success);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _stateCreate.postValue(UiState.Failure);
            }
        });
    }
}
