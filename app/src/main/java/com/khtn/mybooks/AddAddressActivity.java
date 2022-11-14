package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Address;
import com.khtn.mybooks.model.Location;

import java.util.ArrayList;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener{
    private final int LENGTH_NAME = 50;
    private final int LENGTH_PHONE_NUMBER = 10;
    private final int REQUEST_CODE_1 = 1;

    private ImageButton ibBack;
    private TextView tvLimitName;
    private TextView tvChoseProvincesCities;
    private TextView tvChoseDistricts;
    private TextView tvChosePrecinct;
    private EditText edtEnterRecipientName;
    private EditText edtEnterPhone;
    private EditText edtEnterDeliveryAddress;
    private CheckBox cbSetItDefault;
    private AppCompatButton btnConfirmAddress;
    private ProgressBar progressBarAdd;

    private DatabaseReference reference;
    private Address address;
    private final String[] mode = {"mybooks", "google", "facebook"};
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        AppUtil.changeStatusBarColor(this, "#E32127");

        init();

        ibBack.setOnClickListener(this);
        tvChoseProvincesCities.setOnClickListener(this);
        tvChosePrecinct.setOnClickListener(this);
        tvChoseDistricts.setOnClickListener(this);
        btnConfirmAddress.setOnClickListener(this);

        setupLayout();
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = bundle.getInt("pos");

        if (bundle.containsKey("address")) {
            String myGson = bundle.getString("address");
            address = new Gson().fromJson(myGson, Address.class);
        } else
            address = new Address();

        ibBack = findViewById(R.id.ib_exit_add_address);
        tvLimitName = findViewById(R.id.tv_limit_name);
        tvChoseProvincesCities = findViewById(R.id.tv_chose_provinces_cities);
        tvChoseDistricts = findViewById(R.id.tv_chose_districts);
        tvChosePrecinct = findViewById(R.id.tv_chose_precinct);
        edtEnterRecipientName = findViewById(R.id.edt_enter_recipient_name);
        edtEnterPhone = findViewById(R.id.edt_enter_phone);
        edtEnterDeliveryAddress = findViewById(R.id.edt_enter_delivery_address);
        cbSetItDefault = findViewById(R.id.cb_set_it_default);
        btnConfirmAddress = findViewById(R.id.btn_confirm_address);
        progressBarAdd = findViewById(R.id.progress_add_address);

        reference = FirebaseDatabase.getInstance().getReference("user");
    }

    public void setupLayout(){
        edtEnterPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        edtEnterPhone.setFilters(new InputFilter[] {new InputFilter.LengthFilter(LENGTH_PHONE_NUMBER)});
        edtEnterRecipientName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(LENGTH_NAME)});
        edtEnterRecipientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable editable) {
                tvLimitName.setText(String.format(getString(R.string.name_limit), edtEnterRecipientName.getText().length()));
            }
        });

        if (position >= 0)
            showAddress();
    }

    public void showAddress(){
        edtEnterRecipientName.setText(address.getName());
        tvLimitName.setText(String.format(getString(R.string.name_limit), edtEnterRecipientName.getText().length()));
        edtEnterPhone.setText(address.getPhone());
        edtEnterDeliveryAddress.setText(address.getAddress());
        tvChoseDistricts.setText(address.getDistricts().getName_with_type());
        tvChoseDistricts.setEnabled(true);
        tvChosePrecinct.setText(address.getPrecinct().getName_with_type());
        tvChosePrecinct.setEnabled(true);
        tvChoseProvincesCities.setText(address.getProvinces_cities().getName_with_type());
    }

    public void openDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_exit_add_address);

        AppCompatButton btnStay = dialog.findViewById(R.id.btn_stay);
        AppCompatButton btnExit = dialog.findViewById(R.id.btn_exit);

        btnExit.setOnClickListener(view -> {
            dialog.dismiss();
            finish();
        });
        btnStay.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    public void addAddress(boolean defaultAddress){
        btnConfirmAddress.setVisibility(View.INVISIBLE);
        progressBarAdd.setVisibility(View.VISIBLE);
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (defaultAddress){
                    address.setDefaultAddress(true);
                    List<Address> addressList = new ArrayList<>();
                    addressList.add(address);
                    if (Common.currentUser.getAddressList() != null)
                        addressList.addAll(Common.currentUser.getAddressList());
                    if (addressList.size() > 1)
                        addressList.get(1).setDefaultAddress(false);
                    Common.currentUser.setAddressList(addressList);
                    snapshot.getRef().removeValue();
                    for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i) {
                        @SuppressLint("DefaultLocale") String count = String.format("%d", i);
                        snapshot.child(count).getRef().setValue(Common.currentUser.getAddressList().get(i));
                    }
                } else {
                    if (Common.currentUser.getAddressList() != null) {
                        @SuppressLint("DefaultLocale") String count = String.format("%d", Common.currentUser.getAddressList().size());
                        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").child(count).setValue(address);
                    }else {
                        address.setDefaultAddress(true);
                        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").child("0").setValue(address);
                        Common.currentUser.setAddressList(new ArrayList<>());
                    }
                    Common.currentUser.getAddressList().add(address);
                }
                Common.setAddressNow();
                Toast.makeText(AddAddressActivity.this, R.string.add_address_success, Toast.LENGTH_SHORT).show();
                btnConfirmAddress.setVisibility(View.VISIBLE);
                progressBarAdd.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnConfirmAddress.setVisibility(View.VISIBLE);
                progressBarAdd.setVisibility(View.GONE);
            }
        });
    }

    public void saveAddress(boolean defaultAddress){
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Address> addressList = new ArrayList<>();
                if (!defaultAddress) {
                    boolean notDefault = true;
                    for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i) {
                        if (i == position)
                            addressList.add(address);
                        else
                            addressList.add(Common.currentUser.getAddressList().get(i));

                        if (addressList.get(i).isDefaultAddress())
                            notDefault = false;
                    }
                    if (notDefault)
                        Common.currentUser.getAddressList().get(position).setDefaultAddress(true);
                } else {
                    for (int i = 0; i < Common.currentUser.getAddressList().size(); ++i) {
                        if (i == position)
                            addressList.add(address);
                        else
                            addressList.add(Common.currentUser.getAddressList().get(i));
                    }
                    addressList.get(position).setDefaultAddress(true);
                }

                Common.currentUser.setAddressList(addressList);

                snapshot.child(String.valueOf(position)).getRef().setValue(addressList.get(position));
                Toast.makeText(AddAddressActivity.this, R.string.change_address_success, Toast.LENGTH_SHORT).show();
                btnConfirmAddress.setVisibility(View.VISIBLE);
                progressBarAdd.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                btnConfirmAddress.setVisibility(View.VISIBLE);
                progressBarAdd.setVisibility(View.GONE);
            }
        });
    }

    public void checkInfo(){
        if (!AppUtil.isName(edtEnterRecipientName.getText().toString())
                || !AppUtil.isPhoneNumber(edtEnterPhone.getText().toString())
                || tvChoseProvincesCities.getText().toString().isEmpty()
                || !AppUtil.isString(edtEnterDeliveryAddress.getText().toString())){
            if (edtEnterRecipientName.getText().toString().isEmpty())
                edtEnterRecipientName.setError(getString(R.string.enter_recipient_name));
            else if (!edtEnterRecipientName.getText().toString().contains(" "))
                edtEnterRecipientName.setError(getString(R.string.enter_fullname));
            else if (!AppUtil.isName(edtEnterRecipientName.getText().toString()))
                edtEnterRecipientName.setError(getString(R.string.name_not_valid));

            if (edtEnterPhone.getText().toString().isEmpty())
                edtEnterPhone.setError(getString(R.string.enter_phone_number));
            else if (!AppUtil.isPhoneNumber(edtEnterPhone.getText().toString()))
                edtEnterPhone.setError(getString(R.string.phone_not_valid));

            if (tvChoseProvincesCities.getText().toString().isEmpty())
                tvChoseProvincesCities.setError(getString(R.string.chose_address));

            if (edtEnterDeliveryAddress.getText().toString().isEmpty())
                edtEnterDeliveryAddress.setError(getString(R.string.enter_delivery_address));
            else if (!edtEnterDeliveryAddress.getText().toString().contains(" "))
                edtEnterDeliveryAddress.setError(getString(R.string.delivery_must_at_least_two_words));
            else if (!AppUtil.isString(edtEnterDeliveryAddress.getText().toString()))
                edtEnterDeliveryAddress.setError(getString(R.string.delivery_not_valid));
        } else {
            address.setName(edtEnterRecipientName.getText().toString());
            address.setAddress(edtEnterDeliveryAddress.getText().toString());
            address.setPhone(edtEnterPhone.getText().toString());
            if (position < 0)
                addAddress(cbSetItDefault.isChecked());
            else
                saveAddress(cbSetItDefault.isChecked());
        }
    }

    public void startChoseByCity(){
        Intent intent = new Intent(AddAddressActivity.this, ChoseAddressActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_1);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startChoseByDistrict(){
        Intent intent = new Intent(AddAddressActivity.this, ChoseAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("city", new Gson().toJson(address.getProvinces_cities()));
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_1);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startChoseByPrecinct(){
        Intent intent = new Intent(AddAddressActivity.this, ChoseAddressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("city", new Gson().toJson(address.getProvinces_cities()));
        bundle.putString("district", new Gson().toJson(address.getDistricts()));
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_1);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_add_address)
            openDialog();
        if (view.getId() == R.id.btn_confirm_address)
            checkInfo();
        if (view.getId() == R.id.tv_chose_provinces_cities)
            startChoseByCity();
        if (view.getId() == R.id.tv_chose_districts)
            startChoseByDistrict();
        if (view.getId() == R.id.tv_chose_precinct)
            startChoseByPrecinct();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_1){
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();

                address.setProvinces_cities(new Gson().fromJson(bundle.getString("city"), Location.class));
                address.setDistricts(new Gson().fromJson(bundle.getString("district"), Location.class));
                address.setPrecinct(new Gson().fromJson(bundle.getString("precinct"), Location.class));

                tvChoseProvincesCities.setText(address.getProvinces_cities().getName_with_type());
                tvChoseDistricts.setText(address.getDistricts().getName_with_type());
                tvChosePrecinct.setText(address.getPrecinct().getName_with_type());

                tvChoseDistricts.setEnabled(true);
                tvChosePrecinct.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        openDialog();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}