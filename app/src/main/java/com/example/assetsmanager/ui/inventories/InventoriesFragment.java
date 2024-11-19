package com.example.assetsmanager.ui.inventories;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.assetsmanager.R;
import com.example.assetsmanager.async.EmployeeAsync;
import com.example.assetsmanager.async.InventoryAsync;
import com.example.assetsmanager.databinding.FragmentInventoriesBinding;
import com.example.assetsmanager.db.AssetsManagerDatabase;
import com.example.assetsmanager.db.model.Employee;
import com.example.assetsmanager.db.model.Inventory;
import com.example.assetsmanager.recyclerview.EmployeesAdapter;
import com.example.assetsmanager.recyclerview.InventoriesAdapter;
import com.example.assetsmanager.recyclerview.InventoryItem;
import com.example.assetsmanager.ui.employees.EmployeesFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoriesFragment extends Fragment implements InventoriesAdapter.OnInventoryItemClick {

    public FragmentInventoriesBinding binding;
    private SearchView searchViewAsset;
    private SearchView searchViewLocation;
    private RecyclerView recyclerView;
    private AssetsManagerDatabase assetsManagerDatabase;
    private List<InventoryItem> inventories;
    private List<InventoryItem> allInventories;
    private InventoriesAdapter inventoriesAdapter;
    private String assetQuery = "";
    private String locationQuery = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentInventoriesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.recyclerInventory;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        inventories = new ArrayList<>();
        allInventories = new ArrayList<>();
        inventoriesAdapter = new InventoriesAdapter(inventories, requireContext(), this);
        recyclerView.setAdapter(inventoriesAdapter);

        searchViewAsset = binding.searchViewBarcodeInv;
        searchViewAsset.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                assetQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        searchViewLocation = binding.searchViewLocation;
        searchViewLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                locationQuery = newText.toLowerCase();
                filter();
                return false;
            }
        });

        displayList();
    }

    private void displayList(){
        assetsManagerDatabase = AssetsManagerDatabase.getInstance(requireContext());
        new InventoryAsync.RetrieveTask(this).execute();
    }

    private void filter(){
        if(assetQuery.isEmpty() && locationQuery.isEmpty()){
            inventories = new ArrayList<>(allInventories);
        } else {
            inventories = allInventories.stream()
                    .filter(i -> {
                        boolean assetMatch = i.getAssetName().toLowerCase().contains(assetQuery);
                        boolean locationMatch = i.getCurrLocation().toLowerCase().contains(locationQuery);
                        return assetMatch && locationMatch;
                    })
                    .collect(Collectors.toList());
        }

        inventoriesAdapter.filter(inventories);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditInventory(int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("inventory", inventories.get(pos));
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_nav_inventories_to_addInventoryFragment, bundle);
    }

    @Override
    public void onDeleteInventory(int pos) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_alert_title)
                .setMessage(R.string.delete_alert_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    new InventoryAsync.DeleteTask(InventoriesFragment.this, inventories.get(pos)).execute();
                })
                .setNegativeButton(R.string.No, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public AssetsManagerDatabase getAssetsManagerDatabase() {
        return assetsManagerDatabase;
    }

    public List<InventoryItem> getInventories() {
        return inventories;
    }

    public List<InventoryItem> getAllInventories() {
        return allInventories;
    }

    public InventoriesAdapter getInventoriesAdapter() {
        return inventoriesAdapter;
    }
}