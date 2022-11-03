package com.khtn.mybooks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.AddressClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder>{
    List<Address> addressList;
    AddressClickInterface clickInterface;
    int selectedPosition = Common.addressNow;

    public AddressAdapter(List<Address> addressList, AddressClickInterface clickInterface) {
        this.addressList = addressList;
        this.clickInterface = clickInterface;
    }


    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        holder.btnChoseAddress.setChecked(position == selectedPosition);
        holder.tvNameUser.setText(addressList.get(position).getName());
        holder.tvPhoneUser.setText(addressList.get(position).getPhone());
        holder.tvAddressUser.setText(addressList.get(position).getAddress());
        if (addressList.get(position).isDefaultAddress())
            holder.tvDefault.setVisibility(View.VISIBLE);
        else {
            holder.tvDefault.setVisibility(View.INVISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.tvDefault.getLayoutParams();
            params.bottomMargin = 0;
            holder.tvDefault.setLayoutParams(params);
        }
        holder.btnChoseAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    selectedPosition = holder.getAdapterPosition();
                    clickInterface.OnClick();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton btnChoseAddress;
        TextView tvNameUser;
        TextView tvPhoneUser;
        TextView tvAddressUser;
        TextView tvDefault;
        ImageButton ibEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnChoseAddress = itemView.findViewById(R.id.btn_chose_address);
            tvNameUser = itemView.findViewById(R.id.tv_name_user);
            tvPhoneUser = itemView.findViewById(R.id.tv_phone_user);
            tvAddressUser = itemView.findViewById(R.id.tv_address_user);
            tvDefault = itemView.findViewById(R.id.tv_default_location);
            ibEdit = itemView.findViewById(R.id.ib_edit_address);
        }
    }
}
