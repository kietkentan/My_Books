package com.khtn.mybooks.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khtn.mybooks.Interface.ImageClickInterface;
import com.khtn.mybooks.Interface.ItemChangedInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.AgeRangeAdapter;
import com.khtn.mybooks.adapter.ChoseAgeRangeAdapter;
import com.khtn.mybooks.adapter.AuthorAdapter;
import com.khtn.mybooks.adapter.ImageAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.model.Book;
import com.khtn.mybooks.model.Detail;
import com.khtn.mybooks.model.Rating;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private EditText edtEnterType;
    private EditText edtEnterPages;
    private EditText edtEnterWeight;
    private EditText edtEnterHeight;
    private EditText edtEnterWidth;
    private TextInputEditText edtEnterDescribe;
    private TextView tvId;
    private TextView tvSelectDateSell;
    private TextView tvSelectTimeSell;
    private TextView tvAddNewAuthor;
    private TextView tvChoseAgeRange;
    private AppCompatButton btnCancel;
    private AppCompatButton btnSave;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private RecyclerView recListPhoto;
    private RecyclerView recListAuthor;
    private RecyclerView recListAgeRange;
    private FrameLayout layoutLoading;
    private LinearLayout layoutSeeMorePicture;

    private int positionPictureSelected = -1;
    private List<Uri> uriList;
    private List<String> authorList;
    private List<Integer> ageRangeList;
    private String id = null;
    private Book book = null;
    private ImageAdapter imageAdapter;
    private AuthorAdapter authorAdapter;
    private AgeRangeAdapter ageRangeAdapter;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private String tempString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        init();

        if (id == null)
            getData();
        else
            getDataBook(id);

        setUriAdapter();
        setAuthorAdapter();
        setAgeRangeAdapter();

        ivReviewProduct.setColorFilter(new LightingColorFilter(0xaaaaaaaa, 0x11111111));

        ivSelectPhoto.setOnClickListener(EditProductActivity.this);
        ibEditReview.setOnClickListener(EditProductActivity.this);
        ivOpenSeeMore.setOnClickListener(EditProductActivity.this);
        ivCloseSeeMore.setOnClickListener(EditProductActivity.this);
        tvSelectDateSell.setOnClickListener(EditProductActivity.this);
        tvSelectTimeSell.setOnClickListener(EditProductActivity.this);
        tvAddNewAuthor.setOnClickListener(EditProductActivity.this);
        tvChoseAgeRange.setOnClickListener(EditProductActivity.this);
        btnCancel.setOnClickListener(EditProductActivity.this);
        btnSave.setOnClickListener(EditProductActivity.this);
    }

    public void init(){
        ivReviewProduct = findViewById(R.id.iv_item_review_product);
        ivSelectPhoto = findViewById(R.id.iv_select_photo);
        ibEditReview = findViewById(R.id.ib_edit_review_product);
        ivCloseSeeMore = findViewById(R.id.iv_close_see_more);
        ivOpenSeeMore = findViewById(R.id.iv_open_see_more);
        edtEnterName = findViewById(R.id.edt_enter_product_name);
        edtEnterAmount = findViewById(R.id.edt_enter_product_amount);
        edtEnterPrice = findViewById(R.id.edt_enter_product_price);
        edtEnterDiscount = findViewById(R.id.edt_enter_product_discount);
        edtEnterType = findViewById(R.id.edt_enter_type);
        edtEnterPages = findViewById(R.id.edt_enter_pages);
        edtEnterWeight = findViewById(R.id.edt_enter_weight);
        edtEnterHeight = findViewById(R.id.edt_enter_height);
        edtEnterWidth = findViewById(R.id.edt_enter_width);
        edtEnterDescribe = findViewById(R.id.edt_enter_describe);
        tvId = findViewById(R.id.tv_enter_product_id);
        tvSelectDateSell = findViewById(R.id.tv_enter_product_date_of_sell);
        tvSelectTimeSell = findViewById(R.id.tv_enter_product_time_of_sell);
        tvAddNewAuthor = findViewById(R.id.tv_add_new_author);
        tvChoseAgeRange = findViewById(R.id.tv_chose_age_range);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_add_save);
        recListPhoto = findViewById(R.id.rec_list_review_products);
        recListAuthor = findViewById(R.id.rec_list_author);
        recListAgeRange = findViewById(R.id.rec_list_age_range);
        layoutLoading = findViewById(R.id.layout_loading_product);
        layoutSeeMorePicture = findViewById(R.id.layout_see_more_picture);

        uriList = new ArrayList<>();
        authorList = new ArrayList<>();
        ageRangeList = new ArrayList<>();

        recListPhoto.setLayoutManager(new LinearLayoutManager(EditProductActivity.this, RecyclerView.HORIZONTAL, false));
        recListAuthor.setLayoutManager(new LinearLayoutManager(EditProductActivity.this, RecyclerView.VERTICAL, false));
        recListAgeRange.setLayoutManager(new LinearLayoutManager(EditProductActivity.this, RecyclerView.VERTICAL, false));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("book");
        storageReference = FirebaseStorage.getInstance().getReference("src_book");

        layoutLoading.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey("book"))
            id = bundle.getString("book");
    }

    public void setDataBook(){
        for (String str : book.getImage()) {
            uriList.add(Uri.parse(str));
            imageAdapter.notifyItemInserted(uriList.size() - 1);
        }
        if (book.getDetail().getAuthor() != null)
            for (String s : book.getDetail().getAuthor().split("\n")) {
                authorList.add(s);
                authorAdapter.notifyItemInserted(authorList.size() - 1);
            }
        if (book.getDetail().getAgeRange() != null)
            for (String s : book.getDetail().getAgeRange().split("")) {
                ageRangeList.add(Integer.parseInt(s));
                ageRangeAdapter.notifyItemInserted(ageRangeList.size() - 1);
            }
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
            openDialogEnterString(authorList.get(position));
            dialog.setOnDismissListener((DialogInterface dialogI) -> {
                if (tempString != null){
                    authorList.set(position, tempString);
                    authorAdapter.notifyItemChanged(position);
                }
            });
        }

        @Override
        public void onRemove(int position) {
            authorList.remove(position);
            authorAdapter = new AuthorAdapter(authorList, this, EditProductActivity.this);
            recListAuthor.setAdapter(authorAdapter);
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
            case R.id.tv_add_new_author:
                addNewAuthor();
                break;
            case R.id.tv_chose_age_range:
                choseAgeRange();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_add_save:
                checkDataEnter();
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

    public void setAgeRangeAdapter(){
        ageRangeAdapter = new AgeRangeAdapter(ageRangeList, EditProductActivity.this);
        recListAgeRange.setAdapter(ageRangeAdapter);
    }

    @SuppressLint("DefaultLocale")
    public void setData(){
        positionPictureSelected = 0;
        checkImageView();
        tvId.setText(String.format(getString(R.string.status), book.getId()));
        edtEnterName.setText(book.getName());
        edtEnterPrice.setText(String.format(getString(R.string.num), book.getOriginalPrice()));
        edtEnterDiscount.setText(String.format(getString(R.string.num), book.getDiscount()));
        edtEnterAmount.setText(String.format(getString(R.string.num), book.getAmount()));
        tvSelectDateSell.setText(String.format(getString(R.string.status), book.getTimeSell() == null ? getString(R.string.unselected) : book.getTimeSell().split(" ")[0]));
        tvSelectTimeSell.setText(String.format(getString(R.string.status), book.getTimeSell() == null ? getString(R.string.unselected) : book.getTimeSell().split(" ")[1]));
        edtEnterType.setText(book.getDetail().getType() == null ? "" : String.format(getString(R.string.status), book.getDetail().getType()));
        edtEnterPages.setText(book.getDetail().getPages() <= 0 ? "" : String.format(getString(R.string.num), book.getDetail().getPages()));
        edtEnterWeight.setText(book.getDetail().getWeight() <= 0 ? "" : String.format(getString(R.string.num), book.getDetail().getWeight()));
        edtEnterHeight.setText(book.getDetail().getSize() == null ? "" : String.format(getString(R.string.status), book.getDetail().getSize().split("x")[0]));
        edtEnterWidth.setText(book.getDetail().getSize() == null ? "" : String.format(getString(R.string.status), book.getDetail().getSize().split("x")[1]));
        edtEnterDescribe.setText(book.getDetail().getDescribe() == null ? "" : String.format(getString(R.string.status), book.getDetail().getDescribe().replace("<br>", "\n")));
    }

    @SuppressWarnings("deprecation")
    public void selectNewImage(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_CODE_SELECT_NEW_IMAGE);
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @SuppressWarnings("deprecation")
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
        setUriAdapter();
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

    public void addNewAuthor(){
        openDialogEnterString(null);
        dialog.setOnDismissListener((DialogInterface dialogI) -> {
            if (tempString != null){
                authorList.add(tempString);
                authorAdapter.notifyItemInserted(authorList.size() - 1);
            }
        });
    }

    public void choseAgeRange(){
        openDialogChoseAgeRange();
        dialog.setOnDismissListener(dialog -> setAgeRangeAdapter());
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

    public void checkDataEnter(){
        boolean check = true;
        if (uriList.size() <= 0) {
            Toast.makeText(this, R.string.please_chose_image, Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (edtEnterName.getText().toString().isEmpty()) {
            edtEnterName.setError(getString(R.string.enter_info));
            check = false;
        }
        if (edtEnterPrice.getText().toString().isEmpty()) {
            edtEnterPrice.setError(getString(R.string.enter_info));
            check = false;
        }
        if ((edtEnterHeight.getText().toString().isEmpty() && !edtEnterWidth.getText().toString().isEmpty()) ||
                (!edtEnterHeight.getText().toString().isEmpty() && edtEnterWidth.getText().toString().isEmpty())) {
            if (edtEnterHeight.getText().toString().isEmpty())
                edtEnterHeight.setError(getString(R.string.please_enter_full));
            else if (edtEnterWidth.getText().toString().isEmpty())
                edtEnterWidth.setError(getString(R.string.please_enter_full));
            check = false;
        }
        if (edtEnterPages.getText().toString().isEmpty()) {
            edtEnterPages.setError(getString(R.string.enter_info));
            check = false;
        }
        if (edtEnterWeight.getText().toString().isEmpty()) {
            edtEnterWeight.setError(getString(R.string.enter_info));
            check = false;
        }
        if (check)
            upLoadDataImage();
    }

    public void saveInfoProduct(){
        Book bookNew = getDataBook();
        reference.child(bookNew.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (book != null)
                    snapshot.getRef().removeValue();

                snapshot.getRef().setValue(bookNew);
                progressDialog.dismiss();
                Toast.makeText(EditProductActivity.this, R.string.add_successful_product, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this, R.string.adding_failed_products, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void upLoadDataImage() {
        progressDialog = new ProgressDialog(EditProductActivity.this);
        progressDialog.setTitle(getString(R.string.saving_product));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        boolean check = true;
        for (int i = 0; i < uriList.size(); ++i) {
            if (uriList.get(i).toString().contains("content:/")) {
                check = false;
                Date date = new Date();
                String tmp = String.valueOf(date.getTime()) + date.hashCode();
                int finalI = i;
                storageReference.child(tmp).putFile(uriList.get(i)).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(uri -> {
                        String photoLink = uri.toString();
                        uriList.set(finalI, Uri.parse(photoLink));
                        if (finalI == uriList.size() - 1)
                            saveInfoProduct();
                    });
                }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditProductActivity.this, book == null ? getString(R.string.adding_failed_product) : getString(R.string.saving_failed_product), Toast.LENGTH_SHORT).show();
                        });
            }
        }
        if (check)
            saveInfoProduct();
    }

    public void getDataBook(String id) {
        reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                book = snapshot.getValue(Book.class);
                setDataBook();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Book getDataBook() {
        Book tmpBook = new Book();
        Detail tmpDetail = new Detail();
        List<String> strings = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String datePosted = format.format(new Date());

        for (Uri u : uriList)
            strings.add(u.toString());

        if (authorList.size() > 0) {
            StringBuilder author = new StringBuilder(authorList.get(0));
            for (int i = 1; i < authorList.size(); ++i)
                author.append("\n").append(authorList.get(i));
            tmpDetail.setAuthor(String.valueOf(author));
        }
        if (ageRangeList.size() > 0) {
            StringBuilder range = new StringBuilder();
            for (int i : ageRangeList)
                range.append(i);
            tmpDetail.setAgeRange(String.valueOf(range));
        }
        tmpDetail.setSize(edtEnterHeight.getText().toString().isEmpty() ? null : edtEnterHeight.getText().toString() + "x" + edtEnterWidth.getText().toString());
        tmpDetail.setType(edtEnterType.getText().toString().isEmpty() ? null : edtEnterType.getText().toString());
        tmpDetail.setPages(Integer.parseInt(edtEnterPages.getText().toString()));
        tmpDetail.setWeight(Integer.parseInt(edtEnterWeight.getText().toString()));
        tmpDetail.setDescribe(edtEnterDescribe.getText().toString().isEmpty() ? null : edtEnterDescribe.getText().toString().replace("\n", "<br>"));

        tmpBook.setDetail(tmpDetail);
        tmpBook.setImage(strings);
        tmpBook.setOriginalPrice(Integer.parseInt(edtEnterPrice.getText().toString()));
        tmpBook.setName(edtEnterName.getText().toString());
        tmpBook.setId(tvId.getText().toString());
        tmpBook.setDatePosted(book == null ? datePosted.split(" ")[0] : book.getDatePosted());
        tmpBook.setPublisher(Common.currentUser.getStaff().getPublisherId());
        tmpBook.setAmount(edtEnterAmount.getText().toString().isEmpty() ? 0 : Integer.parseInt(edtEnterAmount.getText().toString()));
        tmpBook.setDiscount(edtEnterDiscount.getText().toString().isEmpty() ? 0 : Integer.parseInt(edtEnterDiscount.getText().toString()));
        tmpBook.setRating(book == null ? new Rating() : book.getRating());
        tmpBook.setSold(book == null ? 0 : book.getSold());
        if (!(tvSelectDateSell.getText().toString().equals(getString(R.string.unselected)) &&
                tvSelectTimeSell.getText().toString().equals(getString(R.string.unselected)))) {
            String sell = "";
            sell += tvSelectDateSell.getText().toString().equals(getString(R.string.unselected)) ? datePosted.split(" ")[0] : tvSelectDateSell.getText().toString();
            sell += " ";
            sell += tvSelectTimeSell.getText().toString().equals(getString(R.string.unselected)) ? datePosted.split(" ")[1] : tvSelectTimeSell.getText().toString();
            tmpBook.setTimeSell(sell);
        }

        return tmpBook;
    }

    public void openDatePicker(){
        int year;
        int month;
        int day;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date = (book == null || book.getTimeSell() == null) ? formatter.format(new Date()) : book.getTimeSell().split(" ")[0];
        String[] num = date.split("/");
        year = Integer.parseInt(num[2]);
        month = Integer.parseInt(num[1]);
        day = Integer.parseInt(num[0]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            month1 += 1;
            @SuppressLint("DefaultLocale") String date1 = String.format(getString(R.string.format_date), dayOfMonth, month1, year1);
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
        String time = (book == null || book.getTimeSell() == null) ? dtf.format(LocalDateTime.now()) : book.getTimeSell().split(" ")[1];
        String[] num = time.split(":");
        hour = Integer.parseInt(num[0]);
        minute = Integer.parseInt(num[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            @SuppressLint("DefaultLocale") String time1 = String.format(getString(R.string.format_time), hourOfDay, minute1);
            tvSelectTimeSell.setText(time1);
        }, hour, minute, true);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }

    public void openDialogChoseAgeRange() {
        dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_chose_age_range);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setAttributes(layoutParams);

        int spanCount = (int) (getResources().getDisplayMetrics().widthPixels/AppUtil.dpToPx(182, this));

        RecyclerView recAgeRange = dialog.findViewById(R.id.rec_list_chose_age_range);
        ChoseAgeRangeAdapter adapter = new ChoseAgeRangeAdapter(ageRangeList, EditProductActivity.this);
        recAgeRange.setLayoutManager(new GridLayoutManager(EditProductActivity.this, spanCount));
        recAgeRange.setAdapter(adapter);

        dialog.show();
    }

    public void openDialogEnterString(String str){
        dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_enter_string);
        dialog.getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);

        EditText edtEnter = dialog.findViewById(R.id.edt_enter_string);
        edtEnter.setText(str == null ? "" : str);
        edtEnter.setSelection(edtEnter.getText().length());
        tempString = null;

        // display on soft keyboard
        edtEnter.postDelayed(() -> showKeyboard(edtEnter), 300);

        // receive the input string when pressing Enter
        edtEnter.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard(edtEnter);

                if (!edtEnter.getText().toString().trim().isEmpty())
                    tempString = edtEnter.getText().toString();

                dialog.dismiss();
                return true;
            }
            return false; // very important
        });

        dialog.show();
    }

    @Override
    public void onUserInteraction() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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
            checkImageView();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    protected void showKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(edt.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        edt.requestFocus();
    }

    protected void hideKeyboard(EditText edt) {
        InputMethodManager imm = (InputMethodManager) edt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(edt.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }
}