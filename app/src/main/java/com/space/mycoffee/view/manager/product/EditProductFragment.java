package com.space.mycoffee.view.manager.product;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.space.mycoffee.R;
import com.space.mycoffee.adapter.ImageAdapter;
import com.space.mycoffee.databinding.FragmentEditProductBinding;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.view_model.manager.product.ProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class EditProductFragment extends Fragment {
    private final int REQUEST_CODE_SELECT_ADD_IMAGE = 10001;

    private FragmentEditProductBinding binding;
    private ProductViewModel viewModel;

    private ImageAdapter imageAdapter;

    private int positionPictureSelected = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            viewModel.addImage(data.getClipData(), data.getData());

            positionPictureSelected = 0;
            checkImageView();
        }
    }

    private void initView() {
        viewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        binding.setViewModel(viewModel);

        imageAdapter = new ImageAdapter(ImageAdapter.itemCallback, position -> {
            positionPictureSelected = position;
            String uri = viewModel.coffeeSelected.getValue().getImage().get(position);
            if (uri.contains("content:/"))
                binding.ivItemReviewProduct.setImageURI(Uri.parse(uri));
            else Glide.with(requireContext()).load(uri).into(binding.ivItemReviewProduct);
        });
        binding.recListReviewProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recListReviewProducts.setAdapter(imageAdapter);
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopupMenuPicture() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), binding.ibEditReviewProduct);
        popupMenu.setGravity(Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.in_image_product_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.m_add_picture:
                    selectAddImage();
                    break;
                case R.id.m_remove_picture:
                    if (positionPictureSelected > -1) viewModel.removePicture(positionPictureSelected);
                    checkImageView();
                    break;
                case R.id.m_remove_all_picture:
                    viewModel.removeAllImage();
                    checkImageView();
                    break;
            }
            return true;
        });
    }

    @SuppressWarnings("deprecation")
    public void selectAddImage() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_CODE_SELECT_ADD_IMAGE);
    }

    public void checkImageView() {
        List<String> list = viewModel.coffeeSelected.getValue().getImage();
        if (list.size() == 0) {
            positionPictureSelected = -1;
            binding.ivItemReviewProduct.setImageResource(R.color.background_shimmer);
        } else {
            positionPictureSelected = 0;
            binding.ivItemReviewProduct.setBackgroundResource(R.color.background);
            if (list.get(positionPictureSelected).contains("content:/"))
                binding.ivItemReviewProduct.setImageURI(Uri.parse(list.get(positionPictureSelected)));
            else Glide.with(requireContext()).load(list.get(positionPictureSelected)).into(binding.ivItemReviewProduct);
        }
    }

    private void clickView() {
        binding.btnCancel.setOnClickListener(view -> backView());
        binding.btnAddSave.setOnClickListener(view -> viewModel.upLoadDataImage(requireContext()));
        binding.ivSelectPhoto.setOnClickListener(view -> selectAddImage());
        binding.ibEditReviewProduct.setOnClickListener(view -> showPopupMenuPicture());
        binding.layoutLoadingProduct.setOnClickListener(view -> {});

        binding.recListReviewProducts.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = binding.recListReviewProducts.getWidth();
                if (width > 0) {
                    binding.recListReviewProducts.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    DisplayMetrics outMetrics = new DisplayMetrics();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Display display = requireActivity().getDisplay();
                        display.getRealMetrics(outMetrics);
                    } else {
                        Display display = requireActivity().getWindowManager().getDefaultDisplay();
                        display.getMetrics(outMetrics);
                    }

                    int scWidth = (int) (outMetrics.widthPixels * 0.9 - Extensions.dpToPx(50, requireContext()));

                    if (width > scWidth) {
                        binding.recListReviewProducts.getLayoutParams().width = scWidth;
                    }
                }
            }
        });
    }

    private void observe() {
        viewModel.coffeeSelected.observe(getViewLifecycleOwner(), detail -> {
            if (detail != null) {
                imageAdapter.submitList(detail.getImage());
                if (detail.getImage().size() > 0) {
                    String first = detail.getImage().get(0);
                    if (first.contains("content:/"))
                        binding.ivItemReviewProduct.setImageURI(Uri.parse(first));
                    else Glide.with(requireContext()).load(first).into(binding.ivItemReviewProduct);
                }
            }
        });
    }
}
