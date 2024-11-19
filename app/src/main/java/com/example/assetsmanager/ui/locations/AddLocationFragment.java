package com.example.assetsmanager.ui.locations;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.EmployeeAsync;
import com.example.assetsmanager.async.LocationAsync;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.ui.employees.AddEmployeeFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddLocationFragment extends Fragment implements OnMapReadyCallback {
    private AssetsManagerDatabase assetsManagerDatabase;
    private Location location;
    private GoogleMap map;
    private TextInputEditText etCity;
    private TextInputEditText etLatitude;
    private TextInputEditText etLongitude;
    private MaterialButton btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_location, container, false);

        etCity = root.findViewById(R.id.et_city_name);
        etLatitude = root.findViewById(R.id.et_latitude);
        etLongitude = root.findViewById(R.id.et_longitude);
        btnSave = root.findViewById(R.id.btn_save_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());

        if (getArguments() != null && getArguments().containsKey("location")) {
            location = (Location) getArguments().getSerializable("location");
            etCity.setText(location.getCity());
            etLongitude.setText(String.valueOf(location.getLongitude()));
            etLatitude.setText(String.valueOf(location.getLatitude()));
            btnSave.setText(R.string.location_edit);
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.location_edit);
            }
        } else {
            location = new Location();
        }

        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateCoordinatesFromCityName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString();
                String latitude = etLatitude.getText().toString();
                String longitude = etLongitude.getText().toString();

                if (city.isEmpty()) {
                    ((TextInputLayout) root.findViewById(R.id.city_input_layout)).setError("Unesite ime");
                } else {
                    ((TextInputLayout) root.findViewById(R.id.city_input_layout)).setError(null);
                }

                if (latitude.isEmpty()) {
                    ((TextInputLayout) root.findViewById(R.id.lat_input_layout)).setError("Unesite geografsku širinu");
                } else {
                    ((TextInputLayout) root.findViewById(R.id.lat_input_layout)).setError(null);
                }

                if (longitude.isEmpty()) {
                    ((TextInputLayout) root.findViewById(R.id.long_input_layout)).setError("Unesite geografsku dužinu");
                } else {
                    ((TextInputLayout) root.findViewById(R.id.long_input_layout)).setError(null);
                }

                if(!city.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty()){
                    location.setCity(city);
                    location.setLatitude(Double.parseDouble(latitude));
                    location.setLongitude(Double.parseDouble(longitude));

                    if (getArguments() != null && getArguments().containsKey("location")) {
                        new LocationAsync.UpdateTask(AddLocationFragment.this, location).execute();
                        Toast.makeText(requireContext(), "Lokacija je uspješno ažurirana", Toast.LENGTH_SHORT).show();
                    } else {
                        new LocationAsync.InsertTask(AddLocationFragment.this, location).execute();
                        Toast.makeText(requireContext(), "Lokacija je uspješno dodana", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setZoomControlsEnabled(true);

        if (getArguments() != null && getArguments().containsKey("location")) {
            Location loc = (Location) getArguments().getSerializable("location");
            LatLng pin = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(pin)
                    .title(loc.getCity())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            );
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pin, 10));
        } else {
            LatLng bih = new LatLng(43.9159, 17.6791);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(bih, 5));
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                etLatitude.setText(String.valueOf(latLng.latitude));
                etLongitude.setText(String.valueOf(latLng.longitude));

                String cityName = getCityName(latLng);
                etCity.setText(cityName);

                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Označena lokacija")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );
            }
        });
    }

    private String getCityName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void updateCoordinatesFromCityName(String cityName) {
        if (!cityName.isEmpty()) {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    double latitude = address.getLatitude();
                    double longitude = address.getLongitude();
                    etLatitude.setText(String.valueOf(latitude));
                    etLongitude.setText(String.valueOf(longitude));

                    LatLng latLng = new LatLng(latitude, longitude);
                    map.clear();
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(cityName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    );
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }
}