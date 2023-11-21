package com.space.mycoffee.view.address;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.space.mycoffee.adapter.LocationAdapter;
import com.space.mycoffee.databinding.FragmentFindAddressBinding;
import com.space.mycoffee.listener.ChoseLocationClickInterface;
import com.space.mycoffee.model.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FindAddressFragment extends Fragment {
    private FragmentFindAddressBinding binding;

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
        binding = FragmentFindAddressBinding.inflate(inflater, container, false);

        init();
        setListViewSearch();
        readData();

        return binding.getRoot();
    }

    public void init() {
        locationList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void readData(){
        binding.progressSearchAddress.setVisibility(View.VISIBLE);

        try {
            @SuppressLint("DiscouragedApi") InputStream is = this.getContext().getResources().openRawResource(getContext().getResources().getIdentifier("r" + code,"raw", getContext().getPackageName()));
            StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = bufferedReader.readLine()) != null)
                builder.append(line);
            JSONObject jsonObject = new JSONObject(builder.toString());

            JSONArray arrayName = jsonObject.names();
            for (int i = 0; i < arrayName.length(); ++i){
                locationList.add(new Gson().fromJson(jsonObject.getString(arrayName.get(i).toString()), Location.class));
            }
        } catch (IOException | JSONException exception){
            exception.printStackTrace();
            clickInterface.onFailure();
        }
        binding.progressSearchAddress.setVisibility(View.GONE);
        listAdapter.submitList(locationList);
    }

    public void setListViewSearch(){
        listAdapter = new LocationAdapter(LocationAdapter.itemCallback, clickInterface);
        binding.recSearchAddress.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recSearchAddress.setAdapter(listAdapter);
    }
}