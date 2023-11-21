package com.space.mycoffee.view.manager.order;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.space.mycoffee.R;
import com.space.mycoffee.adapter.ViewPagerOrderManagerAdapter;
import com.space.mycoffee.databinding.FragmentOrderManagerBinding;
import com.space.mycoffee.listener.OnOrderChangeSizeInterface;
import com.space.mycoffee.model.Request;
import com.space.mycoffee.utils.Constants;

public class OrderManagerFragment extends Fragment {
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.REQUEST);

    private FragmentOrderManagerBinding binding;

    private String textSearch = "";
    private int currentSelectedTab = 0;
    private TabLayoutMediator tabMediator;

    private ViewPagerOrderManagerAdapter viewPagerOrderStatusAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderManagerBinding.inflate(inflater, container, false);
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
        viewPagerOrderStatusAdapter = new ViewPagerOrderManagerAdapter(requireActivity(), reference, new OnOrderChangeSizeInterface() {
            @Override
            public void onItemCLicked(Request request) {
                detailOrderView(request);
            }

            @Override
            public void changeSize() {
                viewPagerOrderStatusAdapter.changeKey(textSearch);
            }
        });
        binding.vpOrderManager.setAdapter(viewPagerOrderStatusAdapter);
        currentSelectedTab = getArguments().getInt(Constants.TAB, 0);
        tabMediator = new TabLayoutMediator(binding.tabOrderManager, binding.vpOrderManager, (tab, position) ->
                tab.setText(getResources().getStringArray(R.array.status_manager)[position])
        );
        tabMediator.attach();
        setCurrentItem();
    }

    private void setCurrentItem() {
        binding.vpOrderManager.setCurrentItem(currentSelectedTab, false);
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void detailOrderView(Request request) {
        String rString = new Gson().toJson(request);
        Bundle bundleDetail = new Bundle();
        bundleDetail.putString(Constants.REQUEST, rString);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_orderManagerFragment_to_orderDetailFragment2,
                bundleDetail
        );
    }

    private void clickView() {
        binding.ibExitOrderManager.setOnClickListener(view -> backView());

        binding.edtSearchByOrderCode.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) binding.edtSearchByOrderCode.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.edtSearchByOrderCode.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                binding.edtSearchByOrderCode.clearFocus();

                String text = binding.edtSearchByOrderCode.getText().toString().trim();
                if (textSearch.equals(text))
                    return true;

                textSearch = text;
                if (text.isEmpty()) textSearch = null;
                else viewPagerOrderStatusAdapter.changeKey(textSearch);
                return true;
            }
            return false;
        });
    }
}