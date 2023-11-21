package com.space.mycoffee.view_model.user;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.model.Request;
import com.space.mycoffee.model.User;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserViewModel extends ViewModel {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Activity activity;

    MutableLiveData<User> _user = new MutableLiveData<>();
    public LiveData<User> user = _user;

    MutableLiveData<List<Integer>> _numStatus = new MutableLiveData<>();
    public LiveData<List<Integer>> numStatus = _numStatus;

    public UserViewModel(Activity activity) {
        this.activity = activity;
    }

    public void setUser(User u) {
        _user.postValue(u);
        if (u == null) {
            List<Integer> list = new ArrayList<>(Collections.nCopies(5, 0));
            setNumStatus(list);
        } else getRequestStatus(u.getId());
    }

    private void getRequestStatus(String userId) {
        database.getReference(Constants.REQUEST)
                .orderByChild(Constants.ID_USER)
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Request> requestList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            requestList.add(dataSnapshot.getValue(Request.class));
                        }
                        int[] num = {0, 0, 0, 0, 0};
                        for (Request request : requestList) {
                            if (request.getStatus() < 6)
                                num[request.getStatus() - 1] += 1;
                        }
                        setNumStatus(Extensions.arrayToList(num));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setNumStatus(List<Integer> num) {
        _numStatus.postValue(num);
    }

    public void logout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(activity, gso);
        AppSingleton.clearUser(activity);
        if (AppSingleton.modeLogin == 2)
            gsc.signOut().addOnCompleteListener(activity, task -> {});
        AppSingleton.signOut();
        setUser(null);
    }
}
