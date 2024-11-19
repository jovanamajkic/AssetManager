package com.example.assetsmanager.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.ui.assets.AssetDetailsFragment;
import com.example.assetsmanager.ui.locations.AddLocationFragment;
import com.example.assetsmanager.ui.locations.LocationsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class LocationAsync {
    public static class RetrieveTask extends AsyncTask<Void, Void, List<Location>> {
        private WeakReference<LocationsFragment> weakReference;

        public RetrieveTask(LocationsFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Location> doInBackground(Void... voids) {
            if(weakReference.get() != null){
                return weakReference.get().getAssetsManagerDatabase().getLocationDao().getLocations();
            } else {
                return null;
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<Location> locations) {
            if(locations != null && !locations.isEmpty()){
                weakReference.get().getAllLocations().clear();
                weakReference.get().getLocations().clear();
                weakReference.get().getAllLocations().addAll(locations);
                weakReference.get().getLocations().addAll(locations);
                weakReference.get().getAdapter().notifyDataSetChanged();
            }
        }
    }

    public static class InsertTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddLocationFragment> weakReference;
        private Location location;

        public InsertTask(AddLocationFragment fragment, Location location){
            weakReference = new WeakReference<>(fragment);
            this.location = location;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            long id = weakReference.get().getAssetsManagerDatabase().getLocationDao().insertLocation(location);
            location.setId((int) id);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addLocationFragment_to_nav_locations);
            }
        }
    }

    public static class DeleteTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<LocationsFragment> weakReference;
        private Location location;
        private int pos;

        public DeleteTask(LocationsFragment fragment, Location location) {
            weakReference = new WeakReference<>(fragment);
            this.location = location;
            pos = weakReference.get().getLocations().indexOf(location);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getLocationDao().deleteLocation(location);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                weakReference.get().getLocations().remove(location);
                weakReference.get().getAllLocations().remove(location);
                weakReference.get().getAdapter().notifyItemRemoved(pos);
            }
        }
    }

    public static class UpdateTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddLocationFragment> weakReference;
        private Location location;

        public UpdateTask(AddLocationFragment fragment, Location location){
            weakReference = new WeakReference<>(fragment);
            this.location = location;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getLocationDao().updateLocation(location);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addLocationFragment_to_nav_locations);
            }
        }
    }

    public static class GetByIdTask extends AsyncTask<Void, Void, Location>{
        private WeakReference<AssetDetailsFragment> weakReference;
        private int id;

        public GetByIdTask(AssetDetailsFragment fragment, int id){
            weakReference = new WeakReference<>(fragment);
            this.id = id;
        }

        @Override
        protected Location doInBackground(Void... voids) {
            return weakReference.get().getAssetsManagerDatabase().getLocationDao().getById(id);
        }

        @Override
        protected void onPostExecute(Location location) {
            if(location != null) {
                weakReference.get().setLocation(location);
                weakReference.get().getTvLocation().setText(location.getCity());

                LatLng pin = new LatLng(location.getLatitude(), location.getLongitude());
                weakReference.get().getMap().addMarker(new MarkerOptions()
                        .position(pin)
                        .title(location.getCity())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );
                weakReference.get().getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(pin, 10));

            }
        }
    }
}
