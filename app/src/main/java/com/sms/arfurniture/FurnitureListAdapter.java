package com.sms.arfurniture;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
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

        if (item.getId() < 0) {
            holder.download.setVisibility(View.VISIBLE);
        } else {
            holder.download.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (item.getId() == 0) {
                new ChooserDialog().with((Context) onItemClickListener)
                        .withStartFile(Environment.getExternalStorageState())
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                item.setModel(path);
                                onItemClickListener.OnItemClick(item);
                            }
                        })
                        .build()
                        .show();
            } else {
                onItemClickListener.OnItemClick(item);
            }

        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public ImageView download;

        ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.furniture_item_icon);
            download = view.findViewById(R.id.download_item);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(FurnitureItem item);
    }
}
