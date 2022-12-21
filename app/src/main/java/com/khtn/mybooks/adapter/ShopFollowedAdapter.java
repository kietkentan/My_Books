package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtn.mybooks.R;
import com.khtn.mybooks.common.Common;
import com.khtn.mybooks.model.Publisher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShopFollowedAdapter extends RecyclerView.Adapter<ShopFollowedAdapter.ViewHolder>{
    private final List<Publisher> publisherList;
    private final List<Boolean> check = new ArrayList<>();
    private final Context context;

    private final FirebaseDatabase database;
    private final DatabaseReference reference;
    private final String[] mode = {"mybooks", "google", "facebook"};

    public ShopFollowedAdapter(List<Publisher> publisherList, Context context) {
        this.publisherList = publisherList;
        this.context = context;

        for (int i = 0; i < publisherList.size(); ++i)
            check.add(true);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");
    }

    @NonNull
    @Override
    public ShopFollowedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopFollowedAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_followed, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShopFollowedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(publisherList.get(position).getLogo()).into(holder.ivAvatar);
        holder.tvName.setText(publisherList.get(position).getName());
        holder.tvLocation.setText(publisherList.get(position).getLocation());
        holder.btnFollow.setOnClickListener(v -> {
            check.set(position, !check.get(position));
            if (!check.get(position)){
                holder.btnFollow.setText(context.getText(R.string.follow));
                holder.btnFollow.setBackgroundResource(R.drawable.custom_button_continue_appear);
                holder.btnFollow.setTextColor(Color.WHITE);
            } else {
                holder.btnFollow.setText(context.getText(R.string.followed));
                holder.btnFollow.setBackgroundResource(R.drawable.custom_button_close);
                holder.btnFollow.setTextColor(Color.parseColor("#E32127"));
            }
        });
    }

    public void updateFollowed(){
        reference.child(mode[Common.modeLogin - 1]).child(Common.currentUser.getId()).child("list_shopFollow").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
                Common.currentUser.getList_shopFollow().clear();
                int position = 0;
                for (int i = 0; i < check.size(); ++i) {
                    if (check.get(i)) {
                        snapshot.getRef().child(String.format("%d", position++)).setValue(publisherList.get(i).getId());
                        Common.currentUser.getList_shopFollow().add(publisherList.get(i).getId());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return publisherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAvatar;
        TextView tvName;
        TextView tvLocation;
        AppCompatButton btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_shop_avatar);
            tvName = itemView.findViewById(R.id.tv_shop_name);
            tvLocation = itemView.findViewById(R.id.tv_location_shop);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
