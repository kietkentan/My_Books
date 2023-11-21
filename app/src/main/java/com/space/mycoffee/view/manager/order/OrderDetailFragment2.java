package com.space.mycoffee.view.manager.order;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.space.mycoffee.R;
import com.space.mycoffee.adapter.CartAdapter;
import com.space.mycoffee.adapter.ItemInOrderAdapter;
import com.space.mycoffee.databinding.FragmentOrderDetailBinding;
import com.space.mycoffee.listener.CartListener;
import com.space.mycoffee.model.Order;
import com.space.mycoffee.model.Request;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;

public class OrderDetailFragment2 extends Fragment {
    private FragmentOrderDetailBinding binding;
    private Request request;

    private ItemInOrderAdapter inOrderAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        initView();
        clickView();
    }

    private void initView() {
        request = new Gson().fromJson(getArguments().getString(Constants.REQUEST), Request.class);
        binding.setRequest(request);
        binding.tvStatusInRequest.setText(getResources().getStringArray(R.array.status)[request.getStatus() - 1]);
        binding.setNumberCart(0);
        binding.tvNamePublisher.setVisibility(View.GONE);
        binding.constraintLayout7.setBackgroundColor(requireContext().getColor(R.color.reduced_price));
        binding.textView17.setTextColor(requireContext().getColor(R.color.white));
        binding.ibExitOrderDetail.setImageTintList(ColorStateList.valueOf(requireContext().getColor(R.color.white)));

        inOrderAdapter = new ItemInOrderAdapter(CartAdapter.itemCallback, new CartListener() {
            @Override
            public void onCheckedChange(String idCoffee, boolean isCheck) {}

            @Override
            public void onQuantityChange(String idCoffee, int newQuantity) {}

            @Override
            public void onItemViewChecked(String idCoffee) {}
        });
        binding.recListItem.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListItem.setAdapter(inOrderAdapter);
        inOrderAdapter.submitList(request.getOrderList());

        int quantity = 0;
        for (Order order : request.getOrderList())
            quantity += order.getCoffeeQuantity();
        binding.tvStringTempTotal.setText(String.format(getString(R.string.temp_total), quantity));
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void clickView() {
        binding.ibExitOrderDetail.setOnClickListener(view -> backView());
        binding.tvCopyCodeRequest.setOnClickListener(view ->
                Extensions.copyToClipboard(requireContext(), request.getIdRequest())
        );
        binding.layoutShoppingCart.setVisibility(View.GONE);
        binding.tvTotalPrice1.setOnClickListener(view -> {
            binding.layoutTotalPrice1.setVisibility(View.GONE);
            binding.layoutTotalPrice2.setVisibility(View.VISIBLE);
        });
    }
}
