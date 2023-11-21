package com.space.mycoffee.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.DialogDoneAddCartBinding;
import com.space.mycoffee.listener.VoidClickListener;
import com.space.mycoffee.model.CoffeeDetail;

public class DialogDoneAddCart extends Dialog {
    private final DialogDoneAddCartBinding binding;
    private final VoidClickListener listener;

    public DialogDoneAddCart(Context context, VoidClickListener listener) {
        super(context, R.style.FullScreenDialog);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_done_add_cart, null, false);
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
        getWindow().setGravity(Gravity.BOTTOM);

        binding.btnViewCart.setOnClickListener(view -> {
            dismiss();
            listener.onClicked();
        });
    }

    public void updateItem(CoffeeDetail coffeeDetail) {
        binding.setDetail(coffeeDetail);
    }

    public void showDialog() {
        if (isShowing()) return;
        show();
    }
}
