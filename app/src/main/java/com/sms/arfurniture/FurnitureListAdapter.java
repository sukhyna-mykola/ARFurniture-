package com.sms.arfurniture;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FurnitureListAdapter extends RecyclerView.Adapter<FurnitureListAdapter.ViewHolder> {

    private List<FurnitureItem> data;
    private OnItemClickListener onItemClickListener;

    public FurnitureListAdapter(OnItemClickListener onItemClickListener, List<FurnitureItem> data) {
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

        Picasso.get().load(item.getIcon())
                .placeholder(R.drawable.sceneform_hand_phone)
                .error(R.drawable.sceneform_hand_phone)
                .into(holder.icon);

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
