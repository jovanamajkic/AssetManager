package com.example.assetsmanager.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Asset;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.ui.assets.AddAssetFragment;
import com.example.assetsmanager.ui.assets.AssetDetailsFragment;
import com.example.assetsmanager.ui.assets.AssetsFragment;
import com.example.assetsmanager.ui.inventories.AddInventoryFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class AssetAsync {
    public static class RetrieveTask extends AsyncTask<Void, Void, List<Asset>>{
        private WeakReference<AssetsFragment> weakReference;

        public RetrieveTask(AssetsFragment fragment) {
            this.weakReference = new WeakReference<>(fragment);
        }
        

        @Override
        protected List<Asset> doInBackground(Void... voids) {
            if (weakReference.get() != null)
                return weakReference.get().getAssetsManagerDatabase().getAssetDao().getAssets();
            else
                return null;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<Asset> assets) {
            if (assets != null && !assets.isEmpty()) {
                weakReference.get().getAssets().clear();
                weakReference.get().getAllAssets().clear();
                weakReference.get().getAssets().addAll(assets);
                weakReference.get().getAllAssets().addAll(assets);
                weakReference.get().getAssetsAdapter().notifyDataSetChanged();
                weakReference.get().getProgressBar().setVisibility(View.GONE);
                weakReference.get().getRecyclerView().setVisibility(View.VISIBLE);
            }
        }
    }

    public static class RetrieveEmployeesTask extends AsyncTask<Void, Void, List<Employee>>{
        private WeakReference<AddAssetFragment> weakReference;

        public RetrieveEmployeesTask(AddAssetFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            if(weakReference.get() != null)
                return weakReference.get().getAssetsManagerDatabase().getEmployeeDao().getEmployees();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Employee> employees) {
            if (employees != null && !employees.isEmpty()) {
                weakReference.get().getEmployees().clear();
                weakReference.get().getEmployees().addAll(employees);

                List<String> employeesString = employees.stream()
                        .map(e -> e.getName())
                        .collect(Collectors.toList());

                weakReference.get().setEmployeeAdapter(new ArrayAdapter<>(weakReference.get().getContext(),
                        android.R.layout.simple_dropdown_item_1line, employeesString));
                weakReference.get().getSpinnerEmployee().setAdapter(weakReference.get().getEmployeeAdapter());

                if (weakReference.get().getAsset() != null) {
                    weakReference.get().getSpinnerEmployee().setSelection(
                            findEmployeePosition(weakReference.get().getAsset().getEmployeeId()));
                }
            }
        }

        private int findEmployeePosition(int id){
            Employee e = weakReference.get().getEmployees().stream()
                    .filter(emp -> emp.getId() == id)
                    .findFirst()
                    .orElse(null);
            Log.d("pozicija", "id: " + (e != null));
            return (e != null) ? weakReference.get().getEmployees().indexOf(e) : -1;
        }
    }

    public static class RetrieveLocationsTask extends AsyncTask<Void, Void, List<Location>>{
        private WeakReference<AddAssetFragment> weakReference;

        public RetrieveLocationsTask(AddAssetFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Location> doInBackground(Void... voids) {
            if(weakReference.get() != null)
                return weakReference.get().getAssetsManagerDatabase().getLocationDao().getLocations();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Location> locations) {
            if (locations != null && !locations.isEmpty()) {
                weakReference.get().getLocations().clear();
                weakReference.get().getLocations().addAll(locations);

                List<String> locationsString = locations.stream()
                                .map(l -> l.getCity())
                                .collect(Collectors.toList());

                weakReference.get().setLocationAdapter(new ArrayAdapter<>(weakReference.get().getContext(),
                        android.R.layout.simple_dropdown_item_1line, locationsString));
                weakReference.get().getSpinnerLocation().setAdapter(weakReference.get().getLocationAdapter());

                if (weakReference.get().getAsset() != null) {
                    weakReference.get().getSpinnerLocation().setSelection(
                            findLocationPosition(weakReference.get().getAsset().getLocationId()));
                }
            }
        }

        private int findLocationPosition(int id){
            Location l = weakReference.get().getLocations().stream()
                    .filter(loc -> loc.getId() == id)
                    .findFirst()
                    .orElse(null);
            return (l != null) ? weakReference.get().getLocations().indexOf(l) : -1;
        }
    }

    public static class InsertTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddAssetFragment> weakReference;
        private Asset asset;

        public InsertTask(AddAssetFragment fragment, Asset asset){
            weakReference = new WeakReference<>(fragment);
            this.asset = asset;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            long id = weakReference.get().getAssetsManagerDatabase().getAssetDao().insertAsset(asset);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addAssetFragment_to_nav_assets);
            }
        }
    }

    public static class DeleteTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AssetsFragment> weakReference;
        private Asset asset;
        private int pos;

        public DeleteTask(AssetsFragment fragment, Asset asset){
            weakReference = new WeakReference<>(fragment);
            this.asset = asset;
            pos = weakReference.get().getAssets().indexOf(asset);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getAssetDao().deleteAsset(asset);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                weakReference.get().getAssets().remove(asset);
                weakReference.get().getAllAssets().remove(asset);
                weakReference.get().getAssetsAdapter().notifyItemRemoved(pos);
            }
        }
    }

    public static class UpdateTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddAssetFragment> weakReference;
        private Asset asset;

        public UpdateTask(AddAssetFragment fragment, Asset asset){
            weakReference = new WeakReference<>(fragment);
            this.asset = asset;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getAssetDao().updateAsset(asset);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addAssetFragment_to_nav_assets);
            }
        }
    }

    public static class GetByLocationTask extends AsyncTask<Void, Void, List<Asset>>{
        private WeakReference<AssetDetailsFragment> weakReference;
        private int id;

        public GetByLocationTask(AssetDetailsFragment fragment, int id){
            weakReference = new WeakReference<>(fragment);
            this.id = id;
        }

        @Override
        protected List<Asset> doInBackground(Void... voids) {
            return weakReference.get().getAssetsManagerDatabase().getAssetDao().getAssetsByLocation(id);
        }

        @Override
        protected void onPostExecute(List<Asset> assets) {
            if(assets != null && !assets.isEmpty()){
                weakReference.get().getAssets().clear();
                weakReference.get().getAssets().addAll(assets);
            }
        }
    }

    public static class GetByIdTask extends AsyncTask<Void, Void, Asset>{
        private WeakReference<AddInventoryFragment> weakReference;
        private long id;

        public GetByIdTask(AddInventoryFragment fragment, long id){
            weakReference = new WeakReference<>(fragment);
            this.id = id;
        }

        @Override
        protected Asset doInBackground(Void... voids) {
            Asset a = weakReference.get().getAssetsManagerDatabase().getAssetDao().getById(id);
            return a;
        }

        @Override
        protected void onPostExecute(Asset asset) {
            if(asset != null){
                weakReference.get().setAsset(asset);

                Employee employee = weakReference.get().getEmployees().stream()
                        .filter(e -> e.getId() == asset.getEmployeeId())
                        .findFirst()
                        .orElse(null);
                weakReference.get().setCurrentEmployee(employee);
                weakReference.get().getEtCurrEmployee().setText(employee.getName());

                Location location = weakReference.get().getLocations().stream()
                        .filter(l -> l.getId() == asset.getLocationId())
                        .findFirst()
                        .orElse(null);
                weakReference.get().setCurrentLocation(location);
                weakReference.get().getEtCurrLocation().setText(location.getCity());
            }
        }
    }
}
