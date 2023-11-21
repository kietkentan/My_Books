package com.space.mycoffee.view.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentUserBinding;
import com.space.mycoffee.utils.AppSingleton;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.view_model.user.UserViewModel;

public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private UserViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
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
        viewModel.setUser(AppSingleton.currentUser);
    }

    private void loginView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_userFragment_to_signInSignUpFragment);
    }

    private void noteAddressView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_userFragment_to_noteAddressFragment);
    }

    private void favoriteView() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.favoriteFragment);
    }

    private void orderStatusView(int position) {
        Bundle bundleStatus = new Bundle();
        bundleStatus.putInt(Constants.TAB, position);
        Navigation.findNavController(binding.getRoot()).navigate(
                R.id.action_userFragment_to_orderStatusFragment,
                bundleStatus
        );
    }

    private void initView() {
        viewModel = new UserViewModel(requireActivity());
        binding.setViewModel(viewModel);
    }

    private void clickView() {
        binding.btnLogout.setOnClickListener(view -> viewModel.logout());
        binding.btnLoginUser.setOnClickListener(view -> loginView());
        binding.tvFavoriteItem.setOnClickListener(view -> favoriteView());
        binding.tvNoteAddress.setOnClickListener(view -> noteAddressView());

        binding.layoutListAllRequest.setOnClickListener(view -> orderStatusView(0));
        binding.layoutListOrderWaitConfirm.setOnClickListener(view -> orderStatusView(1));
        binding.layoutListOderWaitShipping.setOnClickListener(view -> orderStatusView(2));
        binding.layoutListOrdersInTransit.setOnClickListener(view -> orderStatusView(3));
        binding.layoutListDelivered.setOnClickListener(view -> orderStatusView(4));
        binding.layoutPacketReturn.setOnClickListener(view -> orderStatusView(5));
    }

    private void observe() {
        viewModel.user.observe(getViewLifecycleOwner(), user -> {
            if (user == null) binding.ivBackgroundUser.clearColorFilter();
        });
    }
}
