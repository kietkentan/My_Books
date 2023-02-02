package com.khtn.mybooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.helper.AppUtil;
import com.khtn.mybooks.R;

import java.util.List;

public class BookDetailAdapter extends RecyclerView.Adapter<BookDetailAdapter.ViewHolder> {
    private final List<List<String>> list;
    private final Context context;

    public BookDetailAdapter(List<List<String>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BookDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookDetailAdapter.ViewHolder holder, int position) {
        holder.tvDetailName.setText(AppUtil.getStringResourceByName(list.get(position).get(0), context));
        switch (list.get(position).get(0)) {
            case "ageRange":
                holder.tvDetailAbout.setText(getAgeRange(position));
                break;
            case "weight":
                holder.tvDetailAbout.setText(String.format(context.getString(R.string.format_weight), list.get(position).get(1)));
                break;
            case "size":
                holder.tvDetailAbout.setText(String.format(context.getString(R.string.format_size), list.get(position).get(1)));
                break;
            default:
                holder.tvDetailAbout.setText(list.get(position).get(1));
                break;
        }
    }

    public String getAgeRange(int position){
        String detail = "";
        int num = Integer.parseInt(list.get(position).get(1));

        while (true){
            detail = String.format("%s%s", detail, AppUtil.getStringResourceByName(context.getResources().getStringArray(R.array.ageRange)[(num % 10) - 1], context));
            num /= 10;
            if (num != 0) detail += "\n";
            else break;
        }
        return detail;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDetailName;
        TextView tvDetailAbout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDetailName = itemView.findViewById(R.id.tv_detail_name);
            tvDetailAbout = itemView.findViewById(R.id.tv_detail_about);
        }
    }
}
