package com.space.mycoffee.view_model.address;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.R;
import com.space.mycoffee.model.Address;
import com.space.mycoffee.model.Location;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;

import java.util.ArrayList;
import java.util.List;

public class AddressViewModel extends ViewModel {
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.USER);

    MutableLiveData<List<Address>> _listAddress = new MutableLiveData<>();
    public LiveData<List<Address>> listAddress = _listAddress;

    MutableLiveData<Address> _addressNowSelected = new MutableLiveData<>();
    public LiveData<Address> addressNowSelected = _addressNowSelected;

    MutableLiveData<Address> _addressSelected = new MutableLiveData<>();
    public LiveData<Address> addressSelected = _addressSelected;

    MutableLiveData<Location> _cityLocation = new MutableLiveData<>();
    public LiveData<Location> cityLocation = _cityLocation;

    MutableLiveData<Location> _districtsLocation = new MutableLiveData<>();
    public LiveData<Location> districtsLocation = _districtsLocation;

    MutableLiveData<Location> _precinctLocation = new MutableLiveData<>();
    public LiveData<Location> precinctLocation = _precinctLocation;

    MutableLiveData<String> _errorName = new MutableLiveData<>(null);
    public LiveData<String> errorName = _errorName;

    MutableLiveData<String> _errorPhone = new MutableLiveData<>(null);
    public LiveData<String> errorPhone = _errorPhone;

    MutableLiveData<String> _errorLocation = new MutableLiveData<>(null);
    public LiveData<String> errorLocation = _errorLocation;

    MutableLiveData<String> _errorAddress = new MutableLiveData<>(null);
    public LiveData<String> errorAddress = _errorAddress;

    MutableLiveData<Boolean> _isNew = new MutableLiveData<>(false);
    public LiveData<Boolean> isNew = _isNew;

    MutableLiveData<Integer> _tabCode = new MutableLiveData<>();
    public LiveData<Integer> tabCode = _tabCode;

    public int lengthName = 50;

    public void getListAddress() {
        _listAddress.postValue(AppSingleton.currentUser.getAddressList());
    }

    public void setAddressSelected(Address address) {
        _isNew.postValue(address == null);
        Address address1;
        if (address != null) {
            address1 = new Address(address.getId(), address.getName(), address.getPhone(), address.getProvinces_cities(), address.getDistricts(), address.getPrecinct(), address.getAddress(), address.isDefaultAddress());
        } else address1 = new Address();
        if (address1.getId() == null) address1.setId(Long.toString(System.currentTimeMillis()));
        _addressSelected.postValue(address1);
    }

    public void setTabCode(int num) {
        _tabCode.postValue(num);
    }

    public void setCityLocation(Location location) {
        _cityLocation.postValue(location);
        setTabCode(location == null ? 1 : 2);
        if (location != null) setDistrictsLocation(null);
        else {
            _districtsLocation.postValue(null);
            _precinctLocation.postValue(null);
        }
    }

    public void setDistrictsLocation(Location location) {
        _districtsLocation.postValue(location);
        setTabCode(location == null ? 2 : 3);
        if (location != null) setPrecinctLocation(null);
        else _precinctLocation.postValue(null);
    }

    public void setPrecinctLocation(Location location) {
        setTabCode(3);
        _precinctLocation.postValue(location);
    }

    public void setName(String str) {
        Address address = _addressSelected.getValue();
        address.setName(str);
        _addressSelected.postValue(address);
    }

    public void setPhone(String str) {
        Address address = _addressSelected.getValue();
        address.setPhone(str);
        _addressSelected.postValue(address);
    }

    public void setAddressString(String str) {
        Address address = _addressSelected.getValue();
        address.setAddress(str);
        _addressSelected.postValue(address);
    }

    public void setIsDefault(boolean b) {
        Address address = _addressSelected.getValue();
        address.setDefaultAddress(b);
        _addressSelected.postValue(address);
    }

    public void setErrorName(String str) {
        _errorName.postValue(str);
    }

    public void setErrorPhone(String str) {
        _errorPhone.postValue(str);
    }

    public void setErrorLocation(String str) {
        _errorLocation.postValue(str);
    }

    public void setErrorAddress(String str) {
        _errorAddress.postValue(str);
    }

    public void clearError() {
        _errorName.postValue(null);
        _errorPhone.postValue(null);
        _errorLocation.postValue(null);
        _errorAddress.postValue(null);
    }

    public void clearAll() {
        clearError();
        _addressSelected.postValue(null);
    }

    public void saveAddressSelected() {
        Address address = _addressSelected.getValue();
        List<Address> list = _listAddress.getValue();
        if (address == null) return;
        if (list == null) list = new ArrayList<>();
        boolean isDe = true;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDefaultAddress() && !list.get(i).getId().equals(address.getId())) {
                if (address.isDefaultAddress()) {
                    list.get(i).setDefaultAddress(false);
                    break;
                }
                isDe = false;
                break;
            }
        }
        address.setDefaultAddress(isDe);
        if (!_isNew.getValue()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(address.getId())) {
                    list.set(i, address);
                    break;
                }
            }
        } else list.add(address);
        if (AppSingleton.addressNow == null) AppSingleton.addressNow = address;
        _listAddress.postValue(list);
        saveListToDatabase(list);
    }

    public void saveLocation() {
        Location city = _cityLocation.getValue();
        Location district = districtsLocation.getValue();
        if (city == null || district == null) return;
        Address address = _addressSelected.getValue();
        address.setProvinces_cities(city);
        address.setDistricts(district);
        address.setPrecinct(_precinctLocation.getValue());
        _addressSelected.postValue(address);
    }

    public void removeAddress(int position, Context context) {
        List<Address> list = _listAddress.getValue();
        if (list.get(position).isDefaultAddress()) {
            Extensions.toast(context, R.string.not_remove_default_address);
            return;
        }
        if (position < list.size()) {
            list.remove(position);
        }
        saveListToDatabase(list);
    }

    public void changeAddressNow(Address address) {
        _addressNowSelected.postValue(address);
    }

    public void changeDefault(int position) {
        List<Address> list = _listAddress.getValue();
        if (position >= list.size() || list.get(position).isDefaultAddress()) return;

        for (Address address : list)
            address.setDefaultAddress(false);
        list.get(position).setDefaultAddress(true);
        saveListToDatabase(list);
    }

    public void saveAddressNow() {
        Address address = _addressNowSelected.getValue();
        if (address != null) AppSingleton.addressNow = address;
    }

    private void saveListToDatabase(List<Address> list) {
        AppSingleton.currentUser.setAddressList(list);
        _listAddress.postValue(list);
        reference.child(AppSingleton.mode[AppSingleton.modeLogin - 1])
                .child(AppSingleton.currentUser.getId())
                .child(Constants.ADDRESS_LIST)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                snapshot.getRef().setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
