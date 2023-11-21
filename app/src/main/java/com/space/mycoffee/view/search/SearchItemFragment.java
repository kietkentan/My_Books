package com.space.mycoffee.view.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.space.mycoffee.R;
import com.space.mycoffee.adapter.CoffeeItemAdapter;
import com.space.mycoffee.adapter.StringSearchHistoryItemAdapter;
import com.space.mycoffee.adapter.StringSearchItemAdapter;
import com.space.mycoffee.databinding.FragmentSearchItemBinding;
import com.space.mycoffee.listener.HistoryStringListener;
import com.space.mycoffee.model.CoffeeItem;
import com.space.mycoffee.utils.Constants;
import com.space.mycoffee.utils.Extensions;
import com.space.mycoffee.utils.UiState;
import com.space.mycoffee.view_model.coffee.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchItemFragment extends Fragment {
    private final int REQUEST_CODE_SPEECH_INPUT = 2006;

    private FragmentSearchItemBinding binding;
    private SearchViewModel viewModel;

    private StringSearchHistoryItemAdapter searchHistoryItemAdapter;
    private StringSearchItemAdapter searchItemAdapter;
    private CoffeeItemAdapter itemAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchItemBinding.inflate(inflater, container, false);
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

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && data != null) {
            List<String> list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (list != null && list.size() > 0) {
                viewModel.setTextSearchLast(list.get(0));
            }
        }
    }

    private void initView() {
        viewModel = new SearchViewModel(requireContext());
        binding.setViewModel(viewModel);
        viewModel.isDetail = getArguments().getBoolean(Constants.IS_DETAIL, false);

        searchHistoryItemAdapter = new StringSearchHistoryItemAdapter(StringSearchHistoryItemAdapter.itemCallback, new HistoryStringListener() {
            @Override
            public void itemClicked(String text) {
                viewModel.readDataItem(text);
            }

            @Override
            public void itemRemoveClicked(int position) {
                viewModel.removeLineSearchHistory(position);
            }
        });
        binding.recListHistorySearch.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListHistorySearch.setAdapter(searchHistoryItemAdapter);

        searchItemAdapter = new StringSearchItemAdapter(StringSearchHistoryItemAdapter.itemCallback, position -> {
            String text = searchItemAdapter.getCurrentList().get(position);
            viewModel.readDataItem(text);
        });
        binding.recListStringSearch.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.recListStringSearch.setAdapter(searchItemAdapter);

        itemAdapter = new CoffeeItemAdapter(CoffeeItemAdapter.itemCallback, this::itemCoffeeClicked);
        binding.recListItemSearch.setLayoutManager(new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false));
        binding.recListItemSearch.setAdapter(itemAdapter);
    }

    private void itemCoffeeClicked(@NonNull CoffeeItem coffeeItem) {
        Bundle bunCoffee = new Bundle();
        bunCoffee.putString(Constants.ID_COFFEE, coffeeItem.getId());
        Navigation.findNavController(binding.getRoot()).navigate(
                viewModel.isDetail ?
                        R.id.action_searchItemFragment_to_coffeeDetailFragment2 :
                        R.id.action_searchItemFragment_to_coffeeDetailFragment,
                bunCoffee
        );
    }

    @SuppressWarnings("deprecation")
    private void startVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Extensions.toast(requireContext(), e.getLocalizedMessage());
        }
    }

    private void backView() {
        Navigation.findNavController(binding.getRoot()).popBackStack();
    }

    private void clickView() {
        binding.ibExitSearchPage.setOnClickListener(view -> backView());
        binding.layoutMicro.setOnClickListener(view -> startVoice());
        binding.tvSeeMoreRemoveHistorySearch.setOnClickListener(view -> {
            if (viewModel.isRemove.getValue())
                viewModel.clearSearchHistory();
            else viewModel.setIsRemove(true);
        });

        binding.edtSearchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setTextSearchLast(editable.toString());
            }
        });

        binding.edtSearchItem.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager imm = (InputMethodManager)  binding.edtSearchItem.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.edtSearchItem.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                viewModel.readDataItem(binding.edtSearchItem.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void observe() {
        viewModel.listSearchHistory.observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                if (viewModel.isRemove.getValue()) searchHistoryItemAdapter.submitList(list);
                else {
                    List<String> lSort = list.size() > 3 ? list.subList(0, 3) : list;
                    searchHistoryItemAdapter.submitList(lSort);
                }
            }
        });

        viewModel.isRemove.observe(getViewLifecycleOwner(), isRemove -> {
            List<String> list = viewModel.listSearchHistory.getValue();
            if (list == null) return;
            if (isRemove) searchHistoryItemAdapter.submitList(list);
            else {
                List<String> lSort = list.size() > 3 ? list.subList(0, 3) : list;
                searchHistoryItemAdapter.submitList(lSort);
            }
        });

        viewModel.listSearch.observe(getViewLifecycleOwner(), list ->
                searchItemAdapter.submitList(list != null ? list : new ArrayList<>())
        );

        viewModel.listItem.observe(getViewLifecycleOwner(), list ->
                itemAdapter.submitList(list != null ? list : new ArrayList<>())
        );

        viewModel.stateSearch.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Failure) {
                Extensions.toast(requireContext(), R.string.error_state);
                viewModel.setStateSearch(null);
            }
        });
    }
}
