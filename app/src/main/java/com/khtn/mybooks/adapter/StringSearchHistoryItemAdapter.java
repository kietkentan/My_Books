package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.StringHistorySearchClickInterface;
import com.khtn.mybooks.R;

import java.util.List;

public class StringSearchHistoryItemAdapter extends RecyclerView.Adapter<StringSearchHistoryItemAdapter.ViewHolder> {
    private final List<String> listSearch;
    private final Context context;
    private final StringHistorySearchClickInterface stringHistorySearchClickInterface;

    public StringSearchHistoryItemAdapter(List<String> listSearch, Context context, StringHistorySearchClickInterface stringHistorySearchClickInterface) {
        this.listSearch = listSearch;
        this.context = context;
        this.stringHistorySearchClickInterface = stringHistorySearchClickInterface;
    }

    @NonNull
    @Override
    public StringSearchHistoryItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string_search_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StringSearchHistoryItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvStringSearch.setText(listSearch.get(position));
        holder.ibRemove.setOnClickListener(v -> stringHistorySearchClickInterface.OnRemove(position));
        holder.tvStringSearch.setOnClickListener(v -> stringHistorySearchClickInterface.OnClick(listSearch.get(position)));
    }

    @Override
    public int getItemCount() {
        return listSearch.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStringSearch;
        ImageButton ibRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStringSearch = itemView.findViewById(R.id.tv_string_search);
            ibRemove = itemView.findViewById(R.id.ib_remove_string_search);
        }
    }
}
