package com.space.mycoffee.view_model.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.model.CoffeeItem;
import com.space.mycoffee.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CoffeeHomeViewModel extends ViewModel {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    MutableLiveData<List<CoffeeItem>> _listCoffeeNew = new MutableLiveData<>();
    public LiveData<List<CoffeeItem>> listCoffeeNew = _listCoffeeNew;

    public void fetchCoffeeNew() {
        database.getReference(Constants.COFFEE)
                .limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<CoffeeItem> newList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CoffeeItem item = dataSnapshot.getValue(CoffeeItem.class);
                            boolean hide = dataSnapshot.child(Constants.HIDE).getValue(Boolean.class);
                            if (!hide) newList.add(item);
                        }
                        _listCoffeeNew.postValue(newList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        _listCoffeeNew.postValue(null);
                    }
                });
    }
}
