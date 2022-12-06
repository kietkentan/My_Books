package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.StringSearchClickInterface;
import com.khtn.mybooks.R;

import java.util.List;

public class StringSearchItemAdapter extends RecyclerView.Adapter<StringSearchItemAdapter.ViewHolder> {
    private final List<String> listSearch;
    private final StringSearchClickInterface stringSearchClickInterface;
    private final Context context;

    public StringSearchItemAdapter(List<String> listSearch, StringSearchClickInterface stringSearchClickInterface, Context context) {
        this.listSearch = listSearch;
        this.stringSearchClickInterface = stringSearchClickInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public StringSearchItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StringSearchItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvStringSearch.setText(listSearch.get(position));
        holder.tvStringSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringSearchClickInterface.OnClick(listSearch.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listSearch.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStringSearch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStringSearch = itemView.findViewById(R.id.tv_string_search);
        }
    }
}
