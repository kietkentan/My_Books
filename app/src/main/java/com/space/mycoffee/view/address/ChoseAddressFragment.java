package com.space.mycoffee.view.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.space.mycoffee.R;
import com.space.mycoffee.databinding.FragmentChoseAddressBinding;
import com.space.mycoffee.listener.ChoseLocationClickInterface;
import com.space.mycoffee.model.Location;
import com.space.mycoffee.view_model.address.AddressViewModel;

public class ChoseAddressFragment extends Fragment {
    private FragmentChoseAddressBinding binding;
    private AddressViewModel viewModel;

    private ChoseLocationClickInterface clickInterface;
    private boolean isSuccess = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChoseAddressBinding.inflate(inflater, container, false);
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

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);
        binding.setViewModel(viewModel);

        clickInterface = new ChoseLocationClickInterface() {
            @Override
            public void onClick(Location location) {
                int code = location.getCode().length();

                if (code == 2) viewModel.setCityLocation(location);
                else if (code == 3) viewModel.setDistrictsLocation(location);
                else if (code == 5) {
                    isSuccess = true;
                    viewModel.setPrecinctLocation(location);
                    backView();
                }
            }

            @Override
            public void onFailure() {
                viewModel.saveLocation();
            }
        };
    }


    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void clickView() {
        binding.ibExitChoseAddress.setOnClickListener(view -> backView());
        binding.tvCities.setOnClickListener(view -> viewModel.setCityLocation(null));
        binding.tvDistricts.setOnClickListener(view -> viewModel.setDistrictsLocation(null));
    }

    private void observe() {
        viewModel.tabCode.observe(getViewLifecycleOwner(), tab -> {
            if (!isSuccess) {
                if (tab == 1) {
                    Fragment citiesFrag = new FindAddressFragment(clickInterface, "0");
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_location, citiesFrag).commit();
                } else if (tab == 2) {
                    Location location = viewModel.cityLocation.getValue();
                    if (location == null) return;
                    Fragment districtFrag = new FindAddressFragment(clickInterface, location.getCode());
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_location, districtFrag).commit();
                } else if (tab == 3) {
                    Location location = viewModel.districtsLocation.getValue();
                    if (location == null) return;
                    Fragment precinctFrag = new FindAddressFragment(clickInterface, location.getCode());
                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list_location, precinctFrag).commit();
                }
            }
        });

        viewModel.precinctLocation.observe(getViewLifecycleOwner(), it -> {
            if (it != null) viewModel.saveLocation();
        });
    }
}
