package com.khtn.mybooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.R;

import java.util.List;

public class ChoseAgeRangeAdapter extends RecyclerView.Adapter<ChoseAgeRangeAdapter.ViewHolder>{
    private final List<Integer> ageRangeList;
    private final String[] strings;

    public ChoseAgeRangeAdapter(List<Integer> ageRangeList, Context context) {
        this.ageRangeList = ageRangeList;
        strings = context.getResources().getStringArray(R.array.ageRange);
    }

    @NonNull
    @Override
    public ChoseAgeRangeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChoseAgeRangeAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_age_range, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChoseAgeRangeAdapter.ViewHolder holder, int position) {
        boolean check = false;
        holder.cbAge.setText(strings[position]);
        for (int i : ageRangeList)
            if ((i - 1) == position) {
                check = true;
                break;
            }
        holder.cbAge.setChecked(check);
        holder.cbAge.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                ageRangeList.add(position + 1);
            else
                for (int i = 0; i < ageRangeList.size(); ++i)
                    if (ageRangeList.get(i) == position + 1) {
                        ageRangeList.remove(i);
                        break;
                    }
        }
        );
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbAge = itemView.findViewById(R.id.cb_age_range);
        }
    }
}
