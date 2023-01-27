package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.ImageClickInterface;
import com.khtn.mybooks.Interface.ItemChangedInterface;
import com.khtn.mybooks.R;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder>{
    private final List<String> authorList;
    private final ItemChangedInterface anInterface;
    private final Context context;

    public AuthorAdapter(List<String> authorList, ItemChangedInterface anInterface, Context context) {
        this.authorList = authorList;
        this.anInterface = anInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public AuthorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AuthorAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_string_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(authorList.get(position));
        holder.tvName.setTextSize(14);

        holder.viewLocationItem.setOnLongClickListener(v -> {
            showPopupMenuAuthor(holder, position);
            return true;
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopupMenuAuthor(AuthorAdapter.ViewHolder holder, int position){
        PopupMenu popupMenu = new PopupMenu(context, holder.tvName);
        popupMenu.getMenuInflater().inflate(R.menu.in_author_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.m_edit_author:
                    anInterface.onChanged(position);
                    break;
                case R.id.m_remove_author:
                    anInterface.onRemove(position);
                    break;
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView viewLocationItem;
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewLocationItem = itemView.findViewById(R.id.view_string_item);
            tvName = itemView.findViewById(R.id.tv_string_item);
        }
    }
}
