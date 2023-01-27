package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.khtn.mybooks.Interface.ImageClickInterface;
import com.khtn.mybooks.Interface.ItemChangedInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.AuthorAdapter;
import com.khtn.mybooks.adapter.ImageAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Book;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class EditProductActivity extends AppCompatActivity implements View.OnClickListener{
    private final int REQUEST_CODE_SELECT_NEW_IMAGE = 10000;
    private final int REQUEST_CODE_SELECT_ADD_IMAGE = 10001;
    private boolean checkSee = false;

    private ImageView ivReviewProduct;
    private ImageView ivSelectPhoto;
    private ImageButton ibEditReview;
    private ShapeableImageView ivCloseSeeMore;
    private ShapeableImageView ivOpenSeeMore;
    private EditText edtEnterName;
    private EditText edtEnterAmount;
    private EditText edtEnterPrice;
    private EditText edtEnterDiscount;
    private TextView tvId;
    private TextView tvSelectDateSell;
    private TextView tvSelectTimeSell;
    private AppCompatButton btnCancel;
    private AppCompatButton btnSave;
    private RecyclerView recListPhoto;
    private RecyclerView recListAuthor;
    private FrameLayout layoutLoading;
    private LinearLayout layoutSeeMorePicture;

    private int positionPictureSelected = -1;
    private List<Uri> uriList;
    private List<String> authorList;
    private List<Integer> ageRangeList;
    private Book book = null;
    private ImageAdapter imageAdapter;
    private AuthorAdapter authorAdapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        init();

        if (book != null)
            setDataBook();
        else
            getData();

        setUriAdapter();
        setAuthorAdapter();

        ivReviewProduct.setColorFilter(new LightingColorFilter(0xaaaaaaaa, 0x11111111));

        ivSelectPhoto.setOnClickListener(EditProductActivity.this);
        ibEditReview.setOnClickListener(EditProductActivity.this);
        ivOpenSeeMore.setOnClickListener(EditProductActivity.this);
        ivCloseSeeMore.setOnClickListener(EditProductActivity.this);
        tvSelectDateSell.setOnClickListener(EditProductActivity.this);
        tvSelectTimeSell.setOnClickListener(EditProductActivity.this);
    }

    public void init(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey("book"))
            book = new Gson().fromJson(bundle.getString("book"), Book.class);

        ivReviewProduct = findViewById(R.id.iv_item_review_product);
        ivSelectPhoto = findViewById(R.id.iv_select_photo);
        ibEditReview = findViewById(R.id.ib_edit_review_product);
        ivCloseSeeMore = findViewById(R.id.iv_close_see_more);
        ivOpenSeeMore = findViewById(R.id.iv_open_see_more);
        edtEnterName = findViewById(R.id.edt_enter_product_name);
        edtEnterAmount = findViewById(R.id.edt_enter_product_amount);
        edtEnterPrice = findViewById(R.id.edt_enter_product_price);
        edtEnterDiscount = findViewById(R.id.edt_enter_product_discount);
        tvId = findViewById(R.id.tv_enter_product_id);
        tvSelectDateSell = findViewById(R.id.tv_enter_product_date_of_sell);
        tvSelectTimeSell = findViewById(R.id.tv_enter_product_time_of_sell);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_add_save);
        recListPhoto = findViewById(R.id.rec_list_review_products);
        recListAuthor = findViewById(R.id.rec_list_author);
        layoutLoading = findViewById(R.id.layout_loading_product);
        layoutSeeMorePicture = findViewById(R.id.layout_see_more_picture);

        uriList = new ArrayList<>();
        authorList = new ArrayList<>();
        ageRangeList = new ArrayList<>();

        recListPhoto.setLayoutManager(new LinearLayoutManager(EditProductActivity.this, RecyclerView.HORIZONTAL, false));
        recListAuthor.setLayoutManager(new LinearLayoutManager(EditProductActivity.this, RecyclerView.VERTICAL, false));

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");

        layoutLoading.setVisibility(View.VISIBLE);
    }

    public void setDataBook(){
        for (String str : book.getImage())
            uriList.add(Uri.parse(str));
        authorList.addAll(Arrays.asList(book.getDetail().getAuthor().split("\n")));
        for (String s : book.getDetail().getAgeRange().split(""))
            ageRangeList.add(Integer.parseInt(s));

        layoutLoading.setVisibility(View.GONE);
        setData();
    }

    public void getData(){
        getId();
        checkImageView();
    }

    public ImageClickInterface photoInterface = new ImageClickInterface() {
        @Override
        public void onClick(int position) {
            positionPictureSelected = position;
            if (uriList.get(position).toString().contains("content:/"))
                ivReviewProduct.setImageURI(uriList.get(position));
            else
                Picasso.get().load(uriList.get(position)).into(ivReviewProduct);
        }
    };

    public ItemChangedInterface authorInterface = new ItemChangedInterface() {
        @Override
        public void onChanged(int position) {

        }

        @Override
        public void onRemove(int position) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_select_photo:
                selectNewImage();
                break;
            case R.id.ib_edit_review_product:
                showPopupMenuPicture();
                break;
            case R.id.iv_open_see_more:
                checkSee = true;
                checkImageView();
                break;
            case R.id.iv_close_see_more:
                checkSee = false;
                checkImageView();
                break;
            case R.id.tv_enter_product_date_of_sell:
                openDatePicker();
                break;
            case R.id.tv_enter_product_time_of_sell:
                openTimePicker();
                break;
        }
    }

    public void getId(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder id;
                Date date = new Date();
                long timeMilli = date.getTime();
                Random random = new Random(timeMilli);
                int idInt = random.nextInt(99999999);

                do {
                    id = new StringBuilder("TN");
                    for (int i = 0; i < 8 - String.valueOf(idInt).length(); ++i)
                        id.append('0');
                    id.append(idInt++);
                } while (snapshot.child(String.valueOf(id)).exists());

                tvId.setText(String.valueOf(id));
                layoutLoading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkSeeMoreLayout(){
        if (uriList.size() > 1){
            layoutSeeMorePicture.setVisibility(checkSee ? View.VISIBLE : View.GONE);
            ivOpenSeeMore.setVisibility(checkSee ? View.GONE : View.VISIBLE);
        } else {
            layoutSeeMorePicture.setVisibility(View.GONE);
            ivOpenSeeMore.setVisibility(View.GONE);
        }
    }

    public void checkImageView(){
        ivSelectPhoto.setVisibility(positionPictureSelected >= 0 ? View.GONE : View.VISIBLE);
        ibEditReview.setVisibility(positionPictureSelected >= 0 ? View.VISIBLE : View.GONE);
        checkSeeMoreLayout();
        if (positionPictureSelected < 0)
            ivReviewProduct.setImageResource(R.color.background_shimmer);
        else {
            ivReviewProduct.setBackgroundResource(R.color.background);
            if (uriList.get(positionPictureSelected).toString().contains("content:/"))
                ivReviewProduct.setImageURI(uriList.get(positionPictureSelected));
            else
                Picasso.get().load(uriList.get(positionPictureSelected)).into(ivReviewProduct);
        }
    }

    public void setUriAdapter(){
        imageAdapter = new ImageAdapter(uriList, photoInterface);
        recListPhoto.setAdapter(imageAdapter);
    }

    public void setAuthorAdapter(){
        authorAdapter = new AuthorAdapter(authorList, authorInterface, EditProductActivity.this);
        recListAuthor.setAdapter(authorAdapter);
    }

    @SuppressLint("DefaultLocale")
    public void setData(){
        positionPictureSelected = 0;
        checkImageView();
        tvId.setText(String.format("%s", book.getId()));
        edtEnterName.setText(book.getName());
        edtEnterPrice.setText(String.format("%d", book.getOriginalPrice()));
        edtEnterDiscount.setText(String.format("%d", book.getDiscount()));
        edtEnterAmount.setText(String.format("%d", book.getAmount()));
    }

    public void selectNewImage(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_CODE_SELECT_NEW_IMAGE);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void selectAddImage(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_CODE_SELECT_ADD_IMAGE);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void removePicture(){
        uriList.remove(positionPictureSelected);
        imageAdapter.notifyItemRemoved(positionPictureSelected);
        positionPictureSelected = uriList.size() > 0 ? 0 : -1;
        checkImageView();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAllPicture(){
        uriList.clear();
        imageAdapter.notifyDataSetChanged();
        positionPictureSelected = -1;
        checkImageView();
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopupMenuPicture(){
        PopupMenu popupMenu = new PopupMenu(this, ibEditReview);
        popupMenu.getMenuInflater().inflate(R.menu.in_image_product_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.m_add_picture:
                    selectAddImage();
                    break;
                case R.id.m_remove_picture:
                    removePicture();
                    break;
                case R.id.m_remove_all_picture:
                    removeAllPicture();
                    break;
            }
            return true;
        });
    }

    public void openDatePicker(){
        int year;
        int month;
        int day;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = book.getTimeSell() == null ? formatter.format(new Date()) : book.getTimeSell().split(" ")[0];
        String[] num = date.split("/");
        year = Integer.parseInt(num[2]);
        month = Integer.parseInt(num[1]);
        day = Integer.parseInt(num[0]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            month1 += 1;
            @SuppressLint("DefaultLocale") String date1 = String.format("%d/%d/%d", dayOfMonth, month1, year1);
            tvSelectDateSell.setText(date1);
        }, year, month - 1, day);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openTimePicker(){
        int hour;
        int minute;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = book.getTimeSell() == null ? dtf.format(LocalDateTime.now()) : book.getTimeSell().split(" ")[1];
        String[] num = time.split(":");
        hour = Integer.parseInt(num[0]);
        minute = Integer.parseInt(num[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            @SuppressLint("DefaultLocale") String time1 = String.format("%d:%d:00", hourOfDay, minute1);
            tvSelectTimeSell.setText(time1);
        }, hour, minute, true);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_NEW_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                uriList.clear();

                for (int i = 0; i < count; ++i) {
                    Uri tempUri = data.getClipData().getItemAt(i).getUri();
                    uriList.add(tempUri);
                    imageAdapter.notifyItemInserted(uriList.size() - 1);
                }
            } else if (data.getData() != null){
                uriList.clear();

                Uri tempUri = data.getData();
                uriList.add(tempUri);
                imageAdapter.notifyItemInserted(uriList.size() - 1);
            }

            positionPictureSelected = 0;
            checkImageView();
        }

        if (requestCode == REQUEST_CODE_SELECT_ADD_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; ++i) {
                    Uri tempUri = data.getClipData().getItemAt(i).getUri();
                    uriList.add(tempUri);
                    imageAdapter.notifyItemInserted(uriList.size() - 1);
                }
            } else if (data.getData() != null){
                Uri tempUri = data.getData();
                uriList.add(tempUri);
                ivReviewProduct.setImageURI(tempUri);
                imageAdapter.notifyItemInserted(uriList.size() - 1);
            }
        }
    }
}