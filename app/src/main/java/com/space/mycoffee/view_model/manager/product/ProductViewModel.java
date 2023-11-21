package com.space.mycoffee.view_model.manager.product;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.space.mycoffee.R;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.utils.VNCharacterUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.COFFEE);
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("src");

    MutableLiveData<List<CoffeeDetail>> _listCoffee = new MutableLiveData<>();
    public LiveData<List<CoffeeDetail>> listCoffee = _listCoffee;

    MutableLiveData<CoffeeDetail> _coffeeSelected = new MutableLiveData<>();
    public LiveData<CoffeeDetail> coffeeSelected = _coffeeSelected;

    MutableLiveData<UiState> _stateUpload = new MutableLiveData<>();
    public LiveData<UiState> stateUpload = _stateUpload;

    MutableLiveData<Boolean> _isNew = new MutableLiveData<>(false);
    public LiveData<Boolean> isNew = _isNew;

    public void setCoffeeSelected(CoffeeDetail detail) {
        _isNew.postValue(detail == null);
        CoffeeDetail detail1;
        if (detail != null) {
            detail1 = new CoffeeDetail(detail);
        } else detail1 = new CoffeeDetail();
        _coffeeSelected.postValue(detail1);
    }

    public void removeAllImage() {
        CoffeeDetail detail = _coffeeSelected.getValue();
        detail.setImage(new ArrayList<>());
        _coffeeSelected.postValue(detail);
    }

    public void removePicture(int position) {
        CoffeeDetail detail = _coffeeSelected.getValue();
        List<String> list = detail.getImage();
        list.remove(position);
        detail.setImage(list);
        _coffeeSelected.postValue(detail);
    }

    public void addImage(ClipData clipData, Uri data) {
        CoffeeDetail detail = _coffeeSelected.getValue();
        List<String> list = detail.getImage();

        if (data != null) {
            list.add(data.toString());
        } else if (clipData != null) {
            int count = clipData.getItemCount();
            for (int i = 0; i < count; ++i) {
                Uri tempUri = clipData.getItemAt(i).getUri();
                list.add(tempUri.toString());
            }
        }
        detail.setImage(list);
        _coffeeSelected.postValue(detail);
    }

    public void itemRemove(int position) {
        List<CoffeeDetail> list = _listCoffee.getValue();
        if (list == null) return;

        boolean b = list.get(position).isHide();
        list.get(position).setHide(!b);
        _listCoffee.postValue(list);

        reference.child(list.get(position).getId()).child(Constants.HIDE).setValue(!b);
    }

    public void fetchListProduct(String key, Context context) {
        _listCoffee.postValue(new ArrayList<>());
        if (key == null || key.isEmpty()) fetchAll();
        else if (key.toUpperCase().startsWith("TN"))
            fetchById(key.toUpperCase());
        else fetchByName(key, context);
    }

    private void fetchAll() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<CoffeeDetail> newList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CoffeeDetail item = dataSnapshot.getValue(CoffeeDetail.class);
                    newList.add(item);
                }
                _listCoffee.postValue(newList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _listCoffee.postValue(null);
            }
        });
    }

    private void fetchById(String key) {
        reference.orderByChild(Constants.ID)
                .equalTo(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<CoffeeDetail> newList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CoffeeDetail item = dataSnapshot.getValue(CoffeeDetail.class);
                                newList.add(item);
                            }
                            _listCoffee.postValue(newList);
                        } else _listCoffee.postValue(null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        _listCoffee.postValue(null);
                    }
                });
    }

    private void fetchByName(String key, Context context) {
        String[] keyList = VNCharacterUtils.removeAccent(key).toLowerCase().split(" ");
        reference.orderByChild(Constants.ID)
                .startAfter(_listCoffee.getValue() == null || _listCoffee.getValue().size() == 0 ? _listCoffee.getValue().get(_listCoffee.getValue().size() - 1).getId() : context.getString(R.string.start_id_book))
                .limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<CoffeeDetail> newList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String name = VNCharacterUtils.removeAccent(dataSnapshot.child(Constants.NAME).getValue(String.class)).toLowerCase();
                                for (String key : keyList)
                                    if (name.contains(key)) {
                                        CoffeeDetail item = dataSnapshot.getValue(CoffeeDetail.class);
                                        newList.add(item);
                                        break;
                                    }
                            }
                            _listCoffee.postValue(newList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void upLoadDataImage(Context context) {
        _stateUpload.postValue(UiState.Loading);
        List<String> uriList = _coffeeSelected.getValue().getImage();

        boolean check = true;
        for (int i = 0; i < uriList.size(); ++i) {
            if (uriList.get(i).contains("content:/")) {
                check = false;
                Date date = new Date();
                String tmp = String.valueOf(date.getTime()) + date.hashCode();
                int finalI = i;
                storageReference.child(tmp).putFile(Uri.parse(uriList.get(i))).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(uri -> {
                        String photoLink = uri.toString();
                        uriList.set(finalI, photoLink);
                        if (finalI == uriList.size() - 1)
                            saveInfoProduct(context);
                    });
                }).addOnFailureListener(e -> {
                    _stateUpload.postValue(UiState.Failure);
                    Extensions.toast(context, R.string.saving_failed_product);
                });
            }
        }
        if (check) saveInfoProduct(context);
    }

    public void saveInfoProduct(Context context) {
        CoffeeDetail detail = _coffeeSelected.getValue();
        reference.child(detail.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (detail != null) snapshot.getRef().removeValue();

                snapshot.getRef().setValue(detail);
                _stateUpload.postValue(UiState.Success);
                Extensions.toast(context, R.string.add_successful_product);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _stateUpload.postValue(UiState.Failure);
                Extensions.toast(context, R.string.saving_failed_product);
            }
        });
    }

    private void saveDetail(CoffeeDetail detail) {

    }
}
