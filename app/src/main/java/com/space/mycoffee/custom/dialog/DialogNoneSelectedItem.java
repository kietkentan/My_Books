package com.space.mycoffee.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.DialogNoneSelectedItemBinding;
import com.space.mycoffee.listener.VoidClickListener;

public class DialogNoneSelectedItem extends Dialog {
    private final DialogNoneSelectedItemBinding binding;

    private final VoidClickListener listener;

    public DialogNoneSelectedItem(Context context, VoidClickListener listener) {
        super(context, R.style.FullScreenDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_none_selected_item, null, false);
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

        binding.tvUnderstood.setOnClickListener(view -> {
            dismiss();
            listener.onClicked();
        });
    }

    public void showDialog() {
        if (isShowing()) return;
        show();
    }
}
