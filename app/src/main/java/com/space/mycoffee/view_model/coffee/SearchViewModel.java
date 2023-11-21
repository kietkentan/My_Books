package com.space.mycoffee.view_model.coffee;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.space.mycoffee.model.CoffeeItem;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.utils.VNCharacterUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private final Context context;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.COFFEE);

    MutableLiveData<List<String>> _listSearchHistory = new MutableLiveData<>();
    public LiveData<List<String>> listSearchHistory = _listSearchHistory;

    MutableLiveData<List<String>> _listSearch = new MutableLiveData<>();
    public LiveData<List<String>> listSearch = _listSearch;

    MutableLiveData<List<CoffeeItem>> _listItem = new MutableLiveData<>();
    public LiveData<List<CoffeeItem>> listItem = _listItem;

    MutableLiveData<String> _textSearchLast = new MutableLiveData<>();
    public LiveData<String> textSearchLast = _textSearchLast;

    MutableLiveData<UiState> _stateSearch = new MutableLiveData<>();
    public LiveData<UiState> stateSearch = _stateSearch;

    MutableLiveData<Boolean> _isRemove = new MutableLiveData<>(false);
    public LiveData<Boolean> isRemove = _isRemove;
    public boolean isDetail = false;

    public SearchViewModel(Context context) {
        this.context = context;
        loadSearchHistory();
    }

    public void setTextSearchLast(String text) {
        _textSearchLast.postValue(text);
        if (text == null || text.trim().isEmpty()) {
            _listItem.postValue(null);
            _listSearch.postValue(null);
        } else readListStringSearch(text);
    }

    public void setStateSearch(UiState state) {
        _stateSearch.postValue(state);
    }

    public void setIsRemove(boolean b) {
        _isRemove.postValue(b);
    }

    public void loadSearchHistory() {
        FileInputStream fis = null;
        List<String> list = new ArrayList<>();

        try {
            fis = context.openFileInput(Constants.FILE_NAME);
            InputStreamReader streamReader = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(streamReader);
            String str;

            while ((str = reader.readLine()) != null){
                if (str == null || str.length() < 1)
                    continue;
                list.add(str);
            }

            Collections.reverse(list);
            _isRemove.postValue(list.size() <= 3);
            _listSearchHistory.postValue(list);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    _listSearchHistory.postValue(list);
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveToSearchHistory(String str) {
        FileOutputStream fos = null;
        List<String> list = listSearchHistory.getValue();
        if (list == null) list = new ArrayList<>();

        try {
            int index = -1;
            for (int i = 0; i < list.size(); ++i)
                if (list.get(i).equals(str)) {
                    index = i;
                    break;
                }

            fos = context.openFileOutput(Constants.FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            if (index > -1) list.remove(index);

            for (int i = list.size() - 1; i >= 0; --i)
                writer.write(list.get(i) + "\n");

            writer.write(str + "\n");
            writer.close();

            list.add(0, str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (list.size() < 3) {
                _isRemove.postValue(true);
            }
            _listSearchHistory.postValue(list);
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeLineSearchHistory(int position) {
        List<String> list = listSearchHistory.getValue();
        if (list == null) list = new ArrayList<>();
        list.remove(position);
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(Constants.FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            for (String str : list)
                writer.write(str + "\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (list.size() < 3) {
                _isRemove.postValue(true);
            }
            _listSearchHistory.postValue(list);
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clearSearchHistory() {
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(Constants.FILE_NAME, MODE_PRIVATE);
            fos.write("".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            _listSearchHistory.postValue(new ArrayList<>());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readDataItem(String strKey) {
        if (strKey == null || strKey.trim().isEmpty()) {
            _listItem.postValue(null);
            return;
        }
        saveToSearchHistory(strKey);
        List<CoffeeItem> list = new ArrayList<>();
        String strRemove = VNCharacterUtils.removeAccent(strKey).toLowerCase().trim();
        String[] keyList = strRemove.split(" ");
        _stateSearch.postValue(UiState.Loading);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nameBook = VNCharacterUtils.removeAccent(dataSnapshot.child("name").getValue(String.class).trim().toLowerCase());

                    for (String key : keyList)
                        if (nameBook.contains(key)){
                            if (nameBook.contains(strRemove))
                                list.add(0, dataSnapshot.getValue(CoffeeItem.class));
                            else list.add(dataSnapshot.getValue(CoffeeItem.class));
                            break;
                        }
                    if (list.size() > 30) break;
                }
                _listSearch.postValue(null);
                _listItem.postValue(list);
                _stateSearch.postValue(UiState.Success);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _stateSearch.postValue(UiState.Failure);
            }
        });
    }

    public void readListStringSearch(String str) {
        String key = VNCharacterUtils.removeAccent(str).toLowerCase().trim();
        List<String> list = new ArrayList<>();
        _stateSearch.postValue(UiState.Loading);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String nameBook = dataSnapshot.child(Constants.NAME).getValue(String.class);
                    if (VNCharacterUtils.removeAccent(nameBook.toLowerCase()).contains(key)) {
                        String str = (nameBook.contains("-") ? nameBook.substring(0, nameBook.indexOf("-")) : nameBook).trim().toLowerCase();
                        boolean contain = false;

                        for (String name : list)
                            if (name.equals(str)) {
                                contain = true;
                                break;
                            }

                        if (!contain) list.add(str);
                    }
                }
                _listSearch.postValue(list);
                _stateSearch.postValue(UiState.Success);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _stateSearch.postValue(UiState.Failure);
            }
        });
    }
}
