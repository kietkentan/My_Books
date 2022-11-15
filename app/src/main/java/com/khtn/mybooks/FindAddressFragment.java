package com.khtn.mybooks;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.khtn.mybooks.Interface.ChoseLocationClickInterface;
import com.khtn.mybooks.adapter.LocationAdapter;
import com.khtn.mybooks.model.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FindAddressFragment extends Fragment{
    private View view;
    private EditText edtSearch;
    private RecyclerView recSearch;
    private ProgressBar progressSearch;

    private List<Location> locationList;
    private LocationAdapter listAdapter;

    private final ChoseLocationClickInterface clickInterface;

    private final String code;

    public FindAddressFragment(ChoseLocationClickInterface clickInterface, String code) {
        this.clickInterface = clickInterface;
        this.code = code;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_find_address, container, false);

        init();
        setListViewSearch();
        readData();

        return view;
    }

    public void init(){
        edtSearch = view.findViewById(R.id.edt_search);
        recSearch = view.findViewById(R.id.rec_search_address);
        progressSearch = view.findViewById(R.id.progress_search_address);

        locationList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void readData(){
        progressSearch.setVisibility(View.VISIBLE);
        InputStream is = this.getContext().getResources().openRawResource(getContext().getResources().getIdentifier("r" + code,"raw", getContext().getPackageName()));
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null)
                builder.append(line);
            JSONObject jsonObject = new JSONObject(builder.toString());

            JSONArray arrayName = jsonObject.names();
            for (int i = 0; i < arrayName.length(); ++i){
                locationList.add(new Gson().fromJson(jsonObject.getString(arrayName.get(i).toString()), Location.class));
            }
        } catch (IOException | JSONException exception){
            exception.printStackTrace();
        }
        progressSearch.setVisibility(View.GONE);
        listAdapter.notifyDataSetChanged();
    }

    public void setListViewSearch(){
        listAdapter = new LocationAdapter(locationList, clickInterface);
        recSearch.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recSearch.setAdapter(listAdapter);
    }
}