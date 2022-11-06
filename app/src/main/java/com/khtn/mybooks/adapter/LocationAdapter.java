package com.khtn.mybooks.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.khtn.mybooks.Interface.ChoseLocationClickInterface;
import com.khtn.mybooks.R;
import com.khtn.mybooks.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder>{
    List<Location> locationList;
    ChoseLocationClickInterface clickInterface;

    public LocationAdapter(List<Location> locationList, ChoseLocationClickInterface clickInterface) {
        this.locationList = locationList;
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvName.setText(locationList.get(position).getName_with_type());

        holder.viewLocationItem.setOnClickListener(v -> clickInterface.OnClick(locationList.get(position)));
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView viewLocationItem;
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewLocationItem = itemView.findViewById(R.id.view_location_item);
            tvName = itemView.findViewById(R.id.tv_location_item);
        }
    }
}
