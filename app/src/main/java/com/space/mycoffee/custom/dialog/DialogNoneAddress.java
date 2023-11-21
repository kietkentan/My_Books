package com.space.mycoffee.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.DialogNoneAddressBinding;
import com.space.mycoffee.listener.DialogTwoButtonListener;

public class DialogNoneAddress extends Dialog {
    private final DialogNoneAddressBinding binding;

    private DialogTwoButtonListener listener;

    public DialogNoneAddress(Context context, DialogTwoButtonListener listener) {
        super(context, R.style.FullScreenDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_none_address, null, false);
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
            listener.onCancelClicked();
        });
        binding.btnAcceptAddAddress.setOnClickListener(view -> {
            dismiss();
            listener.onConfirmClicked();
        });
    }

    public void showDialog() {
        if (isShowing()) return;
        show();
    }
}
