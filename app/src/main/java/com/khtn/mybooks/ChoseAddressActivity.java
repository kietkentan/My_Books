package com.khtn.mybooks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.khtn.mybooks.Interface.ChoseLocationClickInterface;
import com.khtn.mybooks.model.Address;
import com.khtn.mybooks.model.Location;

public class ChoseAddressActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private TextView tvChoseCities;
    private TextView tvChoseDistrict;
    private TextView tvChosePrecinct;
    private View viewCities;
    private View viewDistrict;
    private View viewPrecinct;

    private Fragment citiesFrag;
    private int fragCode = 1;
                        // 0: city
                        // 1: district
                        // 2: precinct

    private ChoseLocationClickInterface clickInterface;
    private Location locationCity, locationDistrict, locationPrecinct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_address);

        init();

        ibBack.setOnClickListener(this);
        tvChoseCities.setOnClickListener(this);
        tvChoseDistrict.setOnClickListener(this);
        tvChosePrecinct.setOnClickListener(this);

        newSwitch();
    }

    public void newSwitch(){
        int code = 1;
        fragCode = 3;
        if (locationDistrict != null){
            tvChoseCities.setText(locationCity.getName_with_type());
            tvChoseDistrict.setText(locationDistrict.getName_with_type());
            code = 3;
        } else if (locationCity != null){
            tvChoseCities.setText(locationCity.getName_with_type());
            code = 2;
        }
        switchFragment(code);
    }

    public void init(){
        Bundle bundle = getIntent().getExtras();
        if (!bundle.isEmpty()){
            if (bundle.containsKey("city"))
                locationCity = new Gson().fromJson(bundle.getString("city"), Location.class);
            if (bundle.containsKey("district"))
                locationDistrict = new Gson().fromJson(bundle.getString("district"), Location.class);
        }

        ibBack = findViewById(R.id.ib_exit_chose_address);
        tvChoseCities = findViewById(R.id.tv_cities);
        tvChoseDistrict = findViewById(R.id.tv_districts);
        tvChosePrecinct = findViewById(R.id.tv_precinct);
        viewCities = findViewById(R.id.view_cities);
        viewDistrict = findViewById(R.id.view_districts);
        viewPrecinct = findViewById(R.id.view_precinct);

        clickInterface = location -> {
            int code = location.getCode().length();
            fragCode = code;

            if (code == 2)
                locationCity = location;
            else if (code == 3)
                locationDistrict = location;
            else if (code == 5)
                locationPrecinct = location;

            switchFragment(code);
        };
        citiesFrag = new FindAddressFragment(clickInterface, "0");
    }

    public void switchFragment(int code){
        if (code == 1){
            fragCities();
        } else if (code == 2){
            tvChoseCities.setText(locationCity.getName_with_type());
            fragDistricts();
        } else if (code == 3){
            tvChoseDistrict.setText(locationDistrict.getName_with_type());
            fragPrecincts();
        } else if (code == 5){
            tvChosePrecinct.setText(locationPrecinct.getName_with_type());

            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            bundle.putString("city", new Gson().toJson(locationCity));
            bundle.putString("district", new Gson().toJson(locationDistrict));
            bundle.putString("precinct", new Gson().toJson(locationPrecinct));

            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void fragCities(){
        if (fragCode != 0) {
            fragCode = 0;
            locationCity = null;
            locationDistrict = null;

            tvChoseCities.setText(getText(R.string.provinces_and_cities));
            tvChoseDistrict.setText(getText(R.string.districts));
            tvChosePrecinct.setText(getText(R.string.precinct));

            viewCities.setVisibility(View.VISIBLE);
            viewDistrict.setVisibility(View.INVISIBLE);
            viewPrecinct.setVisibility(View.INVISIBLE);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_location, citiesFrag).commit();
        }
    }

    public void fragDistricts(){
        if (fragCode > 1){
            fragCode = 1;

            Fragment districtFrag = new FindAddressFragment(clickInterface, locationCity.getCode());
            locationDistrict = null;

            tvChoseDistrict.setText(getText(R.string.districts));
            tvChosePrecinct.setText(getText(R.string.precinct));

            viewCities.setVisibility(View.INVISIBLE);
            viewDistrict.setVisibility(View.VISIBLE);
            viewPrecinct.setVisibility(View.INVISIBLE);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_location, districtFrag).commit();
        }
    }

    public void fragPrecincts(){
        fragCode = 2;

        Fragment precinctFrag = new FindAddressFragment(clickInterface, locationDistrict.getCode());

        viewCities.setVisibility(View.INVISIBLE);
        viewDistrict.setVisibility(View.INVISIBLE);
        viewPrecinct.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_location, precinctFrag).commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_exit_chose_address)
            finish();
        if (v.getId() == R.id.tv_cities)
            fragCities();
        if (v.getId() == R.id.tv_districts)
            fragDistricts();
    }

    @Override
    public void onBackPressed() {
        if (fragCode == 0)
            super.onBackPressed();
        else if (fragCode == 1)
            fragCities();
        else if (fragCode == 2)
            fragDistricts();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}