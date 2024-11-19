package com.example.assetsmanager.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Inventory;

import java.util.List;

public class InventoriesAdapter extends RecyclerView.Adapter<InventoriesAdapter.ViewHolder> {
    private List<InventoryItem> inventories;
    private Context context;
    private LayoutInflater layoutInflater;
    private InventoriesAdapter.OnInventoryItemClick onInventoryItemClick;

    public InventoriesAdapter(List<InventoryItem> inventories, Context context, OnInventoryItemClick onInventoryItemClick){
        this.inventories = inventories;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.onInventoryItemClick = onInventoryItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.recycler_item_inventory, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InventoryItem inventory = inventories.get(position);
        holder.tvAsset.setText(inventory.getInventory().getBarcode() + " - " + inventory.getAssetName());
        holder.tvEmployees.setText(inventory.getCurrEmployee() + " -> " + inventory.getNewEmployee());
        holder.tvLocations.setText(inventory.getCurrLocation() + " -> " + inventory.getNewLocation());
    }

    @Override
    public int getItemCount() {
        return inventories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEmployees;
        public TextView tvAsset;
        public TextView tvLocations;
        private ImageButton btnDelete;
        private ImageButton btnEdit;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            tvEmployees = itemView.findViewById(R.id.tv_employees);
            tvAsset = itemView.findViewById(R.id.tv_inventory_asset);
            tvLocations = itemView.findViewById(R.id.tv_locations);
            btnEdit = itemView.findViewById(R.id.btn_edit_inventory);
            btnDelete = itemView.findViewById(R.id.btn_delete_inventory);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInventoryItemClick.onEditInventory(getAdapterPosition());
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInventoryItemClick.onDeleteInventory(getAdapterPosition());
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<InventoryItem> items){
        inventories = items;
        notifyDataSetChanged();
    }

    public interface OnInventoryItemClick {
        void onEditInventory(int pos);
        void onDeleteInventory(int pos);
    }
}
