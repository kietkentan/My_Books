package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.khtn.mybooks.AddAddressActivity;
import com.khtn.mybooks.Interface.AddressClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    List<Address> addressList;
    AddressClickInterface clickInterface;
    Context context;
    Address addressNow = Common.addressNow;

    public AddressAdapter(List<Address> addressList, AddressClickInterface clickInterface, Context context) {
        this.addressList = addressList;
        this.clickInterface = clickInterface;
        this.context = context;
    }


    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        holder.btnChoseAddress.setChecked(addressNow.equals(addressList.get(position)));
        holder.tvNameUser.setText(addressList.get(position).getName());
        holder.tvPhoneUser.setText(addressList.get(position).getPhone());
        String address = String.format("%s, %s, %s, %s", addressList.get(position).getAddress(),
                addressList.get(position).getPrecinct().getName_with_type(),
                addressList.get(position).getDistricts().getName_with_type(),
                addressList.get(position).getProvinces_cities().getName_with_type());
        holder.tvAddressUser.setText(address);
        if (addressList.get(position).isDefaultAddress())
            holder.tvDefault.setVisibility(View.VISIBLE);
        else {
            holder.tvDefault.setVisibility(View.INVISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.tvDefault.getLayoutParams();
            params.bottomMargin = 0;
            holder.tvDefault.setLayoutParams(params);
        }
        holder.btnChoseAddress.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                addressNow = addressList.get(holder.getAdapterPosition());
                clickInterface.OnClick();
            }
        });
        holder.ibEdit.setOnClickListener(view -> AddressAdapter.this.showMenuPopup(holder, holder.getAdapterPosition()));
    }

    @SuppressLint({"NonConstantResourceId"})
    public void showMenuPopup(AddressAdapter.ViewHolder view, int position){
        PopupMenu popupMenu = new PopupMenu(context, view.ibEdit);
        popupMenu.getMenuInflater().inflate(R.menu.in_address_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.m_set_default:
                    AddressAdapter.this.setDefault(position);
                    notifyItemChanged(0);
                    notifyItemChanged(position);
                    break;
                case R.id.m_edit:
                    startEditAddress(position);
                    notifyItemChanged(position);
                    break;
                case R.id.m_remove:
                    break;
            }
            return false;
        });
    }

    public void startEditAddress(int position){
        Intent intent = new Intent(context, AddAddressActivity.class);
        Bundle bundle = new Bundle();

        bundle.putInt("pos", position);
        bundle.putString("address", new Gson().toJson(addressList.get(position)));

        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void setDefault(int position){
        if (addressList.get(position).isDefaultAddress())
            return;
        Common.currentUser.getAddressList().get(0).setDefaultAddress(false);
        Common.currentUser.getAddressList().get(position).setDefaultAddress(true);

        addressList.get(0).setDefaultAddress(false);
        addressList.get(position).setDefaultAddress(true);
        addressNow = addressList.get(position);
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public Address getSelectedPosition(){
        return addressNow;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton btnChoseAddress;
        TextView tvNameUser;
        TextView tvPhoneUser;
        TextView tvAddressUser;
        TextView tvDefault;
        ImageButton ibEdit;
        ProgressBar progressSaveData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnChoseAddress = itemView.findViewById(R.id.btn_chose_address);
            tvNameUser = itemView.findViewById(R.id.tv_name_user);
            tvPhoneUser = itemView.findViewById(R.id.tv_phone_user);
            tvAddressUser = itemView.findViewById(R.id.tv_address_user);
            tvDefault = itemView.findViewById(R.id.tv_default_location);
            ibEdit = itemView.findViewById(R.id.ib_edit_address);
            progressSaveData = itemView.findViewById(R.id.progress_address_item);
        }
    }
}
