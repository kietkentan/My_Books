package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.khtn.mybooks.AddAddressActivity;
import com.khtn.mybooks.Interface.AddressClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private final List<Address> addressList;
    private final AddressClickInterface clickInterface;
    private final Context context;
    private Address addressNow = Common.addressNow;

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
                    openDialogRemove(position);
                    break;
            }
            return false;
        });
    }

    private void openDialogRemove(int position){
        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_confirm_remove_address);
        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnAccept = dialog.findViewById(R.id.btn_accept_dialog);

        btnClose.setOnClickListener(view -> dialog.dismiss());

        btnAccept.setOnClickListener(view -> {
            dialog.dismiss();
            removeAddress(position);
        });
        dialog.show();
    }


    public void removeAddress(int position){
        if (addressList.get(position).isDefaultAddress()){
            Toast.makeText(context, R.string.not_remove_default_address, Toast.LENGTH_SHORT).show();
            return;
        }
        if (addressNow.equals(Common.currentUser.getAddressList().get(position))){
            if (addressList.size() > 1) {
                addressNow = addressList.get(1);
                Common.addressNow = addressNow;
                notifyItemChanged(1);
            }
            else
                Common.addressNow  = null;
        }
        addressList.remove(position);
        notifyItemRemoved(position);

        Common.currentUser.setAddressList(addressList);
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

        for (Address address:Common.currentUser.getAddressList())
            address.setDefaultAddress(false);

        Common.currentUser.getAddressList().get(position).setDefaultAddress(true);
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
