package com.example.assetsmanager.async;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assetsmanager.R;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Inventory;
import com.example.assetsmanager.db.model.Location;
import com.example.assetsmanager.recyclerview.InventoryItem;
import com.example.assetsmanager.ui.inventories.AddInventoryFragment;
import com.example.assetsmanager.ui.inventories.InventoriesFragment;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryAsync {
    public static class RetrieveTask extends AsyncTask<Void, Void, List<InventoryItem>>{
        private WeakReference<InventoriesFragment> weakReference;

        public RetrieveTask(InventoriesFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<InventoryItem> doInBackground(Void... voids) {
            if(weakReference.get() != null){
                List<Inventory> inventoryList = weakReference.get().getAssetsManagerDatabase().getInventoryDao().getInventories();
                List<InventoryItem> inventoryItems = inventoryList.stream()
                        .map(inv -> {
                            String assetName = weakReference.get().getAssetsManagerDatabase()
                                    .getAssetDao().getById(inv.getBarcode()).getName();
                            String currEmployeeName = weakReference.get().getAssetsManagerDatabase()
                                    .getEmployeeDao().getById(inv.getCurrEmployeeId()).getName();
                            String newEmployeeName = weakReference.get().getAssetsManagerDatabase()
                                    .getEmployeeDao().getById(inv.getNewEmployeeId()).getName();
                            String currLocationName = weakReference.get().getAssetsManagerDatabase()
                                    .getLocationDao().getById(inv.getCurrLocationId()).getCity();
                            String newLocationName = weakReference.get().getAssetsManagerDatabase()
                                    .getLocationDao().getById(inv.getNewLocationId()).getCity();
                            return new InventoryItem(inv, assetName, currEmployeeName, newEmployeeName, currLocationName, newLocationName);
                        })
                        .collect(Collectors.toList());
                return inventoryItems;
            } else {
                return null;
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<InventoryItem> inventories) {
            if(inventories != null && !inventories.isEmpty()){
                weakReference.get().getInventories().clear();
                weakReference.get().getAllInventories().clear();
                weakReference.get().getInventories().addAll(inventories);
                weakReference.get().getAllInventories().addAll(inventories);
                weakReference.get().getInventoriesAdapter().notifyDataSetChanged();
            }
        }
    }

    public static class RetrieveEmployeesTask extends AsyncTask<Void, Void, List<Employee>>{
        private WeakReference<AddInventoryFragment> weakReference;

        public RetrieveEmployeesTask(AddInventoryFragment fragment){
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        protected List<Employee> doInBackground(Void... voids) {
            if(weakReference.get() != null){
                return weakReference.get().getAssetsManagerDatabase().getEmployeeDao().getEmployees();
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Employee> employees) {
            if(employees != null && !employees.isEmpty()){
                weakReference.get().getEmployees().clear();
                weakReference.get().getEmployees().addAll(employees);

                List<String> employeesString = employees.stream()
                        .map(Employee::getName)
                        .collect(Collectors.toList());

                weakReference.get().setEmployeeAdapter(new ArrayAdapter<>(weakReference.get().getContext(),
                        android.R.layout.simple_dropdown_item_1line, employeesString));
                weakReference.get().getSpinnerEmployee().setAdapter(weakReference.get().getEmployeeAdapter());
                if(weakReference.get().getToggleBtnEmployee().isChecked()) {
                    weakReference.get().getSpinnerEmployee().setVisibility(View.VISIBLE);
                    weakReference.get().getSpinnerEmployee().setSelection(
                            findEmployeePosition(weakReference.get().getInventory().getNewEmployeeId()));
                } else {
                    weakReference.get().getSpinnerEmployee().setVisibility(View.GONE);
                }
            }
        }

        private int findEmployeePosition(int id){
            Employee emp = weakReference.get().getEmployees().stream()
                    .filter(e -> e.getId() == id)
                    .findFirst()
                    .orElse(null);
            return (emp != null) ? weakReference.get().getEmployees().indexOf(emp) : -1;
        }
    }

    public static class RetrieveLocationsTask extends AsyncTask<Void, Void, List<Location>>{
        private WeakReference<AddInventoryFragment> weakReference;

        public RetrieveLocationsTask(AddInventoryFragment fragment){
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

        @Override
        protected void onPostExecute(List<Location> locations) {
            if(locations != null && !locations.isEmpty()){
                weakReference.get().getLocations().clear();
                weakReference.get().getLocations().addAll(locations);

                List<String> locationsString = locations.stream()
                        .map(Location::getCity)
                        .collect(Collectors.toList());

                weakReference.get().setLocationAdapter(new ArrayAdapter<>(weakReference.get().getContext(),
                        android.R.layout.simple_dropdown_item_1line, locationsString));
                weakReference.get().getSpinnerLocation().setAdapter(weakReference.get().getLocationAdapter());
                if(weakReference.get().getToggleBtnLocation().isChecked()) {
                    weakReference.get().getSpinnerLocation().setVisibility(View.VISIBLE);
                    weakReference.get().getSpinnerLocation().setSelection(
                            findLocationPosition(weakReference.get().getInventory().getNewLocationId()));
                } else {
                    weakReference.get().getSpinnerLocation().setVisibility(View.GONE);
                }
            }
        }

        private int findLocationPosition(int id){
            Location loc = weakReference.get().getLocations().stream()
                    .filter(l -> l.getId() == id)
                    .findFirst()
                    .orElse(null);
            return (loc != null) ? weakReference.get().getLocations().indexOf(loc) : -1;
        }
    }

    public static class InsertTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddInventoryFragment> weakReference;
        private Inventory inventory;

        public InsertTask(AddInventoryFragment fragment, Inventory inventory){
            weakReference = new WeakReference<>(fragment);
            this.inventory = inventory;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getInventoryDao().insertInventory(inventory);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addInventoryFragment_to_nav_inventories);
            }
        }
    }

    public static class DeleteTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<InventoriesFragment> weakReference;
        private InventoryItem inventoryItem;
        private int pos;

        public DeleteTask(InventoriesFragment fragment, InventoryItem item){
            weakReference = new WeakReference<>(fragment);
            this.inventoryItem = item;
            pos = weakReference.get().getInventories().indexOf(item);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getInventoryDao().deleteInventory(inventoryItem.getInventory());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                weakReference.get().getInventories().remove(inventoryItem);
                weakReference.get().getAllInventories().remove(inventoryItem);
                weakReference.get().getInventoriesAdapter().notifyItemRemoved(pos);
            }
        }
    }

    public static class UpdateTask extends AsyncTask<Void, Void, Boolean>{
        private WeakReference<AddInventoryFragment> weakReference;
        private Inventory inventory;

        public UpdateTask(AddInventoryFragment fragment, Inventory inventory){
            weakReference = new WeakReference<>(fragment);
            this.inventory = inventory;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            weakReference.get().getAssetsManagerDatabase().getInventoryDao().updateInventory(inventory);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                NavController navController = Navigation.findNavController(weakReference.get().requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_addInventoryFragment_to_nav_inventories);
            }
        }
    }
}
