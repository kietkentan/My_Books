package com.khtn.mybooks.adapter;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.StaffManagerCLickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Staff;
import com.khtn.mybooks.model.StaffPermission;

import java.util.List;

public class ListStaffAdapter extends RecyclerView.Adapter<ListStaffAdapter.ViewHolder> {
    private final List<Staff> staffList;
    private final List<Boolean> listClick;
    private final StaffManagerCLickInterface anInterface;
    private final Context context;
    private StaffPermission permission;

    public ListStaffAdapter(List<Staff> staffList, List<Boolean> listClick, StaffManagerCLickInterface anInterface, Context context) {
        this.staffList = staffList;
        this.listClick = listClick;
        this.anInterface = anInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public ListStaffAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListStaffAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_manager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListStaffAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CheckBox[] cb = new CheckBox[]{holder.cbOrderManager, holder.cbProductManager, holder.cbShopManager, holder.cbStaffManager};

        holder.viewTopLine.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvName.setText(String.format(context.getString(R.string.status), staffList.get(position).getName()));
        holder.tvId.setText(String.format(context.getString(R.string.status), staffList.get(position).getId()));
        if (listClick.get(position)){
            permission = staffList.get(position).getPermission();
            showPermission(holder, position);
            defaultButton(holder, position);
            setupCheckBoxOnCheck(holder, position, cb);
            setEnableCheckBoxPermission(holder, !(staffList.get(position).getId().equals(Common.currentUser.getStaff().getId()) ||
                    staffList.get(position).getPermission().isHighPermission()));
        }

        holder.layoutStaffInfo.setOnClickListener(v -> anInterface.onClickShowInfo(listClick.get(position) ? -1 : position));
        holder.tvUncheckAll.setOnClickListener(v -> {
            setupTextViewUnCheckedAll(holder);
            setupButtonClick(holder, position);
        });
        holder.tvCurrentSetup.setOnClickListener(v -> {
            setCheckedPermission(holder, staffList.get(position).getPermission());
            permission = staffList.get(position).getPermission();
            setupButtonClick(holder, position);
        });
        holder.btnSave.setOnClickListener(v -> {
            savePermission(position, permission);
            defaultButton(holder, position);
        });
        holder.btnRemove.setOnClickListener(v -> openDialogRemove(position));
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public void setupCheckBoxOnCheck(ListStaffAdapter.ViewHolder holder, int position, CheckBox[] cb){
        for (CheckBox checkBox : cb){
            checkBox.setOnClickListener(v -> {
                permission = new StaffPermission(cb[3].isChecked(), cb[2].isChecked(), cb[0].isChecked(), cb[1].isChecked());
                setupButtonClick(holder, position);
            });
        }
    }

    public void showPermission(ListStaffAdapter.ViewHolder holder, int position){
        holder.layoutEditPermission.setVisibility(listClick.get(position) ? View.VISIBLE : View.GONE);
        setupLayoutPermission(holder, staffList.get(position).getPermission());
    }

    public void defaultButton(ListStaffAdapter.ViewHolder holder, int position){
        holder.tvUncheckAll.setTextColor(ContextCompat.getColor(getApplicationContext(),
                staffList.get(position).getId().equals(Common.currentUser.getStaff().getId()) ||
                staffList.get(position).getPermission().checkPermission(new StaffPermission(false)) ||
                staffList.get(position).getPermission().isHighPermission() ?
                R.color.original_price : R.color.reduced_price));
        holder.tvUncheckAll.setEnabled(!(staffList.get(position).getId().equals(Common.currentUser.getStaff().getId()) ||
                staffList.get(position).getPermission().checkPermission(new StaffPermission(false)) ||
                staffList.get(position).getPermission().isHighPermission()));

        holder.tvCurrentSetup.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.original_price));
        holder.tvCurrentSetup.setEnabled(false);

        holder.btnSave.setBackgroundResource(R.drawable.custom_button_continue_hidden);
        holder.btnSave.setEnabled(false);

        holder.btnRemove.setTextColor(ContextCompat.getColor(getApplicationContext(),
                staffList.get(position).getId().equals(Common.currentUser.getStaff().getId()) ||
                staffList.get(position).getPermission().isHighPermission()
                ? R.color.original_price : R.color.reduced_price));
        holder.btnRemove.setBackgroundResource(staffList.get(position).getId().equals(Common.currentUser.getStaff().getId()) ||
                staffList.get(position).getPermission().isHighPermission()
                ? R.drawable.custom_button_close_2 : R.drawable.custom_button_close);
        holder.btnRemove.setEnabled(!(staffList.get(position).getId().equals(Common.currentUser.getStaff().getId()) ||
                staffList.get(position).getPermission().isHighPermission()));
    }

    public void setupButtonClick(ListStaffAdapter.ViewHolder holder, int position){
        holder.tvUncheckAll.setTextColor(ContextCompat.getColor(getApplicationContext(),
                permission.checkPermission(new StaffPermission(false))
                ? R.color.original_price : R.color.reduced_price));
        holder.tvUncheckAll.setEnabled(!permission.checkPermission(new StaffPermission(false)));

        holder.tvCurrentSetup.setTextColor(ContextCompat.getColor(getApplicationContext(),
                permission.checkPermission(staffList.get(position).getPermission())
                ? R.color.original_price : R.color.reduced_price));
        holder.tvCurrentSetup.setEnabled(!permission.checkPermission(staffList.get(position).getPermission()));

        holder.btnSave.setBackgroundResource(permission.checkPermission(staffList.get(position).getPermission())
                ? R.drawable.custom_button_continue_hidden : R.drawable.custom_button_continue_appear);
        holder.btnSave.setTextColor(ContextCompat.getColor(getApplicationContext(),
                permission.checkPermission(staffList.get(position).getPermission())
                ? R.color.original_price : R.color.white));
        holder.btnSave.setEnabled(!permission.checkPermission(staffList.get(position).getPermission()));
    }

    public void setupLayoutPermission(ListStaffAdapter.ViewHolder holder, StaffPermission permission){
        holder.cbStaffManager.setVisibility(Common.currentUser.getStaff().getPermission().isHighPermission() ? View.VISIBLE : View.GONE);
        holder.viewUpDown.setBackgroundResource(R.drawable.ic_up);
        setCheckedPermission(holder, permission);
    }

    public void setCheckedPermission(ListStaffAdapter.ViewHolder holder, StaffPermission permission){
        holder.cbOrderManager.setChecked(permission.isOrderManager());
        holder.cbProductManager.setChecked(permission.isProductManager());
        holder.cbShopManager.setChecked(permission.isShopManager());
        holder.cbStaffManager.setChecked(permission.isStaffManager());
    }

    public void setupTextViewUnCheckedAll(ListStaffAdapter.ViewHolder holder){
        permission = new StaffPermission(false);
        setCheckedPermission(holder, permission);
    }

    public void setEnableCheckBoxPermission(ListStaffAdapter.ViewHolder holder, boolean permission){
        holder.cbOrderManager.setEnabled(permission);
        holder.cbProductManager.setEnabled(permission);
        holder.cbShopManager.setEnabled(permission);
        holder.cbStaffManager.setEnabled(permission);
    }

    public void savePermission(int position, StaffPermission permission){
        anInterface.onClickSaveInfo(position, permission);
        staffList.get(position).setPermission(permission);
    }

    private void openDialogRemove(int position){
        Dialog dialog = new Dialog(context, R.style.FullScreenDialog);
        dialog.setContentView(R.layout.dialog_confirm_remove_staff);
        AppCompatButton btnClose = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnAccept = dialog.findViewById(R.id.btn_accept_dialog);

        btnClose.setOnClickListener(view -> dialog.dismiss());

        btnAccept.setOnClickListener(view -> {
            dialog.dismiss();
            anInterface.onClickRemoveInfo(staffList.get(position).getUserId());
            staffList.remove(position);
            notifyItemRemoved(position);
        });
        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View viewUpDown;
        View viewTopLine;
        TextView tvName;
        TextView tvId;
        TextView tvUncheckAll;
        TextView tvCurrentSetup;
        CheckBox cbOrderManager;
        CheckBox cbProductManager;
        CheckBox cbShopManager;
        CheckBox cbStaffManager;
        AppCompatButton btnSave;
        AppCompatButton btnRemove;
        LinearLayout layoutStaffInfo;
        ConstraintLayout layoutEditPermission;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewUpDown = itemView.findViewById(R.id.view_up_down);
            viewTopLine = itemView.findViewById(R.id.view_top_line);
            tvName = itemView.findViewById(R.id.tv_name_staff);
            tvId = itemView.findViewById(R.id.tv_id_staff);
            tvUncheckAll = itemView.findViewById(R.id.tv_uncheck_all);
            tvCurrentSetup = itemView.findViewById(R.id.tv_current_setup);
            cbOrderManager = itemView.findViewById(R.id.cb_order_manager);
            cbProductManager = itemView.findViewById(R.id.cb_product_manager);
            cbShopManager = itemView.findViewById(R.id.cb_shop_manager);
            cbStaffManager = itemView.findViewById(R.id.cb_staff_manager);
            btnSave = itemView.findViewById(R.id.btn_save_permission);
            btnRemove = itemView.findViewById(R.id.btn_remove_staff);
            layoutStaffInfo = itemView.findViewById(R.id.layout_staff_info);
            layoutEditPermission = itemView.findViewById(R.id.layout_edit_permission);
        }
    }
}
