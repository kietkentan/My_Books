package com.space.mycoffee.view_model.sign_in;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.UiState;

public class NewPasswordViewModel extends ViewModel {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.USER);

    private String phone = null;

    MutableLiveData<String> _accountPassword = new MutableLiveData<>("");
    public LiveData<String> accountPassword = _accountPassword;

    MutableLiveData<String> _accountPassword2 = new MutableLiveData<>("");
    public LiveData<String> accountPassword2 = _accountPassword2;

    MutableLiveData<String> _errorPassword = new MutableLiveData<>(null);
    public LiveData<String> errorPassword = _errorPassword;

    MutableLiveData<String> _errorPassword2 = new MutableLiveData<>(null);
    public LiveData<String> errorPassword2 = _errorPassword2;

    MutableLiveData<Boolean> _showPassword = new MutableLiveData<>(false);
    public LiveData<Boolean> showPassword = _showPassword;

    MutableLiveData<Boolean> _showPassword2 = new MutableLiveData<>(false);
    public LiveData<Boolean> showPassword2 = _showPassword2;

    MutableLiveData<UiState> _stateChange = new MutableLiveData<>();
    public LiveData<UiState> stateChange = _stateChange;

    public void setAccountPassword(String str) {
        _accountPassword.postValue(str);
    }

    public void setAccountPassword2(String str) {
        _accountPassword2.postValue(str);
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
        _errorPassword.postValue(null);
        _errorPassword2.postValue(null);
    }

    public void setPhone(String p) {
        phone = p;
    }

    public String getPhone() {
        return phone;
    }

    public void changePassword() {
        _stateChange.postValue(UiState.Loading);
        String newPassword = _accountPassword.getValue();
        reference.child(AppSingleton.mode[0])
                .orderByChild(Constants.PHONE)
                .equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String oldPassword = dataSnapshot.child(Constants.PASSWORD).getValue(String.class);
                    dataSnapshot.child(Constants.OLD_PASSWORD).getRef().child(String.valueOf(dataSnapshot.child(Constants.OLD_PASSWORD).getChildrenCount())).setValue(oldPassword);
                    dataSnapshot.child(Constants.PASSWORD).getRef().setValue(newPassword);

                    _stateChange.postValue(UiState.Success);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _stateChange.postValue(UiState.Failure);
            }
        });
    }
}
