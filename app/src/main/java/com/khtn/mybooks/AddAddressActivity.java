package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
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
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Address;

import java.util.ArrayList;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener{
    private final int LENGTH_NAME = 50;
    private final int LENGTH_PHONE_NUMBER = 10;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        init();

        ibBack.setOnClickListener(this);
        tvChoseProvincesCities.setOnClickListener(this);
        tvChosePrecinct.setOnClickListener(this);
        tvChoseDistricts.setOnClickListener(this);
        btnConfirmAddress.setOnClickListener(this);

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
                tvLimitName.setText(String.format("%d/50", edtEnterRecipientName.getText().length()));
            }
        });
    }

    public void init(){
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
        String[] mode = {"mybooks", "google", "facebook"};
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (defaultAddress){
                    List<Address> addressList = new ArrayList<>();
                    addressList.add(address);
                    addressList.addAll(Common.addressLists);

                    if (addressList.size() > 1)
                        addressList.get(1).setDefaultAddress(false);
                    Common.addressLists = addressList;

                    snapshot.getRef().removeValue();
                    for (int i = 0; i < Common.addressLists.size(); ++i) {
                        @SuppressLint("DefaultLocale") String count = String.format("%d", i);
                        snapshot.child(count).getRef().setValue(Common.addressLists.get(i));
                    }
                } else {
                    @SuppressLint("DefaultLocale") String count = String.format("%d", Common.addressLists.size());
                    reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("addressList").child(count).setValue(address);
                    Common.addressLists.add(address);
                }
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

    public void checkInfo(){
        if (!AppUtil.isName(edtEnterRecipientName.getText().toString())
            || !AppUtil.isPhoneNumber(edtEnterPhone.getText().toString())
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

            if (edtEnterDeliveryAddress.getText().toString().isEmpty())
                edtEnterDeliveryAddress.setError(getString(R.string.enter_delivery_address));
            else if (!edtEnterDeliveryAddress.getText().toString().contains(" "))
                edtEnterDeliveryAddress.setError(getString(R.string.delivery_must_at_least_two_words));
            else if (!AppUtil.isString(edtEnterDeliveryAddress.getText().toString()))
                edtEnterDeliveryAddress.setError(getString(R.string.delivery_not_valid));

            //if (tvChoseProvincesCities.getText().toString().isEmpty())
                //tvChoseProvincesCities.setError(getString(R.string.chose_provinces_cities));
        } else {
            address = new Address(edtEnterRecipientName.getText().toString(), edtEnterPhone.getText().toString(),
                    edtEnterDeliveryAddress.getText().toString(), cbSetItDefault.isChecked());
            addAddress(cbSetItDefault.isChecked());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_exit_add_address)
            openDialog();
        if (view.getId() == R.id.btn_confirm_address)
            checkInfo();
    }

    @Override
    public void onBackPressed() {
        openDialog();
    }
}