package com.khtn.mybooks.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.SwitchFavoritePageInterface;
import com.khtn.mybooks.activity.AddAddressActivity;
import com.khtn.mybooks.activity.AddressActivity;
import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.activity.CompletePaymentActivity;
import com.khtn.mybooks.Interface.ViewCartClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.adapter.CartAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Order;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements View.OnClickListener, ViewCartClickInterface {
    private View view;
    private CheckBox cbAllCart;
    private TextView tvTotalPrice;
    private TextView tvAddress;
    private AppCompatButton btnBuy;
    private AppCompatButton btnContinueShopping;
    private ImageButton ibRemoveCart;
    private RecyclerView rcShowCart;
    private ConstraintLayout layoutNoneCart;
    private ConstraintLayout layoutViewCart;

    private CartAdapter adapter;
    private List<Order> orderList;
    private List<Integer> listChecked;
    private DatabaseCart dataBaseOrder;
    private final String[] mode = {"mybooks", "google", "facebook"};
    private final SwitchFavoritePageInterface continueShoppingClickInterface;
    private DatabaseReference referenceUser;

    public CartFragment(SwitchFavoritePageInterface continueShoppingClickInterface) {
        this.continueShoppingClickInterface = continueShoppingClickInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        AppUtil.changeStatusBarColor(getContext(), "#E32127");

        init();
        getData();

        listChecked = adapter.getSelectedCart();
        tvAddress.setOnClickListener(this);
        btnContinueShopping.setOnClickListener(this);
        btnBuy.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    public void init(){
        dataBaseOrder = new DatabaseCart(getActivity());

        cbAllCart = view.findViewById(R.id.cb_check_all_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvAddress = view.findViewById(R.id.tv_address);
        btnBuy = view.findViewById(R.id.btn_buy);
        btnContinueShopping = view.findViewById(R.id.btn_continue_shopping);
        ibRemoveCart = view.findViewById(R.id.ib_remove_cart);
        rcShowCart = view.findViewById(R.id.rec_carts);
        layoutNoneCart = view.findViewById(R.id.layout_none_cart);
        layoutViewCart = view.findViewById(R.id.layout_view_cart);

        referenceUser = FirebaseDatabase.getInstance().getReference("user");
    }

    public void getData(){
        rcShowCart.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        orderList = dataBaseOrder.getCarts();
        adapter = new CartAdapter(orderList, this, getContext());
        rcShowCart.setAdapter(adapter);
        setupLayout();
    }

    public void setupAddress(){
        if (Common.addressNow != null)
            tvAddress.setText(AppUtil.getStringAddress(Common.addressNow));
        else
            tvAddress.setText(getString(R.string.add_address));
    }

    public void setupLayout(){
        if (orderList.size() == 0) {
            layoutNoneCart.setVisibility(View.VISIBLE);
            layoutViewCart.setVisibility(View.GONE);
        } else {
            cbAllCart.setChecked(false);
            layoutNoneCart.setVisibility(View.GONE);
            layoutViewCart.setVisibility(View.VISIBLE);
            ibRemoveCart.setOnClickListener(this);
            setupAddress();
        }
        setupCheckboxAllCart();
        setTotal();
    }

    public void setupCheckboxAllCart(){
        cbAllCart.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                adapter.selectedAllCart();
            else
                adapter.unSelectedAllCart();
            setTotal();
        });
    }

    @SuppressLint("DefaultLocale")
    public void setTotal(){
        listChecked = adapter.getSelectedCart();
        if (listChecked.size() == 0){
            tvTotalPrice.setText(getString(R.string.please_chose_product));
            tvTotalPrice.setTextSize(14);
            tvTotalPrice.setTypeface(null, Typeface.NORMAL);
            btnBuy.setText(String.format(getString(R.string.default_buy), 0));
        } else {
            int total = 0;
            for (int i = 0; i < listChecked.size(); ++i)
                total = total + (orderList.get(listChecked.get(i)).getBookPrice()*
                        (100 - orderList.get(listChecked.get(i)).getBookDiscount())/100)*
                        orderList.get(listChecked.get(i)).getBookQuantity();
            tvTotalPrice.setText(String.format(getString(R.string.book_price), AppUtil.convertNumber(total)));
            tvTotalPrice.setTextSize(20);
            tvTotalPrice.setTypeface(null, Typeface.BOLD);
            btnBuy.setText(String.format(getString(R.string.default_buy), listChecked.size()));
        }
    }

    public void clickAddress(){
        if (Common.currentUser.getAddressList() != null)
            startAddressPage();
        else
            startAddAddressPage();
    }

    public void startAddressPage(){
        startActivity(new Intent(getActivity(), AddressActivity.class));
        getActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    public void startAddAddressPage(){
        Intent intent = new Intent(getActivity(), AddAddressActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("pos", -1);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startCompletePayment(){
        Intent intent = new Intent(getActivity(), CompletePaymentActivity.class);
        Bundle bundle = new Bundle();

        bundle.putIntegerArrayList("list_buy", (ArrayList<Integer>) adapter.getSelectedCart());
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.switch_enter_activity, R.anim.switch_exit_activity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeCart(){
        if (cbAllCart.isChecked())
            dataBaseOrder.cleanCarts();
        else {
            List<Integer> listRemove = adapter.getSelectedCart();
            if (listRemove.size() > 0)
                for (int i = 0; i < listRemove.size(); i++)
                    dataBaseOrder.removeCarts(orderList.get(listRemove.get(i)).getBookId());
            else
                return;
        }
        getData();
        OnSaveAllCart(orderList);
    }

    public void openDialogRemoveCart(){
        if (adapter.getSelectedCart().size() == 0) {
            Toast.makeText(getContext(), R.string.not_item_selected_to_remove, Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog dialog = new Dialog(getContext(), R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_confirm_remove_cart);

        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnRemove = dialog.findViewById(R.id.btn_remove);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnRemove.setOnClickListener(v -> {
            dialog.dismiss();
            removeCart();
        });

        dialog.show();
    }

    public void clickBuyButton(){
        if (Common.addressNow == null)
            openDialogAddAddress();
        else if (adapter.getSelectedCart().size() > 0)
            startCompletePayment();
        else
            openDialogUnselected();
    }

    public void openDialogAddAddress(){
        Dialog dialog = new Dialog(getContext(), R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_none_address);

        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnAddAddress = dialog.findViewById(R.id.btn_accept_add_address);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnAddAddress.setOnClickListener(v -> {
            startAddAddressPage();
            dialog.dismiss();
        });

        dialog.show();
    }

    public void openDialogUnselected(){
        Dialog dialog = new Dialog(getContext(), R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_unselected_book);

        TextView tvUnderstood = dialog.findViewById(R.id.tv_understood);
        tvUnderstood.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_address)
            clickAddress();
        if (view.getId() == R.id.ib_remove_cart)
            openDialogRemoveCart();
        if (view.getId() == R.id.btn_buy)
            clickBuyButton();
        if (view.getId() == R.id.btn_continue_shopping)
            continueShoppingClickInterface.OnContinueShopping();
    }

    @Override
    public void OnCheckedChanged() {
        if (adapter.getSelectedCart().size() == orderList.size())
            cbAllCart.setChecked(true);
        else {
            List<Integer> tempChecked = adapter.getSelectedCart();
            cbAllCart.setChecked(false);
            adapter.setSelectedCart(tempChecked);
        }
        orderList = dataBaseOrder.getCarts();
        setTotal();
    }

    @Override
    public void OnSaveAllCart(List<Order> orderList) {
        referenceUser.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId())
                .child("cartList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        for (int i = 0; i < orderList.size(); ++i){
                            snapshot.child(String.valueOf(i)).child("bookId").getRef().setValue(orderList.get(i).getBookId());
                            snapshot.child(String.valueOf(i)).child("bookQuantity").getRef().setValue(orderList.get(i).getBookQuantity());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void OnChangeDataCart(int position, int quantity) {
        referenceUser.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId())
                .child("cartList").child(String.valueOf(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.child("bookQuantity").getRef().setValue(quantity);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}