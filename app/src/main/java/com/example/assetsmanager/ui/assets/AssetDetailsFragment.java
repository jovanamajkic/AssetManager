package com.example.assetsmanager.ui.assets;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.AssetAsync;
import com.example.assetsmanager.async.EmployeeAsync;
import com.example.assetsmanager.async.LocationAsync;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Location;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AssetDetailsFragment extends Fragment implements OnMapReadyCallback {
    private AssetsManagerDatabase assetsManagerDatabase;
    private GoogleMap map;
    private Asset asset;
    private TextView tvName, tvDescription, tvBarcode, tvPrice, tvDate, tvEmployee, tvLocation;
    private ImageView ivImage;
    private Employee employee;
    private Location location;
    private List<Asset> assets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_asset_details, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.details_map);
        mapFragment.getMapAsync(this);

        assets = new ArrayList<>();

        tvName = root.findViewById(R.id.tv_asset_title);
        tvDescription = root.findViewById(R.id.tv_details_description);
        tvBarcode = root.findViewById(R.id.tv_details_barcode);
        tvPrice = root.findViewById(R.id.tv_details_price);
        tvDate = root.findViewById(R.id.tv_details_date);
        tvEmployee = root.findViewById(R.id.tv_details_employee);
        tvLocation = root.findViewById(R.id.tv_details_location);
        ivImage = root.findViewById(R.id.iv_asset_image);

        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        if (getArguments() != null && getArguments().containsKey("asset")) {
            asset = (Asset) getArguments().getSerializable("asset");

            new EmployeeAsync.GetByIdTask(this, asset.getEmployeeId()).execute();
            new LocationAsync.GetByIdTask(this, asset.getLocationId()).execute();
            new AssetAsync.GetByLocationTask(this, asset.getLocationId()).execute();

            tvName.setText(asset.getName());
            tvDescription.setText(asset.getDescription());
            tvBarcode.setText(String.valueOf(asset.getBarcode()));
            tvPrice.setText(String.valueOf(asset.getPrice()));
            tvDate.setText(format.format(asset.getCreationDate()));
            tvLocation.setText(String.valueOf(asset.getLocationId()));
            File file = new File(asset.getImage());
            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.ic_menu_gallery)
                    .error(R.drawable.ic_error)
                    .into(ivImage);
        }

        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.menu_assets);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                        assets.stream().map(Asset::getName).collect(Collectors.toList()));

                builder.setAdapter(adapter, null);

                builder.setNegativeButton(R.string.close, (dialog, which) -> dialog.dismiss());
                builder.show();
                return false;
            }
        });
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TextView getTvLocation() {
        return tvLocation;
    }

    public TextView getTvEmployee() {
        return tvEmployee;
    }

    public GoogleMap getMap() {
        return map;
    }

    public List<Asset> getAssets() {
        return assets;
    }
}