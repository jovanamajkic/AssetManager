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
import com.example.assetsmanager.db.model.Location;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {
    private List<Location> locations;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnLocationsItemClick onLocationsItemClick;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.recycler_item, parent, false);
        return new LocationsAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Location location = locations.get(position);
        holder.tvCity.setText(location.getCity());
        holder.tvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCity;
        public TextView tvLocation;
        public ImageButton btnDelete;
        public ImageButton btnEdit;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            tvCity = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_description);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocationsItemClick.onDeleteLocation(getAdapterPosition());
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocationsItemClick.onEditLocation(getAdapterPosition());
                }
            });
        }
    }

    public LocationsAdapter(List<Location> locations, Context context, LocationsAdapter.OnLocationsItemClick onLocationsItemClick){
        this.locations = locations;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.onLocationsItemClick = onLocationsItemClick;
    }

    public interface OnLocationsItemClick {
        void onEditLocation(int pos);
        void onDeleteLocation(int pos);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<Location> locations){
        this.locations = locations;
        notifyDataSetChanged();
    }
}
