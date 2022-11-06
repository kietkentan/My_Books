package com.khtn.mybooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.Interface.CartFragmentClickInterface;
import com.khtn.mybooks.Interface.ViewCartClickInterface;
import com.khtn.mybooks.adapter.CartAdapter;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.databases.DatabaseCart;
import com.khtn.mybooks.model.Order;

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
    private DatabaseCart dataBaseOrder;
    private final String[] mode = {"mybooks", "google", "facebook"};
    private final CartFragmentClickInterface cartFragmentClickInterface;
    private DatabaseReference referenceUser;

    public CartFragment(CartFragmentClickInterface cartFragmentClickInterface) {
        this.cartFragmentClickInterface = cartFragmentClickInterface;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        init();
        getData();

        tvAddress.setOnClickListener(this);
        btnContinueShopping.setOnClickListener(this);
        btnBuy.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
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

    public void setupLayout(){
        if (orderList.size() == 0) {
            layoutNoneCart.setVisibility(View.VISIBLE);
            layoutViewCart.setVisibility(View.GONE);
        } else {
            cbAllCart.setChecked(false);
            layoutNoneCart.setVisibility(View.GONE);
            layoutViewCart.setVisibility(View.VISIBLE);
            ibRemoveCart.setOnClickListener(this);
            if (Common.addressNow != null) {
                String defaultAddress = String.format("%s, %s, %s, %s", Common.addressNow.getAddress(),
                        Common.addressNow.getPrecinct().getName_with_type(),
                        Common.addressNow.getDistricts().getName_with_type(),
                        Common.addressNow.getProvinces_cities().getName_with_type());
                tvAddress.setText(defaultAddress);
            }
        }
        setupCheckboxAllCart();
    }

    public void setupCheckboxAllCart(){
        cbAllCart.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                adapter.selectedAllCart();
            else
                adapter.unSelectedAllCart();
        });
        setTotal(adapter.getSelectedCart());
    }

    @SuppressLint("DefaultLocale")
    public void setTotal(List<Integer> selectedCart){
        if (selectedCart.size() == 0){
            tvTotalPrice.setText(getString(R.string.please_chose_product));
            tvTotalPrice.setTextSize(14);
            tvTotalPrice.setTypeface(null, Typeface.NORMAL);
            btnBuy.setText(getString(R.string.default_buy));
        } else {
            int total = 0;
            for (int i = 0; i < selectedCart.size(); ++i)
                total = total + (orderList.get(selectedCart.get(i)).getBookPrice()*
                        (100 - orderList.get(selectedCart.get(i)).getBookDiscount())/100)*
                        orderList.get(selectedCart.get(i)).getBookQuantity();
            tvTotalPrice.setText(String.format("%s₫", AppUtil.convertNumber(total)));
            tvTotalPrice.setTextSize(20);
            tvTotalPrice.setTypeface(null, Typeface.BOLD);
            btnBuy.setText(String.format("%s (%d)", getString(R.string.buy), selectedCart.size()));
        }
    }

    public void startAddressPage(){
        startActivity(new Intent(getActivity(), AddressActivity.class));
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
            setTotal(listRemove);
        }
        getData();
        OnSaveAllCart(orderList);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_address)
            startAddressPage();
        if (view.getId() == R.id.ib_remove_cart)
            removeCart();
        if (view.getId() == R.id.btn_buy)
            ; // buy
        if (view.getId() == R.id.btn_continue_shopping)
            cartFragmentClickInterface.OnClick();
    }

    @Override
    public void OnCheckedChanged(List<Integer> selectedCart) {
        if (selectedCart.size() == orderList.size())
            cbAllCart.setChecked(true);
        else {
            List<Integer> tempChecked = adapter.getSelectedCart();
            cbAllCart.setChecked(false);
            adapter.setSelectedCart(tempChecked);
        }
        setTotal(selectedCart);
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