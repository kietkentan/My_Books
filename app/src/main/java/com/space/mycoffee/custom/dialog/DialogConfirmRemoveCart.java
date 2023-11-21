package com.space.mycoffee.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.DialogConfirmRemoveCartBinding;
import com.space.mycoffee.listener.DialogConfirmRemoveCartListener;

import java.util.List;

public class DialogConfirmRemoveCart extends Dialog {
    private final DialogConfirmRemoveCartBinding binding;
    private final DialogConfirmRemoveCartListener listener;
    private List<String> listRemove;

    public DialogConfirmRemoveCart(Context context, DialogConfirmRemoveCartListener listener) {
        super(context, R.style.FullScreenDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_confirm_remove_cart, null, false);
        this.listener = listener;
        initView();
    }

    private void initView() {
        setContentView(binding.getRoot());
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setAttributes(layoutParams);
        getWindow().setGravity(Gravity.CENTER_VERTICAL);

        binding.btnCloseDialog.setOnClickListener(view -> {
            dismiss();
            listener.onCancelClick();
        });
        binding.btnRemove.setOnClickListener(view -> {
            dismiss();
            listener.onConfirmClick(listRemove);
        });
    }

    public void showDialog(List<String> listRemove) {
        this.listRemove = listRemove;
        if (isShowing()) return;
        show();
    }
}
