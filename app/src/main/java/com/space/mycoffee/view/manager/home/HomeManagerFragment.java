package com.space.mycoffee.view.manager.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentHomeManagerBinding;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.view.main.MainActivity;
import com.space.mycoffee.view_model.manager.product.ProductViewModel;

public class HomeManagerFragment extends Fragment {
    private FragmentHomeManagerBinding binding;

    private boolean clickProduct = false;
    private boolean clickOrder = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeManagerBinding.inflate(inflater, container, false);
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
        changeLayout();
    }

    private void logout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(requireActivity(), gso);
        AppSingleton.clearUser(requireActivity());
        if (AppSingleton.modeLogin == 2)
            gsc.signOut().addOnCompleteListener(requireActivity(), task -> {});
        AppSingleton.signOut();

        Intent intent = new Intent(requireActivity(), MainActivity.class);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.alpha_appear_100, R.anim.alpha_hidden_100);
        requireActivity().finish();
    }

    private void changeLayout() {
        binding.tvProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_product, 0, clickProduct ? R.drawable.ic_up : R.drawable.ic_down, 0);
        binding.tvOrder.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_product, 0, clickOrder ? R.drawable.ic_up : R.drawable.ic_down, 0);
        binding.layoutOptionOrder.setVisibility(clickOrder ? View.VISIBLE : View.GONE);
        binding.layoutOptionProduct.setVisibility(clickProduct ? View.VISIBLE : View.GONE);
    }

    private void allProductView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeManagerFragment_to_productManagerFragment);
    }

    private void orderStatusView(int position) {
        Bundle bundleStatus = new Bundle();
        bundleStatus.putInt(Constants.TAB, position);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_homeManagerFragment_to_orderManagerFragment,
                bundleStatus
        );
    }

    private void clickView() {
        binding.btnLogout.setOnClickListener(view -> logout());
        binding.tvProduct.setOnClickListener(view -> {
            clickProduct = !clickProduct;
            clickOrder = false;
            changeLayout();
        });

        binding.tvOrder.setOnClickListener(view -> {
            clickProduct = false;
            clickOrder = !clickOrder;
            changeLayout();
        });

        binding.tvOptionAllProduct.setOnClickListener(view -> allProductView());
        binding.tvOptionAddProduct.setOnClickListener(view -> {
            (new ViewModelProvider(requireActivity()).get(ProductViewModel.class)).setCoffeeSelected(null);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeManagerFragment_to_editProductFragment);
        });

        binding.tvOptionOrderWaiting.setOnClickListener(view -> orderStatusView(0));
        binding.tvOptionOrderShipping.setOnClickListener(view -> orderStatusView(1));
        binding.tvOptionOrderCancel.setOnClickListener(view -> orderStatusView(3));
        binding.tvOptionAllOrder.setOnClickListener(view -> orderStatusView(4));
    }
}
