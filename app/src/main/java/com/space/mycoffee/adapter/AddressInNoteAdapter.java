package com.space.mycoffee.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.space.mycoffee.R;
import com.space.mycoffee.custom.dialog.DialogConfirmRemoveAddress;
import com.space.mycoffee.databinding.ItemAddressNoteBinding;
import com.space.mycoffee.listener.AddressListener;
import com.space.mycoffee.listener.DialogTwoButtonListener;
import com.space.mycoffee.listener.OnAddressListener;
import com.space.mycoffee.model.Address;

import java.util.List;

public class AddressInNoteAdapter extends ListAdapter<Address, AddressInNoteAdapter.ViewHolder> {
    private final DialogConfirmRemoveAddress dialogConfirmRemoveAddress;
    private final Context context;
    private final AddressListener listener;
    private final OnAddressListener onAddressListener = this::showMenuPopup;

    public AddressInNoteAdapter(@NonNull DiffUtil.ItemCallback<Address> diffCallback, Context context, AddressListener listener) {
        super(diffCallback);
        this.context = context;
        this.listener = listener;
        dialogConfirmRemoveAddress = new DialogConfirmRemoveAddress(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressNoteBinding itemView = ItemAddressNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address item = getItem(position);
        holder.bind(item, position, onAddressListener, listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void submitList(@Nullable List<Address> list) {
        super.submitList(list);
        notifyDataSetChanged();
    }

    @SuppressLint({"NonConstantResourceId"})
    public void showMenuPopup(View view, int position, Address address){
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setGravity(Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.in_address_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.m_set_default:
                    listener.onDefaultChange(position);
                    break;
                case R.id.m_edit:
                    listener.onItemClicked(address);
                    break;
                case R.id.m_remove:
                    openDialogRemove(position);
                    break;
            }
            return false;
        });
    }

    private void openDialogRemove(int position) {
        dialogConfirmRemoveAddress.setListener(new DialogTwoButtonListener() {
            @Override
            public void onCancelClicked() {}

            @Override
            public void onConfirmClicked() {
                listener.onRemoveItem(position);
            }
        });
        dialogConfirmRemoveAddress.showDialog();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAddressNoteBinding binding;
        public ViewHolder(@NonNull ItemAddressNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Address item, int position, OnAddressListener addressListener, AddressListener listener) {
            binding.setAddress(item);
            binding.ibEditAddress.setOnClickListener(view -> addressListener.openListOption(binding.ibEditAddress, position, item));
            binding.csItemAddress.setOnClickListener(view -> listener.onItemClicked(item));
        }
    }
}
