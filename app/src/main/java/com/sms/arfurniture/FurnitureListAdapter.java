package com.sms.arfurniture;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mykola.fishing.R;
import mykola.fishing.database.model.CaughtFish;
import mykola.fishing.database.model.Fish;
import mykola.fishing.helpers.AssetsHelper;
import mykola.fishing.helpers.DataHelper;
import mykola.fishing.helpers.Utils;
import mykola.fishing.views.TextViewWithFont;


public class LocalTopAdapter extends RecyclerView.Adapter<LocalTopAdapter.ViewHolder> {
    private List<FurnitureItem> data;
    private OnItemClickListener onItemClickListener;

    public LocalTopAdapter(OnItemClickListener onItemClickListener, List<FurnitureItem> data) {
        this.data = data;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.furniture_item_card, null);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FurnitureItem item = data.get(position);

        holder.icon.
        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.OnItemClick(item);
        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;


        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.furniture_item_icon);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(FurnitureItem item);
    }
}
