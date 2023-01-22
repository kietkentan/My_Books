package com.khtn.mybooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.helper.AppUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InformationUserActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton ibBack;
    private ImageView ivSearchItem;
    private TextView tvNumCart;
    private TextView tvChangePassword;
    private TextView tvNameUser;
    private TextView tvGenderUser;
    private TextView tvDateOfBirthUser;
    private TextView tvPhoneUser;
    private TextView tvEmailUser;
    private FrameLayout layoutCart;
    private LinearLayout layoutNameUser;
    private LinearLayout layoutGenderUser;
    private LinearLayout layoutDateOfBirthUser;
    private LinearLayout layoutPhoneUser;
    private LinearLayout layoutEmailUser;

    private DatabaseCart databaseCart;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private final String[] mode = {"mybooks", "google", "facebook"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_user);

        init();
        setupCart();
        setupUserInformation();

        ibBack.setOnClickListener(this);
        ivSearchItem.setOnClickListener(this);
        layoutCart.setOnClickListener(this);
        layoutNameUser.setOnClickListener(this);
        layoutGenderUser.setOnClickListener(this);
        layoutDateOfBirthUser.setOnClickListener(this);
    }

    public void init(){
        ibBack = findViewById(R.id.ib_exit_information_user);
        ivSearchItem = findViewById(R.id.iv_search_item);
        tvNumCart = findViewById(R.id.tv_num_cart);
        tvChangePassword = findViewById(R.id.tv_change_password);
        tvNameUser = findViewById(R.id.tv_name_user);
        tvGenderUser = findViewById(R.id.tv_gender_user);
        tvDateOfBirthUser = findViewById(R.id.tv_date_of_birth_user);
        tvPhoneUser = findViewById(R.id.tv_phone_user);
        tvEmailUser = findViewById(R.id.tv_email_user);
        layoutCart = findViewById(R.id.layout_shopping_cart);
        layoutNameUser = findViewById(R.id.layout_name_user);
        layoutGenderUser = findViewById(R.id.layout_gender_user);
        layoutDateOfBirthUser = findViewById(R.id.layout_date_of_birth_user);
        layoutPhoneUser = findViewById(R.id.layout_phone_user);
        layoutEmailUser = findViewById(R.id.layout_email_user);

        databaseCart = new DatabaseCart(InformationUserActivity.this);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
    }

    public void setupUserInformation(){
        tvNameUser.setText(Common.currentUser.getName());
        tvGenderUser.setText((Common.currentUser.getGender() < 1 || Common.currentUser.getGender() > 3) ? getText(R.string.not_set_up_yet) :
                getResources().getStringArray(R.array.gender)[Common.currentUser.getGender() - 1]);
        tvDateOfBirthUser.setText(Common.currentUser.getDateOfBirth() != null ? Common.currentUser.getDateOfBirth() : getText(R.string.not_set_up_yet));
        tvPhoneUser.setText(Common.currentUser.getPhone() != null ? Common.currentUser.getHiddenPhone() : getText(R.string.not_set_up_yet));
        tvEmailUser.setText(Common.currentUser.getEmail() != null ? Common.currentUser.getHiddenEmail() : getText(R.string.not_set_up_yet));
    }

    @SuppressLint("DefaultLocale")
    public void setupCart(){
        int i = databaseCart.getCarts().size();
        if (i > 0) {
            tvNumCart.setVisibility(View.VISIBLE);
            tvNumCart.setText(String.format("%d", i));
        }
        else
            tvNumCart.setVisibility(View.GONE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_exit_information_user:
                finish();
                break;
            case R.id.layout_shopping_cart:
                startCart();
                break;
            case R.id.iv_search_item:
                startSearchItemPage();
                break;
            case R.id.layout_name_user:
                openDialogNameUser();
                break;
            case R.id.layout_gender_user:
                openDialogGenderUser();
                break;
            case R.id.layout_date_of_birth_user:
                openDatePicker();
                break;
        }
    }

    public void startCart() {
        Intent intent = new Intent(InformationUserActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("fragment", 5);
        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startSearchItemPage(){
        Intent intent = new Intent(InformationUserActivity.this, SearchItemActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void openDialogNameUser(){
        Dialog dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_change_name);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);

        ImageButton ibClose = dialog.findViewById(R.id.ib_exit_dialog_enter_name);
        EditText edtEnterName = dialog.findViewById(R.id.edt_enter_name);
        ShapeableImageView ivRemoveText = dialog.findViewById(R.id.iv_remove_text);
        AppCompatButton btnSave = dialog.findViewById(R.id.btn_save_name);

        ibClose.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            ivRemoveText.setVisibility(View.GONE);
            if (edtEnterName.getText().toString().isEmpty())
                edtEnterName.setError(getString(R.string.name_not_empty));
            else if (!edtEnterName.getText().toString().contains(" "))
                edtEnterName.setError(getString(R.string.enter_fullname));
            else if (!AppUtil.isName(edtEnterName.getText().toString()))
                edtEnterName.setError(getString(R.string.name_not_valid));
            else {
                Common.currentUser.setName(edtEnterName.getText().toString());
                reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("name").setValue(edtEnterName.getText().toString());
                dialog.dismiss();
                tvNameUser.setText(Common.currentUser.getName());
            }
        });
        ivRemoveText.setOnClickListener(v -> {
            if (!edtEnterName.getText().toString().isEmpty())
                edtEnterName.setText("");
        });

        edtEnterName.setText(Common.currentUser.getName());
        edtEnterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtEnterName.getText().toString().isEmpty()) {
                    edtEnterName.setError(getString(R.string.name_not_empty));
                    ivRemoveText.setVisibility(View.GONE);
                } else
                    ivRemoveText.setVisibility(View.VISIBLE);
            }
        });

        dialog.show();
    }

    public void openDialogGenderUser(){
        Dialog dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_select_gender);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setAttributes(layoutParams);

        ImageButton ibClose = dialog.findViewById(R.id.ib_exit_dialog_change_gender);
        TextView tvMale = dialog.findViewById(R.id.tv_male);
        TextView tvFemale = dialog.findViewById(R.id.tv_female);
        TextView tvOther = dialog.findViewById(R.id.tv_other);
        TextView[] tvSelect = new TextView[]{tvMale, tvFemale, tvOther};
        ShapeableImageView ivSelectedMale = dialog.findViewById(R.id.iv_selected_male);
        ShapeableImageView ivSelectedFemale = dialog.findViewById(R.id.iv_selected_female);
        ShapeableImageView ivSelectedOther = dialog.findViewById(R.id.iv_selected_other);
        ShapeableImageView[] ivSelect = new ShapeableImageView[]{ivSelectedMale, ivSelectedFemale, ivSelectedOther};

        if (Common.currentUser.getGender() >= 1 || Common.currentUser.getGender() <= 3)
            setupGender(tvSelect, ivSelect, Common.currentUser.getGender() - 1);

        for (int i = 0; i < 3; ++i){
            int finalI = i;
            tvSelect[i].setText(getResources().getStringArray(R.array.gender)[i]);
            tvSelect[i].setOnClickListener(v -> {
                setupGender(tvSelect, ivSelect, finalI);
                reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("gender").setValue(finalI + 1);
                Common.currentUser.setGender(finalI + 1);
                tvGenderUser.setText(getResources().getStringArray(R.array.gender)[finalI]);
            });
        }

        ibClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void openDatePicker(){
        int year;
        int month;
        int day;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = Common.currentUser.getDateOfBirth() == null ? formatter.format(new Date()) : Common.currentUser.getDateOfBirth();
        String[] num = date.split("/");
        year = Integer.parseInt(num[2]);
        month = Integer.parseInt(num[1]);
        day = Integer.parseInt(num[0]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(InformationUserActivity.this, (view, year1, month1, dayOfMonth) -> {
            month1 += 1;
            String date1 = dayOfMonth + "/" + month1 + "/" + year1;
            tvDateOfBirthUser.setText(date1);
            Common.currentUser.setDateOfBirth(date1);
            reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("dateOfBirth").setValue(date1);
        }, year, month - 1, day);
        datePickerDialog.show();
    }

    public void setupGender(TextView[] tvSelect, ShapeableImageView[] ivSelect, int position){
        for (int i = 0; i < 3; ++i){
            tvSelect[i].setTextColor(i == position ? Color.parseColor("#E32127") : Color.parseColor("#000000"));
            ivSelect[i].setVisibility(i == position ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}