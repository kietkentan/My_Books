package com.khtn.mybooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.R;

import java.util.List;

public class AgeRangeAdapter extends RecyclerView.Adapter<AgeRangeAdapter.ViewHolder>{
    private final List<Integer> ageRangeList;
    private final String[] strings;

    public AgeRangeAdapter(List<Integer> ageRangeList, Context context) {
        this.ageRangeList = ageRangeList;
        strings = context.getResources().getStringArray(R.array.ageRange);
    }

    @NonNull
    @Override
    public AgeRangeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AgeRangeAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AgeRangeAdapter.ViewHolder holder, int position) {
        holder.tvName.setTextSize(14);
        holder.tvName.setText(strings[ageRangeList.get(position) - 1]);
    }

    @Override
    public int getItemCount() {
        return ageRangeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_string_item);
        }
    }
}
