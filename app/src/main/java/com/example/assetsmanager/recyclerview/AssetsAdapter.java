package com.example.assetsmanager.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Asset;

import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.ViewHolder> {
    private List<Asset> assets;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnAssetsItemClick onAssetsItemClick;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.recycler_item_asset, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Asset asset = assets.get(position);
        holder.tvName.setText(asset.getName());
        holder.tvBarcode.setText(String.valueOf(asset.getBarcode()));
        Bitmap bitmap = BitmapFactory.decodeFile(asset.getImage());
        holder.ivAsset.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return assets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivAsset;
        public TextView tvName;
        public TextView tvBarcode;
        private ImageButton btnDelete;
        private ImageButton btnEdit;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            layout = itemView;
            tvName = itemView.findViewById(R.id.tv_asset_name);
            tvBarcode = itemView.findViewById(R.id.tv_barcode);
            ivAsset = itemView.findViewById(R.id.asset_image);
            btnEdit = itemView.findViewById(R.id.btn_edit_asset);
            btnDelete = itemView.findViewById(R.id.btn_delete_asset);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAssetsItemClick.onDeleteAsset(getAdapterPosition());
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAssetsItemClick.onEditAsset(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            onAssetsItemClick.onAssetClick(getAdapterPosition());
        }
    }

    public interface OnAssetsItemClick {
        void onAssetClick(int pos);
        void onEditAsset(int pos);
        void onDeleteAsset(int pos);
    }

    public AssetsAdapter(List<Asset> assets, Context context, OnAssetsItemClick click){
        this.assets = assets;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.onAssetsItemClick = click;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<Asset> assets){
        this.assets = assets;
        notifyDataSetChanged();
    }
}
