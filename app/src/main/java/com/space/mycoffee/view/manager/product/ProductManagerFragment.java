package com.space.mycoffee.view.manager.product;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.ProductManagerAdapter;
import com.space.mycoffee.databinding.FragmentProductManagerBinding;
import com.space.mycoffee.listener.ProductManagerListener;
import com.space.mycoffee.model.CoffeeDetail;
import com.space.mycoffee.view_model.manager.product.ProductViewModel;
import com.space.mycoffee.view_model.sign_in.SignInViewModel;

public class ProductManagerFragment extends Fragment {
    private FragmentProductManagerBinding binding;
    private ProductViewModel viewModel;

    private ProductManagerAdapter productManagerAdapter;

    private String search = "";

    private OnBackPressedCallback callback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        initView();
        clickView();
        observe();
    }

    @Override
    public void onResume() {
        super.onResume();
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backView();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callback != null) callback.remove();
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        viewModel.fetchListProduct(null, requireContext());
        productManagerAdapter = new ProductManagerAdapter(ProductManagerAdapter.itemCallback, requireContext(), new ProductManagerListener() {
            @Override
            public void onItemRemoveClicked(int position) {
                viewModel.itemRemove(position);
            }

            @Override
            public void onItemClicked(CoffeeDetail detail) {
                viewModel.setCoffeeSelected(detail);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_productManagerFragment_to_editProductFragment);
            }
        });
        binding.recListProductManager.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListProductManager.setAdapter(productManagerAdapter);
    }

    private void backView() {
        requireActivity().getViewModelStore().clear();
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void clickView() {
        binding.ibExitProductManager.setOnClickListener(view -> backView());
        binding.edtSearchByProductName.setOnKeyListener((view, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager) binding.edtSearchByProductName.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.edtSearchByProductName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                binding.edtSearchByProductName.clearFocus();

                String text = binding.edtSearchByProductName.getText().toString().trim();
                if (search.equals(text))
                    return true;

                search = text;
                viewModel.fetchListProduct(search, requireContext());
                return true;
            }
            return false;
        });
    }

    private void observe() {
        viewModel.listCoffee.observe(getViewLifecycleOwner(), list -> {
            if (list != null) productManagerAdapter.submitList(list);
        });
    }
}
